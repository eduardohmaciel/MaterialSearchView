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
import com.lapism.searchview.database.SearchHistoryTable
import com.lapism.searchview.internal.SearchViewHolder
import java.lang.ref.WeakReference
import java.util.*


class SearchAdapter(context: Context) : RecyclerView.Adapter<SearchViewHolder>(), Filterable {

    private var mContext: WeakReference<Context>? = null
    private var mDatabase: MutableList<SearchItem>
    private var mConstraint: CharSequence? = null
    private var suggestionsList: List<SearchItem>? = null
    private var resultsList: List<SearchItem>? = null
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
    private val mHistoryDatabase: SearchHistoryTable
    /// var suggestionsList: MutableList<SearchItem>

    init {
        mContext = WeakReference(context)
        mHistoryDatabase = SearchHistoryTable(context)
        mDatabase = mHistoryDatabase.allItems
        resultsList = mDatabase// todo
        suggestionsList = ArrayList()
        setTheme(MaterialSearchView.Theme.LIGHT)
    }

    fun setSuggestionsList(suggestionsList: List<SearchItem>) {
        this.suggestionsList = suggestionsList
    }

    // ---------------------------------------------------------------------------------------------
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.search_item, parent, false)
        return SearchViewHolder(view, mSearchItemClickListener)
    }

    override fun onBindViewHolder(holder: SearchViewHolder, position: Int) {
        val item = resultsList!![position]

        if (item.icon1Resource != 0) {
            holder.icon1.setImageResource(item.icon1Resource)
            holder.icon1.setColorFilter(mIcon1Color)
        } else if (item.icon1Drawable != null) {
            holder.icon1.setImageDrawable(item.icon1Drawable)
            holder.icon1.setColorFilter(mIcon1Color, PorterDuff.Mode.SRC_IN)
        } else {
            holder.icon1.visibility = View.GONE
        }

        if (item.icon2Resource != 0) {
            holder.icon2.setImageResource(item.icon2Resource)
            holder.icon2.setColorFilter(mIcon1Color, PorterDuff.Mode.SRC_IN)
        } else if (item.icon2Drawable != null) {
            holder.icon2.setImageDrawable(item.icon2Drawable)
            holder.icon2.setColorFilter(mIcon2Color)
        } else {
            holder.icon2.visibility = View.GONE
        }

        if (!TextUtils.isEmpty(item.title)) {
            holder.title.typeface = Typeface.create(mTextFont, mTextStyle)
            holder.title.setTextColor(mTitleColor)

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
                holder.title.setText(s, TextView.BufferType.SPANNABLE)
            } else {
                holder.title.text = item.title
            }
        } else {
            holder.title.visibility = View.GONE
        }

        if (!TextUtils.isEmpty(item.subtitle)) {
            holder.subtitle.typeface = Typeface.create(mTextFont, mTextStyle)
            holder.subtitle.setTextColor(mSubtitleColor)
            holder.subtitle.text = item.subtitle
        } else {
            holder.subtitle.visibility = View.GONE
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
    private fun setData(data: List<SearchItem>) {
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
                    val history = ArrayList<SearchItem>()
                    val results = ArrayList<SearchItem>()

                    mDatabase.clear()
                    mDatabase = mHistoryDatabase.allItems

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
                    val dataSet = ArrayList<SearchItem>()
                    val resultSet = results.values as List<*>
                    val size = if (results.count < 8) results.count else 8

                    for (i in 0 until size) {
                        if (resultSet[i] is SearchItem) {
                            dataSet.add(resultSet[i] as SearchItem)
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
