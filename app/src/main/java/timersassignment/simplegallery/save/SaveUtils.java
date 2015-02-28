package timersassignment.simplegallery.save;

import android.content.Context;

import java.io.File;

/**
 *
 * Contains static methods to save actions
 *
 * @author Byungchul, Cho
 * @version 1.0
 */
public class SaveUtils {

    private static final String IMAGE_PATH = "/images/";

    public static String getImagePath(Context context) {
        String path = getAppDataPath(context) + IMAGE_PATH;
        File file = new File(path);
        if(!file.exists()) {
            file.mkdirs();
        }
        return path;
    }

    public static String getAppDataPath(Context context) {
        File file = context.getCacheDir().getParentFile();
        return file.getAbsolutePath();
    }

}
