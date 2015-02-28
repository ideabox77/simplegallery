package timersassignment.simplegallery.image;

import android.provider.MediaStore;

/**
 *
 * Contains ImageTable Projection and column indices
 *
 * @author Byungchul, Cho
 * @version 1.0
 */
public interface ImageTable {
    public static String[] IMAGE_PROJECTION = {
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.DISPLAY_NAME,
            MediaStore.Images.Media.DATA,
            MediaStore.Images.Media.ORIENTATION
    };

    public static final int COLUMN_INDEX_ID = 0;
    public static final int COLUMN_INDEX_DISPLAY_NAME = 1;
    public static final int COLUMN_INDEX_DATA = 2;
    public static final int COLUMN_ORIENTATION = 3;
}
