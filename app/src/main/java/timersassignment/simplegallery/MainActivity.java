package timersassignment.simplegallery;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import timersassignment.simplegallery.cameraroll.CameraRollActivity;
import timersassignment.simplegallery.image.CheckableImageGridFragment;


public class MainActivity extends ActionBarActivity
        implements Runnable, CheckableImageGridFragment.CheckableGridFragmentListener{

    private static final int QUIT_DELAY_TIME = 2000;

    private Handler mHandler;
    private boolean mQuitMode;

    private CheckableImageGridFragment mMainFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(Constants.TAG_MODE ? R.layout.activity_main_tag : R.layout.activity_main);

        mMainFragment = (CheckableImageGridFragment)getFragmentManager().findFragmentById(R.id.fragment_one);
        mMainFragment.setListener(this);
        mHandler = new Handler();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if(mMainFragment.isCheckMode()) {
            MenuItem cameraRoll = menu.findItem(R.id.action_camera_roll);
            cameraRoll.setVisible(false);
        } else {
            MenuItem delete = menu.findItem(R.id.action_delete);
            delete.setVisible(false);
            MenuItem share = menu.findItem(R.id.action_share);
            share.setVisible(false);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_camera_roll) {
            startActivity(new Intent(getApplicationContext(), CameraRollActivity.class));
            return true;
        }
        if (id == R.id.action_delete) {
            mMainFragment.deleteCheckedItems();
        }

        if (id == R.id.action_share) {
            mMainFragment.shareCheckedItems();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if(mMainFragment.isCheckMode()) {
            mMainFragment.setCheckMode(false);
            return;
        }
        if(mQuitMode) {
            super.onBackPressed();
        } else {
            mQuitMode = true;
            Toast.makeText(this, R.string.main_quit, Toast.LENGTH_SHORT).show();
            mHandler.postDelayed(this, QUIT_DELAY_TIME);
        }
    }

    @Override
    public void run() {
        mQuitMode = false;
    }

    @Override
    public void onCheckModeChanged() {
        if(!mMainFragment.isCheckMode()) {
            setTitle(R.string.app_name);
        }
        invalidateOptionsMenu();
    }

    @Override
    public void onCheckStateChanged(int count) {
        setTitle(getString(R.string.item_selected_count, count));
    }
}
