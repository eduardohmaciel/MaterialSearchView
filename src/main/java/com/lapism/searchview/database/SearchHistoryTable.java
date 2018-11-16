package com.lapism.searchview.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import com.lapism.searchview.R;
import com.lapism.searchview.widget.SearchItem;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;


public class SearchHistoryTable {

    @NonNull
    private final WeakReference<Context> mContext;

    public SearchHistoryTable(Context context) {
        mContext = new WeakReference<>(context);
    }

    public void addItem(@NonNull SearchItem item) {

    }

    public List<SearchItem> getAllItems() {

    }

    public void clearDatabase() {

    }

}
