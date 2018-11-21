package com.lapism.searchview.widget

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Parcel
import android.os.Parcelable
import android.text.TextUtils


class MaterialSearchItem : Parcelable {

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
        val bitmap1 = parcel.readParcelable<Bitmap>(javaClass.classLoader)
        this.icon1Drawable = BitmapDrawable(context?.resources, bitmap1)
        val bitmap2 = parcel.readParcelable<Bitmap>(javaClass.classLoader)
        this.icon2Drawable = BitmapDrawable(context?.resources, bitmap2)

        this.icon1Resource = parcel.readInt()
        this.icon2Resource = parcel.readInt()

        this.title = TextUtils.CHAR_SEQUENCE_CREATOR.createFromParcel(parcel)
        this.subtitle = TextUtils.CHAR_SEQUENCE_CREATOR.createFromParcel(parcel)
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        val bitmap1 = (this.icon1Drawable as BitmapDrawable).bitmap
        dest.writeParcelable(bitmap1, flags)
        val bitmap2 = (this.icon2Drawable as BitmapDrawable).bitmap
        dest.writeParcelable(bitmap2, flags)

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
        val CREATOR: Parcelable.Creator<MaterialSearchItem> = object : Parcelable.Creator<MaterialSearchItem> {
            override fun createFromParcel(parcel: Parcel): MaterialSearchItem {
                return MaterialSearchItem(parcel)
            }

            override fun newArray(size: Int): Array<MaterialSearchItem?> {
                return arrayOfNulls(size)
            }
        }
    }

}
