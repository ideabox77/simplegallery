package timersassignment.simplegallery;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Map;

/**
 *
 *  To manage Tag informations in TAG_MODE
 *
 * @author Byungchul, Cho
 * @version 1.0
 */
public class GalleryStorage {
    private static final String SHARED_KEY_GALLERY_TITLE = "timersassignment.simplegallery.title";
    public static void saveStorage(Context context, long id, String title) {
        if(context == null) {
            return;
        }
        SharedPreferences preferences = context.getSharedPreferences(SHARED_KEY_GALLERY_TITLE, Context.MODE_PRIVATE);
        preferences.edit().putString(String.valueOf(id), title).commit();
    }

    public static Map<String, Object> getAllTitle(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(SHARED_KEY_GALLERY_TITLE, Context.MODE_PRIVATE);
        return (Map<String, Object>)preferences.getAll();
    }

    public static String getTitle(Context context, long id) {
        SharedPreferences preferences = context.getSharedPreferences(SHARED_KEY_GALLERY_TITLE, Context.MODE_PRIVATE);
        return preferences.getString(String.valueOf(id), null);
    }
}
