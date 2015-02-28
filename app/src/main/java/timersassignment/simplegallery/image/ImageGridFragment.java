package timersassignment.simplegallery.image;

import android.app.Fragment;
import android.app.LoaderManager;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

/**
 *
 * Load images from Loader and set to Gridview
 *
 * @author Byungchul, Cho
 * @version 1.0
 */
public abstract class ImageGridFragment<T extends ImageListAdapter> extends Fragment
        implements LoaderManager.LoaderCallbacks<Cursor> {
    protected final String TAG = getClass().getSimpleName();
    private static final int ID_LOADER_IMAGE = 1;
    private T mImageAdapter;
    private GridView mGridView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View layout = inflateView(inflater);
        if(layout != null) {
            View gridView = layout.findViewById(android.R.id.list);
            if(gridView instanceof GridView) {
                mImageAdapter = getImageAdapter();
                mGridView = (GridView)gridView;
                mGridView.setAdapter(mImageAdapter);
                View emptyView = layout.findViewById(android.R.id.empty);
                mGridView.setEmptyView(emptyView);
            }

        }
        return layout;
    }

    protected GridView getGridView() {
        return mGridView;
    }

    protected abstract T getImageAdapter();

    protected abstract View inflateView(LayoutInflater inflater);
    @Override
    public void onStart() {
        super.onStart();
        loadImage();
    }

    protected void loadImage() {
        if(getLoaderManager().getLoader(ID_LOADER_IMAGE) == null) {
            getLoaderManager().initLoader(ID_LOADER_IMAGE, null, this);
        } else {
            getLoaderManager().destroyLoader(ID_LOADER_IMAGE);
            getLoaderManager().restartLoader(ID_LOADER_IMAGE, null, this);
        }
    }

    @Override
    public void onLoadFinished(Loader loader, Cursor data) {
        Log.v(TAG, "found " + data.getCount() + "item(s)");
        if(mImageAdapter != null) {
            mImageAdapter.changeCursor(data);
        }
    }

    @Override
    public void onLoaderReset(Loader loader) {
    }

    protected void refreshAdapter() {
        if(mImageAdapter != null) {
            mImageAdapter.notifyDataSetChanged();
        }
    }

}
