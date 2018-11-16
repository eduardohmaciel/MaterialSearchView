package com.lapism.searchview

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.speech.RecognizerIntent

import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy

import androidx.annotation.IntDef


object Search {

    val SPEECH_REQUEST_CODE = 99

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

    @IntDef(Logo.HAMBURGER, Logo.ARROW, Logo.HAMBURGER_TO_ARROW_ANIMATION)
    @Retention(RetentionPolicy.SOURCE)
    annotation class Logo {
        companion object {
            val HAMBURGER = 100
            val ARROW = 101
            val HAMBURGER_TO_ARROW_ANIMATION = 102
        }
    }

    @IntDef(Shape.CLASSIC, Shape.ROUNDED)
    @Retention(RetentionPolicy.SOURCE)
    annotation class Shape {
        companion object {
            const val CLASSIC = 200
            const val ROUNDED = 201
        }
    }

    @IntDef(Theme.LIGHT, Theme.DARK)
    @Retention(RetentionPolicy.SOURCE)
    annotation class Theme {
        companion object {
            const val LIGHT = 300
            const val DARK = 301
        }
    }

    @IntDef(Version.TOOLBAR, Version.MENU_ITEM)
    @Retention(RetentionPolicy.SOURCE)
    annotation class Version {
        companion object {
            const val TOOLBAR = 400
            const val MENU_ITEM = 401
        }
    }

    @IntDef(VersionMargins.TOOLBAR, VersionMargins.MENU_ITEM)
    @Retention(RetentionPolicy.SOURCE)
    annotation class VersionMargins {
        companion object {
            const val TOOLBAR = 500
            const val MENU_ITEM = 501
        }
    }

    interface OnLogoClickListener {

        fun onLogoClick()

    }

    interface OnMicClickListener {

        fun onMicClick()

    }

    interface OnMenuClickListener {

        fun onMenuClick()

    }

    interface OnOpenCloseListener {

        fun onOpen()

        fun onClose()

    }

    interface OnQueryTextListener {

        fun onQueryTextSubmit(query: CharSequence): Boolean

        fun onQueryTextChange(newText: CharSequence)

    }

}
