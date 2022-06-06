package view.local.zz.customviews.utils

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint


fun addMark(bitmap: Bitmap): Bitmap {
    val ret = bitmap.copy(bitmap.getConfig(), true)
    val canvas = Canvas(ret)
    val temp = "我"
    val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    paint.textSize = 100f
    paint.textAlign = Paint.Align.CENTER
    val x = ret.width/2.0f
    val y = ret.height/2+ ( paint.fontMetrics.bottom- paint.fontMetrics.top)/2-paint.fontMetrics.bottom
    canvas.drawText(temp, x, y, paint)
    return ret
}