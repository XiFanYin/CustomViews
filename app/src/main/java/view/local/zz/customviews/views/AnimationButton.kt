package MyViews

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import android.view.animation.LinearInterpolator


/**
 * Created by Administrator on 2018/7/9.
 * 主要练习：
 * 1.绘制文字的开始位置计算
 * 2.绘制动画曲线的方法
 *
 */
class AnimationButton : View, View.OnClickListener {
    //动画集合
    val animatorset: AnimatorSet = AnimatorSet()
    //背景颜色
    val bg_color = "#88880000"
    //控件的默认宽
    var bg_width = DisplayUtils.dip2px(context, 140F)
    //控件的默认高
    var bg_height = DisplayUtils.dip2px(context, 50F)
    //文字画笔
    val text_Paint: Paint
    //文字默认的大小
    var text_size = DisplayUtils.dip2px(context, 20F)
    //默认文字内容
    val text_content = "确认完成"
    //画背景矩形的画笔
    val bg_Paint: Paint
    //矩形长度默认变化
    var change: Float = 0F
        set(value) {
            field = value
            invalidate()
        }
    //背景默认角度,重写set方法，去自定义属性动画
    var default_Round = 0.0F
        set(value) {
            field = value
            invalidate()
        }
    //是否画上对钩
    var check: Boolean = false
    //对号画笔
    val checkPaint: Paint
    //对号的路径
    lateinit var checkPath: Path
    //对号的虚线绘制数组
    lateinit var intervals: FloatArray
    //路径变化的百分比
    var percent = 0F
        set(value) {
            //打开绘制的开关
            check = true
            field = value
            checkPaint.setPathEffect(getDashPathEffect())//设置画笔的偏移量
            //重新绘制
            invalidate()
        }

    //绘制线的变换
    fun getDashPathEffect(): PathEffect = DashPathEffect(intervals, intervals.get(0) - percent * intervals.get(0))

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
        checkPaint.setStyle(Paint.Style.STROKE)//设checkPath置画线模式
        checkPaint.setStrokeWidth(8F) // 线条宽度为 20 像素

        //设置点击事件
        setOnClickListener(this)
        //设置动画完成监听,
        animatorset.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                // 调用完这个之后最后会再去调用一次绘制方法，我们可以在这里边去恢复之前的动画
                postDelayed({
                    reset()
                }, 500)

            }
        })

    }

    //告诉父布局，我需要的尺寸
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        setMeasuredDimension(resolveSize(bg_width.toInt(), widthMeasureSpec), resolveSize(bg_height.toInt(), heightMeasureSpec))
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        //改变文字画笔的大小
        text_size = text_size * w / bg_width
        text_Paint.textSize = text_size
        //改变控件的默认宽高，然后基于改变后的宽高去计算所有的坐标，然后去绘制
        bg_width = w.toFloat()
        bg_height = h.toFloat()
        //绘制对号的路径
        checkPath = Path()
        //计算对号关键点绝对坐标
        val startX = (bg_width - bg_height) / 2 + bg_height / 4
        val startY = bg_height / 3
        val guaiX = bg_width / 2
        val guaiY = bg_height * 2 / 3
        val endX = (bg_width - bg_height) / 2 + 5 * bg_height / 6
        val endY = bg_height / 4
        checkPath.moveTo(startX, startY)
        checkPath.lineTo(guaiX, guaiY)
        checkPath.lineTo(endX, endY)
        //计算出路径的长度
        val measure = PathMeasure(checkPath, false)
        intervals = floatArrayOf(measure.length, measure.length)

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
        canvas.drawPath(checkPath, checkPaint)
    }

    //触摸之后的属性动画
    override fun onClick(v: View) {
        //直角矩形变成圆角巨型的动画
        val Round = ObjectAnimator.ofFloat(this, "default_Round", 0F, 90F)
        //文字的透明度的变化动画
        val text_Alpha = ObjectAnimator.ofInt(text_Paint, "alpha", 255, 0)
        //距型变成圆动画
        val kuan = ObjectAnimator.ofFloat(this, "change", 0F, (bg_width - bg_height) / 2)
        //偏移量控制线的绘制过程
        val duihao = ObjectAnimator.ofFloat(this, "percent", 0.0F, 1.0F)
        //添加到集合，然后开始动画
        animatorset.play(Round).with(text_Alpha).with(kuan).before(duihao)
        animatorset.duration = 800
        animatorset.interpolator = LinearInterpolator()
        animatorset.start()
    }

    /**
     * 动画还原
     */
    fun reset() {
        check = false
        default_Round = 0F
        percent = 0F
        change = 0F
        text_Paint.alpha = 255
        invalidate()
    }

}