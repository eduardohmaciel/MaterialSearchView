package com.lapism.searchview.widget

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Parcel
import android.os.Parcelable
import android.text.TextUtils

import com.lapism.searchview.R


class SearchItem : Parcelable {

    var icon1Drawable: Drawable? = null
    var icon2Drawable: Drawable? = null
    var icon1Resource: Int = 0
    var icon2Resource: Int = 0
    var title: CharSequence? = null
    var subtitle: CharSequence? = null
    private var context: Context? = null

    constructor(context: Context) {
        this.context = context
    }

    private constructor(parcel: Parcel) {
        val bitmap_1 = parcel.readParcelable<Bitmap>(javaClass.classLoader)
        this.icon1Drawable = BitmapDrawable(context?.resources, bitmap_1)
        val bitmap_2 = parcel.readParcelable<Bitmap>(javaClass.classLoader)
        this.icon2Drawable = BitmapDrawable(context?.resources, bitmap_2)

        this.icon1Resource = parcel.readInt()
        this.icon2Resource = parcel.readInt()

        this.title = TextUtils.CHAR_SEQUENCE_CREATOR.createFromParcel(parcel)
        this.subtitle = TextUtils.CHAR_SEQUENCE_CREATOR.createFromParcel(parcel)
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        val bitmap_1 = (this.icon1Drawable as BitmapDrawable).bitmap
        dest.writeParcelable(bitmap_1, flags)
        val bitmap_2 = (this.icon2Drawable as BitmapDrawable).bitmap
        dest.writeParcelable(bitmap_2, flags)

        dest.writeInt(this.icon1Resource)
        dest.writeInt(this.icon2Resource)

        TextUtils.writeToParcel(this.title, dest, flags)
        TextUtils.writeToParcel(this.subtitle, dest, flags)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object {

        @JvmField
        val CREATOR: Parcelable.Creator<SearchItem> = object : Parcelable.Creator<SearchItem> {
            override fun createFromParcel(`in`: Parcel): SearchItem {
                return SearchItem(`in`)
            }

            override fun newArray(size: Int): Array<SearchItem?> {
                return arrayOfNulls(size)
            }
        }
    }

}
