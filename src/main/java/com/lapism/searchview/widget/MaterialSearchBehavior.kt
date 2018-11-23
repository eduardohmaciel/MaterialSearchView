package com.lapism.searchview.widget

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.ViewCompat
import com.google.android.material.appbar.AppBarLayout


class MaterialSearchBehavior : CoordinatorLayout.Behavior<MaterialSearchView> {

    constructor()

    constructor(context: Context, attrs: AttributeSet)

    override fun layoutDependsOn(parent: CoordinatorLayout, child: MaterialSearchView, dependency: View): Boolean {
        if (dependency is AppBarLayout) {
            ViewCompat.setElevation(child, ViewCompat.getElevation(dependency))
            ViewCompat.setZ(child, ViewCompat.getZ(dependency) + 1) // TODO no click background

            /*
                app:layout_anchor="@id/viewA"
    app:layout_anchorGravity="bottom|right|end"/>
            */
            return true
        }
        return super.layoutDependsOn(parent, child, dependency)
    }

    override fun onDependentViewChanged(
        parent: CoordinatorLayout,
        child: MaterialSearchView,
        dependency: View
    ): Boolean {
        if (dependency is AppBarLayout) {
            child.translationY = dependency.getY()
            return true
        }
        return super.onDependentViewChanged(parent, child, dependency)
    }

}
