package timersassignment.simplegallery.image;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.MediaStore;
import android.util.Log;

import timersassignment.simplegallery.save.ImageDatabaseHelper;

/**
 *
 * Load Image informations from an internal database
 *
 * @author Byungchul, Cho
 * @version 1.0
 */
public class InternalImageLoader extends AsyncTaskLoader<Cursor> implements ImageTable {
    private static final String TAG = "InternalImageLoader";

    private long mId = -1;

    public InternalImageLoader(Context context) {
        super(context);
    }

    public InternalImageLoader(Context context, long id) {
        super(context);
        mId = id;
    }
    @Override
    public Cursor loadInBackground() {
        Log.v(TAG, "Internal image loading...");
        ImageDatabaseHelper helper = new ImageDatabaseHelper(getContext());
        SQLiteDatabase db = helper.getReadableDatabase();

        String selection = null;
        String[] selectionArgs = null;
        if(mId > -1) {
            selection = getSelection();
            selectionArgs = getSelectionArgs();
        }

        return db.query(ImageDatabaseHelper.IMAGE_TABLE.TABLE_NAME, IMAGE_PROJECTION,
                selection, selectionArgs, null, null, null);
    }
    @Override
    protected void onStartLoading() {
        super.onStartLoading();
        forceLoad();
    }

    private String getSelection() {
        StringBuilder builder = new StringBuilder();
        builder.append(MediaStore.Images.Media._ID + "=?");
        return builder.toString();
    }

    private String[] getSelectionArgs() {
        return new String[] { String.valueOf(mId) };
    }
}
