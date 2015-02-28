package timersassignment.simplegallery.image;

import android.content.Context;
import android.content.CursorLoader;
import android.net.Uri;
import android.provider.MediaStore;

/**
 *
 * Loader to get image data from External Disk
 *
 * @author Byungchul, Cho
 * @version 1.0
 */
public class DiskImageLoader extends CursorLoader implements ImageTable {

    private static final Uri MEDIA_URI = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

    public DiskImageLoader(Context context) {
        super(context, MEDIA_URI, IMAGE_PROJECTION, null, null, null);
    }

    public DiskImageLoader(Context context, Uri imageUri) {
        super(context, imageUri, IMAGE_PROJECTION, null, null, null);
    }

    public DiskImageLoader(Context context, long[] imageIds) {
        super(context);
        setUri(MEDIA_URI);
        setProjection(IMAGE_PROJECTION);
        setSelection(getIdSelection(imageIds));
    }

    private String getIdSelection(long[] imageIds) {
        StringBuilder builder = new StringBuilder();
        if(imageIds == null || imageIds.length == 0) {
            builder.append("0");
        } else {
            builder.append(MediaStore.Images.Media._ID);
            builder.append(" IN (");
            boolean isFirst = true;
            for(long id : imageIds) {
                if(!isFirst) {
                    builder.append(",");
                }
                builder.append(id);
                isFirst =  false;
            }
            builder.append(")");
        }
        return builder.toString();
    }

    @Override
    public String getSelection() {
        return super.getSelection();
    }
}
