package timersassignment.simplegallery;

import android.content.ContentUris;
import android.content.Intent;
import android.content.Loader;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import java.util.Map;
import java.util.Set;

import timersassignment.simplegallery.detail.DetailViewActivity;
import timersassignment.simplegallery.image.CheckableImageGridFragment;
import timersassignment.simplegallery.image.DiskImageLoader;

/**
 *
 * apeear to Main Screen
 *
 * @author Byungchul, Cho
 * @version 1.0
 */
public class MainTagFragment extends CheckableImageGridFragment implements AdapterView.OnItemClickListener {
    private long[] mSavedIds;

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        GridView gridView = getGridView();
        if(gridView != null) {
            gridView.setOnItemClickListener(this);
        }
    }

    @Override
    public void onStart() {
        if(Constants.TAG_MODE) {
            loadSavedIds();
        }
        super.onStart();
    }

    @Override
    protected View inflateView(LayoutInflater inflater) {
              return inflater.inflate(R.layout.fragment_image_tile, null);
    }

    @Override
    public Loader onCreateLoader(int id, Bundle args) {
        return new DiskImageLoader(getActivity(), mSavedIds);
    }

    private void loadSavedIds() {
        Map<String, Object> map = GalleryStorage.getAllTitle(getActivity());
        if(map != null && map.size() > 0) {
            Set<String> keySet = map.keySet();
            mSavedIds = new long[keySet.size()];
            int index = -1;
            for(String key : keySet) {
                mSavedIds[++index] = Long.valueOf(key);
            }
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        super.onItemClick(parent, view, position, id);
        if(!isCheckMode()) {
            Intent intent = new Intent(getActivity(), DetailViewActivity.class);
            intent.putExtra(GalleryIntents.EXTRA_IMAGE_URI, getImageUri(id));
            startActivity(intent);
        }
    }

    private Uri getImageUri(long id) {
        return ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id);
    }

}
