package com.lapism.searchview.widget

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.bottomnavigation.BottomNavigationView

// DONE OVERRIDES + SEARCHBEHAVIOR A TODO A FABBEHAVIOR
class BottomNavigationViewBehavior(context: Context?, attrs: AttributeSet?) : CoordinatorLayout.Behavior<BottomNavigationView>(context, attrs) {

    override fun onDependentViewChanged(parent: CoordinatorLayout, child: BottomNavigationView, dependency: View): Boolean {
        if (dependency is AppBarLayout) {
            child.translationY = -dependency.y
            return true
        }
        return super.onDependentViewChanged(parent, child, dependency)
    }

    override fun layoutDependsOn(parent: CoordinatorLayout, child: BottomNavigationView, dependency: View): Boolean {
        return dependency is AppBarLayout || super.layoutDependsOn(parent, child, dependency)
    }

}
