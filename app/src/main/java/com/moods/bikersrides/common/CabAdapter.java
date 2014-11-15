package com.moods.bikersrides.common;


import android.content.Context;
import android.util.SparseBooleanArray;
import android.widget.ArrayAdapter;

import java.util.List;

/**
 * CabAdapter is implementing classic Android pattern Contextual Action Bar for selecting, deselecting items in list
 * For usage custom array adapter has to extend this class
 */
public abstract class CabAdapter extends ArrayAdapter {

    protected SparseBooleanArray mSelectedItemsIds;

    public CabAdapter(Context context, int resource, List objects) {
        super(context, resource, objects);
        mSelectedItemsIds = new SparseBooleanArray();
    }

    public void toggleSelection(int position) {
        selectView(position, !mSelectedItemsIds.get(position));
    }

    public void removeSelection() {
        mSelectedItemsIds = new SparseBooleanArray();
        notifyDataSetChanged();
    }

    private void selectView(int position, boolean value) {
        if (value)
            mSelectedItemsIds.put(position, value);
        else
            mSelectedItemsIds.delete(position);

        notifyDataSetChanged();
    }

    public int getSelectedCount() {
        return mSelectedItemsIds.size();
    }

    public SparseBooleanArray getSelectedIds() {
        return mSelectedItemsIds;
    }
}
