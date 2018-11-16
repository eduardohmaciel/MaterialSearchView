package com.lapism.searchview.widget

import android.content.Context
import android.os.Parcelable
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.View
import android.view.View.OnFocusChangeListener
import android.view.ViewTreeObserver
import android.view.inputmethod.InputMethodManager
import android.widget.Filter
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.lapism.searchview.R
import com.lapism.searchview.internal.SearchEditText


class SearchViewX : FrameLayout, View.OnClickListener, Filter.FilterListener, CoordinatorLayout.AttachedBehavior {

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
    private var mRecyclerView: RecyclerView? = null
    private var mMaterialCardView: MaterialCardView? = null

    constructor(context: Context) : super(context) {}

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {}

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {}

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes) {}

    init {
        inflate(context, R.layout.search_view, this)

        // TODO chose let or ....
        mViewShadow = findViewById(R.id.search_view_shadow)
        mViewShadow?.visibility = View.GONE
        mViewShadow?.setOnClickListener(this@SearchViewX) // todo

        mViewDivider = findViewById(R.id.search_view_divider)
        mViewDivider?.visibility = View.GONE

        mLinearLayout = findViewById(R.id.search_linearLayout)

        mImageViewLogo = findViewById(R.id.search_imageView_logo)
        mImageViewLogo?.setOnClickListener(this)

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
            override  fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                if (newState == RecyclerView.SCROLL_STATE_DRAGGING) {
                    //hideKeyboard()
                }
            }

            override  fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {

            }
        })

        mMaterialCardView = findViewById(R.id.search_materialCardView)
        val viewTreeObserver = mMaterialCardView?.viewTreeObserver
        viewTreeObserver?.let {
            if(it.isAlive){
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


        // LAYOUT, FILE PROVIDER, IKONKY, ATD... barvy ...            <!-- ?android:attr/listDivider never-->

    }
    //  TODO prevest do init, kouknout do searchview v 7

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


    override fun onSaveInstanceState(): Parcelable? {
        return super.onSaveInstanceState()
    }

    override fun onRestoreInstanceState(state: Parcelable?) {
        super.onRestoreInstanceState(state)
    }

    override fun onClick(v: View?) {

    }

    override fun onFilterComplete(count: Int) {

    }

    override fun getBehavior(): CoordinatorLayout.Behavior<*> {
        return SearchBehavior()
    }

}
