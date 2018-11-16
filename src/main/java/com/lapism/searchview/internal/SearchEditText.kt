package com.lapism.searchview.internal

import android.content.Context
import android.util.AttributeSet
import android.view.KeyEvent
import androidx.annotation.RestrictTo
import androidx.appcompat.widget.AppCompatEditText
import com.lapism.searchview.widget.SearchView


@RestrictTo(RestrictTo.Scope.LIBRARY)
class SearchEditText : AppCompatEditText {

    private var mSearchView: SearchView? = null

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    fun setSearchView(searchView: SearchView) {
        mSearchView = searchView
    }

    override fun onKeyPreIme(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK && event?.action == KeyEvent.ACTION_UP) {
            mSearchView?.let {
                if (it.isOpen && hasFocus()) {
                    it.close()
                    return true
                }
            }
        }

        return super.onKeyPreIme(keyCode, event)
    }

}
