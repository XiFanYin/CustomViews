package MyViews

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.View
import android.view.animation.LinearInterpolator

/**
 * Created by Administrator on 2018/7/11.
 *贝塞尔曲线
 */
class BezierLine : View {
    //控件默认的宽 = 屏幕的宽度
    private var defult_Widht = DisplayUtils.getDisplayWidth(context).toFloat()
    //控件默认的高
    private var defult_Height = DisplayUtils.dip2px(context, 100F)

    private val gaodu = DisplayUtils.dip2px(context, 30F)


    //画线波浪的画笔
    val wave_Paint: Paint
    //绘制路径
    val mWavePath: Path

    //平移的距离
    var movelenght = 0F
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
        //画线波浪的画笔
        wave_Paint = Paint(Paint.ANTI_ALIAS_FLAG)
        //设置画线模式
        wave_Paint.style = Paint.Style.FILL
        //设置线的颜色
        wave_Paint.color = Color.parseColor("#FFAA00")
        //初始化绘制路径
        mWavePath = Path()
    }


    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        setMeasuredDimension(resolveSize(defult_Widht.toInt(), widthMeasureSpec), resolveSize(defult_Height.toInt(), heightMeasureSpec))
    }


    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        //去矫正宽度和高度
        defult_Widht = w.toFloat()
        defult_Height = h.toFloat()

    }

    override fun onDraw(canvas: Canvas) {
        drawBez(canvas)
    }

    private fun drawBez(canvas: Canvas) {
        mWavePath.reset()
        //起始点,这里是屏幕和屏幕的坐为坐标原点
        var startX1 = -defult_Widht + movelenght
        var startY1 = defult_Height / 2
        //控制点
        var controlX1 = -defult_Widht * 3 / 4 + movelenght
        var controlY1 = 0F + gaodu
        //结束点,也是第二个曲线的起点
        var endX1 = -defult_Widht / 2 + movelenght
        var endY1 = defult_Height / 2
        //控制点2
        var controlX2 = -defult_Widht / 4 + movelenght
        var controlY2 = defult_Height - gaodu
        //结束点2也是第三个曲线的起点
        var endX2 = 0F + movelenght
        var endY2 = defult_Height / 2

//        //控制点
        var controlX3 = defult_Widht / 4 + movelenght
        var controlY3 = 0F + gaodu
        //结束点,也是第二个曲线的起点
        var endX4 = defult_Widht / 2 + movelenght
        var endY4 = defult_Height / 2
        //控制点2
        var controlX5 = defult_Widht * 3 / 4 + movelenght
        var controlY5 = defult_Height - gaodu
        //结束点2
        var endX6 = defult_Widht + movelenght
        var endY6 = defult_Height / 2


        //位置全部算出来了，之后就是绘制
        mWavePath.moveTo(startX1, startY1)
        mWavePath.quadTo(controlX1, controlY1, endX1, endY1)
        mWavePath.quadTo(controlX2, controlY2, endX2, endY2)
        mWavePath.quadTo(controlX3, controlY3, endX4, endY4)
        mWavePath.quadTo(controlX5, controlY5, endX6, endY6)

        mWavePath.lineTo(width.toFloat(), height.toFloat())
        mWavePath.lineTo(-width.toFloat(), height.toFloat())
        canvas.drawPath(mWavePath, wave_Paint)
    }


    fun start() {
        //防止内存泄漏，记得停止动画，不是本文的重点，不再提供方法
        val animator = ObjectAnimator.ofFloat(this, "movelenght", 0F, defult_Widht)
        animator.duration = 2000
        animator.repeatCount = -1
        animator.interpolator = LinearInterpolator()
        animator.start()
    }

}