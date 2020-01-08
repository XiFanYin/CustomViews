package MyViews

import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.os.Build

import android.util.AttributeSet
import android.view.View
import androidx.annotation.RequiresApi

/**
 * Created by Administrator on 2018/7/10.
 * 自定义圆形进度条
 */
class CircleProgress : View {
    //控件默认的宽
    private var defult_Widht = DisplayUtils.dip2px(context, 150F)
    //控件默认的高
    private var defult_Height = DisplayUtils.dip2px(context, 180F)
    //默认圆绘制线宽度
    private var defult_bg_StrokeWidth = DisplayUtils.dip2px(context, 10F)
    //画背景圆的画笔
    private lateinit var bg_Paint: Paint
    //绘制中心文字的画笔
    private lateinit var centerText_Paint: Paint
    //默认文字的大小
    private val textSize = DisplayUtils.sp2px(context, 38F)
    //进度值
    private var progress = 0
        set(value) {
            field = value
            invalidate()
        }
    //绘制底部文字的画笔
    private lateinit var bottomText_Paint: Paint
    //绘制弧线的画笔
    private lateinit var arc_Paint: Paint
    //最后结束的值
    var endProgress = 0


    //构造方法
    constructor(context: Context?) : this(context, null)

    //构造方法
    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs, 0)

    //构造方法
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : this(context, attrs, defStyleAttr, 0)

    //构造方法
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes) {
        initPaint()
    }

    //初始化画笔
    fun initPaint() {
        //创建画背景圆的画笔
        bg_Paint = Paint(Paint.ANTI_ALIAS_FLAG)
        //设置画线模式
        bg_Paint.style = Paint.Style.STROKE
        //设置画线的宽度
        bg_Paint.strokeWidth = defult_bg_StrokeWidth
        //设置线的颜色
        bg_Paint.color = Color.parseColor("#cccccc")

        //创建绘制中心文字的画笔
        centerText_Paint = Paint(Paint.ANTI_ALIAS_FLAG)
        //设置绘制文字画笔颜色
        centerText_Paint.color = Color.parseColor("#ff9de2")
        //计算文字开始大小
        centerText_Paint.textSize = textSize.toFloat()
        //设置中心位置
        centerText_Paint.textAlign = Paint.Align.CENTER
        //设置文字加粗
        centerText_Paint.isFakeBoldText = true

        //创建绘制底部文字的画笔
        bottomText_Paint = Paint(Paint.ANTI_ALIAS_FLAG)
        //设置绘制文字画笔颜色
        bottomText_Paint.color = Color.parseColor("#999999")
        //计算文字开始大小
        bottomText_Paint.textSize = textSize.toFloat() / 2
        //设置中心位置
        bottomText_Paint.textAlign = Paint.Align.CENTER
        //设置文字加粗
        bottomText_Paint.isFakeBoldText = true

        //创建绘制弧形的画笔
        arc_Paint = Paint(Paint.ANTI_ALIAS_FLAG)
        //画笔的颜色
        arc_Paint.color = Color.parseColor("#FFAA00")
        //设置画线模式
        arc_Paint.style = Paint.Style.STROKE
        //设置画线的宽度
        arc_Paint.strokeWidth = defult_bg_StrokeWidth
        //设置绘制源头
        arc_Paint.strokeCap = Paint.Cap.ROUND
    }

    //测量控件的大小
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        //获取父布局的宽高和模式要求
        val widthMode = View.MeasureSpec.getMode(widthMeasureSpec)
        val widthSize = View.MeasureSpec.getSize(widthMeasureSpec)
        val heightMode = View.MeasureSpec.getMode(heightMeasureSpec)
        val heightSize = View.MeasureSpec.getSize(heightMeasureSpec)
        //宽默认缩放系数，高默认缩放系数
        var hScale = 1.0f
        var vScale = 1.0f
        //计算两个缩放比例
        //父布局不能滚动，并且给的值不能满足绘制完整，就需要缩放
        if (widthMode != View.MeasureSpec.UNSPECIFIED && widthSize < defult_Widht) {
            hScale = widthSize.toFloat() / defult_Widht
        }

        if (heightMode != View.MeasureSpec.UNSPECIFIED && heightSize < defult_Height) {
            vScale = heightSize.toFloat() / defult_Height
        }
        //取较小的缩放比例
        var scale = Math.min(hScale, vScale)
        //设置进去正确的数字
        setMeasuredDimension(resolveSize((defult_Widht * scale).toInt(), widthMeasureSpec), resolveSize((defult_Height * scale).toInt(), heightMeasureSpec))
    }


    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
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
        //绘制背景圆形
        drawCircleBG(canvas)
        //绘制中心文字
        drawCenterText(canvas)
        //绘制底部文字
        drawBottomText(canvas)
        //绘制进度
        drawProgressCircle(canvas)

    }


    //画背景圆
    private fun drawCircleBG(canvas: Canvas) {
        //计算圆心和半径
        val centerX = defult_Widht / 2
        val centerY = defult_Widht / 2
        //这里的半径指定的是线宽度的中心位置
        val radius = (defult_Widht - defult_bg_StrokeWidth) / 2
        canvas.drawCircle(centerX, centerY, radius, bg_Paint)
    }

    //绘制中心文字
    private fun drawCenterText(canvas: Canvas) {
        //计算绘制文字开始xy坐标,这里计算基线是把圆形看成一个矩形去计算
        val x = defult_Widht / 2
        val fontMetrics = centerText_Paint.getFontMetrics()
        val baseLine = defult_Widht / 2 + (fontMetrics.bottom - fontMetrics.top) / 2 - fontMetrics.bottom
        canvas.drawText("$progress%", x, baseLine, centerText_Paint)
    }

    //绘制底部文字
    private fun drawBottomText(canvas: Canvas) {
        //计算底部文字的开始绘制坐标，这里是以底部巨型做为文字的起始点去计算
        val x = defult_Widht / 2
        val fontMetrics = bottomText_Paint.getFontMetrics()
        val baseLine = defult_Widht + (defult_Height - defult_Widht) / 2 + (fontMetrics.bottom - fontMetrics.top) / 2 - fontMetrics.bottom
        canvas.drawText("当前进度$progress%", x, baseLine, bottomText_Paint)
    }

    //绘制进度
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun drawProgressCircle(canvas: Canvas) {
        val d = progress * 360 / 100
        //边界要考虑线的宽度的一半计算进去
        canvas.drawArc(0F + defult_bg_StrokeWidth / 2,
                0F + defult_bg_StrokeWidth / 2,
                defult_Widht - defult_bg_StrokeWidth / 2,
                defult_Widht - defult_bg_StrokeWidth / 2,
                90F, d.toFloat(), false, arc_Paint)

    }


    //===================================以下是设置属性的方法================================
    //开启动画
    fun start() {
        //创建动画
        val animator = ObjectAnimator.ofInt(this, "progress", 0, endProgress)
        animator.duration = 1000
        animator.start()
    }


}