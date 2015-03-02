package timersassignment.simplegallery.image;

import android.content.Context;
import android.database.Cursor;
import android.util.SparseBooleanArray;
import android.view.View;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * This fragment gives ImageListAdapter multiple check interface functions
 *
 * @author Byungchul, Cho
 * @version 1.0
 */
public class CheckableImageListAdapter extends ImageListAdapter
        implements View.OnClickListener{
    private boolean mCheckMode;
    private HashMap<Long, Boolean> mCheckedStateMap;
    private SparseBooleanArray mArray;

    public CheckableImageListAdapter(Context context, boolean showTitle) {
        super(context, showTitle);
        mCheckedStateMap = new HashMap<Long, Boolean>();
    }

    public void setCheckState(HashMap<Long, Boolean> checkState) {
        mCheckedStateMap = checkState;
        notifyDataSetChanged();
    }

    public HashMap<Long, Boolean> getCheckState() {
        return mCheckedStateMap;
    }

    public ArrayList<Integer> getCheckedImageList() {
        ArrayList<Integer> mCheckedIds = new ArrayList<Integer>();
        for(Long id : mCheckedStateMap.keySet()) {
            if(mCheckedStateMap.get(id)) {
                mCheckedIds.add(id.intValue());
            }
        }
        return mCheckedIds;
    }

    @Override
    public void changeCursor(Cursor cursor) {
        updateCheckStates(cursor);
        super.changeCursor(cursor);
    }

    private void updateCheckStates(Cursor cursor) {
        if(cursor == null || cursor.getCount() == 0) {
            mCheckedStateMap.clear();
            return;
        }

        HashMap<Long, Boolean> newCheckState = new HashMap<Long, Boolean>();

        cursor.moveToPosition(-1);
        while(cursor.moveToNext()) {
            Long id = cursor.getLong(ImageTable.COLUMN_INDEX_ID);
            if(mCheckedStateMap.get(id)) {
                newCheckState.put(id, true);
            }
        }

        mCheckedStateMap = newCheckState;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor, long id) {
        if(view instanceof ImageGridItemView) {
            if(mCheckMode) {
                ((ImageGridItemView)view).setChecked(isChecked(id));
            } else {
                ((ImageGridItemView)view).setCheckBox(false);
            }
        }
    }

    public void setCheckMode(boolean checkMode) {
        if(mCheckMode != checkMode) {
            mCheckMode = checkMode;
            notifyDataSetChanged();
        }
    }

    public boolean isCheckMode() {
        return mCheckMode;
    }

    @Override
    public void onClick(View v) {
        ImageItem item  = (ImageItem)v.getTag();
        long id = item.id;
        if(v instanceof ImageGridItemView) {
            ((ImageGridItemView)v).setChecked(true);
        }
    }

    public void checkId(long id) {
        mCheckedStateMap.put(id, !isChecked(id));
        notifyDataSetChanged();
    }

    private boolean isChecked(Long id) {
        Boolean isChecked = mCheckedStateMap.get(id);
        return isChecked != null && mCheckedStateMap.get(id);
    }

}
