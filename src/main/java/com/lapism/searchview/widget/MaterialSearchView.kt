package com.lapism.searchview.widget

import android.content.Context
import android.graphics.Rect
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
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.annotation.*
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.lapism.searchview.R
import com.lapism.searchview.graphics.MaterialSearchAnimator
import com.lapism.searchview.graphics.MaterialSearchArrowDrawable
import com.lapism.searchview.internal.MaterialSearchEditText
import com.lapism.searchview.internal.MaterialSearchViewSavedState


class MaterialSearchView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0
) :
    FrameLayout(context, attrs, defStyleAttr, defStyleRes),
    View.OnClickListener,
    Filter.FilterListener,
    CoordinatorLayout.AttachedBehavior {

    @Logo
    private var mLogo: Int = Logo.HAMBURGER_TO_ARROW_ANIMATION
    @Theme
    private var mTheme: Int = Theme.LIGHT
    @Version
    private var mVersion: Int = Version.TOOLBAR
    @VersionMargins
    private var mVersionMargins: Int = VersionMargins.TOOLBAR
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
    private var mMaterialSearchEditText: MaterialSearchEditText? = null
    private var mMaterialSearchArrowDrawable: MaterialSearchArrowDrawable? = null
    private var mRecyclerView: RecyclerView? = null
    private var mMaterialCardView: MaterialCardView? = null

    private var mOnLogoClickListener: OnLogoClickListener? = null
    private var mOnMicClickListener: OnMicClickListener? = null
    private var mOnMenuClickListener: OnMenuClickListener? = null
    private var mOnOpenCloseListener: OnOpenCloseListener? = null
    private var mOnQueryTextListener: OnQueryTextListener? = null

    init {
        // TODO chose let or .... JETPACK KTX ROOM
        // TODO PROJIT SEARCHVIEW V7 METODY A INTERFACES
        // TODO ROOM, LINT, SWIPERFESH, CHILD PARAMETR, ANIMACE, PROMENNE GRADLE // OVERRDES A DO KOTLINU A UPRAVIT KOTLINPROJITsearch_{
        inflate(context, R.layout.material_search_view, this)

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

        mMaterialSearchEditText = findViewById(R.id.search_searchEditText)
        mMaterialSearchEditText?.setSearchView(this)
        mMaterialSearchEditText?.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                onTextChanged(s)
            }
        })
        mMaterialSearchEditText?.setOnEditorActionListener { _, _, _ ->
            onSubmitQuery()
            true
        }
        mMaterialSearchEditText?.onFocusChangeListener = OnFocusChangeListener { _, hasFocus ->
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

        val typedArray =
            context.obtainStyledAttributes(attrs, R.styleable.MaterialSearchView, defStyleAttr, defStyleRes)

        setLogo(typedArray.getInteger(R.styleable.MaterialSearchView_search_logo, Logo.HAMBURGER_TO_ARROW_ANIMATION))

        setTheme(typedArray.getInteger(R.styleable.MaterialSearchView_search_theme, Theme.LIGHT))
        setVersion(typedArray.getInteger(R.styleable.MaterialSearchView_search_version, Version.TOOLBAR))
        setVersionMargins(
            typedArray.getInteger(
                R.styleable.MaterialSearchView_search_version_margins,
                VersionMargins.TOOLBAR
            )
        )

        setShadowColor(
            typedArray.getColor(
                R.styleable.MaterialSearchView_search_shadow_color,
                ContextCompat.getColor(context, R.color.search_shadow)
            )
        )

        // todo zkontrolovat zalomeni
        setRadius(resources.getDimensionPixelSize(R.dimen.search_shape_rounded).toFloat())

        typedArray.recycle()

        /// todo ===, ?:, ::
        //  TODO kouknout do searchview v 7
        // LAYOUT, FILE PROVIDER, IKONKY, ATD... barvy ...            <!-- ?android:attr/listDivider never-->
    }

    @Logo
    fun getLogo(): Int {
        return mLogo
    }

    fun setLogo(@Logo logo: Int) {
        mLogo = logo

        when (mLogo) {
            Logo.HAMBURGER -> {
                mImageViewLogo?.setImageDrawable(
                    ContextCompat.getDrawable(
                        context,
                        R.drawable.search_ic_outline_menu_24px
                    )
                )
            }
            Logo.ARROW -> {
                mImageViewLogo?.setImageDrawable(
                    ContextCompat.getDrawable(
                        context,
                        R.drawable.search_ic_outline_arrow_back_24px
                    )
                )
            }
            Logo.HAMBURGER_TO_ARROW_ANIMATION -> {
                mMaterialSearchArrowDrawable = MaterialSearchArrowDrawable(context)
                mImageViewLogo?.setImageDrawable(mMaterialSearchArrowDrawable)
            }
        }
    }

    @Theme
    fun getTheme(): Int {
        return mTheme
    }

    // TODO colorres a color int
    // todo companion object ===, ::, striska erovn a se, ?:,
    fun setTheme(@Theme theme: Int) {
        mTheme = theme

        when (mTheme) {
            Theme.LIGHT -> {
                setBackgroundColor(ContextCompat.getColor(context, R.color.search_light_background))
                setDividerColor(ContextCompat.getColor(context, R.color.search_light_divider))
                setLogoColor(ContextCompat.getColor(context, R.color.search_light_icon))
                setMicColor(ContextCompat.getColor(context, R.color.search_light_icon))
                setClearColor(ContextCompat.getColor(context, R.color.search_light_icon))
                setMenuColor(ContextCompat.getColor(context, R.color.search_light_icon))
                setHintColor(ContextCompat.getColor(context, R.color.search_light_hint))
                setTextColor(ContextCompat.getColor(context, R.color.search_light_title))
            }
            Theme.DARK -> {
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

    @Version
    fun getVersion(): Int {
        return mVersion
    }

    fun setVersion(@Version version: Int) {
        mVersion = version

        when (mVersion) {
            Version.TOOLBAR -> visibility = View.VISIBLE
            Version.MENU_ITEM -> visibility = View.GONE
        }
    }

    @VersionMargins
    fun getVersionMargins(): Int {
        return mVersionMargins
    }

    fun setVersionMargins(@VersionMargins versionMargins: Int) {
        mVersionMargins = versionMargins

        val params = FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        val left: Int
        val top: Int
        val right: Int
        val bottom: Int

        when (mVersionMargins) {
            VersionMargins.TOOLBAR -> {
                left = context.resources.getDimensionPixelSize(R.dimen.search_toolbar_margin_left_right)
                top = context.resources.getDimensionPixelSize(R.dimen.search_toolbar_margin_top_bottom)
                right = context.resources.getDimensionPixelSize(R.dimen.search_toolbar_margin_left_right)
                bottom = context.resources.getDimensionPixelSize(R.dimen.search_toolbar_margin_top_bottom)

                params.setMargins(left, top, right, bottom)
                mMaterialCardView?.layoutParams = params
            }
            VersionMargins.MENU_ITEM -> {
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
        // colorres
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
        return mMaterialSearchEditText?.text
    }

    fun setQuery(query: CharSequence?, submit: Boolean) {
        mMaterialSearchEditText?.setText(query)
        if (query != null) {
            mMaterialSearchEditText?.setSelection(mMaterialSearchEditText?.length()!!)
            mQueryText = query
        }

        if (submit && !TextUtils.isEmpty(query)) {
            onSubmitQuery()
        }
    }

    fun setQuery(@StringRes query: Int, submit: Boolean) {
        mMaterialSearchEditText?.setText(query)
        if (query != 0) {
            mMaterialSearchEditText?.setSelection(mMaterialSearchEditText?.length()!!)
            mQueryText = query.toString()
        }

        if (submit && !query.toString().isEmpty()) {
            onSubmitQuery()
        }
    }

    fun getText(): Editable? {
        return mMaterialSearchEditText?.text
    }

    fun setText(@StringRes text: Int) {
        mMaterialSearchEditText?.setText(text)
    }

    fun setText(text: CharSequence) {
        mMaterialSearchEditText?.setText(text)
    }

    fun setTextColor(@ColorInt color: Int) {
        mMaterialSearchEditText?.setTextColor(color)
    }

    fun setTextSize(size: Float) {
        mMaterialSearchEditText?.textSize = size
    }

    fun setTextGravity(gravity: Int) {
        mMaterialSearchEditText?.gravity = gravity
    }

    fun setTextImeOptions(imeOptions: Int) {
        mMaterialSearchEditText?.imeOptions = imeOptions
    }

    fun setTextInputType(inputType: Int) {
        mMaterialSearchEditText?.inputType = inputType
    }

    fun setHint(hint: CharSequence?) {
        mMaterialSearchEditText?.hint = hint
    }

    fun setHint(@StringRes hint: Int) {
        mMaterialSearchEditText?.setHint(hint)
    }

    fun setHintColor(@ColorInt color: Int) {
        mMaterialSearchEditText?.setHintTextColor(color)
    }

    /**
     * Typeface.NORMAL
     * Typeface.BOLD
     * Typeface.ITALIC
     * Typeface.BOLD_ITALIC
     */
    fun setTextStyle(style: Int) {
        mTextStyle = style
        mMaterialSearchEditText?.typeface = Typeface.create(mTextFont, mTextStyle)
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
        mMaterialSearchEditText?.typeface = Typeface.create(mTextFont, mTextStyle)
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
    fun setOnLogoClickListener(listener: OnLogoClickListener) {
        mOnLogoClickListener = listener
    }

    fun setOnMicClickListener(listener: OnMicClickListener) {
        mOnMicClickListener = listener
        if (mOnMicClickListener != null) {
            mImageViewMic?.visibility = View.VISIBLE
        } else {
            mImageViewMic?.visibility = View.GONE
        }
    }

    fun setOnMenuClickListener(listener: OnMenuClickListener) {
        mOnMenuClickListener = listener
        if (mOnMenuClickListener != null) {
            mImageViewMenu?.visibility = View.VISIBLE
        } else {
            mImageViewMenu?.visibility = View.GONE
        }
    }

    fun setOnOpenCloseListener(listener: OnOpenCloseListener) {
        mOnOpenCloseListener = listener
    }

    fun setOnQueryTextListener(listener: OnQueryTextListener) {
        mOnQueryTextListener = listener
    }

    // *********************************************************************************************
    private fun showKeyboard() {
        if (!isInEditMode) {
            val inputMethodManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.showSoftInput(mMaterialSearchEditText, 0)
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
            Version.TOOLBAR -> mMaterialSearchEditText?.requestFocus()
            Version.MENU_ITEM -> {
                visibility = View.VISIBLE
                /*if (mMenuItem != null) {
                    getMenuItemPosition(mMenuItem.getItemId())
                }*/
                val viewTreeObserver = mMaterialCardView?.viewTreeObserver
                viewTreeObserver?.let {
                    if (it.isAlive) {
                        it.addOnGlobalLayoutListener {
                            // TODO companion object + ?
                            MaterialSearchAnimator.revealOpen(
                                context,
                                mMaterialCardView,
                                mMenuItemCx,
                                mAnimationDuration,
                                mMaterialSearchEditText,
                                mOnOpenCloseListener
                            )
                        }

                        mMaterialCardView?.viewTreeObserver?.removeOnGlobalLayoutListener()
                    }
                }
            }
        }
    }

    fun close() {
        when (mVersion) {
            Version.TOOLBAR -> mMaterialSearchEditText?.clearFocus()
            Version.MENU_ITEM -> {
                /*if (mMenuItem != null) {
                    getMenuItemPosition(mMenuItem.getItemId())
                }*/
                MaterialSearchAnimator.revealClose(
                    context,
                    mMaterialCardView,
                    mMenuItemCx,
                    mAnimationDuration,
                    mMaterialSearchEditText,
                    this@MaterialSearchView,
                    mOnOpenCloseListener
                )
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
        val query = mMaterialSearchEditText?.getText()
        if (query != null && TextUtils.getTrimmedLength(query) > 0) {
            if (mOnQueryTextListener == null || !mOnQueryTextListener!!.onQueryTextSubmit(query.toString())) {
                mMaterialSearchEditText?.text = query
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
            MaterialSearchAnimator.fadeOpen(mViewShadow!!, mAnimationDuration)
        }

        if (mMaterialSearchArrowDrawable != null) {
            mMaterialSearchArrowDrawable?.animate(MaterialSearchArrowDrawable.STATE_ARROW, mAnimationDuration)
        }

        setMicOrClearIcon(true)

        // todo ===
        if (mVersion == Version.TOOLBAR) {
            // todo SavedState, marginy kulate a barva divideru
            if (mOnOpenCloseListener != null) {
                mOnOpenCloseListener!!.onOpen()
            }
        }

        postDelayed({ showKeyboard() }, mAnimationDuration)
    }

    private fun removeFocus() {

        if (mViewShadow?.visibility == View.VISIBLE) {
            MaterialSearchAnimator.fadeClose(mViewShadow!!, mAnimationDuration)
        }

        if (mMaterialSearchArrowDrawable != null) {
            mMaterialSearchArrowDrawable?.animate(MaterialSearchArrowDrawable.STATE_HAMBURGER, mAnimationDuration)
        }

        // todo error + shadow error pri otoceni, pak mizi animace
        hideSuggestions()
        hideKeyboard()
        setMicOrClearIcon(false)

        if (mVersion == Version.TOOLBAR) {
            postDelayed({
                if (mOnOpenCloseListener != null) {
                    mOnOpenCloseListener!!.onClose()
                }
            }, mAnimationDuration)
        }
    }


    override fun requestFocus(direction: Int, previouslyFocusedRect: Rect?): Boolean {
        mMaterialSearchEditText?.requestFocus(direction, previouslyFocusedRect)

        return super.requestFocus(direction, previouslyFocusedRect)
    }

    override fun clearFocus() {
        super.clearFocus()

        mMaterialSearchEditText?.clearFocus()
    }


    // todo v7 + todo tadycleanup code. projit anotace
    override fun onSaveInstanceState(): Parcelable? {
        val superState = super.onSaveInstanceState()
        val ss = MaterialSearchViewSavedState(superState!!)
        ss.hasFocus = mMaterialSearchEditText?.hasFocus()!!
        ss.shadowVisibility = mViewShadow?.visibility!!
        ss.query = mQueryText.toString()
        return ss
    }

    override fun onRestoreInstanceState(state: Parcelable?) {
        if (state !is MaterialSearchViewSavedState) {
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
            if (mMaterialSearchEditText?.hasFocus()!!) {
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
            if (mMaterialSearchEditText?.length()!! > 0) {
                mMaterialSearchEditText?.text!!.clear()
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
        return MaterialSearchBehavior()
    }

    // *********************************************************************************************
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

        fun onQueryTextSubmit(query: CharSequence?): Boolean

        fun onQueryTextChange(newText: CharSequence?)
    }

    // *********************************************************************************************
    @IntDef(Logo.HAMBURGER, Logo.ARROW, Logo.HAMBURGER_TO_ARROW_ANIMATION)
    @Retention(AnnotationRetention.SOURCE)
    annotation class Logo {
        companion object {
            const val HAMBURGER = 100
            const val ARROW = 101
            const val HAMBURGER_TO_ARROW_ANIMATION = 102
        }
    }

    @IntDef(Theme.LIGHT, Theme.DARK)
    @Retention(AnnotationRetention.SOURCE)
    annotation class Theme {
        companion object {
            const val LIGHT = 300
            const val DARK = 301
        }
    }

    @IntDef(Version.TOOLBAR, Version.MENU_ITEM)
    @Retention(AnnotationRetention.SOURCE)
    annotation class Version {
        companion object {
            const val TOOLBAR = 400
            const val MENU_ITEM = 401
        }
    }

    @IntDef(VersionMargins.TOOLBAR, VersionMargins.MENU_ITEM)
    @Retention(AnnotationRetention.SOURCE)
    annotation class VersionMargins {
        companion object {
            const val TOOLBAR = 500
            const val MENU_ITEM = 501
        }
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

/*setLogo(a.getInteger(R.styleable.SearchView_search_logo, MaterialSearchUtils.Logo.Companion.getHAMBURGER_TO_ARROW_ANIMATION()));
setShape(a.getInteger(R.styleable.SearchView_search_shape, MaterialSearchUtils.Shape.Companion.getCLASSIC()));
setTheme(a.getInteger(R.styleable.SearchView_search_theme, MaterialSearchUtils.Theme.Companion.getLIGHT()));
setVersionMargins(a.getInteger(R.styleable.SearchView_search_version_margins, MaterialSearchUtils.VersionMargins.Companion.getTOOLBAR()));
setVersion(a.getInteger(R.styleable.SearchView_search_version, MaterialSearchUtils.Version.Companion.getTOOLBAR()));

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