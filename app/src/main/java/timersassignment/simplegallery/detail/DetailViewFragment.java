package timersassignment.simplegallery.detail;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.LoaderManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import timersassignment.simplegallery.GalleryIntents;
import timersassignment.simplegallery.R;
import timersassignment.simplegallery.image.CachedImageLoader;
import timersassignment.simplegallery.image.ImageLoader;
import timersassignment.simplegallery.image.InternalImageLoader;
import timersassignment.simplegallery.save.SaveUtils;

/**
 *
 * This fragment contains full screen image
 *
 * Created by Chobyungchul on 15. 2. 22..
 */
public class DetailViewFragment extends Fragment
        implements LoaderManager.LoaderCallbacks<Cursor>, View.OnClickListener {
    private final String TAG = getClass().getSimpleName();

    private static final int LOADER_ID_IMAGE = 0;
    private static final int LOADER_ID_INTERNAL_IMAGE = 1;

    private static final int REQUEST_CODE_SHARE = 1001;

    private PinchToZoomImageView mMainImage;
    private Uri mImageUri;
    private View mContentView;
    private long mId;

    private String mImageTitle;
    private View mMenuView;
    private TextView mTitleView;
    private View mShareButton;
    private View mDeleteButton;

    /**
     *
     * Callbacks to handle user pitch action
     *
     */
    interface ZoomCallbacks {
        public void onClick();
        public void onZoomBackOn();
        public void onZoomOut(double zoomOut);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mContentView =  inflater.inflate(R.layout.fragment_image_detail, null);
        mMenuView = mContentView.findViewById(R.id.custom_action_bar);
        mShareButton = mMenuView.findViewById(R.id.action_share);
        mDeleteButton = mMenuView.findViewById(R.id.action_delete);
        mTitleView = (TextView)mMenuView.findViewById(R.id.title);

        mShareButton.setOnClickListener(this);
        mDeleteButton.setOnClickListener(this);
        return mContentView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mMainImage = (PinchToZoomImageView)view.findViewById(R.id.image);
        mMainImage.setZoomListener(new ZoomCallbacks() {
            @Override
            public void onZoomOut(double rate) {
                // apply dim effect when user zoom out the picture
                if(rate < 1.0) {
                    int alpha = (int)(rate * 0xFF);
                    int color = 0x000000 + (alpha * 0x1000000 );
                    mContentView.setBackgroundColor(color);
                }
                if(rate <= PinchToZoomImageView.ZOOM_OUT_LIMIT_RATE) {
                    getActivity().finish();
                }
            }

            @Override
            public void onZoomBackOn() {
                // this is called when user canceled zoomOut
                mContentView.setBackgroundColor(Color.BLACK);
            }

            @Override
            public void onClick() {
                showMenu();
            }
        });

        // If there's no URI, load image from Path
        if(mImageUri != null) {
            getLoaderManager().initLoader(LOADER_ID_IMAGE, null, this);
        } else if(mId > -1) {
            getLoaderManager().initLoader(LOADER_ID_INTERNAL_IMAGE, null, this);
        }

        registerReceiver();
    }

    private void registerReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(GalleryIntents.ACTION_DELETE);
        intentFilter.addAction(GalleryIntents.ACTION_SHARE);
        getActivity().registerReceiver(mReceiver, intentFilter);
    }

    private BroadcastReceiver mReceiver= new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(GalleryIntents.ACTION_DELETE.equals(intent.getAction())) {
                Log.v(TAG, "delete finished");
                getActivity().finish();
            } else if(GalleryIntents.ACTION_SHARE.equals(intent.getAction())) {
                ArrayList<Uri> shareImageUriList = intent.getParcelableArrayListExtra(GalleryIntents.EXTRA_IMAGE_URI);
                if(shareImageUriList != null && !shareImageUriList.isEmpty()) {
                    startShareIntent(shareImageUriList);
                } else {
                    Toast.makeText(context, R.string.error_share_failed, Toast.LENGTH_SHORT).show();
                }
            }
        }
    };

    public void setImageUri(Uri uri) {
        mImageUri = uri;
    }
    public void setImageId(long id) {
        mId = id;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if(id == LOADER_ID_IMAGE) {
            return new ImageLoader(getActivity(), mImageUri);
        } else {
            return new InternalImageLoader(getActivity());
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if(data != null && data.moveToFirst()) {
            String title = data.getString(ImageLoader.COLUMN_INDEX_DISPLAY_NAME);
            String path = data.getString(ImageLoader.COLUMN_INDEX_DATA);
            if(!TextUtils.isEmpty(path)) {
                CachedImageLoader.getInstance().loadImage(mMainImage, path, CachedImageLoader.CACHE_ID_DETAIL);
            }
            setTitle(title);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if(mMainImage != null) {
            mMainImage.setZoomListener(null);
        }
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.action_share:
                startShareItem(getIdList());
                break;
            case R.id.action_delete:
                showDeletePopup(getIdList());
                break;
        }

    }

    private ArrayList<Integer> getIdList() {
        ArrayList<Integer> idList = new ArrayList<Integer>();
        idList.add((int)mId);
        return idList;
    }

    private void setTitle(String title) {
        if(mTitleView != null && !TextUtils.isEmpty(title)) {
            mTitleView.setText(title);
        }
    }

    private void showDeletePopup(final ArrayList<Integer> items) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(getString(R.string.image_delete_message_for_one,
                mImageTitle));
        builder.setTitle(getString(R.string.camera_roll_popup_title));
        builder.setPositiveButton(getString(R.string.common_ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startDeleteItem(items);
            }
        });
        builder.setNegativeButton(getString(R.string.common_cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
    }

    private void showMenu() {
        if(mMenuView != null) {
            int visibility =  mMenuView.getVisibility();
            mMenuView.setVisibility(visibility == View.VISIBLE ? View.GONE : View.VISIBLE);
        }
    }

    private void startDeleteItem(ArrayList<Integer> items) {
        SaveUtils.startDeleteItems(getActivity(), items);
    }

    private void startShareItem(ArrayList<Integer> items) {
        SaveUtils.shareCheckedItems(getActivity(), items);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_CODE_SHARE) {
            SaveUtils.cleanTempFiles(getActivity());
        }
    }

    private void startShareIntent(ArrayList<Uri> shareImageUriList) {
        Intent intent = new Intent(Intent.ACTION_SEND_MULTIPLE);
        intent.setType("image/jpg");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(Intent.EXTRA_STREAM, shareImageUriList);
        startActivityForResult(Intent.createChooser(intent, "Choose"), REQUEST_CODE_SHARE);
    }
}
