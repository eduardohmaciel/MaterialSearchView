package com.lapism.searchview.widget

import android.content.Context
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.os.Parcelable
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.MenuItem
import android.view.View
import android.view.View.OnFocusChangeListener
import android.view.ViewGroup
import android.view.ViewParent
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.annotation.ColorInt
import androidx.annotation.Dimension
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.lapism.searchview.R
import com.lapism.searchview.Search
import com.lapism.searchview.graphics.SearchAnimator
import com.lapism.searchview.graphics.SearchArrowDrawable
import com.lapism.searchview.internal.SearchEditText
import com.lapism.searchview.internal.SearchViewSavedState


class MaterialSearchView @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0,
        defStyleRes: Int = 0) :
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
    private var mAnimationDuration: Long = 300L
    private var mQueryText: CharSequence? = null

    private var mMenuItemCx = -1
    private var mMenuItem: MenuItem? = null

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
        // TODO chose let or .... JETPACK KTX ROOM
        // TODO PROJIT SEARCHVIEW V7 METODY A INTERFACES
        // TODO ROOM, LINT, SWIPERFESH, CHILD PARAMETR, ANIMACE, PROMENNE GRADLE // OVERRDES A DO KOTLINU A UPRAVIT KOTLINPROJITsearch_{
        inflate(context, R.layout.search_view_material, this)

        mViewShadow = findViewById(R.id.search_view_shadow)
        mViewShadow?.visibility = View.GONE
        mViewShadow?.setOnClickListener(this@MaterialSearchView) // todo

        mViewDivider = findViewById(R.id.search_view_divider)
        mViewDivider?.visibility = View.GONE

        mLinearLayout = findViewById(R.id.search_linearLayout)

        mImageViewLogo = findViewById(R.id.search_imageView_logo)
        mImageViewLogo?.setOnClickListener(this@MaterialSearchView)

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
                onTextChanged(s)
            }
        })
        mSearchEditText?.setOnEditorActionListener { _, _, _ ->
            onSubmitQuery()
            true
        }
        mSearchEditText?.onFocusChangeListener = OnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                addFocus()
            } else {
                removeFocus()
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

        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.MaterialSearchView, defStyleAttr, defStyleRes)

        setLogo(typedArray.getInteger(R.styleable.MaterialSearchView_search_logo, Search.Logo.HAMBURGER_TO_ARROW_ANIMATION))
        setShape(typedArray.getInteger(R.styleable.MaterialSearchView_search_shape, Search.Shape.ROUNDED))
        setTheme(typedArray.getInteger(R.styleable.MaterialSearchView_search_theme, Search.Theme.LIGHT))
        setVersion(typedArray.getInteger(R.styleable.MaterialSearchView_search_version, Search.Version.TOOLBAR))
        setVersionMargins(typedArray.getInteger(R.styleable.MaterialSearchView_search_version_margins, Search.VersionMargins.TOOLBAR))

        setShadowColor(typedArray.getColor(R.styleable.MaterialSearchView_search_shadow_color, ContextCompat.getColor(context, R.color.search_shadow)))

        typedArray.recycle()

        /// todo ===, ?:, ::
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

        when (mVersion) {
            Search.Version.TOOLBAR -> visibility = View.VISIBLE
            Search.Version.MENU_ITEM -> visibility = View.GONE
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

    // *********************************************************************************************
    fun setShadowVisibility(visibility: Int) {
        mViewShadow?.visibility = visibility
    }

    fun setShadowColor(@ColorInt color: Int) {
        mViewShadow?.setBackgroundColor(color)
    }

    // *********************************************************************************************
    fun setDividerColor(@ColorInt color: Int) {
        mViewDivider?.setBackgroundColor(color)
    }

    // *********************************************************************************************
    fun getSearchViewHeight(): Int {
        val params = mLinearLayout?.layoutParams
        return params?.height!!
    }

    fun setSearchViewHeight(height: Int) {
        val params = mLinearLayout?.layoutParams
        params?.height = height
        params?.width = ViewGroup.LayoutParams.MATCH_PARENT
        mLinearLayout?.layoutParams = params
    }

    // *********************************************************************************************
    fun setLogoResource(@DrawableRes resource: Int) {
        mImageViewLogo?.setImageResource(resource)
    }

    fun setLogoDrawable(drawable: Drawable?) {
        mImageViewLogo?.setImageDrawable(drawable)
    }

    fun setLogoColor(@ColorInt color: Int) {
        mImageViewLogo?.setColorFilter(color)
    }

    // *********************************************************************************************
    fun setMicResource(@DrawableRes resource: Int) {
        mImageViewMic?.setImageResource(resource)
    }

    fun setMicDrawable(drawable: Drawable?) {
        mImageViewMic?.setImageDrawable(drawable)
    }

    fun setMicColor(@ColorInt color: Int) {
        mImageViewMic?.setColorFilter(color)
    }

    // *********************************************************************************************
    fun setClearResource(@DrawableRes resource: Int) {
        mImageViewClear?.setImageResource(resource)
    }

    fun setClearDrawable(drawable: Drawable?) {
        mImageViewClear?.setImageDrawable(drawable)
    }

    fun setClearColor(@ColorInt color: Int) {
        mImageViewClear?.setColorFilter(color)
    }

    // *********************************************************************************************
    fun setMenuResource(@DrawableRes resource: Int) {
        mImageViewMenu?.setImageResource(resource)
    }

    fun setMenuDrawable(drawable: Drawable?) {
        mImageViewMenu?.setImageDrawable(drawable)
    }

    fun setMenuColor(@ColorInt color: Int) {
        mImageViewMenu?.setColorFilter(color)
    }

    // *********************************************************************************************
    fun getQuery(): Editable? {
        return mSearchEditText?.text
    }

    fun setQuery(query: CharSequence?, submit: Boolean) {
        mSearchEditText?.setText(query)
        if (query != null) {
            mSearchEditText?.setSelection(mSearchEditText?.length()!!)
            mQueryText = query
        }

        if (submit && !TextUtils.isEmpty(query)) {
            onSubmitQuery()
        }
    }

    fun setQuery(@StringRes query: Int, submit: Boolean) {
        mSearchEditText?.setText(query)
        if (query != 0) {
            mSearchEditText?.setSelection(mSearchEditText?.length()!!)
            mQueryText = query.toString()
        }

        if (submit && !query.toString().isEmpty()) {
            onSubmitQuery()
        }
    }

    fun getText(): Editable? {
        return mSearchEditText?.text
    }

    fun setText(@StringRes text: Int) {
        mSearchEditText?.setText(text)
    }

    fun setText(text: CharSequence) {
        mSearchEditText?.setText(text)
    }

    fun setTextColor(@ColorInt color: Int) {
        mSearchEditText?.setTextColor(color)
    }

    fun setTextSize(size: Float) {
        mSearchEditText?.textSize = size
    }

    fun setTextGravity(gravity: Int) {
        mSearchEditText?.gravity = gravity
    }

    fun setTextImeOptions(imeOptions: Int) {
        mSearchEditText?.imeOptions = imeOptions
    }

    fun setTextInputType(inputType: Int) {
        mSearchEditText?.inputType = inputType
    }

    fun setHint(hint: CharSequence?) {
        mSearchEditText?.hint = hint
    }

    fun setHint(@StringRes hint: Int) {
        mSearchEditText?.setHint(hint)
    }

    fun setHintColor(@ColorInt color: Int) {
        mSearchEditText?.setHintTextColor(color)
    }

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

    // *********************************************************************************************
    fun getAdapter(): RecyclerView.Adapter<*>? {
        return mRecyclerView?.adapter
    }

    fun setAdapter(adapter: RecyclerView.Adapter<*>) {
        mRecyclerView?.adapter = adapter
    }

    fun addItemDecoration(itemDecoration: RecyclerView.ItemDecoration) {
        mRecyclerView?.addItemDecoration(itemDecoration)
    }

    fun removeItemDecoration(itemDecoration: RecyclerView.ItemDecoration) {
        mRecyclerView?.removeItemDecoration(itemDecoration)
    }

    // *********************************************************************************************
    override fun setBackgroundColor(@ColorInt color: Int) {
        mMaterialCardView?.setCardBackgroundColor(color)
    }

    fun setStrokeWidth(@Dimension strokeWidth: Int) {
        mMaterialCardView?.strokeWidth = strokeWidth
    }

    fun setStrokeColor(@ColorInt strokeColor: Int) {
        mMaterialCardView?.strokeColor = strokeColor
    }

    fun setRadius(radius: Float) {
        mMaterialCardView?.radius = radius // cleanup code
    }

    override fun setElevation(elevation: Float) {
        mMaterialCardView?.cardElevation = elevation
    }

    // *********************************************************************************************
    fun isOpen(): Boolean {
        return visibility == View.VISIBLE
    }

    fun setAnimationDuration(animationDuration: Long) {
        mAnimationDuration = animationDuration
    }

    // *********************************************************************************************
    fun setOnLogoClickListener(listener: Search.OnLogoClickListener) {
        mOnLogoClickListener = listener
    }

    fun setOnMicClickListener(listener: Search.OnMicClickListener) {
        mOnMicClickListener = listener
        if (mOnMicClickListener != null) {
            mImageViewMic?.visibility = View.VISIBLE
        } else {
            mImageViewMic?.visibility = View.GONE
        }
    }

    fun setOnMenuClickListener(listener: Search.OnMenuClickListener) {
        mOnMenuClickListener = listener
        if (mOnMenuClickListener != null) {
            mImageViewMenu?.visibility = View.VISIBLE
        } else {
            mImageViewMenu?.visibility = View.GONE
        }
    }

    fun setOnOpenCloseListener(listener: Search.OnOpenCloseListener) {
        mOnOpenCloseListener = listener
    }

    fun setOnQueryTextListener(listener: Search.OnQueryTextListener) {
        mOnQueryTextListener = listener
    }

    // *********************************************************************************************
    private fun showKeyboard() {
        if (!isInEditMode) {
            val inputMethodManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.showSoftInput(mSearchEditText, 0)
        }
    }

    private fun hideKeyboard() {
        if (!isInEditMode) {
            val inputMethodManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(windowToken, 0)
        }
    }

    private fun setMicOrClearIcon(hasFocus: Boolean) {
        if (hasFocus && !TextUtils.isEmpty(mQueryText)) {
            if (mOnMicClickListener != null) {
                mImageViewMic?.visibility = View.GONE
            }
            mImageViewClear?.visibility = View.VISIBLE
        } else {
            mImageViewClear?.visibility = View.GONE
            if (mOnMicClickListener != null) {
                mImageViewMic?.visibility = View.VISIBLE
            }
        }
    }

    /*private fun getMenuItemPosition(menuItemId: Int) {
        if (mMenuItemView != null) {
            mMenuItemCx = getCenterX(mMenuItemView)
        }
        var viewParent: ViewParent? = parent
        if (viewParent != null) {
            while (viewParent is View) {
                val parent = viewParent as View?
                val view = parent!!.findViewById<View>(menuItemId)
                if (view != null) {
                    mMenuItemView = view
                    mMenuItemCx = getCenterX(mMenuItemView)
                    break
                }
                viewParent = viewParent.parent
            }
        }
    }*/

    private fun getCenterX(view: View): Int {
        val location = IntArray(2)
        view.getLocationOnScreen(location)
        return location[0] + view.width / 2
    }

    fun open() {
        open(null)
    }

    fun open(menuItem: MenuItem?) {
        mMenuItem = menuItem

        when (mVersion) {
            Search.Version.TOOLBAR -> mSearchEditText?.requestFocus()
            Search.Version.MENU_ITEM -> {
                visibility = View.VISIBLE
                /*if (mMenuItem != null) {
                    getMenuItemPosition(mMenuItem.getItemId())
                }*/
                val viewTreeObserver = mMaterialCardView?.viewTreeObserver
                viewTreeObserver?.let {
                    if (it.isAlive) {
                        it.addOnGlobalLayoutListener {
                            // TODO companion object + ?
                            SearchAnimator.revealOpen(
                                    context,
                                    mMaterialCardView,
                                    mMenuItemCx,
                                    mAnimationDuration,
                                    mSearchEditText,
                                    mOnOpenCloseListener)
                        }

                        //mMaterialCardView?.viewTreeObserver?.removeOnGlobalLayoutListener()
                    }
                }
            }
        }
    }

    fun close() {
        when (mVersion) {
            Search.Version.TOOLBAR -> mSearchEditText?.clearFocus()
            Search.Version.MENU_ITEM -> {
                /*if (mMenuItem != null) {
                    getMenuItemPosition(mMenuItem.getItemId())
                }*/
                SearchAnimator.revealClose(
                        context,
                        mMaterialCardView,
                        mMenuItemCx,
                        mAnimationDuration,
                        mSearchEditText,
                        this@MaterialSearchView,
                        mOnOpenCloseListener)
            }
        }
    }

    private fun filter(s: CharSequence?) {
        if (getAdapter() != null && getAdapter() is Filterable) {
            (getAdapter() as Filterable).filter.filter(s, this)
        }
    }

    private fun showSuggestions() {
        if (getAdapter()?.itemCount!! > 0) {
            mViewDivider?.visibility = View.VISIBLE
            mRecyclerView?.visibility = View.VISIBLE
        }
    }

    private fun hideSuggestions() {
        getAdapter()?.let {
            if (it.itemCount > 0) {
                mViewDivider?.visibility = View.GONE
                mRecyclerView?.visibility = View.GONE
            }
        }
    }

    // TODO PROJIT VSECHNY METODY, ? ATD
    private fun clearIconsColor() {
        mImageViewLogo?.clearColorFilter()
        mImageViewMic?.clearColorFilter()
        mImageViewClear?.clearColorFilter()
    }

    private fun onSubmitQuery() {
        val query = mSearchEditText?.getText()
        if (query != null && TextUtils.getTrimmedLength(query) > 0) {
            if (mOnQueryTextListener == null || !mOnQueryTextListener!!.onQueryTextSubmit(query.toString())) {
                mSearchEditText?.text = query
            }
        }
    }

    private fun onTextChanged(s: CharSequence?) {
        mQueryText = s

        setMicOrClearIcon(true)
        filter(s)

        if (mOnQueryTextListener != null) {
            mOnQueryTextListener!!.onQueryTextChange(mQueryText)
        }
    }

    private fun addFocus() {
        filter(mQueryText)

        if (mViewShadow?.visibility == View.GONE) {
            SearchAnimator.fadeOpen(mViewShadow!!, mAnimationDuration)
        }

        setMicOrClearIcon(true)


        // todo ===
        if (mVersion == Search.Version.TOOLBAR) {
            // todo SavedState, marginy kulate a barva divideru
            if (mOnOpenCloseListener != null) {
                mOnOpenCloseListener!!.onOpen()
            }
        }

        postDelayed({ showKeyboard() }, mAnimationDuration)
    }

    private fun removeFocus() {

        if (mViewShadow?.visibility == View.VISIBLE) {
            SearchAnimator.fadeClose(mViewShadow!!, mAnimationDuration)
        }

        // todo error + shadow error pri otoceni, pak mizi animace
        hideSuggestions()
        hideKeyboard()
        setMicOrClearIcon(false)

        if (mVersion == Search.Version.TOOLBAR) {
            postDelayed({
                if (mOnOpenCloseListener != null) {
                    mOnOpenCloseListener!!.onClose()
                }
            }, mAnimationDuration)
        }
    }

    // *****************************************************************************************************************
    // todo v7 + todo tadycleanup code. projit anotace
    override fun onSaveInstanceState(): Parcelable? {
        val superState = super.onSaveInstanceState()
        val ss = SearchViewSavedState(superState!!)
        ss.hasFocus = mSearchEditText?.hasFocus()!!
        ss.shadowVisibility = mViewShadow?.visibility!!
        ss.query = mQueryText.toString()
        return ss
    }

    override fun onRestoreInstanceState(state: Parcelable?) {
        if (state !is SearchViewSavedState) {
            super.onRestoreInstanceState(state)
            return
        }
        super.onRestoreInstanceState(state.superState)
        mViewShadow?.visibility = state.shadowVisibility
        if (state.hasFocus) {
            open()
        }
        if (state.query != null) {
            setText(state.query!!)
        }
        requestLayout()
    }

    override fun onClick(v: View?) {
        if (v == mImageViewLogo) {
            if (mSearchEditText?.hasFocus()!!) {
                close()
            } else {
                if (mOnLogoClickListener != null) {
                    mOnLogoClickListener!!.onLogoClick()
                }
            }
        } else if (v == mImageViewMic) {
            if (mOnMicClickListener != null) {
                mOnMicClickListener!!.onMicClick()
            }
        } else if (v == mImageViewClear) {
            if (mSearchEditText?.length()!! > 0) {
                mSearchEditText?.text!!.clear()
            }
        } else if (v == mImageViewMenu) {
            if (mOnMenuClickListener != null) {
                mOnMenuClickListener!!.onMenuClick()
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

//mRecyclerView.setLayoutManager(new LinearLayoutManager(context));
//mRecyclerView.setItemAnimator(new DefaultItemAnimator());

/*setLogo(a.getInteger(R.styleable.SearchView_search_logo, Search.Logo.Companion.getHAMBURGER_TO_ARROW_ANIMATION()));
setShape(a.getInteger(R.styleable.SearchView_search_shape, Search.Shape.Companion.getCLASSIC()));
setTheme(a.getInteger(R.styleable.SearchView_search_theme, Search.Theme.Companion.getLIGHT()));
setVersionMargins(a.getInteger(R.styleable.SearchView_search_version_margins, Search.VersionMargins.Companion.getTOOLBAR()));
setVersion(a.getInteger(R.styleable.SearchView_search_version, Search.Version.Companion.getTOOLBAR()));

if (a.hasValue(R.styleable.SearchView_search_logo_icon)) {
    setLogoIcon(a.getInteger(R.styleable.SearchView_search_logo_icon, 0));
}

if (a.hasValue(R.styleable.SearchView_search_logo_color)) {
    setLogoColor(ContextCompat.getColor(context, a.getResourceId(R.styleable.SearchView_search_logo_color, 0)));
}

if (a.hasValue(R.styleable.SearchView_search_mic_icon)) {
    setMicIcon(a.getResourceId(R.styleable.SearchView_search_mic_icon, 0));
}

if (a.hasValue(R.styleable.SearchView_search_mic_color)) {
    setMicColor(a.getColor(R.styleable.SearchView_search_mic_color, 0));
}

if (a.hasValue(R.styleable.SearchView_search_clear_icon)) {
    setClearIcon(a.getDrawable(R.styleable.SearchView_search_clear_icon));
} else {
    setClearIcon(ContextCompat.getDrawable(context, R.drawable.search_ic_outline_clear_24px));
    // getDrawable z contextu a getColor z Contextu
}

if (a.hasValue(R.styleable.SearchView_search_clear_color)) {
    setClearColor(a.getColor(R.styleable.SearchView_search_clear_color, 0));
}

if (a.hasValue(R.styleable.SearchView_search_menu_icon)) {
    setMenuIcon(a.getResourceId(R.styleable.SearchView_search_menu_icon, 0));
}

if (a.hasValue(R.styleable.SearchView_search_menu_color)) {
    setMenuColor(a.getColor(R.styleable.SearchView_search_menu_color, 0));
}

if (a.hasValue(R.styleable.SearchView_search_background_color)) {
    setBackgroundColor(a.getColor(R.styleable.SearchView_search_background_color, 0));
}

if (a.hasValue(R.styleable.SearchView_search_text_image)) {
    setTextImage(a.getResourceId(R.styleable.SearchView_search_text_image, 0));
}

if (a.hasValue(R.styleable.SearchView_search_text_color)) {
    setTextColor(a.getColor(R.styleable.SearchView_search_text_color, 0));
}

if (a.hasValue(R.styleable.SearchView_search_text_size)) {
    setTextSize(a.getDimension(R.styleable.SearchView_search_text_size, 0));
}

if (a.hasValue(R.styleable.SearchView_search_text_style)) {
    setTextStyle(a.getInt(R.styleable.SearchView_search_text_style, 0));
}

if (a.hasValue(R.styleable.SearchView_search_hint)) {
    setHint(a.getString(R.styleable.SearchView_search_hint));
}

if (a.hasValue(R.styleable.SearchView_search_hint_color)) {
    setHintColor(a.getColor(R.styleable.SearchView_search_hint_color, 0));
}

setAnimationDuration(a.getInt(R.styleable.SearchView_search_animation_duration, context.getResources().getInteger(R.integer.search_animation_duration)));
setShadow(a.getBoolean(R.styleable.SearchView_search_shadow, true));


if (a.hasValue(R.styleable.SearchView_search_elevation)) {
    setElevation(a.getDimensionPixelSize(R.styleable.SearchView_search_elevation, 0));
}

a.recycle();

//setSaveEnabled(true);*/


/**
 * @param listener
 */
// @FloatRange(from = 0.5, to = 1.0)
// Listeners