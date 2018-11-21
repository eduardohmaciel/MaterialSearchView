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
import com.google.android.material.card.MaterialCardView
import com.lapism.searchview.R
import com.lapism.searchview.internal.MaterialSearchEditText
import com.lapism.searchview.widget.MaterialSearchView

// todo companion object
object MaterialSearchAnimator {

    fun revealOpen(
        context: Context,
        materialCardView: MaterialCardView?,
        cardViewX: Int,
        duration: Long,
        materialSearchEditText: MaterialSearchEditText?,
        listener: MaterialSearchView.OnOpenCloseListener?
    ) {
        var cx = cardViewX
        if (cx <= 0) {
            val padding = context.resources.getDimensionPixelSize(R.dimen.search_reveal)
            cx = if (isRtlLayout(context)) {
                padding
            } else {
                materialCardView?.width!! - padding
            }
        }

        val cy = context.resources.getDimensionPixelSize(R.dimen.search_height_view) / 2

        if (cx != 0 && cy != 0) {
            val displaySize = Point()
            val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
            windowManager.defaultDisplay?.getSize(displaySize)
            val finalRadius = Math.hypot(Math.max(cx, displaySize.x - cx).toDouble(), cy.toDouble()).toFloat()

            val anim = ViewAnimationUtils.createCircularReveal(materialCardView, cx, cy, 0.0f, finalRadius)
            anim.interpolator = AccelerateDecelerateInterpolator()
            anim.duration = duration
            anim.addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    super.onAnimationEnd(animation)
                    materialSearchEditText?.requestFocus()
                }

                override fun onAnimationStart(animation: Animator) {
                    super.onAnimationStart(animation)
                    materialCardView?.visibility = View.VISIBLE
                    listener?.onOpen()
                }
            })
            anim.start()
        }
    }

    fun revealClose(
        context: Context,
        materialCardView: MaterialCardView?,
        cardViewX: Int,
        duration: Long,
        materialSearchEditText: MaterialSearchEditText?,
        searchView: MaterialSearchView,
        listener: MaterialSearchView.OnOpenCloseListener?
    ) {
        var cx = cardViewX
        if (cx <= 0) {
            val padding = context.resources.getDimensionPixelSize(R.dimen.search_reveal)
            cx = if (isRtlLayout(context)) {
                padding
            } else {
                materialCardView?.width!! - padding
            }
        }

        val cy = context.resources.getDimensionPixelSize(R.dimen.search_height_view) / 2

        if (cx != 0 && cy != 0) {
            val displaySize = Point()
            val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
            windowManager.defaultDisplay?.getSize(displaySize)
            val initialRadius = Math.hypot(Math.max(cx, displaySize.x - cx).toDouble(), cy.toDouble()).toFloat()

            val anim = ViewAnimationUtils.createCircularReveal(materialCardView, cx, cy, initialRadius, 0.0f)
            anim.interpolator = AccelerateDecelerateInterpolator()
            anim.duration = duration
            anim.addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    super.onAnimationEnd(animation)
                    materialCardView?.visibility = View.GONE
                    searchView.visibility = View.GONE
                    listener?.onClose()
                }

                override fun onAnimationStart(animation: Animator) {
                    super.onAnimationStart(animation)
                    materialSearchEditText?.clearFocus()
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
