package com.lapism.searchview.widget

import android.content.Context
import android.graphics.PorterDuff
import android.graphics.Typeface
import android.text.SpannableString
import android.text.Spanned
import android.text.TextUtils
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.lapism.searchview.R
import com.lapism.searchview.database.MaterialSearchHistoryDao
import com.lapism.searchview.internal.MaterialSearchViewHolder
import java.lang.ref.WeakReference
import java.util.*


class MaterialSearchAdapter(context: Context) : RecyclerView.Adapter<MaterialSearchViewHolder>(), Filterable {

    private var mContext: WeakReference<Context>? = null
    private var mDatabase: MutableList<MaterialSearchItem>
    private var mConstraint: CharSequence? = null
    private var suggestionsList: List<MaterialSearchItem>? = null
    private var resultsList: List<MaterialSearchItem>? = null
    private var mSearchItemClickListener: OnSearchItemClickListener? = null
    @ColorInt
    private var mIcon1Color: Int = 0
    @ColorInt
    private var mIcon2Color: Int = 0
    @ColorInt
    private var mTitleColor: Int = 0
    @ColorInt
    private var mSubtitleColor: Int = 0
    @ColorInt
    private var mTitleHighlightColor: Int = 0
    private var mTextStyle = Typeface.NORMAL
    private var mTextFont = Typeface.DEFAULT
    private val mHistoryDatabaseMaterial: MaterialSearchHistoryDao
    /// var suggestionsList: MutableList<MaterialSearchItem>

    init {
        mContext = WeakReference(context)
        mHistoryDatabaseMaterial = MaterialSearchHistoryDao(context)
        mDatabase = mHistoryDatabaseMaterial.allItems
        resultsList = mDatabase// todo
        suggestionsList = ArrayList()
        setTheme(MaterialSearchView.Theme.LIGHT)
    }

    fun setSuggestionsList(suggestionsList: List<MaterialSearchItem>) {
        this.suggestionsList = suggestionsList
    }

    // ---------------------------------------------------------------------------------------------
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MaterialSearchViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.material_search_item, parent, false)
        return MaterialSearchViewHolder(view, mSearchItemClickListener)
    }

    override fun onBindViewHolder(holderMaterial: MaterialSearchViewHolder, position: Int) {
        val item = resultsList!![position]

        if (item.icon1Resource != 0) {
            holderMaterial.icon1.setImageResource(item.icon1Resource)
            holderMaterial.icon1.setColorFilter(mIcon1Color)
        } else if (item.icon1Drawable != null) {
            holderMaterial.icon1.setImageDrawable(item.icon1Drawable)
            holderMaterial.icon1.setColorFilter(mIcon1Color, PorterDuff.Mode.SRC_IN)
        } else {
            holderMaterial.icon1.visibility = View.GONE
        }

        if (item.icon2Resource != 0) {
            holderMaterial.icon2.setImageResource(item.icon2Resource)
            holderMaterial.icon2.setColorFilter(mIcon1Color, PorterDuff.Mode.SRC_IN)
        } else if (item.icon2Drawable != null) {
            holderMaterial.icon2.setImageDrawable(item.icon2Drawable)
            holderMaterial.icon2.setColorFilter(mIcon2Color)
        } else {
            holderMaterial.icon2.visibility = View.GONE
        }

        if (!TextUtils.isEmpty(item.title)) {
            holderMaterial.title.typeface = Typeface.create(mTextFont, mTextStyle)
            holderMaterial.title.setTextColor(mTitleColor)

            val title = item.title!!.toString()
            val titleLower = title.toLowerCase(Locale.getDefault())

            if (!TextUtils.isEmpty(mConstraint) && titleLower.contains(mConstraint!!)) {
                val s = SpannableString(title)
                s.setSpan(
                    ForegroundColorSpan(mTitleHighlightColor),
                    titleLower.indexOf(mConstraint!!.toString()),
                    titleLower.indexOf(mConstraint!!.toString()) + mConstraint!!.length,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                )
                holderMaterial.title.setText(s, TextView.BufferType.SPANNABLE)
            } else {
                holderMaterial.title.text = item.title
            }
        } else {
            holderMaterial.title.visibility = View.GONE
        }

        if (!TextUtils.isEmpty(item.subtitle)) {
            holderMaterial.subtitle.typeface = Typeface.create(mTextFont, mTextStyle)
            holderMaterial.subtitle.setTextColor(mSubtitleColor)
            holderMaterial.subtitle.text = item.subtitle
        } else {
            holderMaterial.subtitle.visibility = View.GONE
        }
    }

    override fun getItemCount(): Int {
        return resultsList!!.size
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    // ---------------------------------------------------------------------------------------------
    private fun setIcon1Color(@ColorInt color: Int) {
        mIcon1Color = color
    }

    private fun setIcon2Color(@ColorInt color: Int) {
        mIcon2Color = color
    }

    private fun setTitleColor(@ColorInt color: Int) {
        mTitleColor = color
    }

    private fun setTitleHighlightColor(@ColorInt color: Int) {
        mTitleHighlightColor = color
    }

    private fun setSubtitleColor(@ColorInt color: Int) {
        mSubtitleColor = color
    }

    fun setTextStyle(style: Int) {
        mTextStyle = style
    }

    fun setTextFont(font: Typeface) {
        mTextFont = font
    }

    fun setTheme(@MaterialSearchView.Theme theme: Int) {
        mContext?.get().let {
            when (theme) {
                MaterialSearchView.Theme.LIGHT -> {
                    setIcon1Color(ContextCompat.getColor(it!!, R.color.search_light_icon_1_2))
                    setIcon2Color(ContextCompat.getColor(it, R.color.search_light_icon_1_2))
                    setTitleColor(ContextCompat.getColor(it, R.color.search_light_title))
                    setTitleHighlightColor(ContextCompat.getColor(it, R.color.search_light_title_highlight))
                    setSubtitleColor(ContextCompat.getColor(it, R.color.search_light_subtitle))
                }
                MaterialSearchView.Theme.DARK -> {
                    setIcon1Color(ContextCompat.getColor(it!!, R.color.search_dark_icon_1_2))
                    setIcon2Color(ContextCompat.getColor(it, R.color.search_dark_icon_1_2))
                    setTitleColor(ContextCompat.getColor(it, R.color.search_dark_title))
                    setTitleHighlightColor(ContextCompat.getColor(it, R.color.search_dark_title_highlight))
                    setSubtitleColor(ContextCompat.getColor(it, R.color.search_dark_subtitle))
                }
            }
        }
    }

    fun setOnSearchItemClickListener(listener: OnSearchItemClickListener) {
        mSearchItemClickListener = listener
    }

    // ---------------------------------------------------------------------------------------------
    private fun setData(data: List<MaterialSearchItem>) {
        resultsList = data
        notifyDataSetChanged() // todo notifyDataInserted
    }

    // ---------------------------------------------------------------------------------------------
    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence): Filter.FilterResults {
                val filterResults = Filter.FilterResults()

                mConstraint = constraint.toString().toLowerCase(Locale.getDefault())

                if (!TextUtils.isEmpty(mConstraint)) {
                    val history = ArrayList<MaterialSearchItem>()
                    val results = ArrayList<MaterialSearchItem>()

                    mDatabase.clear()
                    mDatabase = mHistoryDatabaseMaterial.allItems

                    if (!mDatabase.isEmpty()) {
                        history.addAll(mDatabase)
                    }
                    history.addAll(suggestionsList!!)

                    for (item in history) {
                        val string = item.title!!.toString().toLowerCase(Locale.getDefault())
                        if (string.contains(mConstraint!!)) {
                            results.add(item)
                        }
                    }
                    if (results.size > 0) {
                        filterResults.values = results
                        filterResults.count = results.size
                    }
                } else {
                    if (!mDatabase.isEmpty()) {
                        filterResults.values = mDatabase
                        filterResults.count = mDatabase.size
                    }
                }

                return filterResults
            }

            override fun publishResults(constraint: CharSequence, results: Filter.FilterResults) {
                if (results.count > 0) {
                    val dataSet = ArrayList<MaterialSearchItem>()
                    val resultSet = results.values as List<*>
                    val size = if (results.count < 8) results.count else 8

                    for (i in 0 until size) {
                        if (resultSet[i] is MaterialSearchItem) {
                            dataSet.add(resultSet[i] as MaterialSearchItem)
                        }
                    }

                    setData(dataSet)
                }
            }
        }
    }

    // ---------------------------------------------------------------------------------------------


    interface OnSearchItemClickListener {

        fun onSearchItemClick(position: Int, title: CharSequence, subtitle: CharSequence)

    }


    // TODO OVERRIDES ZNOVA...KOTLINOVE+ANJOTACE
}
