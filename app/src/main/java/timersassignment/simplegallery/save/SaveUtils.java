package timersassignment.simplegallery.save;

import android.content.Context;
import android.content.Intent;

import java.io.File;
import java.util.ArrayList;

import timersassignment.simplegallery.GalleryIntents;

/**
 *
 * Contains static methods to save actions
 *
 * @author Byungchul, Cho
 * @version 1.0
 */
public class SaveUtils {

    private static final String IMAGE_PATH = "/images/";

    /**
     *
     * get internal image path
     *
     * @param context
     * @return
     */
    public static String getImagePath(Context context) {
        String path = getAppDataPath(context) + IMAGE_PATH;
        File file = new File(path);
        if(!file.exists()) {
            file.mkdirs();
        }
        return path;
    }
    /**
     *
     * get internal image directory
     *
     * @param context
     * @return
     */
    public static String getAppDataPath(Context context) {
        File file = context.getCacheDir().getParentFile();
        return file.getAbsolutePath();
    }
    /**
     *
     * get external image temporarily used for share
     *
     * @param context
     * @return
     */
    public static String getTempPath(Context context) {
        File file = context.getExternalCacheDir();
        if(!file.exists()) {
            file.mkdirs();
        }
        return file.getAbsolutePath() + "/";
    }
    /**
     *
     * after sharing, clean all images
     *
     * @param context
     * @return
     */
    public static void cleanTempFiles(Context context) {
        String path = SaveUtils.getTempPath(context);
        File dir = new File(path);
        if(dir.isDirectory()) {
            String[] file = dir.list();
            for(int i=0; i < file.length ; i++) {
                new File(dir, file[i]).delete();
            }
        }
    }

    public static void startDeleteItems(Context context, final ArrayList<Integer> items) {
        Intent intent = new Intent(context, ImageSaveService.class);
        intent.setAction(GalleryIntents.ACTION_DELETE);
        intent.putExtra(GalleryIntents.EXTRA_IMAGE_IDS, items);
        context.startService(intent);
    }

    public static void shareCheckedItems(Context context, final ArrayList<Integer> items) {
        Intent intent = new Intent(context, ImageSaveService.class);
        intent.setAction(GalleryIntents.ACTION_SHARE);
        intent.putExtra(GalleryIntents.EXTRA_IMAGE_IDS, items);
        context.startService(intent);
    }

}
