package timersassignment.simplegallery.save;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Gallery;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;

import timersassignment.simplegallery.GalleryIntents;
import timersassignment.simplegallery.R;
import timersassignment.simplegallery.image.ImageListAdapter;
import timersassignment.simplegallery.image.ImageTable;

/**
 *
 * IntentService to process Image Insert, Update, Delete
 *
 * @author Byungchul, Cho
 * @version 1.0
 */
public class ImageSaveService extends IntentService {
    private static final String TAG = "ImageSaveService";
    private static final String NAME = "ImageSaveService";

    public ImageSaveService() {
        super(NAME);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if(GalleryIntents.ACTION_SAVE.equals(intent.getAction())) {
            String title = intent.getStringExtra(GalleryIntents.EXTRA_IMAGE_TITLE);
            int orientation = intent.getIntExtra(GalleryIntents.EXTRA_IMAGE_ORIENTATION, 0);
            String path = intent.getStringExtra(GalleryIntents.EXTRA_IMAGE_PATH);
            if(TextUtils.isEmpty(path)) {
                return;
            }
            saveImages(title, orientation, path);
        } else if(GalleryIntents.ACTION_DELETE.equals(intent.getAction())) {
            ArrayList<Integer> checkedIds = intent.getIntegerArrayListExtra(GalleryIntents.EXTRA_IMAGE_IDS);
            deleteImages(checkedIds);
        }
    }

    private void saveImages(String title, int orientation, String path) {
        Log.v(TAG, "Save Image data " + ", Title : " + title + ", Path " + path);
        File file = new File(path);
        if(file.exists()) {
            String name = file.getName();
            String newName = SaveUtils.getImagePath(this) + name;
            boolean success = copyFile(new File(path), newName);
            if(success) {
                ImageDatabaseHelper helper = new ImageDatabaseHelper(this);
                SQLiteDatabase db = helper.getReadableDatabase();
                Cursor cursor = db.rawQuery("select " + MediaStore.Images.Media._ID +
                                ", " + MediaStore.Images.Media.DATA
                                + " from " + ImageDatabaseHelper.IMAGE_TABLE.TABLE_NAME
                                + " where " + MediaStore.Images.Media.DATA + "=?",
                        new String[] { newName });
                try {
                    if(cursor != null && cursor.moveToFirst()) {
                        int id = cursor.getInt(cursor.getColumnIndex(MediaStore.Images.Media._ID));
                        updateImageData(id, title, orientation, newName);
                    } else {
                        saveImageData(title, orientation, newName);
                    }
                } finally {
                    if(cursor != null) {
                        cursor.close();;
                    }
                }
            }
        }
    }

    private void deleteImages(ArrayList<Integer> ids) {
        ImageDatabaseHelper helper = new ImageDatabaseHelper(this);
        SQLiteDatabase db = helper.getReadableDatabase();

        ArrayList<String> fileToDelete = new ArrayList<String>();

        Cursor cursor = db.query(ImageDatabaseHelper.IMAGE_TABLE.TABLE_NAME,
                new String[] { MediaStore.Images.Media._ID, MediaStore.Images.Media.DATA },
                MediaStore.Images.Media._ID + getInSelectionFromIds(ids),
                null, null, null, null);
        try {
            if(cursor != null && cursor.getCount() > 0) {
                cursor.moveToPosition(-1);
                while(cursor.moveToNext()) {
                    String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
                    fileToDelete.add(path);
                }
            }
        } finally {
            if(cursor != null) {
                cursor.close();
            }
        }
        deleteImageData(ids);
        for(String path : fileToDelete) {
            File file = new File(path);
            if(file.exists()) {
                Log.v(TAG, "Delete file at " + path);
                file.delete();
            }
        }
        sendBroadcast(new Intent(GalleryIntents.ACTION_DELETE));
    }

    private void deleteImageData(ArrayList<Integer> ids) {
        Log.v(TAG, "Delete id list : " + getInSelectionFromIds(ids));
        ImageDatabaseHelper helper = new ImageDatabaseHelper(this);
        SQLiteDatabase db = helper.getWritableDatabase();
        int result = db.delete(ImageDatabaseHelper.IMAGE_TABLE.TABLE_NAME,
                MediaStore.Images.Media._ID + getInSelectionFromIds(ids), null );
        Log.v(TAG, "delete count : " + result);
    }

    private void updateImageData(int id, String title, int orientation, String path) {
        Log.v(TAG, "Update Image data  ID" + id + ", Title : " + title + ", Path " + path);
        ImageDatabaseHelper helper = new ImageDatabaseHelper(this);
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.DISPLAY_NAME, title);
        values.put(MediaStore.Images.Media.ORIENTATION, orientation);
        values.put(MediaStore.Images.Media.DATA, path);
        db.update(ImageDatabaseHelper.IMAGE_TABLE.TABLE_NAME, values,
                MediaStore.Images.Media._ID + "=?", new String[] { String.valueOf(id) });
        sendBroadcast(new Intent(GalleryIntents.ACTION_UPDATE));
    }

    private void saveImageData(String title, int orientation, String path) {
        Log.v(TAG, "Save Image data " + " Title : " + title + ", Path " + path);
        ImageDatabaseHelper helper = new ImageDatabaseHelper(this);
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.DISPLAY_NAME, title);
        values.put(MediaStore.Images.Media.ORIENTATION, orientation);
        values.put(MediaStore.Images.Media.DATA, path);
        db.insert(ImageDatabaseHelper.IMAGE_TABLE.TABLE_NAME, null, values);
        sendBroadcast(new Intent(GalleryIntents.ACTION_SAVE));
    }

    private String getInSelectionFromIds(ArrayList<Integer> ids) {
        StringBuilder builder = new StringBuilder();
        builder.append(" IN(");
        boolean isFirst = true;
        for(Integer id : ids) {
            if(!isFirst) {
                builder.append(", ");
            }
            builder.append(id);
            isFirst = false;
        }
        builder.append(")");
        return builder.toString();
    }

    /**
     * 파일 복사
     * @param file
     * @param save_file
     * @return
     */
    private boolean copyFile(File file , String save_file) {
        boolean result;
        if(file != null && file.exists()) {
            try {
                FileInputStream fis = new FileInputStream(file);
                FileOutputStream newfos = new FileOutputStream(save_file);
                int readcount = 0;
                byte[] buffer = new byte[1024];
                while ((readcount = fis.read(buffer, 0, 1024)) != -1) {
                    newfos.write(buffer, 0, readcount);
                }
                newfos.close();
                fis.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            result = true;
        }else{
            result = false;
        }
        return result;
    }
}
