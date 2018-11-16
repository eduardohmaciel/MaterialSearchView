package com.lapism.searchview.widget

import android.view.View
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.ViewCompat
import com.google.android.material.appbar.AppBarLayout


class SearchBehavior : CoordinatorLayout.Behavior<SearchView>() {

    override fun layoutDependsOn(parent: CoordinatorLayout, child: SearchView, dependency: View): Boolean {
        if (dependency is AppBarLayout) {
            ViewCompat.setElevation(child, ViewCompat.getElevation(dependency))
            ViewCompat.setZ(child, ViewCompat.getZ(dependency) + 1) // TODO no click background
            return true
        }
        return super.layoutDependsOn(parent, child, dependency)
    }

    override fun onDependentViewChanged(parent: CoordinatorLayout, child: SearchView, dependency: View): Boolean {
        if (dependency is AppBarLayout) {
            child.translationY = dependency.getY()
            return true
        }
        return super.onDependentViewChanged(parent, child, dependency)
    }

}
