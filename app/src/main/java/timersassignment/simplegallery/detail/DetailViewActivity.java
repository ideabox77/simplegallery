package timersassignment.simplegallery.detail;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

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
        String path = intent.getStringExtra(GalleryIntents.EXTRA_IMAGE_PATH);
        if(uri != null && fragment instanceof DetailViewFragment) {
            ((DetailViewFragment)fragment).setImageUri(uri);
        } else if(!TextUtils.isEmpty(path)) {
            ((DetailViewFragment)fragment).setImagePath(path);
        }
    }

}
