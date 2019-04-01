package com.lapism.searchview.internal

import android.content.Context
import android.util.AttributeSet
import android.view.KeyEvent
import androidx.annotation.RestrictTo
import androidx.appcompat.widget.AppCompatEditText
import com.lapism.searchview.widget.MaterialSearchView


@RestrictTo(RestrictTo.Scope.LIBRARY)
class MaterialSearchEditText : AppCompatEditText {

    private var mMaterialSearchView: MaterialSearchView? = null

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    fun setSearchView(materialSearchView: MaterialSearchView) {
        mMaterialSearchView = materialSearchView
    }

    override fun onKeyPreIme(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK && event?.action == KeyEvent.ACTION_UP) {
            mMaterialSearchView?.let {
                if (it.isOpen() && hasFocus()) {
                    //it.close()
                    return true
                }
            }
        }

        return super.onKeyPreIme(keyCode, event)
    }

}
