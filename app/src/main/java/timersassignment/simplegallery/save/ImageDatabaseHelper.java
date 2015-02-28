package timersassignment.simplegallery.save;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.MediaStore;

/**
 *
 * Manage a database that contains image informations in internal app data folder
 *
 * @author Byungchul, Cho
 * @version 1.0
 */
public class ImageDatabaseHelper extends SQLiteOpenHelper {

    private static final int VERSION = 1;
    private static final String DB_NAME = "imagedata.db";

    public static class IMAGE_TABLE {
        public static final String TABLE_NAME = "internal_image";
    }

    // internal image table uses same column names as Media Class
    // to match interfaces
    private static final String CREATE_DB =
            "create table " + IMAGE_TABLE.TABLE_NAME + " ("
            + MediaStore.Images.Media._ID + " integer primary key autoincrement, "
            + MediaStore.Images.Media.DISPLAY_NAME + " text, "
            + MediaStore.Images.Media.ORIENTATION + " integer, "
            + MediaStore.Images.Media.DATA + " text not null"
             + ")";

    public ImageDatabaseHelper(Context context) {
        super(context, DB_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_DB);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
