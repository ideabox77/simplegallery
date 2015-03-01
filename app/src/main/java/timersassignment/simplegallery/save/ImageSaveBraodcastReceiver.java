package timersassignment.simplegallery.save;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import android.net.Uri;

import java.util.ArrayList;

import timersassignment.simplegallery.GalleryIntents;
import timersassignment.simplegallery.R;

/**
 *
 * BroadcastReceiver that revceived Image save actions
 *
 * @author Byungchul, Cho
 * @version 1.0
 */
public class ImageSaveBraodcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if(GalleryIntents.ACTION_SAVE.equals(intent.getAction())) {
            Toast.makeText(context, R.string.image_save_complete, Toast.LENGTH_SHORT).show();
        } else if(GalleryIntents.ACTION_DELETE.equals(intent.getAction())) {
            Toast.makeText(context, R.string.image_delete_complete, Toast.LENGTH_SHORT).show();
        } else if(GalleryIntents.ACTION_UPDATE.equals(intent.getAction())) {
            Toast.makeText(context, R.string.image_update_complete, Toast.LENGTH_SHORT).show();
        }
    }


}
