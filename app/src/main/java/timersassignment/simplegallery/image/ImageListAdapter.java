package timersassignment.simplegallery.image;

import android.content.Context;
import android.database.Cursor;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.CursorAdapter;
import android.widget.FrameLayout;

import java.util.Map;

import timersassignment.simplegallery.Constants;
import timersassignment.simplegallery.GalleryStorage;
import timersassignment.simplegallery.R;

/**
 *
 * Adpater Set to GridView and express Image Items
 *
 * @author Byungchul, Cho
 * @version 1.0
 */
public class ImageListAdapter extends CursorAdapter {
    private Context mContext;
    private LayoutInflater mInflater;
    private Map<String, Object> mTitleMap;
    private boolean mShowTitle;

    public class ImageItem {
        public long id;
        public String imageName;
        public String imagePath;
        public int orientation;
    }

    public ImageListAdapter(Context context, boolean showTitle) {
        super(context, null, FLAG_REGISTER_CONTENT_OBSERVER);
        mContext = context;
        mInflater = LayoutInflater.from(context);
        mShowTitle = showTitle;
    }

    @Override
    public long getItemId(int position) {
        getCursor().moveToPosition(position);
        return getCursor().getLong(ImageLoader.COLUMN_INDEX_ID);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        int widthPixels = ImageUtils.getDisplayWidthPixel(context);
        int columnPixels = widthPixels / context.getResources().getInteger(R.integer.gallery_column_count);
        ImageGridItemView view = new ImageGridItemView(mContext);
        view.setLayoutParams(new FrameLayout.LayoutParams(columnPixels, columnPixels));
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
//        ImageView imageView = (ImageView)view.findViewById(R.id.image);
//        ImageItem item = getImageItem(cursor);
//        CachedImageLoader.getInstance().loadImage(imageView, item.imagePath, item.orientation);

        if(view instanceof ImageGridItemView) {
            ImageItem item = getImageItem(cursor);
            ImageGridItemView gridItemView = (ImageGridItemView)view;
            gridItemView.setPhoto(item.imagePath, item.orientation);
            gridItemView.setTitle(item.imageName);
            bindView(view, context, cursor, item.id);
            view.setTag(item);
        }
    }

    public void bindView(View view, Context context, Cursor cursor, long id) {
    }

    private void setTitleMap(Map<String, Object> map) {
        mTitleMap = map;
    }

    private ImageItem getImageItem(Cursor cursor) {
        ImageItem item = new ImageItem();
        item.id = cursor.getLong(ImageTable.COLUMN_INDEX_ID);
        // item.imageName = getImageName(ImageTable.COLUMN_INDEX_DISPLAY_NAME);
        if(mShowTitle) {
            if(Constants.TAG_MODE) {
                item.imageName = getImageName(item.id);
            } else {
                item.imageName = cursor.getString(ImageTable.COLUMN_INDEX_DISPLAY_NAME);
            }
        }
        item.imagePath = cursor.getString(ImageTable.COLUMN_INDEX_DATA);
        item.orientation = cursor.getInt(ImageTable.COLUMN_ORIENTATION);
        return item;
    }

    private String getImageName(long id) {
        String idString = String.valueOf(id);
        if(mTitleMap != null && mTitleMap.containsKey(idString)) {
            Object object = mTitleMap.get(idString);
            if(object instanceof String) {
                return (String)object;
            }
        }
        return null;
    }

    @Override
    public void notifyDataSetChanged() {
        loadMap(mContext);
        super.notifyDataSetChanged();
    }

      private void loadMap(Context context) {
        mTitleMap = GalleryStorage.getAllTitle(context);
    }
}
