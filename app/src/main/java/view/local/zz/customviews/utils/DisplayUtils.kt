package DisplayUtils

import android.content.Context
import android.view.WindowManager

/**
 * 静态工具类
 */


fun dip2px(context: Context, dipValue: Float): Float {
    val scale = context.resources.displayMetrics.density
    return (dipValue * scale + 0.5f)
}


fun px2dip(context: Context, pxValue: Float): Float {
    val scale = context.resources.displayMetrics.density
    return (pxValue / scale + 0.5f)
}


/**
 * 将 px 转换为 sp， 保证尺寸大小不变
 *
 * @param context
 * @param pxValue
 * @return
 */
fun px2sp(context: Context, pxValue: Float): Int {

    /* scaledDensity，字体的比例因子，类似 density， 会根据用户偏好返回不同的值*/
    val fontScale = context.resources.displayMetrics.scaledDensity
    return (pxValue / fontScale + 0.5f).toInt()
}

/**
 * 将 sp 转换为 px， 保证尺寸大小不变
 *
 * @param context
 * @param pxValue
 * @return
 */
fun sp2px(context: Context, pxValue: Float): Int {
    val fontScale = context.resources.displayMetrics.scaledDensity
    return (pxValue * fontScale + 0.5f).toInt()
}



/**
 * 获取屏幕分辨率
 * @param context
 * @return
 */
fun getScreenDispaly(context: Context): IntArray {
    val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    val width = windowManager.defaultDisplay.width// 手机屏幕的宽度
    val height = windowManager.defaultDisplay.height// 手机屏幕的高度
    return intArrayOf(width, height)
}

/** 获取屏幕宽度  */
fun getDisplayWidth(context: Context?): Int {
    if (context != null) {
        val dm = context.resources.displayMetrics
// int h_screen = dm.heightPixels;
        return dm.widthPixels
    }
    return 720
}

/** 获取屏幕高度  */
fun getDisplayHight(context: Context?): Int {
    if (context != null) {
        val dm = context.resources.displayMetrics
        // int w_screen = dm.widthPixels;
        return dm.heightPixels
    }
    return 1280
}