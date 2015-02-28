package timersassignment.simplegallery.cameraroll;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

import timersassignment.simplegallery.R;

/**
 *
 * Camera roll Activity
 *
 * @author Byungchul, Cho
 * @version 1.0
 */
public class CameraRollActivity extends ActionBarActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(R.string.camera_roll_title);
        setContentView(R.layout.activity_camera_roll);
    }
}
