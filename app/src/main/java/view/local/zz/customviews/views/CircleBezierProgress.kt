package MyViews

import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.animation.LinearInterpolator

/**
 * Created by Administrator on 2018/7/13.
 * 自定义水波纹进度球
 *
 */
class CircleBezierProgress : View {
    //控件默认的宽
    private var defult_Widht = DisplayUtils.dip2px(context, 150F)
    //控件默认的高
    private var defult_Height = DisplayUtils.dip2px(context, 150F)
    //默认圆绘制线宽度
    private var defult_bg_StrokeWidth = DisplayUtils.dip2px(context, 4F)
    //绘制圆的画笔
    private lateinit var bg_Paint: Paint
    //需要裁剪的区域
    private val clipCirclePath: Path
    //绘制波浪的路径
    private val mWavePath: Path
    //波形的一个周期长度
    private val cycle = DisplayUtils.dip2px(context, 90F)
    //波振幅高度
    private val waveHeight = DisplayUtils.dip2px(context, 20F)
    //屏幕外波形绘制几个周期
    private val cycle_Count = 4
    //画线波浪的画笔
    private lateinit var wave_Paint: Paint
    //平移的距离
    private var movelenght = 0F
        set(value) {
            field = value
            invalidate()
        }
    //Y方法关键点移动的距离
    private var progress = 0F
        set(value) {
            field = value
            invalidate()
        }
    //默认文字的大小
    private val textSize = DisplayUtils.sp2px(context, 38F)
    //绘制中心文字的画笔
    private lateinit var centerText_Paint: Paint

    constructor(context: Context?) : this(context, null)
    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : this(context, attrs, defStyleAttr, 0)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes) {
        initPaint()
        //需要裁剪的区域
        clipCirclePath = Path()
        //绘制曲线的路径
        mWavePath = Path()
    }

    //初始化画笔
    private fun initPaint() {
        //创建绘制弧形的画笔
        bg_Paint = Paint(Paint.ANTI_ALIAS_FLAG)
        //画笔的颜色
        bg_Paint.color = Color.parseColor("#FFAA00")
        //设置画线模式
        bg_Paint.style = Paint.Style.STROKE
        //设置画线的宽度
        bg_Paint.strokeWidth = defult_bg_StrokeWidth

        //画线波浪的画笔
        wave_Paint = Paint(Paint.ANTI_ALIAS_FLAG)
        //设置画线模式
        wave_Paint.style = Paint.Style.FILL
        //设置线的颜色
        wave_Paint.color = Color.parseColor("#FFAA00")

        //创建绘制中心文字的画笔
        centerText_Paint = Paint(Paint.ANTI_ALIAS_FLAG)
        //设置绘制文字画笔颜色
        centerText_Paint.color = Color.parseColor("#eeeeee")
        //计算文字开始大小
        centerText_Paint.textSize = textSize.toFloat()
        //设置中心位置
        centerText_Paint.textAlign = Paint.Align.CENTER
        //设置文字加粗
        centerText_Paint.isFakeBoldText = true
    }


    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        setMeasuredDimension(resolveSize(defult_Widht.toInt(), widthMeasureSpec), resolveSize(defult_Height.toInt(), heightMeasureSpec))
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        //矫正宽度和高度
        defult_Widht = w.toFloat()
        defult_Height = h.toFloat()
        //添加裁剪区域
        clipCirclePath.addCircle(defult_Widht / 2, defult_Height / 2, defult_Widht / 2, Path.Direction.CW)
        //左右播放的动画
        // 防止内存泄漏，记得停止动画，不是本文的重点，不再提供方法
        val animator = ObjectAnimator.ofFloat(this, "movelenght", 0F, cycle * cycle_Count)
        animator.duration = 4000
        animator.repeatCount = -1
        animator.interpolator = LinearInterpolator()
        animator.start()
    }

    override fun onDraw(canvas: Canvas) {
        //计算控件真实显示的宽高
        val realWidht = right - left
        val realHeight = bottom - top
        //默认缩放比例
        var scal = 1.0F
        //说明需要缩放，计算缩放绘制
        if (realWidht < defult_Widht || realHeight < defult_Height) {
            scal = Math.min(realWidht / defult_Widht, realHeight / defult_Height)
            canvas.save()
            //缩放绘制
            canvas.scale(scal, scal)
        }
        //裁剪
        canvas.clipPath(clipCirclePath)
        //绘制背景圆形
        drawCircle(canvas)
        //绘制波形图
        drawBez(canvas)

        drawText(canvas)

    }


    //画背景圆
    private fun drawCircle(canvas: Canvas) {
        //计算圆心和半径
        val centerX = defult_Widht / 2
        val centerY = defult_Height / 2
        //这里的半径指定的是线宽度的中心位置
        val radius = (defult_Widht - defult_bg_StrokeWidth) / 2
        canvas.drawCircle(centerX, centerY, radius, bg_Paint)
    }

    //绘制波形图，用贝塞尔曲线,
    private fun drawBez(canvas: Canvas) {
        mWavePath.reset()
        //开始点
        var startX = -cycle * cycle_Count + movelenght
        var startY = defult_Height + waveHeight / 2 - progress
        mWavePath.moveTo(startX, startY)
        //计算屏幕外左边所有控制点
        for (i in cycle_Count downTo 1) {
            //波峰
            var fengX = -cycle * (i) + cycle / 4 + movelenght
            var fengY = defult_Height - progress
            //中间点1
            var endX = -cycle * (i) + cycle / 2 + movelenght
            var endY = defult_Height + waveHeight / 2 - progress
            mWavePath.quadTo(fengX, fengY, endX, endY)

            //波谷的点
            var guX = -cycle * (i) + cycle * 3 / 4 + movelenght
            var guY = defult_Height + waveHeight - progress

            //中间点2
            var endX2 = -cycle * (i - 1) + movelenght
            var endY2 = defult_Height + waveHeight / 2 - progress

            mWavePath.quadTo(fengX, fengY, endX, endY)
            mWavePath.quadTo(guX, guY, endX2, endY2)
        }


        //计算屏幕右边所有控制点
        for (i in 1..cycle_Count) {
            //波峰
            var fengX = cycle * (i) - cycle * 3 / 4 + movelenght
            var fengY = defult_Height - progress
            //中间点1
            var endX = cycle * (i) - cycle / 2 + movelenght
            var endY = defult_Height + waveHeight / 2 - progress
            //波谷的点
            var guX = cycle * (i) - cycle * 1 / 4 + movelenght
            var guY = defult_Height + waveHeight - progress

            //中间点2
            var endX2 = cycle * (i) + movelenght
            var endY2 = defult_Height + waveHeight / 2 - progress

            mWavePath.quadTo(fengX, fengY, endX, endY)
            mWavePath.quadTo(guX, guY, endX2, endY2)
        }

        mWavePath.lineTo(cycle_Count * cycle, defult_Height + waveHeight)
        mWavePath.lineTo(-cycle_Count * cycle, defult_Height + waveHeight)

        //以上吧点添加完毕，然后就是上移一下看看效果
        canvas.drawPath(mWavePath, wave_Paint)

    }

    //要想绘制文字
    private fun drawText(canvas: Canvas) {
        //要想绘制文字，就需要计算文字的开始位置
        val startX = defult_Widht / 2
        val fontMetrics = centerText_Paint.fontMetrics
        val baseLine = defult_Height / 2 + (fontMetrics.bottom - fontMetrics.top) / 2 - fontMetrics.bottom
        canvas.drawText("${(progress / (defult_Height + waveHeight) * 100).toInt()}%", startX, baseLine, centerText_Paint)
    }


    /**
     * 设置进度，然后进行动画播放
     */

    fun setProgress(tagretProgress: Int, duration: Long = 5000) {

        val animator2 = ObjectAnimator.ofFloat(this, "progress", 0F, (defult_Height + waveHeight) * tagretProgress / 100)
        animator2.duration = duration
        animator2.interpolator = LinearInterpolator()
        animator2.start()
    }


}