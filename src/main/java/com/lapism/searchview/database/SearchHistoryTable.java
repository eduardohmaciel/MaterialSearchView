package com.lapism.searchview.database;

import android.content.Context;
import androidx.annotation.NonNull;
import com.lapism.searchview.widget.SearchItem;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;


public class SearchHistoryTable {

    @NonNull
    private final WeakReference<Context> mContext;

    public SearchHistoryTable(Context context) {
        mContext = new WeakReference<>(context);
    }

    public void addItem(@NonNull SearchItem item) {

    }

    public List<SearchItem> getAllItems() {
        return new ArrayList<>();
    }

    public void clearDatabase() {

    }

}
