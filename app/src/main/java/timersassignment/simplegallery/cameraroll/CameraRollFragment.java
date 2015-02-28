package timersassignment.simplegallery.cameraroll;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;

import timersassignment.simplegallery.Constants;
import timersassignment.simplegallery.GalleryIntents;
import timersassignment.simplegallery.GalleryStorage;
import timersassignment.simplegallery.R;
import timersassignment.simplegallery.image.ImageGridFragment;
import timersassignment.simplegallery.image.ImageListAdapter;
import timersassignment.simplegallery.image.ImageLoader;
import timersassignment.simplegallery.save.ImageSaveService;

/**
 *
 * Camera roll fragment
 * Load Images from disk
 *
 * @author Byungchul, Cho
 * @version 1.0
 */
public class CameraRollFragment extends ImageGridFragment implements AdapterView.OnItemClickListener {

    private LayoutInflater mInflater;

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        GridView gridView = getGridView();
        if (gridView != null) {
            gridView.setOnItemClickListener(this);
        }
    }

    @Override
    protected ImageListAdapter getImageAdapter() {
        return new ImageListAdapter(getActivity(), false);
    }

    @Override
    protected View inflateView(LayoutInflater inflater) {
        mInflater = inflater;
        return inflater.inflate(R.layout.fragment_camera_roll, null);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        ImageListAdapter.ImageItem item = (ImageListAdapter.ImageItem)view.getTag();
        showTitleEditPopup(id, item.imagePath);
    }

    /**
     * show to entitle and save image
     *
     * @param id Image id
     * @param path Image path
     * @return
     */
    private void showTitleEditPopup(final long id, final String path) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(getString(R.string.camera_roll_popup_title));

        int margin = getResources().getDimensionPixelSize(R.dimen.common_layout_padding);

        View inputLayout = getInputLayout();
        final EditText inputView = (EditText)inputLayout.findViewById(R.id.dialog_text);
        builder.setView(inputLayout);
        builder.setPositiveButton(getString(R.string.common_ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String title = inputView.getText().toString();
                if(TextUtils.isEmpty(title)) {
                    return;
                }
                if (Constants.TAG_MODE) {
                    changePictureTitle(id, title);
                } else {
                    startImportFile(title, path);
                }
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

    private View getInputLayout() {
        return mInflater.inflate(R.layout.dialog_text_input,null);
    }


    /**
     *
     * To change picture's title
     *
     * @param id Image id
     * @param title Title to change
     * @return
     */
    private void changePictureTitle(long id, String title) {
        GalleryStorage.saveStorage(getActivity(), id, title);
        refreshAdapter();
    }

    /**
     *
     * Start service to get Image File from Disk
     *
     * @param title String to entitle image
     * @param path Original image file path
     * @return
     */
    private void startImportFile(String title, String path) {
        Intent intent = new Intent(getActivity(), ImageSaveService.class);
        intent.setAction(GalleryIntents.ACTION_SAVE);
        intent.putExtra(GalleryIntents.EXTRA_IMAGE_TITLE, title);
        intent.putExtra(GalleryIntents.EXTRA_IMAGE_PATH, path);
        getActivity().startService(intent);
    }

    @Override
    public Loader onCreateLoader(int id, Bundle args) {
        return new ImageLoader(getActivity());
    }

}
