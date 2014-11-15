package com.moods.bikersrides.common;

import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.view.ActionMode;
import android.util.SparseBooleanArray;
import android.view.Menu;
import android.view.MenuItem;

import com.moods.bikersrides.R;

/**
 * Class is implementing classic Android pattern Contextual Action Bar.
 * For usage basic fragment has to extend this class
 */
public abstract class CabFragment extends Fragment {

    private CabAdapter mBaseAdapter;
    protected ActionMode mActionMode;

    public void onListItemSelect(int position) {

        mBaseAdapter.toggleSelection(position);
        boolean hasCheckedItems = mBaseAdapter.getSelectedCount() > 0;

        if (hasCheckedItems && mActionMode == null)
            // there are some selected items, start the actionMode
            mActionMode = ((ActionBarActivity) getActivity()).startSupportActionMode(new ActionModeCallback());
        else if (!hasCheckedItems && mActionMode != null)
            // there no selected items, finish the actionMode
            mActionMode.finish();

        if (mActionMode != null)
            mActionMode.setTitle(String.valueOf(mBaseAdapter
                    .getSelectedCount()) + " selected");
    }

    private class ActionModeCallback implements ActionMode.Callback {

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            // inflate contextual menu
            mode.getMenuInflater().inflate(R.menu.context_main, menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {

            switch (item.getItemId()) {
                case R.id.menu_delete:
                    // retrieve selected items and delete them out
                    SparseBooleanArray selected = mBaseAdapter
                            .getSelectedIds();
                    for (int i = (selected.size() - 1); i >= 0; i--) {
                        if (selected.valueAt(i)) {
                            onContextualActionBarItemClicked(selected, i);
                            mBaseAdapter.notifyDataSetChanged();
                        }
                    }
                    mode.finish(); // Action picked, so close the CAB
                    return true;
                default:
                    return false;
            }

        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            // remove selection
            mBaseAdapter.removeSelection();
            mActionMode = null;
        }
    }

    /**
     * @param selected
     * @param position Subclass must implement method for defining action on button delete
     */
    protected abstract void onContextualActionBarItemClicked(SparseBooleanArray selected, int position);

    public CabAdapter getmBaseAdapter() {
        return mBaseAdapter;
    }

    /**
     *
     * @param mBaseAdapter
     * Subclass must define appropriate adapter
     */
    public void setmBaseAdapter(CabAdapter mBaseAdapter) {
        this.mBaseAdapter = mBaseAdapter;
    }
}
