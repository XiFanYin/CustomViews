package MyViews

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View

/**
 * Created by Administrator on 2018/7/6.
 */
class AnimationButton : View {
    //背景颜色
    val bg_color = "#88880000"
    //控件的默认宽
    val bg_width = DisplayUtils.px2dip(context, 320F)
    //控件的默认高
    val bg_height = DisplayUtils.px2dip(context, 120F)
    //背景默认角度
    var default_Round = 0.0F
    //文字画笔
    val text_Paint: Paint
    //文字默认的大小
    val text_size = DisplayUtils.px2sp(context, 48F)
    //默认文字内容
    val text_content = "确认完成"
    //画背景矩形的画笔
    val bg_Paint: Paint

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
        text_Paint.textSize = text_size.toFloat()
        //设置中心位置
        text_Paint.textAlign = Paint.Align.CENTER


        //创建画背景的画笔
        bg_Paint = Paint(Paint.ANTI_ALIAS_FLAG)
        //设置画笔颜色
        bg_Paint.color = Color.parseColor(bg_color)

        //设置点击事件

    }


    //告诉父布局，我需要的尺寸
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        setMeasuredDimension(resolveSize(bg_width.toInt(), widthMeasureSpec), resolveSize(bg_height.toInt(), heightMeasureSpec))
    }

    //开始绘制
    override fun onDraw(canvas: Canvas) {
        drawRectBg(canvas)
        drawText(canvas)

    }

    //绘制背景
    fun drawRectBg(canvas: Canvas) {
        canvas.drawRoundRect(RectF(0F, 0F, bg_width, bg_height), default_Round, default_Round, bg_Paint)
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

   //==============================以下是触摸之后的属性动画==========================







}