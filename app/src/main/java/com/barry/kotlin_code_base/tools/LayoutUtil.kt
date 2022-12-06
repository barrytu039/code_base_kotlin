package com.barry.kotlin_code_base.tools

import android.app.Activity
import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Build
import android.util.DisplayMetrics
import android.util.TypedValue
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import com.barry.kotlin_code_base.R
import java.lang.NullPointerException

object LayoutUtil {

    val WIDTH = 0
    val HEIGHT = 1

//    fun isTablet(context: Context): Boolean {
//        return "true" == context.getString(R.string.is_tablet)
//    }

    fun dpToPx(context: Context, dp: Float): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp,
            context.resources.displayMetrics
        )
            .toInt()
    }

    fun pixelsByPercentage(activity: Activity, percentage: Double, base: Int): Int {
        val metrics = DisplayMetrics()
        return try {
            // window manager might be null
            activity.windowManager.defaultDisplay.getMetrics(metrics)
            when (base) {
                WIDTH -> (metrics.widthPixels * percentage).toInt()
                HEIGHT -> (metrics.heightPixels * percentage).toInt()
                else -> 0
            }
        } catch (e: NullPointerException) {
            // catch window manager null exception
            0
        }
    }

    fun getWidth(activity: Activity, span: Int, margin: Int): Int {
        val totalMargin = dpToPx(activity, (margin * (span + 1)).toFloat())
        return (pixelsByPercentage(activity, 1.0, WIDTH) - totalMargin) / span
    }

    fun getWidth(activity: Activity, contentWidth: Int, span: Int, margin: Int): Int {
        val totalMargin = dpToPx(activity, (margin * (span + 1)).toFloat())
        return (contentWidth - totalMargin) / span
    }

    fun findPoint(activity: Activity, v: View): IntArray {
        val point = IntArray(2)
        v.getLocationInWindow(point)
        if (hasCutout(activity) || Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            point[1] -= getStatusBarHeight(activity)
        }
        return point
    }

    fun findCenterPoint(activity: Activity, v: View): IntArray? {
        val point = findPoint(activity, v)
        point[0] += v.width / 2
        point[1] += v.height / 2
        return point
    }

    fun getTintDrawable(context: Context, resId: Int, colorId: Int): Drawable? {
        val drawable = ContextCompat.getDrawable(context, resId)
        if (drawable != null) {
            val wrap = DrawableCompat.wrap(drawable)
            DrawableCompat.setTint(wrap.mutate(), ContextCompat.getColor(context, colorId))
            return drawable
        }
        return null
    }

    fun getStatusBarHeight(context: Context): Int {
        val resourceId = context.resources.getIdentifier("status_bar_height", "dimen", "android")
        return if (resourceId > 0) context.resources.getDimensionPixelSize(resourceId) else 0
    }

    fun getActionbarHeight(context: Context): Int {
        val tv = TypedValue()
        return if (context.theme.resolveAttribute(R.attr.actionBarSize, tv, true)) {
            TypedValue.complexToDimensionPixelSize(tv.data, context.resources.displayMetrics)
        } else 0
    }

    private fun hasCutout(activity: Activity): Boolean {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.P && activity.window.decorView.rootWindowInsets.displayCutout != null
    }
}