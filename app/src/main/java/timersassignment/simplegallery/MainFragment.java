package timersassignment.simplegallery;

import android.content.Intent;
import android.content.Loader;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import timersassignment.simplegallery.detail.DetailViewActivity;
import timersassignment.simplegallery.image.CheckableImageGridFragment;
import timersassignment.simplegallery.image.ImageListAdapter;
import timersassignment.simplegallery.image.InternalImageLoader;

/**
 *
 * apeear to Main Screen not in TAG_MODE
 *
 * @author Byungchul, Cho
 * @version 1.0
 */
public class MainFragment extends CheckableImageGridFragment  {

    @Override
    protected View inflateView(LayoutInflater inflater) {
        return inflater.inflate(R.layout.fragment_image_tile, null);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        GridView gridView = getGridView();
        if(gridView != null) {
            gridView.setOnItemClickListener(this);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        super.onItemClick(parent, view, position, id);
        if(!isCheckMode()) {
            Intent intent = new Intent(getActivity(), DetailViewActivity.class);
            ImageListAdapter.ImageItem item  = (ImageListAdapter.ImageItem)view.getTag();
            intent.putExtra(GalleryIntents.EXTRA_IMAGE_PATH, item.imagePath);
            startActivity(intent);
        }
    }

    @Override
    public Loader onCreateLoader(int id, Bundle args) {
        return new InternalImageLoader(getActivity());
    }

}
