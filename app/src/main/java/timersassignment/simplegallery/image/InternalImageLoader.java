package timersassignment.simplegallery.image;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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
    public InternalImageLoader(Context context) {
        super(context);
    }
    @Override
    public Cursor loadInBackground() {
        Log.v(TAG, "Internal image loading...");
        ImageDatabaseHelper helper = new ImageDatabaseHelper(getContext());
        SQLiteDatabase db = helper.getReadableDatabase();
        return db.query(ImageDatabaseHelper.IMAGE_TABLE.TABLE_NAME, IMAGE_PROJECTION,
                null, null, null, null, null);
    }
    @Override
    protected void onStartLoading() {
        super.onStartLoading();
        forceLoad();
    }
}
