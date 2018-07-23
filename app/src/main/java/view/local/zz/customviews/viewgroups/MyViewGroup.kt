package MyViewGroups

import android.content.Context
import android.util.AttributeSet
import android.view.ViewGroup

/**
 * Created by Administrator on 2018/7/23.
 */
class MyViewGroup : ViewGroup {


    constructor(context: Context?) : this(context, null)
    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : this(context, attrs, defStyleAttr, 0)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes) {


    }


    override fun generateLayoutParams(attrs: AttributeSet?): LayoutParams {
        return MarginLayoutParams(getContext(), attrs)
    }


    //这个方法需要干两件事，1测量子View的大小 2.设置自己大小
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {

        /**
         * 获得此ViewGroup上级容器为其推荐的宽和高，以及计算模式
         */
        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        val sizeWidth = MeasureSpec.getSize(widthMeasureSpec)
        val sizeHeight = MeasureSpec.getSize(heightMeasureSpec)


        //1测量子View的大小，测量结果保存在子View中
        measureChildren(widthMeasureSpec, heightMeasureSpec)

        //2.设置自己的大小，因为有可能是warp_Contnet模式，所以这里就需要先计算，然后再去判断设置自己的大小
        //包裹内容计算出来的宽度
        var width = 0
        //包裹内容计算出来的高度
        var height = 0

        // 用于计算左边两个childView的高度
        var lHeight = 0
        // 用于计算右边两个childView的高度，最终高度取二者之间大值
        var rHeight = 0
        // 用于计算上边两个childView的宽度
        var tWidth = 0
        // 用于计算下面两个childiew的宽度，最终宽度取二者之间大值
        var bWidth = 0

        for (a in 0 until childCount) {
            //获取控件中的子View
            val childView = getChildAt(a)
            //获取子View测量的宽度和高度
            val cWidth = childView.measuredWidth
            val cHeight = childView.measuredHeight
            //获取子View测布局参数
            val cParams = childView.layoutParams as ViewGroup.MarginLayoutParams

            if (a == 0 || a == 1) {
                tWidth += cWidth + cParams.leftMargin + cParams.rightMargin
            }

            if (a == 2 || a == 3) {
                bWidth += cWidth + cParams.leftMargin + cParams.rightMargin
            }

            if (a == 0 || a == 2) {
                lHeight += cHeight + cParams.topMargin + cParams.bottomMargin
            }

            if (a == 1 || a == 3) {
                rHeight += cHeight + cParams.topMargin + cParams.bottomMargin
            }
        }
        //计算出来宽度和高度的最大值
        width = Math.max(tWidth, bWidth)
        height = Math.max(lHeight, rHeight)


        //设置自己的大小
        setMeasuredDimension(if (widthMode == MeasureSpec.EXACTLY) sizeWidth else width, if (heightMode == MeasureSpec.EXACTLY) sizeHeight else height)


    }


    //需要干一件事：设置子View的位置，这里参数是父View的位置
    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {

        for (a in 0 until childCount) {
            //获取所有的子View
            val childView = getChildAt(a)
            //计算每个View 的位置
            var cWidth = childView.measuredWidth
            var cHeight = childView.measuredHeight
            var cParams = childView.layoutParams as ViewGroup.MarginLayoutParams
            //每个位置坐标的值，这里的坐标是相对于父View的
            var cl = 0
            var ct = 0
            var cr = 0
            var cb = 0
            when (a) {
                0 -> {
                    cl = cParams.leftMargin
                    ct = cParams.topMargin
                }
                1 -> {
                    cl = getWidth() - cWidth - cParams.leftMargin - cParams.rightMargin
                    ct = cParams.topMargin
                }
                2 -> {
                    cl = cParams.leftMargin;
                    ct = getHeight() - cHeight - cParams.bottomMargin;
                }

                3 -> {
                    cl = getWidth() - cWidth - cParams.leftMargin - cParams.rightMargin;
                    ct = getHeight() - cHeight - cParams.bottomMargin;
                }

            }
            //有了左边和上边的坐标，根据测量的大小，就可以计算出右边和下边的坐标
            cr = cl + cWidth
            cb = ct + cHeight
            //设置位置
            childView.layout(cl, ct, cr, cb)
        }

    }


}