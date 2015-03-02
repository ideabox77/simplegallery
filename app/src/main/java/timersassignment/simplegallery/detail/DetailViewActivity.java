package timersassignment.simplegallery.detail;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import timersassignment.simplegallery.GalleryIntents;
import timersassignment.simplegallery.R;

/**
 *
 * This activity shows full sized Image on screen
 *
 * @author Byungchul, Cho
 * @version 1.0
 */
public class DetailViewActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
    }

    @Override
    public void onAttachFragment(Fragment fragment) {
        super.onAttachFragment(fragment);
        Intent intent = getIntent();
        Uri uri = intent.getParcelableExtra(GalleryIntents.EXTRA_IMAGE_URI);
        long id = intent.getLongExtra(GalleryIntents.EXTRA_IMAGE_ID, -1);
        if(uri != null && fragment instanceof DetailViewFragment) {
            ((DetailViewFragment)fragment).setImageUri(uri);
        } else if(id > -1) {
            ((DetailViewFragment)fragment).setImageId(id);
        } else {
            Toast.makeText(this, R.string.error_image_information, Toast.LENGTH_SHORT).show();
        }
    }

}
