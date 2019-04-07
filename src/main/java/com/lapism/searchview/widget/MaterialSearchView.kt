package com.lapism.searchview.widget

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.ColorFilter
import android.graphics.Rect
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.os.Parcelable
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.View
import android.view.View.OnFocusChangeListener
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.ScaleAnimation
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.annotation.*
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.lapism.searchview.R
import com.lapism.searchview.internal.MaterialSearchEditText
import com.lapism.searchview.internal.MaterialSearchViewSavedState


@Suppress("unused")
class MaterialSearchView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0
) : FrameLayout(context, attrs, defStyleAttr, defStyleRes),
    View.OnClickListener,
    Filter.FilterListener,
    CoordinatorLayout.AttachedBehavior {

    @Logo
    private var mLogo = Logo.ANIMATION

    private var mTextStyle = Typeface.NORMAL
    private var mTextFont = Typeface.DEFAULT
    private var mAnimationDuration = 400L

    private var mQueryText: CharSequence? = null
    private var mViewShadowBackground: View? = null
    private var mViewShadowForeground: View? = null
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
        inflate(context, R.layout.material_search_view, this@MaterialSearchView)

        mViewDivider = findViewById(R.id.search_view_divider)
        mViewDivider?.visibility = View.GONE

        mLinearLayout = findViewById(R.id.search_linearLayout)

        mImageViewLogo = findViewById(R.id.search_imageView_logo)
        mImageViewLogo?.setOnClickListener(this)

        mImageViewMic = findViewById(R.id.search_imageView_mic)
        mImageViewMic?.visibility = View.GONE
        mImageViewMic?.setOnClickListener(this)

        mImageViewClear = findViewById(R.id.search_imageView_clear)
        mImageViewClear?.visibility = View.GONE
        mImageViewClear?.setOnClickListener(this)

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
            return@setOnEditorActionListener true
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



        setLogo(typedArray.getInteger(R.styleable.MaterialSearchView_search_logo, Logo.ANIMATION))



        mViewShadowBackground = findViewById(R.id.search_view_shadow_background)
        mViewShadowBackground?.visibility = View.GONE
        mViewShadowBackground?.setBackgroundColor(ContextCompat.getColor(context, R.color.search_shadow))

        mViewShadowForeground = findViewById(R.id.search_view_shadow_foreground)
        mViewShadowForeground ?.visibility = View.GONE
        mViewShadowForeground ?.setOnClickListener(this)
        mViewShadowForeground?.setBackgroundColor(mMaterialCardView?.cardBackgroundColor!!.defaultColor)











        // TODO chose let or .... JETPACK KTX ROOM
        // TODO PROJIT SEARCHVIEW V7 METODY A INTERFACES
        // TODO ROOM, LINT, SWIPERFESH, CHILD PARAMETR, ANIMACE, PROMENNE GRADLE // OVERRDES A DO KOTLINU A UPRAVIT KOTLINPROJITsearch_{
        // TODO colorres a color int
        // todo companion object ===, ::, striska erovn a se, ?:,
        //setMicResource()
        // DOPSAT
        // todo zkontrolovat zalomeni + layout


        typedArray.recycle()

        /// todo ===, ?:, ::
        // LAYOUT, FILE PROVIDER, IKONKY, ATD... barvy ...            <!-- ?android:attr/listDivider never-->

        //setShadowColor(ContextCompat.getColor(getContext(), R.color.search_shadow))
        removeMargin()
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
            Logo.ANIMATION -> {
                mMaterialSearchArrowDrawable = MaterialSearchArrowDrawable(context)
                mImageViewLogo?.setImageDrawable(mMaterialSearchArrowDrawable)
            }
        }
    }

    // *****************************************************************************************************************
    fun setDividerColor(@ColorInt color: Int) {
        mViewDivider?.setBackgroundColor(color)
    }

    // *****************************************************************************************************************
    fun getLayoutHeight(): Int {
        val params = mLinearLayout?.layoutParams
        return params?.height!!
    }

    fun setLayoutHeight(height: Int) {
        val params = mLinearLayout?.layoutParams
        params?.height = height
        params?.width = ViewGroup.LayoutParams.MATCH_PARENT
        mLinearLayout?.layoutParams = params
    }

    // *****************************************************************************************************************
    fun setLogoResource(@DrawableRes resource: Int) {
        mImageViewLogo?.setImageResource(resource)
    }

    fun setLogoDrawable(@Nullable drawable: Drawable?) {
        mImageViewLogo?.setImageDrawable(drawable)
    }

    fun setLogoColorFilter(color: Int) {
        mImageViewLogo?.setColorFilter(color)
    }

    fun setLogoColorFilter(colorFilter: ColorFilter?) {
        // val colorFilter = PorterDuffColorFilter(color, PorterDuff.Mode.SRC_IN)
        mImageViewLogo?.colorFilter = colorFilter
    }

    // *****************************************************************************************************************
    fun setMicResource(@DrawableRes resource: Int) {
        mImageViewMic?.setImageResource(resource)
    }

    fun setMicDrawable(@Nullable drawable: Drawable?) {
        mImageViewMic?.setImageDrawable(drawable)
    }

    fun setMicColorFilter(color: Int) {
        mImageViewMic?.setColorFilter(color)
    }

    fun setMicColorFilter(colorFilter: ColorFilter?) {
        mImageViewMic?.colorFilter = colorFilter
    }

    // *****************************************************************************************************************
    fun setClearResource(@DrawableRes resource: Int) {
        mImageViewClear?.setImageResource(resource)
    }

    fun setClearDrawable(@Nullable drawable: Drawable?) {
        mImageViewClear?.setImageDrawable(drawable)
    }

    fun setClearColorFilter(color: Int) {
        mImageViewClear?.setColorFilter(color)
    }

    fun setClearColorFilter(colorFilter: ColorFilter?) {
        mImageViewLogo?.colorFilter = colorFilter
    }

    // *****************************************************************************************************************
    fun setMenuResource(@DrawableRes resource: Int) {
        mImageViewMenu?.setImageResource(resource)
    }

    fun setMenuDrawable(@Nullable drawable: Drawable?) {
        mImageViewMenu?.setImageDrawable(drawable)
    }

    fun setMenuColorFilter(color: Int) {
        mImageViewMenu?.setColorFilter(color)
    }

    fun setMenuColorFilter(colorFilter: ColorFilter?) {
        mImageViewMenu?.colorFilter = colorFilter
    }

    // *****************************************************************************************************************
    fun getQuery(): CharSequence? {
        return mQueryText
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

    // *****************************************************************************************************************
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

    fun setTextInputType(inputType: Int) {
        mMaterialSearchEditText?.inputType = inputType
    }

    fun getTextImeOptions(): Int? {
        return mMaterialSearchEditText?.imeOptions
    }

    fun setTextImeOptions(imeOptions: Int) {
        mMaterialSearchEditText?.imeOptions = imeOptions
    }

    fun getTextInputType(): Int? {
        return mMaterialSearchEditText?.inputType
    }

    fun setTextHint(hint: CharSequence?) {
        mMaterialSearchEditText?.hint = hint
    }

    fun setTextHint(@StringRes hint: Int) {
        mMaterialSearchEditText?.setHint(hint)
    }

    fun setTextHintColor(@ColorInt color: Int) {
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

    // *****************************************************************************************************************
    private fun getAdapter(): RecyclerView.Adapter<*>? {
        return mRecyclerView?.adapter
    }

    fun setAdapter(@Nullable adapter: RecyclerView.Adapter<*>) {
        mRecyclerView?.adapter = adapter
    }

    fun addItemDecoration(@NonNull itemDecoration: RecyclerView.ItemDecoration) {
        mRecyclerView?.addItemDecoration(itemDecoration)
    }

    fun removeItemDecoration(itemDecoration: RecyclerView.ItemDecoration) {
        mRecyclerView?.removeItemDecoration(itemDecoration)
    }

    // todo ANOTACE A NULLABLE projet
    // *****************************************************************************************************************
    override fun setBackgroundColor(@ColorInt color: Int) {
        mMaterialCardView?.setCardBackgroundColor(color)
        mViewShadowForeground?.setBackgroundColor(color)
    }

    fun setBackgroundColor(@Nullable color: ColorStateList?) {
        mMaterialCardView?.setCardBackgroundColor(color)
    }

    fun setStrokeWidth(@Dimension strokeWidth: Int) {
        mMaterialCardView?.strokeWidth = strokeWidth
    }

    fun setStrokeColor(@ColorInt strokeColor: Int) {
        mMaterialCardView?.strokeColor = strokeColor
    }

    fun setRadius(radius: Float) {
        mMaterialCardView?.radius = radius
    }

    override fun setElevation(elevation: Float) {
        mMaterialCardView?.cardElevation = elevation
    }

    // *****************************************************************************************************************
    fun isOpen(): Boolean {
        return visibility == View.VISIBLE
    }

    fun setAnimationDuration(animationDuration: Long) {
        mAnimationDuration = animationDuration
    }

    // *****************************************************************************************************************
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

    // TODO KONTROLA JAK VYPADA VSUDE TO VIEW A PAGEINDICATOR, PADDINGY ATD, MARGINY
    private fun removeMargin() {
        var left = context.resources.getDimensionPixelSize(R.dimen.search_toolbar_margin_left_right)
        var top = context.resources.getDimensionPixelSize(R.dimen.search_toolbar_margin_top_bottom)
        var right = context.resources.getDimensionPixelSize(R.dimen.search_toolbar_margin_left_right)
        var bottom = context.resources.getDimensionPixelSize(R.dimen.search_toolbar_margin_top_bottom)

        val params = FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        params.setMargins(left, top, right, bottom)
        mMaterialCardView?.layoutParams = params

        left = 0
        top = 0
        right = 0
        bottom = 0

        val params2 =
            LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        params2.setMargins(left, top, right, bottom)
        mMaterialSearchEditText?.layoutParams = params2
    }

    private fun addMargin() {
        var left = 0
        val top = 0
        val right = 0
        val bottom = 0

        val params = FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        params.setMargins(left, top, right, bottom)
        mMaterialCardView?.layoutParams = params

        left = context.resources.getDimensionPixelSize(R.dimen.search_key_line_8)

        val params2 =
            LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        params2.setMargins(left, top, right, bottom)
        mMaterialSearchEditText?.layoutParams = params2
    }

    private fun addFocus() {
        mMaterialSearchArrowDrawable?.animate(MaterialSearchArrowDrawable.STATE_ARROW, mAnimationDuration)

        mViewShadowBackground?.visibility = View.VISIBLE
        mViewShadowForeground?.visibility = View.VISIBLE
        mViewDivider?.visibility = View.VISIBLE

        addMargin()
        setRadius(context.resources.getDimensionPixelSize(R.dimen.search_corner_radius_focused).toFloat())
        setLayoutHeight(context.resources.getDimensionPixelSize(R.dimen.search_layout_height_focused))
        elevation = context.resources.getDimensionPixelSize(R.dimen.search_elevation_focused).toFloat()

        //setMicOrClearIcon(true)
        //filter(mQueryText)

        mOnOpenCloseListener?.onOpen()
        postDelayed({ showKeyboard() }, mAnimationDuration)

        /*val anim = ScaleAnimation(0f, 1f, 0f, 1f)//,  Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0f)
        anim.duration = mAnimationDuration
        //anim.fillAfter = true
        mMaterialCardView?.startAnimation(anim)*/
    }

    private fun removeFocus() {
        mMaterialSearchArrowDrawable?.animate(MaterialSearchArrowDrawable.STATE_HAMBURGER, mAnimationDuration)

        mViewShadowBackground?.visibility = View.GONE
        mViewShadowForeground?.visibility = View.GONE
        mViewDivider?.visibility = View.GONE

        removeMargin()
        setRadius(context.resources.getDimensionPixelSize(R.dimen.search_corner_radius_default).toFloat())
        setLayoutHeight(context.resources.getDimensionPixelSize(R.dimen.search_layout_height_default))
        elevation = context.resources.getDimensionPixelSize(R.dimen.search_elevation_default).toFloat()

        //HideSuggestions()
        //setMicOrClearIcon(false)

        hideKeyboard()
        postDelayed({
            mOnOpenCloseListener?.onClose()
        }, mAnimationDuration)

        /*val anim = ScaleAnimation(1f, 0f, 1f, 0f)//,  Animation.RELATIVE_TO_SELF, 1f, Animation.RELATIVE_TO_SELF, 1f)
        anim.duration = mAnimationDuration
        //anim.fillAfter = true interpolator
        mMaterialCardView?.startAnimation(anim)*/
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

    // todo @colorres
    // TODO PROJIT VSECHNY METODY, ? ATD
    /*private fun clearIconsColor() {
        mImageViewLogo?.clearColorFilter()
        mImageViewMic?.clearColorFilter()
        mImageViewClear?.clearColorFilter()
    }*/

    private fun onSubmitQuery() {
        val query = mMaterialSearchEditText?.text
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
            mOnQueryTextListener?.onQueryTextChange(mQueryText)
        }
    }


    // close s nastavenim


    override fun requestFocus(direction: Int, previouslyFocusedRect: Rect?): Boolean {

        mMaterialSearchEditText?.requestFocus()
        mMaterialSearchEditText?.clearFocus()
        // PROJIT KLASICKE SEARCHVIEW TODO
        /*
        if (!isIconified()) {
            val result = mSearchSrcTextView.requestFocus(direction, previouslyFocusedRect)
            if (result) {
                updateViewsVisibility(false)
            }
            return result
        } else {
            return super.requestFocus(direction, previouslyFocusedRect)
        }*/
        return false
    }

    override fun clearFocus() {
        /*
        super.clearFocus()
        mMaterialSearchEditText?.clearFocus()
        mMaterialSearchEditText.setImeVisibility(false)*/
    }

    // todo tadycleanup code. projit anotace
    override fun onSaveInstanceState(): Parcelable? {
        val superState = super.onSaveInstanceState()
        val ss = MaterialSearchViewSavedState(superState!!)
        ss.hasFocus = mMaterialSearchEditText?.hasFocus()!!
        ss.shadowVisibility = mViewShadowForeground?.visibility!!
        ss.query = mQueryText.toString()
        return ss
    }

    override fun onRestoreInstanceState(state: Parcelable?) {
        if (state !is MaterialSearchViewSavedState) {
            super.onRestoreInstanceState(state)
            return
        }
        super.onRestoreInstanceState(state.superState)
        mViewShadowForeground?.visibility = state.shadowVisibility
        if (state.hasFocus) {
            //open()
        }
        if (state.query != null) {
            setText(state.query!!)
        }
        requestLayout()
    }

    override fun onClick(v: View?) {
        if (v == mImageViewLogo) {
            if (mMaterialSearchEditText?.hasFocus()!!) {
                mMaterialSearchEditText?.clearFocus()
            } else {
                if (mOnLogoClickListener != null) {
                    mOnLogoClickListener?.onLogoClick()
                }
            }
        } else if (v == mImageViewMic) {
            if (mOnMicClickListener != null) {
                mOnMicClickListener?.onMicClick()
            }
        } else if (v == mImageViewClear) {
            if (mMaterialSearchEditText?.length()!! > 0) {
                mMaterialSearchEditText?.text!!.clear()
            }
        } else if (v == mImageViewMenu) {
            if (mOnMenuClickListener != null) {
                mOnMenuClickListener?.onMenuClick()
            }
        } else if (v == mViewShadowForeground) {
            mMaterialSearchEditText?.clearFocus()
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

    // *****************************************************************************************************************
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

        fun onOpen(): Boolean

        fun onClose(): Boolean
    }

    interface OnQueryTextListener {

        fun onQueryTextSubmit(query: CharSequence?): Boolean

        fun onQueryTextChange(newText: CharSequence?): Boolean
    }

    // *****************************************************************************************************************
    @IntDef(Logo.HAMBURGER, Logo.ARROW, Logo.ANIMATION)
    @Retention(AnnotationRetention.SOURCE)
    annotation class Logo {
        companion object {
            const val HAMBURGER = 100
            const val ARROW = 101
            const val ANIMATION = 102
        }
    }

}


/*when (mVersion) {
    Version.TOOLBAR ->
    Version.MENU_ITEM -> {
        visibility = View.VISIBLE
        /*if (mMenuItem != null) {
            getMenuItemPosition(mMenuItem.getItemId())
        }*/

        val viewTreeObserver = mMaterialCardView?.viewTreeObserver
        if (viewTreeObserver?.isAlive!!) {
            viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
                override fun onGlobalLayout() {

                    MaterialSearchAnimator.revealOpen(
                        context,
                        mMaterialCardView,
                        mMenuItemCx,
                        mAnimationDuration,
                        mMaterialSearchEditText,
                        mOnOpenCloseListener
                    )

                    mMaterialCardView?.viewTreeObserver?.removeOnGlobalLayoutListener(this)
                }
            })
        }
    }*/


/*when (mVersion) {
    Version.TOOLBAR ->
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
            this,
            mOnOpenCloseListener
        )
    }
}*/

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

/*setLogo(a.getInteger(R.styleable.SearchView_search_logo, MaterialUtils.Logo.Companion.getHAMBURGER_TO_ARROW_ANIMATION()));
setShape(a.getInteger(R.styleable.SearchView_search_shape, MaterialUtils.Shape.Companion.getCLASSIC()));
setTheme(a.getInteger(R.styleable.SearchView_search_theme, MaterialUtils.Theme.Companion.getLIGHT()));
setVersionMargins(a.getInteger(R.styleable.SearchView_search_version_margins, MaterialUtils.VersionMargins.Companion.getTOOLBAR()));
setVersion(a.getInteger(R.styleable.SearchView_search_version, MaterialUtils.Version.Companion.getTOOLBAR()));

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

    /*private fun getCenterX(view: View): Int {
        val location = IntArray(2)
        view.getLocationOnScreen(location)
        return location[0] + view.width / 2
    }*/

//setSaveEnabled(true);*/


/**
 * @param listener
 */
// @FloatRange(from = 0.5, to = 1.0)
// Listeners


// BOTTOM NAV ACTIVITY  A SAMPLES
