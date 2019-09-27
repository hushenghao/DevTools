package com.dede.devtools.main

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.graphics.Point
import android.os.Build
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import android.view.animation.OvershootInterpolator
import android.widget.ImageView
import android.widget.PopupWindow
import android.widget.Toast
import androidx.core.view.setPadding
import androidx.fragment.app.FragmentActivity
import com.dede.devtools.R
import com.dede.devtools.ext.dip
import com.dede.devtools.ext.screenHeight
import com.dede.devtools.ext.screenWidth
import kotlin.math.abs

/**
 * Created by hsh on 2019-09-26 20:04
 */
internal class MainPop(activity: Activity) : Runnable {

    private val parent: View = activity.findViewById(android.R.id.content)
    private val context: Context = activity
    private var popupWindow: PopupWindow? = null

    private val defaultMarginX = 15f

    companion object {
        private val point = Point()// 多个pop共享
        private val popMap = HashMap<Activity, MainPop>()

        fun show(activity: Activity) {
            var mainPop = popMap[activity]
            if (mainPop == null) {
                mainPop = MainPop(activity)
                popMap[activity] = mainPop
            }
            mainPop.show()
        }

        fun hide(activity: Activity) {
            popMap.remove(activity)?.hide()
        }
    }

    override fun run() {
        popupWindow!!.showAtLocation(parent, Gravity.NO_GRAVITY, point.x, point.y)
    }

    fun show() {
        if (popupWindow == null) {
            initPop()
        }
        if (popupWindow == null) return

        parent.post(this)
    }

    fun hide() {
        parent.removeCallbacks(this)
        popupWindow?.dismiss()
    }

    private fun initPop() {
        val view = ImageView(context).apply {
            setImageResource(R.drawable.ic_dev_tools)
            setPadding(context.dip(6))
            setBackgroundResource(R.drawable.bg_dev_pop)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                translationZ = 10f
            }
            setOnClickListener(ClickListener())
            setOnTouchListener(ClickMoveFilter())
        }
        popupWindow = PopupWindow(view, -2, -2, false)
        if (point.equals(0, 0)) {
            val y = (context.screenHeight() * 2 / 3f).toInt()
            val x = context.screenWidth() -
                    context.resources.getDimensionPixelSize(R.dimen.dimen_dev_pop_size) -
                    context.dip(defaultMarginX)
            point.set(x, y)
        }
    }

    private inner class ClickListener : View.OnClickListener {
        override fun onClick(v: View?) {
            if (context !is FragmentActivity) {
                Toast.makeText(context, "Activity !instanceOf FragmentActivity", Toast.LENGTH_SHORT)
                    .show()
                return
            }

            val dialogFragment = MainDialogFragment()
            dialogFragment.show(context.supportFragmentManager, "DEV_TOOLS_TAG")
        }
    }

    /**
     * 处理点击事件和移动事件冲突
     */
    private inner class ClickMoveFilter : View.OnTouchListener {
        var p = Point()
        var rp = Point()
        var moved = false
        val slop = ViewConfiguration.get(context).scaledTouchSlop

        @SuppressLint("ClickableViewAccessibility")
        override fun onTouch(v: View?, event: MotionEvent?): Boolean {
            when (event?.action) {
                MotionEvent.ACTION_DOWN -> {
                    p.x = event.x.toInt()
                    p.y = event.y.toInt()
                    rp.x = event.rawX.toInt()
                    rp.y = event.rawY.toInt()
                    moved = false
                    clearAnim()
                }
                MotionEvent.ACTION_MOVE -> {
                    if (!moved) {
                        val dx = abs(event.rawX - rp.x)
                        val dy = abs(event.rawY - rp.y)
                        moved = dx > slop || dy > slop// 大于最小移动距离
                    }
                    val x = (event.rawX - p.x).toInt()
                    val y = (event.rawY - p.y).toInt()
                    updatePop(x, y)
                    return true
                }
                MotionEvent.ACTION_UP -> {
                    if (moved) {
                        // 恢复pressed状态
                        popupWindow?.contentView?.isPressed = false
                        moveEndAnim()
                    }
                    return moved
                }
            }
            return false
        }
    }

    private fun updatePop(x: Int, y: Int) {
        point.set(x, y)
        if (popupWindow?.isShowing == true) {
            popupWindow!!.update(x, y, -2, -2, false)
        }
    }

    private fun clearAnim() {
        val animator = popupWindow?.contentView?.tag as? ValueAnimator ?: return
        if (animator.isRunning) {
            animator.cancel()
        }
    }

    private fun moveEndAnim() {
        if (popupWindow == null) return

        clearAnim()

        val screenWidth = context.screenWidth()
        val half = screenWidth / 2f
        val viewWidth = context.resources.getDimension(R.dimen.dimen_dev_pop_size)
        val x = point.x + viewWidth / 2f
        val ex = if (x > half) {
            (screenWidth - context.dip(defaultMarginX) - viewWidth).toInt()
        } else {
            context.dip(defaultMarginX)
        }

        val dx = abs(point.x - ex)
        val time = (dx / half * 500).toLong()

        val animator = ObjectAnimator.ofInt(point.x, ex)
        animator.duration = time
        animator.interpolator = OvershootInterpolator(1.8f)
        animator.addUpdateListener {
            updatePop(it.animatedValue as Int, point.y)
        }
        animator.start()
        popupWindow!!.contentView.tag = animator// 暂存动画对象
    }
}