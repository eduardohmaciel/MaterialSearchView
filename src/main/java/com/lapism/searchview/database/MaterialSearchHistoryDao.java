package com.lapism.searchview.database;

import android.content.Context;
import androidx.annotation.NonNull;
import com.lapism.searchview.widget.MaterialSearchItem;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;


public class MaterialSearchHistoryDao {

    // MaterialSearchHistoryDatabase
    @NonNull
    private final WeakReference<Context> mContext;

    public MaterialSearchHistoryDao(Context context) {
        mContext = new WeakReference<>(context);
    }

    public void addItem(@NonNull MaterialSearchItem item) {

    }

    public List<MaterialSearchItem> getAllItems() {
        return new ArrayList<>();
    }

    public void clearDatabase() {

    }

}
