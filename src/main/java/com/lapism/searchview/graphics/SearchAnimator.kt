package com.lapism.searchview.graphics

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.Context
import android.graphics.Point
import android.view.View
import android.view.ViewAnimationUtils
import android.view.WindowManager
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import androidx.cardview.widget.CardView
import com.lapism.searchview.R
import com.lapism.searchview.Search
import com.lapism.searchview.internal.SearchEditText
import com.lapism.searchview.widget.SearchView

// todo companion object
object SearchAnimator {

    fun revealOpen(context: Context, cardView: CardView, cardViewX: Int, duration: Long, editText: SearchEditText, listener: Search.OnOpenCloseListener?) {
        var cx = cardViewX
        if (cx <= 0) {
            val padding = context.resources.getDimensionPixelSize(R.dimen.search_reveal)
            cx = if (isRtlLayout(context)) {
                padding
            } else {
                cardView.width - padding
            }
        }

        val cy = context.resources.getDimensionPixelSize(R.dimen.search_height_view) / 2

        if (cx != 0 && cy != 0) {
            val displaySize = Point()
            val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
            windowManager.defaultDisplay?.getSize(displaySize)
            val finalRadius = Math.hypot(Math.max(cx, displaySize.x - cx).toDouble(), cy.toDouble()).toFloat()

            val anim = ViewAnimationUtils.createCircularReveal(cardView, cx, cy, 0.0f, finalRadius)
            anim.interpolator = AccelerateDecelerateInterpolator()
            anim.duration = duration
            anim.addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    super.onAnimationEnd(animation)
                    editText.requestFocus()
                }

                override fun onAnimationStart(animation: Animator) {
                    super.onAnimationStart(animation)
                    cardView.visibility = View.VISIBLE
                    listener?.onOpen()
                }
            })
            anim.start()
        }
    }

    fun revealClose(context: Context, cardView: CardView, cardViewX: Int, duration: Long, editText: SearchEditText, searchView: SearchView, listener: Search.OnOpenCloseListener?) {
        var cx = cardViewX
        if (cx <= 0) {
            val padding = context.resources.getDimensionPixelSize(R.dimen.search_reveal)
            cx = if (isRtlLayout(context)) {
                padding
            } else {
                cardView.width - padding
            }
        }

        val cy = context.resources.getDimensionPixelSize(R.dimen.search_height_view) / 2

        if (cx != 0 && cy != 0) {
            val displaySize = Point()
            val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
            windowManager.defaultDisplay?.getSize(displaySize)
            val initialRadius = Math.hypot(Math.max(cx, displaySize.x - cx).toDouble(), cy.toDouble()).toFloat()

            val anim = ViewAnimationUtils.createCircularReveal(cardView, cx, cy, initialRadius, 0.0f)
            anim.interpolator = AccelerateDecelerateInterpolator()
            anim.duration = duration
            anim.addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    super.onAnimationEnd(animation)
                    cardView.visibility = View.GONE
                    searchView.visibility = View.GONE
                    listener?.onClose()
                }

                override fun onAnimationStart(animation: Animator) {
                    super.onAnimationStart(animation)
                    editText.clearFocus()
                }
            })
            anim.start()
        }
    }

    fun fadeOpen(view: View, duration: Long) {
        val anim = AlphaAnimation(0.0f, 1.0f)
        anim.interpolator = AccelerateDecelerateInterpolator()
        anim.duration = duration
        anim.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) {
                view.visibility = View.VISIBLE
            }

            override fun onAnimationEnd(animation: Animation) {

            }

            override fun onAnimationRepeat(animation: Animation) {

            }
        })
        view.startAnimation(anim)
    }

    fun fadeClose(view: View, duration: Long) {
        val anim = AlphaAnimation(1.0f, 0.0f)
        anim.interpolator = AccelerateDecelerateInterpolator()
        anim.duration = duration
        anim.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) {

            }

            override fun onAnimationEnd(animation: Animation) {
                view.visibility = View.GONE
            }

            override fun onAnimationRepeat(animation: Animation) {

            }
        })
        view.startAnimation(anim)
    }

    private fun isRtlLayout(context: Context): Boolean {
        return context.resources.configuration.layoutDirection == View.LAYOUT_DIRECTION_RTL
    }

}
