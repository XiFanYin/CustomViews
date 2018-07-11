package MyViews

import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.os.Build
import android.support.annotation.RequiresApi
import android.util.AttributeSet
import android.util.Log
import android.view.View

/**
 * Created by Administrator on 2018/7/10.
 * 自定义水平进度条，主要学习padding设置的用法
 */
class HorizontalProgress : View {

    //控件默认的宽
    val defult_Widht: Float
    //控件默认高度
    val defult_Hdight: Float
    //topRect高度
    val topRectHeight = DisplayUtils.dip2px(context, 14F)
    //topRect宽度
    val topRectWidth = DisplayUtils.dip2px(context, 24F)
    //三角形的宽度高度
    val triangle_Height_width = DisplayUtils.dip2px(context, 6F)
    //进度条和三角形的间隔
    val progressToTeiangle = DisplayUtils.dip2px(context, 6F)
    //进度条线线的宽度
    val progressLineWidth = DisplayUtils.dip2px(context, 4F)
    //进度条和控件底部间隔
    val progressToBottom = DisplayUtils.dip2px(context, 6F)
    //画背景的画笔
    private lateinit var bg_Paint: Paint
    //画头部矩形的画笔
    private lateinit var top_Paint: Paint
    //顶部文字的画笔
    private lateinit var text_Paint: Paint
    //画进度条的画笔
    private lateinit var progress_Paint: Paint
    //当前进度
    private var progress = 0
        set(value) {
            field = value
            invalidate()
        }
    //结束进度，
    var endProgress = 0


    //构造方法
    constructor(context: Context?) : this(context, null)

    //构造方法
    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs, 0)

    //构造方法
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : this(context, attrs, defStyleAttr, 0)

    //构造方法
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes) {
        //控件的高度 = 上边巨型的高度+三角形的高度+三角形和进度条的间隔+进度条的高度+进度条到底部的高度
        defult_Hdight = topRectHeight + triangle_Height_width + progressToTeiangle + progressLineWidth + progressToBottom
        defult_Widht = DisplayUtils.getDisplayWidth(context) - topRectWidth
        initPaint()
    }

    private fun initPaint() {
        //创建画背景圆的画笔
        bg_Paint = Paint(Paint.ANTI_ALIAS_FLAG)
        //设置画线模式
        bg_Paint.style = Paint.Style.STROKE
        //设置画线的宽度
        bg_Paint.strokeWidth = progressLineWidth
        //设置线的颜色
        bg_Paint.color = Color.parseColor("#cccccc")
        //创建画背景的画笔
        top_Paint = Paint(Paint.ANTI_ALIAS_FLAG)
        //设置画笔颜色
        top_Paint.color = Color.parseColor("#FFAA00")
        //创建文字画笔
        text_Paint = Paint(Paint.ANTI_ALIAS_FLAG)
        //设置画笔颜色
        text_Paint.color = Color.WHITE
        //计算文字开始大小
        text_Paint.textSize = DisplayUtils.sp2px(context, 8F).toFloat()
        //设置中心位置
        text_Paint.textAlign = Paint.Align.CENTER

        progress_Paint = Paint(Paint.ANTI_ALIAS_FLAG)
        //设置画线模式
        progress_Paint.style = Paint.Style.STROKE
        //设置画线的宽度
        progress_Paint.strokeWidth = progressLineWidth
        //设置线的颜色
        progress_Paint.color = Color.parseColor("#FFAA00")
    }


    //测量
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {

        setMeasuredDimension(resolveSize(defult_Widht.toInt(), widthMeasureSpec), resolveSize(defult_Hdight.toInt() + paddingBottom + paddingTop, heightMeasureSpec))

    }

    //绘制
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onDraw(canvas: Canvas) {

        drawBgLine(canvas)

        drawTopRect(canvas)

        drawTriangle(canvas)

        drawTopText(canvas)

        drawProgressLine(canvas)


    }


    //画背景线,这里考虑了Padding值
    private fun drawBgLine(canvas: Canvas) {
        //画线需要算出起始点和终止点
        canvas.drawLine(0F + paddingLeft+topRectWidth/2, topRectHeight + triangle_Height_width + progressToTeiangle + paddingTop,
                defult_Widht - paddingRight+topRectWidth/2, topRectHeight + triangle_Height_width + progressToTeiangle + paddingTop,
                bg_Paint)
    }

    //画顶部矩形
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun drawTopRect(canvas: Canvas) {
        var newLine = (defult_Widht - paddingRight - paddingLeft) * progress / 100 - topRectWidth / 2
        canvas.drawRoundRect(0F + paddingLeft + newLine+topRectWidth/2, 0F + paddingTop, topRectWidth + paddingLeft + newLine+topRectWidth/2, topRectHeight + paddingTop, 2F, 2F, top_Paint)
    }

    //绘制三角形
    fun drawTriangle(canvas: Canvas) {
        var newLine = (defult_Widht.toFloat() - paddingRight - paddingLeft) * progress / 100 - topRectWidth / 2
        var path = Path()
        //需要计算三角形的三个点的坐标
        var startX = (topRectWidth - triangle_Height_width) / 2 + paddingLeft + newLine+topRectWidth/2
        var startY = topRectHeight - 2 + paddingTop
        var twoX = (topRectWidth + triangle_Height_width) / 2 + paddingLeft + newLine+topRectWidth/2
        var twoY = topRectHeight - 2 + paddingTop
        var endX = topRectWidth / 2 + paddingLeft + newLine+topRectWidth/2
        var endY = topRectHeight + triangle_Height_width + paddingTop
        path.moveTo(startX, startY)
        path.lineTo(twoX, twoY)
        path.lineTo(endX, endY)
        //封闭缺口
        path.close()
        canvas.drawPath(path, top_Paint)

    }


    //绘制顶部文字
    private fun drawTopText(canvas: Canvas) {
        var newLine = (defult_Widht.toFloat() - paddingRight - paddingLeft) * progress / 100 - topRectWidth / 2
        //需要算出来文字的x以及baseLine
        var x = topRectWidth / 2 + paddingLeft + newLine+topRectWidth/2
        val fontMetrics = text_Paint.getFontMetrics()
        var baseline = topRectHeight / 2 + (fontMetrics.bottom - fontMetrics.top) / 2 - fontMetrics.bottom + paddingTop
        canvas.drawText("$progress%", x, baseline, text_Paint)
    }

    //绘制进度走的值
    fun drawProgressLine(canvas: Canvas) {

        //需要动态去设置这个终点的值，所以需要计算
        //因为这里是直线，所以除了用DashPathEffect之外，还可以通过改变终点的坐标去执行动画，但是如果是曲线，就只有用DashPathEffect去绘制动画
        var newLine = (defult_Widht - paddingRight - paddingLeft) * progress / 100
        Log.e("rrrrrrrr", newLine.toString())

        //画线需要算出起始点和终止点
        canvas.drawLine(0F + paddingLeft+topRectWidth/2, topRectHeight + triangle_Height_width + progressToTeiangle + paddingTop,
                newLine + paddingLeft+topRectWidth/2, topRectHeight + triangle_Height_width + progressToTeiangle + paddingTop,
                progress_Paint)

    }

    //开启动画
    fun start() {
        val ofInt = ObjectAnimator.ofInt(this, "progress", 0, endProgress)
        ofInt.duration = 1000
        ofInt.start()
    }

}