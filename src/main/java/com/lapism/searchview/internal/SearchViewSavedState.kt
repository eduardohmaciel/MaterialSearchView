package com.lapism.searchview.internal

import android.os.Parcel
import android.os.Parcelable
import android.view.View
import androidx.annotation.RestrictTo

/**
 * @hide
 */
@RestrictTo(RestrictTo.Scope.LIBRARY)
class SearchViewSavedState(superState: Parcelable) : View.BaseSavedState(superState) {

    var query: String? = null
    var hasFocus: Boolean = false
    var shadow: Boolean = false

    override fun writeToParcel(out: Parcel, flags: Int) {
        super.writeToParcel(out, flags)
        out.writeString(query)
        out.writeInt(if (hasFocus) 1 else 0)
    }

} // TIID PREPSAT OVERRIDES
