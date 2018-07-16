package MyViews

import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import view.local.zz.customviews.R
import android.graphics.LightingColorFilter
import android.view.animation.LinearInterpolator


/**
 * Created by Administrator on 2018/7/16.
 * 绘制任意图片背景的波浪进度条
 *
 */
class BitmapView : View {
    //控件默认的宽
    var defult_width: Int
    //控件默认的高
    var defult_height: Int
    //默认Bitmap对象
    var mBitmap: Bitmap
    //向 bitmapView中画mBitmap的画笔
    var bg_Paint: Paint
    //绘制波浪的路径
    private val mWavePath: Path
    //波形的一个周期长度
    private val cycle = DisplayUtils.dip2px(context, 90F)
    //波振幅高度
    private val waveHeight = DisplayUtils.dip2px(context, 20F)
    //屏幕外波形绘制几个周期
    private val cycle_Count = 4
    //画线波浪的画笔
    private  var wave_Paint: Paint
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
    //构造方法
    constructor(context: Context?) : this(context, null)

    //构造方法
    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs, 0)

    //构造方法
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : this(context, attrs, defStyleAttr, 0)

    //构造方法
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes) {
        //获取原始图片的bitmap对象
        mBitmap = BitmapFactory.decodeResource(resources, R.drawable.kkkl)
        //初始化控件的宽度和高度
        defult_width = mBitmap.width
        defult_height = mBitmap.height

        //创建绘制阴影的画笔
        bg_Paint = Paint(Paint.ANTI_ALIAS_FLAG)
        //设置颜色过滤
        bg_Paint.colorFilter = LightingColorFilter(0x000000, 0xeeeeee)

        //向创建图片中画线的画笔
        wave_Paint = Paint(Paint.ANTI_ALIAS_FLAG)
        //把图片设置成画笔颜色
        wave_Paint.shader = BitmapShader(mBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
        //绘制曲线的路径
        mWavePath = Path()
    }


    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        setMeasuredDimension(resolveSize(defult_width, widthMeasureSpec), resolveSize(defult_height, heightMeasureSpec))
    }


    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        defult_width = w
        defult_height = h
        //开始波浪移动动画
        val animator = ObjectAnimator.ofFloat(this, "movelenght", 0F, cycle * cycle_Count)
        animator.duration = 4000
        animator.repeatCount = -1
        animator.interpolator = LinearInterpolator()
        animator.start()
    }


    override fun onDraw(canvas: Canvas) {
        //绘制图片背景
        canvas.drawBitmap(mBitmap, 0F, 0F, bg_Paint)
        //绘制波浪图
        drawBez(canvas)

    }



    //绘制波形图，用贝塞尔曲线,
    private fun drawBez(canvas: Canvas) {
        mWavePath.reset()
        //开始点
        var startX = -cycle * cycle_Count + movelenght
        var startY = defult_height + waveHeight / 2 - progress
        mWavePath.moveTo(startX, startY)
        //计算屏幕外左边所有控制点
        for (i in cycle_Count downTo 1) {
            //波峰
            var fengX = -cycle * (i) + cycle / 4 + movelenght
            var fengY = defult_height - progress
            //中间点1
            var endX = -cycle * (i) + cycle / 2 + movelenght
            var endY = defult_height + waveHeight / 2 - progress
            mWavePath.quadTo(fengX, fengY, endX, endY)

            //波谷的点
            var guX = -cycle * (i) + cycle * 3 / 4 + movelenght
            var guY = defult_height + waveHeight - progress

            //中间点2
            var endX2 = -cycle * (i - 1) + movelenght
            var endY2 = defult_height + waveHeight / 2 - progress

            mWavePath.quadTo(fengX, fengY, endX, endY)
            mWavePath.quadTo(guX, guY, endX2, endY2)
        }


        //计算屏幕右边所有控制点
        for (i in 1..cycle_Count) {
            //波峰
            var fengX = cycle * (i) - cycle * 3 / 4 + movelenght
            var fengY = defult_height - progress
            //中间点1
            var endX = cycle * (i) - cycle / 2 + movelenght
            var endY = defult_height + waveHeight / 2 - progress
            //波谷的点
            var guX = cycle * (i) - cycle * 1 / 4 + movelenght
            var guY = defult_height + waveHeight - progress

            //中间点2
            var endX2 = cycle * (i) + movelenght
            var endY2 = defult_height + waveHeight / 2 - progress

            mWavePath.quadTo(fengX, fengY, endX, endY)
            mWavePath.quadTo(guX, guY, endX2, endY2)
        }

        mWavePath.lineTo(cycle_Count * cycle, defult_height + waveHeight)
        mWavePath.lineTo(-cycle_Count * cycle, defult_height + waveHeight)

        //以上吧点添加完毕，然后就是上移一下看看效果
        canvas.drawPath(mWavePath, wave_Paint)

    }

    /**
     * 开启动画
     */
    fun start( duration: Long = 5000) {
        val animator2 = ObjectAnimator.ofFloat(this, "progress", 0F, (defult_height + waveHeight) * 100 / 100)
        animator2.duration = duration
        animator2.interpolator = LinearInterpolator()
        animator2.start()
        animator2
    }

}