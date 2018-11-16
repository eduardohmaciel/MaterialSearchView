package com.lapism.searchview.widget

import android.content.Context
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.os.Parcelable
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.View
import android.view.View.OnFocusChangeListener
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Filter
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.annotation.ColorInt
import androidx.annotation.Dimension
import androidx.annotation.DrawableRes
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.lapism.searchview.R
import com.lapism.searchview.Search
import com.lapism.searchview.graphics.SearchArrowDrawable
import com.lapism.searchview.internal.SearchEditText


class SearchViewX @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0
) :
    FrameLayout(context, attrs, defStyleAttr, defStyleRes),
    View.OnClickListener,
    Filter.FilterListener,
    CoordinatorLayout.AttachedBehavior {

    @Search.Logo
    private var mLogo: Int = Search.Logo.HAMBURGER_TO_ARROW_ANIMATION
    @Search.Shape
    private var mShape: Int = Search.Shape.CLASSIC
    @Search.Theme
    private var mTheme: Int = Search.Theme.LIGHT
    @Search.Version
    private var mVersion: Int = Search.Version.TOOLBAR
    @Search.VersionMargins
    private var mVersionMargins: Int = Search.VersionMargins.TOOLBAR

    private var mTextStyle = Typeface.NORMAL
    private var mTextFont = Typeface.DEFAULT
    // TODO chose let or .... JETPACK KTX ROOM
    // TODO PROJIT SEARCHVIEW V7 METODY A INTERFACES
    // TODO ROOM, LINT, SWIPERFESH, CHILD PARAMETR, ANIMACE, PROMENNE GRADLE // OVERRDES A DO KOTLINU A UPRAVIT KOTLINPROJITsearch_
    private var mViewShadow: View? = null
    private var mViewDivider: View? = null
    private var mLinearLayout: LinearLayout? = null
    private var mImageViewLogo: ImageView? = null
    private var mImageViewMic: ImageView? = null
    private var mImageViewClear: ImageView? = null
    private var mImageViewMenu: ImageView? = null
    private var mSearchEditText: SearchEditText? = null
    private var mSearchArrowDrawable: SearchArrowDrawable? = null
    private var mRecyclerView: RecyclerView? = null
    private var mMaterialCardView: MaterialCardView? = null

    private var mOnLogoClickListener: Search.OnLogoClickListener? = null
    private var mOnMicClickListener: Search.OnMicClickListener? = null
    private var mOnMenuClickListener: Search.OnMenuClickListener? = null
    private var mOnOpenCloseListener: Search.OnOpenCloseListener? = null
    private var mOnQueryTextListener: Search.OnQueryTextListener? = null

    init {
        inflate(context, R.layout.search_view, this)

        mViewShadow = findViewById(R.id.search_view_shadow)
        mViewShadow?.visibility = View.GONE
        mViewShadow?.setOnClickListener(this@SearchViewX) // todo

        mViewDivider = findViewById(R.id.search_view_divider)
        mViewDivider?.visibility = View.GONE

        mLinearLayout = findViewById(R.id.search_linearLayout)

        mImageViewLogo = findViewById(R.id.search_imageView_logo)
        mImageViewLogo?.setOnClickListener(this@SearchViewX)

        mImageViewMic = findViewById(R.id.search_imageView_mic)
        mImageViewMic?.visibility = View.GONE
        mImageViewMic?.setOnClickListener(this)

        mImageViewClear = findViewById(R.id.search_imageView_clear)
        mImageViewClear?.setOnClickListener(this)
        mImageViewClear?.visibility = View.GONE

        mImageViewMenu = findViewById(R.id.search_imageView_menu)
        mImageViewMenu?.visibility = View.GONE
        mImageViewMenu?.setOnClickListener(this)

        mSearchEditText = findViewById(R.id.search_searchEditText)
        mSearchEditText?.setSearchView(this)
        mSearchEditText?.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // onTextChanged(s);
            }
        })
        mSearchEditText?.setOnEditorActionListener { _, _, _ ->
            //onSubmitQuery()
            true
        }
        mSearchEditText?.onFocusChangeListener = OnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                //addFocus()
            } else {
                //removeFocus()
            }
        }

        mRecyclerView = findViewById(R.id.search_recyclerView)
        mRecyclerView?.visibility = View.GONE
        mRecyclerView?.isNestedScrollingEnabled = false
        mRecyclerView?.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                if (newState == RecyclerView.SCROLL_STATE_DRAGGING) {
                    hideKeyboard()
                }
            }

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {

            }
        })

        mMaterialCardView = findViewById(R.id.search_materialCardView)
        val viewTreeObserver = mMaterialCardView?.viewTreeObserver
        viewTreeObserver?.let {
            if (it.isAlive) {
                it.addOnGlobalLayoutListener {
                    /*mMaterialCardView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
     // prohodit
     // TODO
     SearchAnimator.INSTANCE.revealOpen(
             getContext(),
             mMaterialCardView,
             mMenuItemCx,
             mAnimationDuration,
             mSearchEditText,
             mOnOpenCloseListener);*/
                }
            }
        }


        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.SearchViewX, defStyleAttr, defStyleRes)








        typedArray.recycle()

        //  TODO kouknout do searchview v 7
        // LAYOUT, FILE PROVIDER, IKONKY, ATD... barvy ...            <!-- ?android:attr/listDivider never-->
    }

    @Search.Logo
    fun getLogo(): Int {
        return mLogo
    }

    fun setLogo(@Search.Logo logo: Int) {
        mLogo = logo

        when (mLogo) {
            Search.Logo.HAMBURGER -> {
                mImageViewLogo?.setImageDrawable(
                    ContextCompat.getDrawable(
                        context,
                        R.drawable.search_ic_outline_menu_24px
                    )
                )
            }
            Search.Logo.ARROW -> {
                mImageViewLogo?.setImageDrawable(
                    ContextCompat.getDrawable(
                        context,
                        R.drawable.search_ic_outline_arrow_back_24px
                    )
                )
            }
            Search.Logo.HAMBURGER_TO_ARROW_ANIMATION -> {
                mSearchArrowDrawable = SearchArrowDrawable(context)
                mImageViewLogo?.setImageDrawable(mSearchArrowDrawable)
            }
        }
    }

    @Search.Shape
    fun getShape(): Int {
        return mShape
    }

    fun setShape(@Search.Shape shape: Int) {
        mShape = shape

        when (mShape) {
            Search.Shape.CLASSIC -> setRadius(resources.getDimensionPixelSize(R.dimen.search_shape_classic).toFloat())
            Search.Shape.ROUNDED -> setRadius(resources.getDimensionPixelSize(R.dimen.search_shape_rounded).toFloat())
        }
    }

    @Search.Theme
    fun getTheme(): Int {
        return mTheme
    }

    fun setTheme(@Search.Theme theme: Int) {
        mTheme = theme

        when (mTheme) {
            Search.Theme.LIGHT -> {
                setBackgroundColor(ContextCompat.getColor(context, R.color.search_light_background))
                setDividerColor(ContextCompat.getColor(context, R.color.search_light_divider))
                setLogoColor(ContextCompat.getColor(context, R.color.search_light_icon))
                setMicColor(ContextCompat.getColor(context, R.color.search_light_icon))
                setClearColor(ContextCompat.getColor(context, R.color.search_light_icon))
                setMenuColor(ContextCompat.getColor(context, R.color.search_light_icon))
                setHintColor(ContextCompat.getColor(context, R.color.search_light_hint))
                setTextColor(ContextCompat.getColor(context, R.color.search_light_title))
            }
            Search.Theme.DARK -> {
                setBackgroundColor(ContextCompat.getColor(context, R.color.search_dark_background))
                setDividerColor(ContextCompat.getColor(context, R.color.search_dark_divider))
                setLogoColor(ContextCompat.getColor(context, R.color.search_dark_icon))
                setMicColor(ContextCompat.getColor(context, R.color.search_dark_icon))
                setClearColor(ContextCompat.getColor(context, R.color.search_dark_icon))
                setMenuColor(ContextCompat.getColor(context, R.color.search_dark_icon))
                setHintColor(ContextCompat.getColor(context, R.color.search_dark_hint))
                setTextColor(ContextCompat.getColor(context, R.color.search_dark_title))
            }
        }
    }

    @Search.Version
    fun getVersion(): Int {
        return mVersion
    }

    fun setVersion(@Search.Version version: Int) {
        mVersion = version

        /// todo ===, ?:, ::
        when (mVersion) {
            Search.Version.TOOLBAR -> visibility = View.GONE
            Search.Version.MENU_ITEM -> visibility = View.VISIBLE
        }
    }

    @Search.VersionMargins
    fun getVersionMargins(): Int {
        return mVersionMargins
    }

    fun setVersionMargins(@Search.VersionMargins versionMargins: Int) {
        mVersionMargins = versionMargins

        val params = FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        val left: Int
        val top: Int
        val right: Int
        val bottom: Int

        when (mVersionMargins) {
            Search.VersionMargins.TOOLBAR -> {
                left = context.resources.getDimensionPixelSize(R.dimen.search_toolbar_margin_left_right)
                top = context.resources.getDimensionPixelSize(R.dimen.search_toolbar_margin_top_bottom)
                right = context.resources.getDimensionPixelSize(R.dimen.search_toolbar_margin_left_right)
                bottom = context.resources.getDimensionPixelSize(R.dimen.search_toolbar_margin_top_bottom)

                params.setMargins(left, top, right, bottom)

                mMaterialCardView?.layoutParams = params
            }
            Search.VersionMargins.MENU_ITEM -> {
                left = context.resources.getDimensionPixelSize(R.dimen.search_menu_item_margin)
                top = context.resources.getDimensionPixelSize(R.dimen.search_menu_item_margin)
                right = context.resources.getDimensionPixelSize(R.dimen.search_menu_item_margin)
                bottom = context.resources.getDimensionPixelSize(R.dimen.search_menu_item_margin)

                params.setMargins(left, top, right, bottom)

                mMaterialCardView?.layoutParams = params
            }
        }
    }

    // *****************************************************************************************************************
    fun setLogoResource(@DrawableRes resource: Int) {
        mImageViewLogo?.setImageResource(resource)
    }

    fun setLogoDrawable(drawable: Drawable?) {
        if (drawable != null) {
            mImageViewLogo?.setImageDrawable(drawable)
        } else {
            mImageViewLogo?.visibility = View.GONE
        }
    }

    fun setLogoColor(@ColorInt color: Int) {
        mImageViewLogo?.setColorFilter(color)
    }

    // *****************************************************************************************************************
    fun setMicResource(@DrawableRes resource: Int) {
        mImageViewMic?.setImageResource(resource)
    }

    fun setMicDrawable(drawable: Drawable?) {
        mImageViewMic?.setImageDrawable(drawable)
    }

    fun setMicColor(@ColorInt color: Int) {
        mImageViewMic?.setColorFilter(color)
    }

    // *****************************************************************************************************************
    /**
     * Typeface.NORMAL
     * Typeface.BOLD
     * Typeface.ITALIC
     * Typeface.BOLD_ITALIC
     */
    fun setTextStyle(style: Int) {
        mTextStyle = style
        mSearchEditText?.typeface = Typeface.create(mTextFont, mTextStyle)
    }

    /**
     * Typeface.DEFAULT
     * Typeface.DEFAULT_BOLD
     * Typeface.SANS_SERIF
     * Typeface.SERIF
     * Typeface.MONOSPACE
     */
    fun setTextFont(font: Typeface) {
        mTextFont = font
        mSearchEditText?.typeface = Typeface.create(mTextFont, mTextStyle)
    }

    fun getAdapter(): RecyclerView.Adapter<*>? {
        return mRecyclerView?.getAdapter()
    }

    fun setAdapter(adapter: RecyclerView.Adapter<*>) {
        mRecyclerView?.setAdapter(adapter)
    }

    fun addItemDecoration(itemDecoration: RecyclerView.ItemDecoration) {
        mRecyclerView?.addItemDecoration(itemDecoration)
    }

    fun removeItemDecoration(itemDecoration: RecyclerView.ItemDecoration) {
        mRecyclerView?.removeItemDecoration(itemDecoration)
    }

    fun showKeyboard() {
        if (!isInEditMode) {
            val inputMethodManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.showSoftInput(mSearchEditText, 0)
        }
    }

    fun hideKeyboard() {
        if (!isInEditMode) {
            val inputMethodManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(windowToken, 0)
        }
    }

    // *****************************************************************************************************************
    fun setStrokeWidth(@Dimension strokeWidth: Int) {
        mMaterialCardView?.setStrokeWidth(strokeWidth)
    }

    // todo projit anotace
    fun setStrokeColor(@ColorInt strokeColor: Int) {
        mMaterialCardView?.strokeColor = strokeColor
    }

    fun setRadius(radius: Float) {
        mMaterialCardView?.setRadius(radius) // cleanup code
    }

    override fun setElevation(elevation: Float) {
        mMaterialCardView?.cardElevation = elevation
    }

    override fun onSaveInstanceState(): Parcelable? {
        return super.onSaveInstanceState()
    }

    override fun onRestoreInstanceState(state: Parcelable?) {
        super.onRestoreInstanceState(state)
    }

    override fun onClick(v: View?) {
        if (v == mImageViewLogo) {
            if (mSearchEditText.hasFocus()) {
                close()
            } else {
                if (mOnLogoClickListener != null) {
                    mOnLogoClickListener.onLogoClick()
                }
            }
        } else if (v == mImageViewMic) {
            if (mOnMicClickListener != null) {
                mOnMicClickListener.onMicClick()
            }
        } else if (v == mImageViewClear) {
            if (mSearchEditText.length() > 0) {
                mSearchEditText.getText()!!.clear()
            }
        } else if (v == mImageViewMenu) {
            if (mOnMenuClickListener != null) {
                mOnMenuClickListener.onMenuClick()
            }
        } else if (v == mViewShadow) {
            close()
        }
    }

    override fun onFilterComplete(count: Int) {
        if (count > 0) {
            showSuggestions()
        } else {
            hideSuggestions()
        }
    }

    override fun getBehavior(): CoordinatorLayout.Behavior<*> {
        return SearchBehavior()
    }

}

/* kulate rohy a light a zkontrolvat Bar a compat nekde pouzito???
 * ZKONTROLOVAT VZHLED KODU ...
 * readme
 * +  todo obraky a dodelat vypis metod
 * THIS A PRIVATE
 * colorpicker
 * komENTARE A BUGY
 * */

/*
if ( drawable != null ) {
    Bitmap bitmap = (Bitmap) ((BitmapDrawable) drawable).getBitmap();
    parcel.writeParcelable(bitmap, flags);
}
else {
    parcel.writeParcelable(null, flags);
}

To read the Drawable from the Parcelable:

Bitmap bitmap = (Bitmap) in.readParcelable(getClass().getClassLoader());
if ( bitmap != null ) {
    drawable = new BitmapDrawable(bitmap);
}
else {
    drawable = null;
}
*/