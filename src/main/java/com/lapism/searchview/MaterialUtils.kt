package com.lapism.searchview

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.speech.RecognizerIntent
import android.util.TypedValue
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat


object MaterialUtils {

    const val SPEECH_REQUEST_CODE = 99

    fun isLayoutRtl(view: View): Boolean {
        return ViewCompat.getLayoutDirection(view) == ViewCompat.LAYOUT_DIRECTION_RTL
    }

    fun isLayoutRtl(context: Context): Boolean {
        return context.resources.configuration.layoutDirection == View.LAYOUT_DIRECTION_RTL
    }

    fun setVoiceSearch(activity: Activity, text: String) {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, text)
        intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1)
        activity.startActivityForResult(intent, SPEECH_REQUEST_CODE)
    }

    fun isVoiceSearchAvailable(context: Context): Boolean {
        val pm = context.packageManager
        val activities = pm.queryIntentActivities(Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH), 0)
        return activities.size != 0
    }

    fun getAttrColor(context: Context, resid: Int): Int {
        val typedValue = TypedValue()
        val theme = context.theme
        theme.resolveAttribute(resid, typedValue, true)
        return typedValue.data
    }

    fun getAttrColorRes(context: Context, resid: Int): Int {
        val typedValue = TypedValue()
        val theme = context.theme
        theme.resolveAttribute(resid, typedValue, true)
        return ContextCompat.getColor(context, typedValue.resourceId)
    }

    fun getBitmapFromVectorDrawable(context: Context, id: Int): Bitmap {

        //if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){

        //AppCompatResources.getDrawable(requireContext(), drawableId)

        val drawable = ContextCompat.getDrawable(context, id)
        drawable?.let {
            if (it is BitmapDrawable) {
                return it.bitmap
            } else {
                // VectorDrawable or AdaptiveIconDrawable
                val bitmap: Bitmap = if (it.intrinsicWidth <= 0 || it.intrinsicHeight <= 0) {
                    Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)
                } else {
                    Bitmap.createBitmap(drawable.intrinsicWidth, drawable.intrinsicHeight, Bitmap.Config.ARGB_8888)
                }
                // BitmapFactory.decodeResource(context.resources, drawableId)
                val canvas = Canvas(bitmap)
                drawable.setBounds(0, 0, canvas.width, canvas.height)
                drawable.draw(canvas)
                return bitmap
            }
        }
    }

}
// android.R.attr.textColorPrimary
// FILE PROVIDER , CHECK BAREV 2X, LINT A ANIMACE, FAB BEHAVIOR
// https://proandroiddev.com/enter-animation-using-recyclerview-and-layoutanimation-part-2-grids-688829b1d29b
// https://proandroiddev.com/enter-animation-using-recyclerview-and-layoutanimation-part-1-list-75a874a5d213

