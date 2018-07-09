package MyViews

import android.animation.*
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.View

/**
 * Created by Administrator on 2018/7/6.
 */
class AnimationButton : View, View.OnClickListener {

    //背景颜色
    val bg_color = "#88880000"
    //控件的默认宽
    val bg_width = DisplayUtils.dip2px(context, 140F)
    //控件的默认高
    val bg_height = DisplayUtils.dip2px(context, 50F)
    //文字画笔
    val text_Paint: Paint
    //文字默认的大小
    val text_size = DisplayUtils.dip2px(context, 20F)
    //默认文字内容
    val text_content = "确认完成"
    //画背景矩形的画笔
    val bg_Paint: Paint
    //矩形长度默认变化
    var change: Float = 0F
    //背景默认角度
    var default_Round = 0.0F
    //是否画上对钩
    var check: Boolean = false
    //对号画笔
    val checkPaint: Paint


    //构造方法
    constructor(context: Context?) : this(context, null)

    //构造方法
    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs, 0)

    //构造方法
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : this(context, attrs, defStyleAttr, 0)

    //构造方法
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes) {
        //创建文字画笔
        text_Paint = Paint(Paint.ANTI_ALIAS_FLAG)
        //设置画笔颜色
        text_Paint.color = Color.WHITE
        //计算文字开始大小
        text_Paint.textSize = text_size
        //设置中心位置
        text_Paint.textAlign = Paint.Align.CENTER

        //创建画背景的画笔
        bg_Paint = Paint(Paint.ANTI_ALIAS_FLAG)
        //设置画笔颜色
        bg_Paint.color = Color.parseColor(bg_color)

        //创建对号画笔
        checkPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        checkPaint.color = Color.WHITE
        checkPaint.setStyle(Paint.Style.STROKE)//设置画线模式
        checkPaint.setStrokeWidth(8F) // 线条宽度为 20 像素

        //设置点击事件
        setOnClickListener(this)
    }


    //告诉父布局，我需要的尺寸
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        setMeasuredDimension(resolveSize(bg_width.toInt(), widthMeasureSpec), resolveSize(bg_height.toInt(), heightMeasureSpec))
    }

    //开始绘制
    override fun onDraw(canvas: Canvas) {
        drawRectBg(canvas)
        drawText(canvas)
        if (check) {
            drawCheck(canvas)
        }

    }


    //绘制背景
    fun drawRectBg(canvas: Canvas) {
        canvas.drawRoundRect(RectF(0F + change, 0F, bg_width - change, bg_height), default_Round, default_Round, bg_Paint)
    }

    //绘制文字
    fun drawText(canvas: Canvas) {
        //因为上边设置了x是中心点坐标，所以直接指定控件的一半就是x的中心位置
        var x = (bg_width / 2)
        val fontMetrics = text_Paint.getFontMetrics()
        //计算公式:baseline=getHeight()/2+(fontMetrics.bottom-fontMetrics.top)/2-fontMetrics.bottom
        var baseline = bg_height / 2 + (fontMetrics.bottom - fontMetrics.top) / 2 - fontMetrics.bottom
        canvas.drawText(text_content, x, baseline, text_Paint)
    }

    //绘制对号
    fun drawCheck(canvas: Canvas) {

    }

    //==============================以下是触摸之后的属性动画==========================

    override fun onClick(v: View) {
        //动画集合
        val animatorset = AnimatorSet()
        //直角矩形变成圆角巨型的动画
        val Round = ValueAnimator.ofFloat(90F)
        Round.addUpdateListener {
            default_Round = it.getAnimatedValue() as Float
            invalidate()
        }
        //文字的透明度的变化动画
        val text_Alpha = ObjectAnimator.ofInt(text_Paint, "alpha", 255, 0)
        //巨型变成圆动画
        val kuan = ValueAnimator.ofFloat(0F, (bg_width - bg_height) / 2)
        kuan.addUpdateListener {
            change = it.getAnimatedValue() as Float
            invalidate()
        }
        //添加到集合，然后开始动画
        animatorset.play(Round).with(text_Alpha).with(kuan)
        animatorset.duration = 1200
        animatorset.start()

    }


}