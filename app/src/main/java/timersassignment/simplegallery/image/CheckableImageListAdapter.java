package timersassignment.simplegallery.image;

import android.content.Context;
import android.database.Cursor;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.HashMap;

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
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return super.newView(context, cursor, parent);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor, long id) {
        if(view instanceof ImageGridItemView) {
            Boolean isChecked = mCheckedStateMap.containsKey(id);

            ((ImageGridItemView)view).setCheckBox(mCheckMode);
            ((ImageGridItemView)view).setChecked(isChecked(id));
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
        if(isChecked(id)) {
            mCheckedStateMap.put(id, false);
        } else {
            mCheckedStateMap.put(id, true);
        }
        notifyDataSetChanged();
    }

    private boolean isChecked(Long id) {
        Boolean isChecked = mCheckedStateMap.get(id);
        return isChecked != null && mCheckedStateMap.get(id);
    }

}
