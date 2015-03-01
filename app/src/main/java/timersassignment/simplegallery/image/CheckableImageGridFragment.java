package timersassignment.simplegallery.image;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

import timersassignment.simplegallery.GalleryIntents;
import timersassignment.simplegallery.R;
import timersassignment.simplegallery.save.ImageSaveService;
import timersassignment.simplegallery.save.SaveUtils;

/**
 *
 * This fragment gives Gridfragment multiple check interface functions
 *
 * @author Byungchul, Cho
 * @version 1.0
 */
public abstract class CheckableImageGridFragment extends ImageGridFragment
        implements AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener{

    private static final String KEY_CHECK_STATE = "checkState";
    private static final String KEY_CHECK_MODE = "checkMode";
    private CheckableGridFragmentListener mCheckListener;
    private static final int REQUEST_CODE_SHARE = 1001;

    private CheckableImageListAdapter mImageAdapter;
    public interface CheckableGridFragmentListener {
        public void onCheckModeChanged();
        public void onCheckStateChanged(int count);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getGridView().setOnItemLongClickListener(this);

        onRestoreState(savedInstanceState);
        registerReceiver();
    }

    private void onRestoreState(Bundle savedInstanceState) {
        if(savedInstanceState != null) {
            boolean isCheckMode = savedInstanceState.getBoolean(KEY_CHECK_MODE);
            HashMap<Long,Boolean> savedCheckState =
                    (HashMap<Long,Boolean>)savedInstanceState.getSerializable(KEY_CHECK_STATE);
            mImageAdapter.setCheckMode(isCheckMode);
            mImageAdapter.setCheckState(savedCheckState);
        }
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
                loadImage();
                setCheckMode(false);
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

    public void setListener(CheckableGridFragmentListener listener) {
        mCheckListener = listener;
    }

    @Override
    protected ImageListAdapter getImageAdapter() {
        mImageAdapter = new CheckableImageListAdapter(getActivity(), true);
        return mImageAdapter;
    }

    public void setCheckMode(boolean checkMode) {
        if(isCheckMode() != checkMode) {
            if(mImageAdapter != null) {
                mImageAdapter.setCheckMode(checkMode);
            }
            if(mCheckListener != null) {
                mCheckListener.onCheckModeChanged();
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(KEY_CHECK_MODE, mImageAdapter.isCheckMode());
        outState.putSerializable(KEY_CHECK_STATE, (Serializable)mImageAdapter.getCheckState());
    }

    public void deleteCheckedItems() {
        ArrayList<Integer> checkedItems = mImageAdapter.getCheckedImageList();
        if(checkedItems != null && !checkedItems.isEmpty()) {
            showDeletePopup(checkedItems);
        } else {
            Toast.makeText(getActivity(), R.string.item_no_selected, Toast.LENGTH_SHORT).show();
        }
    }

    private void showDeletePopup(final ArrayList<Integer> checkedItems) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(getString(R.string.image_delete_message, checkedItems.size()));
        builder.setTitle(getString(R.string.camera_roll_popup_title));
        builder.setPositiveButton(getString(R.string.common_ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startDeleteItems(checkedItems);
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

    private void startDeleteItems(final ArrayList<Integer> items) {
        Intent intent = new Intent(getActivity(), ImageSaveService.class);
        intent.setAction(GalleryIntents.ACTION_DELETE);
        intent.putExtra(GalleryIntents.EXTRA_IMAGE_IDS, items);
        getActivity().startService(intent);
    }

    public void shareCheckedItems() {
        ArrayList<Integer> items = mImageAdapter.getCheckedImageList();
        Intent intent = new Intent(getActivity(), ImageSaveService.class);
        intent.setAction(GalleryIntents.ACTION_SHARE);
        intent.putExtra(GalleryIntents.EXTRA_IMAGE_IDS, items);
        getActivity().startService(intent);
/*
        if(checkedItems.size() == 0) {
            Toast.makeText(getActivity(), R.string.item_no_selected, Toast.LENGTH_SHORT).show();
            return;
        }

        ArrayList<Uri> shareImageUriList = new ArrayList<Uri>();
        for(int i=0; i< getGridView().getCount(); i++) {
            Cursor cursor = (Cursor)getGridView().getItemAtPosition(i);
            Integer id = cursor.getInt(ImageTable.COLUMN_INDEX_ID);
            if(checkedItems.contains(id)) {
                String path = cursor.getString(ImageTable.COLUMN_INDEX_DATA);
                Uri imageUri = getImageUri(path);
                if(imageUri != null) {
                    shareImageUriList.add(imageUri);
                }
            }
        }

        if(!shareImageUriList.isEmpty()) {
            Intent intent = new Intent(Intent.ACTION_SEND_MULTIPLE);
            intent.setType("image/jpg");
            intent.putExtra(Intent.EXTRA_STREAM, shareImageUriList);
            startActivity(Intent.createChooser(intent, "Choose"));
        }
        */
    }

    private Uri getImageUri(String path) {
        File file = new File(path);
        if(file.exists()) {
            return Uri.fromFile(file);
        } else {
            return null;
        }
    }

    public boolean isCheckMode() {
        if(mImageAdapter != null) {
            return mImageAdapter.isCheckMode();
        } else {
            return false;
        }
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        setCheckMode(true);
        return false;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if(isCheckMode()) {
            mImageAdapter.checkId(id);
            if(mCheckListener != null) {
                mCheckListener.onCheckStateChanged(mImageAdapter.getCheckedImageList().size());
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        getActivity().unregisterReceiver(mReceiver);
    }

    private void startShareIntent(ArrayList<Uri> shareImageUriList) {
        Intent intent = new Intent(Intent.ACTION_SEND_MULTIPLE);
        intent.setType("image/jpg");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(Intent.EXTRA_STREAM, shareImageUriList);
        startActivityForResult(Intent.createChooser(intent, "Choose"), REQUEST_CODE_SHARE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_CODE_SHARE) {
            SaveUtils.cleanTempFiles(getActivity());
        }
    }
}
