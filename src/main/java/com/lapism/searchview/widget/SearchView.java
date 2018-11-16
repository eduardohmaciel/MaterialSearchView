package com.lapism.searchview.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Parcelable;
import android.text.Editable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.google.android.material.card.MaterialCardView;
import com.lapism.searchview.R;
import com.lapism.searchview.Search;
import com.lapism.searchview.graphics.SearchAnimator;
import com.lapism.searchview.graphics.SearchArrowDrawable;
import com.lapism.searchview.internal.SearchEditText;
import com.lapism.searchview.internal.SearchViewSavedState;

import java.util.Objects;

import androidx.annotation.AttrRes;
import androidx.annotation.ColorInt;
import androidx.annotation.Dimension;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.annotation.StyleRes;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


public class SearchView extends FrameLayout{






    private CharSequence mQueryText = "";

    private int mMenuItemCx = -1;
    private boolean mShadow;
    private long mAnimationDuration;
    private MenuItem mMenuItem;



    // ---------------------------------------------------------------------------------------------
    private int getCenterX(@NonNull View view) {
        int[] location = new int[2];
        view.getLocationOnScreen(location);
        return location[0] + view.getWidth() / 2;
    }

    // ---------------------------------------------------------------------------------------------
    private void onTextChanged(CharSequence s) {
        mQueryText = s;

        setMicOrClearIcon(true);
        filter(s);

        if (mOnQueryTextListener != null) {
            mOnQueryTextListener.onQueryTextChange(mQueryText);
        }
    }

    private void addFocus() {
        filter(mQueryText);

        if (mShadow) {
            SearchAnimator.INSTANCE.fadeOpen(mViewShadow, mAnimationDuration);
        }

        setMicOrClearIcon(true);

        setLogoHamburgerToLogoArrowWithAnimation(true);

        if (mVersion == Search.Version.Companion.getTOOLBAR()) {
            // todo SavedState, marginy kulate a barva divideru
            if (mOnOpenCloseListener != null) {
                mOnOpenCloseListener.onOpen();
            }
        }

        postDelayed(new Runnable() {
            @Override
            public void run() {
                showKeyboard();
            }
        }, mAnimationDuration);
    }

    private void removeFocus() {
        if (mShadow) {
            SearchAnimator.INSTANCE.fadeClose(mViewShadow, mAnimationDuration);
        }

        //setTextImageVisibility(false); todo error + shadow error pri otoceni, pak mizi animace
        hideSuggestions();
        hideKeyboard();
        setMicOrClearIcon(false);

        setLogoHamburgerToLogoArrowWithAnimation(false); // TODO

        if (mVersion == Search.Version.Companion.getTOOLBAR()) {
            postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (mOnOpenCloseListener != null) {
                        mOnOpenCloseListener.onClose();
                    }
                }
            }, mAnimationDuration);
        }
    }

    protected void open() {
        open(null);
    }

    public void close() {
        switch (mVersion) {
            case Search.Version.Companion.getTOOLBAR():
                mSearchEditText.clearFocus();
                break;
            case Search.Version.Companion.getMENU_ITEM():
                if (mMenuItem != null) {
                    getMenuItemPosition(mMenuItem.getItemId());
                }
                SearchAnimator.INSTANCE.revealClose(
                        getContext(),
                        mMaterialCardView,
                        mMenuItemCx,
                        mAnimationDuration,
                        mSearchEditText,
                        this,
                        mOnOpenCloseListener);
                break;
        }
    }


    public void init(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {


        //mRecyclerView.setLayoutManager(new LinearLayoutManager(context));
        //mRecyclerView.setItemAnimator(new DefaultItemAnimator());


        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.SearchView, defStyleAttr, defStyleRes);

        setLogo(a.getInteger(R.styleable.SearchView_search_logo, Search.Logo.Companion.getHAMBURGER_TO_ARROW_ANIMATION()));
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
        setShadowColor(a.getColor(R.styleable.SearchView_search_shadow_color, ContextCompat.getColor(context, R.color.search_shadow)));

        if (a.hasValue(R.styleable.SearchView_search_elevation)) {
            setElevation(a.getDimensionPixelSize(R.styleable.SearchView_search_elevation, 0));
        }

        a.recycle();

        //setSaveEnabled(true);

    }




    public void setDividerColor(@ColorInt int color) {
        mViewDivider.setBackgroundColor(color);
    }

    public void setClearIcon(@DrawableRes int resource) {
        mImageViewClear.setImageResource(resource);
    }

    public void setClearIcon(@Nullable Drawable drawable) {
        mImageViewClear.setImageDrawable(drawable);
    }

    public void setClearColor(@ColorInt int color) {
        mImageViewClear.setColorFilter(color);
    }

    public void setTextImage(@DrawableRes int resource) {
        mImageViewImage.setImageResource(resource);
        setTextImageVisibility(false);
    }

    public void setTextImage(@Nullable Drawable drawable) {
        mImageViewImage.setImageDrawable(drawable);
        setTextImageVisibility(false);
    }

    public void setAnimationDuration(long animationDuration) {
        mAnimationDuration = animationDuration;
    }

    public void setShadow(boolean shadow) {
        mShadow = shadow;
    }

    public void setShadowColor(@ColorInt int color) {
        mViewShadow.setBackgroundColor(color);
    }



    public void open(MenuItem menuItem) {
        mMenuItem = menuItem;

        switch (mVersion) {
            case Search.Version.Companion.getTOOLBAR():
                mSearchEditText.requestFocus();
                break;
            case Search.Version.Companion.getMENU_ITEM():
                setVisibility(View.VISIBLE);
                if (mMenuItem != null) {
                    getMenuItemPosition(mMenuItem.getItemId());
                }
                break;
        }
    }

    public void setLogoHamburgerToLogoArrowWithAnimation(boolean animate) {
        if (mSearchArrowDrawable != null) {
            if (animate) {
                mSearchArrowDrawable.setVerticalMirror(false);
                mSearchArrowDrawable.animate(SearchArrowDrawable.STATE_ARROW, mAnimationDuration);
            } else {
                mSearchArrowDrawable.setVerticalMirror(true);
                mSearchArrowDrawable.animate(SearchArrowDrawable.STATE_HAMBURGER, mAnimationDuration);
            }
        }
    }

    public void setLogoHamburgerToLogoArrowWithoutAnimation(boolean animation) {
        if (mSearchArrowDrawable != null) {
            if (animation) {
                mSearchArrowDrawable.setProgress(SearchArrowDrawable.STATE_ARROW);
            } else {
                mSearchArrowDrawable.setProgress(SearchArrowDrawable.STATE_HAMBURGER);
            }
        }
    }


    // ---------------------------------------------------------------------------------------------
    private void setMicOrClearIcon(boolean hasFocus) {
        if (hasFocus && !TextUtils.isEmpty(mQueryText)) {
            if (mOnMicClickListener != null) {
                mImageViewMic.setVisibility(View.GONE);
            }
            mImageViewClear.setVisibility(View.VISIBLE);
        } else {
            mImageViewClear.setVisibility(View.GONE);
            if (mOnMicClickListener != null) {
                mImageViewMic.setVisibility(View.VISIBLE);
            }
        }
    }



    private void filter(CharSequence s) {
        if (getAdapter() != null && getAdapter() instanceof Filterable) {
            ((Filterable) getAdapter()).getFilter().filter(s, this);
        }
    }

    private void showSuggestions() {
        if (getAdapter() != null && getAdapter().getItemCount() > 0) {
            mViewDivider.setVisibility(View.VISIBLE);
            mRecyclerView.setVisibility(View.VISIBLE);
        }
    }

    private void hideSuggestions() {
        if (getAdapter() != null && getAdapter().getItemCount() > 0) {
            mViewDivider.setVisibility(View.GONE);
            mRecyclerView.setVisibility(View.GONE);
        }
    }

    private void getMenuItemPosition(int menuItemId) {
        if (mMenuItemView != null) {
            mMenuItemCx = getCenterX(mMenuItemView);
        }
        ViewParent viewParent = getParent();
        if (viewParent != null) {
            while (viewParent instanceof View) {
                View parent = (View) viewParent;
                View view = parent.findViewById(menuItemId);
                if (view != null) {
                    mMenuItemView = view;
                    mMenuItemCx = getCenterX(mMenuItemView);
                    break;
                }
                viewParent = viewParent.getParent();
            }
        }
    }

    // ---------------------------------------------------------------------------------------------
    @Nullable
    @Override
    protected Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        SearchViewSavedState ss = new SearchViewSavedState(superState);
        ss.setHasFocus(mSearchEditText.hasFocus());
        ss.setShadow(mShadow);
        ss.setQuery(mQueryText); // TODO
        return ss;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (!(state instanceof SearchViewSavedState)) {
            super.onRestoreInstanceState(state);
            return;
        }
        SearchViewSavedState ss = (SearchViewSavedState) state;
        super.onRestoreInstanceState(ss.getSuperState());
        mShadow = ss.getShadow();
        if (mShadow) {
            mViewShadow.setVisibility(View.VISIBLE);
        }
        if (ss.getHasFocus()) {
            open();
        }
        if (ss.getQuery() != null) {
            setText(ss.getQuery());
        }
        requestLayout();
    }






    public void setMenuIcon(@DrawableRes int resource) {
        mImageViewMenu.setImageResource(resource);
    }

    public void setMenuIcon(@Nullable Drawable drawable) {
        mImageViewMenu.setImageDrawable(drawable);
    }

    public void setMenuColor(@ColorInt int color) {
        mImageViewMenu.setColorFilter(color);
    }

    // Text
    public void setTextImeOptions(int imeOptions) {
        mSearchEditText.setImeOptions(imeOptions);
    }

    public void setTextInputType(int inputType) {
        mSearchEditText.setInputType(inputType);
    }

    @Nullable
    public Editable getText() {
        return mSearchEditText.getText();
    }

    public void setText(@StringRes int text) {
        mSearchEditText.setText(text);
    }

    public void setText(CharSequence text) {
        mSearchEditText.setText(text);
    }

    public void setTextColor(@ColorInt int color) {
        mSearchEditText.setTextColor(color);
    }

    public void setTextSize(float size) {
        mSearchEditText.setTextSize(size);
    }




    // Use Gravity or GravityCompat
    public void setTextGravity(int gravity) {
        mSearchEditText.setGravity(gravity);
    }

    public void setHint(CharSequence hint) {
        mSearchEditText.setHint(hint);
    }

    public void setHint(@StringRes int hint) {
        mSearchEditText.setHint(hint);
    }

    public void setHintColor(@ColorInt int color) {
        mSearchEditText.setHintTextColor(color);
    }


    @Nullable
    public Editable getQuery() {
        return mSearchEditText.getText();
    }

    public void setQuery(@Nullable CharSequence query, boolean submit) {
        mSearchEditText.setText(query);
        if (query != null) {
            mSearchEditText.setSelection(mSearchEditText.length());
            mQueryText = query;
        }

        if (submit && !TextUtils.isEmpty(query)) {
            onSubmitQuery();
        }
    }

    public void setQuery(@StringRes int query, boolean submit) {
        mSearchEditText.setText(query);
        if (query != 0) {
            mSearchEditText.setSelection(mSearchEditText.length());
            mQueryText = String.valueOf(query);
        }

        if (submit && !(String.valueOf(query).isEmpty())) {
            onSubmitQuery();
        }
    }

    public int getCustomHeight() {
        ViewGroup.LayoutParams params = mLinearLayout.getLayoutParams();
        return params.height;
    }

    public void setCustomHeight(int height) {
        ViewGroup.LayoutParams params = mLinearLayout.getLayoutParams();
        params.height = height;
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        mLinearLayout.setLayoutParams(params);
    }

    // MaterialCardView


    // Overrides


    @Override
    public void setBackgroundColor(@ColorInt int color) {
        mMaterialCardView.setCardBackgroundColor(color);
    }

    // @FloatRange(from = 0.5, to = 1.0)
    public boolean isOpen() {
        return getVisibility() == View.VISIBLE;
    }

    // Listeners
    public void setOnLogoClickListener(Search.OnLogoClickListener listener) {
        mOnLogoClickListener = listener;
    }

    /**
     * @param listener
     */
    public void setOnOpenCloseListener(Search.OnOpenCloseListener listener) {
        mOnOpenCloseListener = listener;
    }


    public void setOnMicClickListener(Search.OnMicClickListener listener) {
        mOnMicClickListener = listener;
        if (mOnMicClickListener != null) {
            mImageViewMic.setVisibility(View.VISIBLE);
        } else {
            mImageViewMic.setVisibility(View.GONE);
        }
    }

    public void setOnMenuClickListener(Search.OnMenuClickListener listener) {
        mOnMenuClickListener = listener;
        if (mOnMenuClickListener != null) {
            mImageViewMenu.setVisibility(View.VISIBLE);
        } else {
            mImageViewMenu.setVisibility(View.GONE);
        }
    }

    public void setOnQueryTextListener(Search.OnQueryTextListener listener) {
        mOnQueryTextListener = listener;
    }



    private void clearIconsColor() {
        mImageViewLogo.clearColorFilter();
        mImageViewMic.clearColorFilter();
        if (mImageViewClear != null) {
            mImageViewClear.clearColorFilter();
        }
    }

    private void onSubmitQuery() {
        CharSequence query = mSearchEditText.getText();
        if (query != null && TextUtils.getTrimmedLength(query) > 0) {
            if (mOnQueryTextListener == null || !mOnQueryTextListener.onQueryTextSubmit(query.toString())) {
                mSearchEditText.setText(query);
            }
        }
    }

}

