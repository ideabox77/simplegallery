package timersassignment.simplegallery.detail;

import android.app.Fragment;
import android.app.LoaderManager;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import timersassignment.simplegallery.R;
import timersassignment.simplegallery.image.CachedImageLoader;
import timersassignment.simplegallery.image.ImageLoader;

/**
 *
 * This fragment contains full screen image
 *
 * Created by Chobyungchul on 15. 2. 22..
 */
public class DetailViewFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final int LOADER_ID_IMAGE = 0;
    private PinchToZoomImageView mMainImage;
    private Uri mImageUri;
    private View mContentView;
    private String mImagePath;

    /**
     *
     * Callbacks to handle user pitch action
     *
     * Created by Chobyungchul on 15. 2. 22..
     */
    interface ZoomCallbacks {
        public void onZoomBackOn();
        public void onZoomOut(double zoomOut);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mContentView =  inflater.inflate(R.layout.fragment_image_detail, null);
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
                if(rate < 0.4) {
                    getActivity().finish();
                }
            }

            @Override
            public void onZoomBackOn() {
                // this is called when user canceled zoomOut
                mContentView.setBackgroundColor(Color.BLACK);
            }
        });


        // If there's no URI, load image from Path
        if(mImageUri != null) {
            getLoaderManager().initLoader(LOADER_ID_IMAGE, null, this);
        } else {
            CachedImageLoader.getInstance().loadImage(mMainImage, mImagePath, CachedImageLoader.CACHE_ID_DETAIL);
        }
    }

    public void setImageUri(Uri uri) {
        mImageUri = uri;
    }

    public void setImagePath(String imagePath) {
        mImagePath = imagePath;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new ImageLoader(getActivity(), mImageUri);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if(data != null && data.moveToFirst()) {
            String path = data.getString(ImageLoader.COLUMN_INDEX_DATA);
            if(!TextUtils.isEmpty(path)) {
                CachedImageLoader.getInstance().loadImage(mMainImage, path, CachedImageLoader.CACHE_ID_DETAIL);
            }
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
}
