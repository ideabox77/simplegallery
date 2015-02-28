package timersassignment.simplegallery.image;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 *
 * If an image has Orientation value, rotate canvas to reduce image processing costs.
 *
 * @author Byungchul, Cho
 * @version 1.0
 */
public class CanvasRotateImageView extends ImageView {

    private int mOrientation;

    public CanvasRotateImageView(Context context) {
        super(context);
    }
    public CanvasRotateImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    public CanvasRotateImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setImageBitmap(Bitmap bm, String path) {
        mOrientation = ImageUtils.getExifOrientation(path);
        setImageBitmap(bm);
    }

    public void setImageDrawable(Drawable drawable, String path) {
        mOrientation = ImageUtils.getExifOrientation(path);
        setImageDrawable(drawable);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.save();
        if (mOrientation != 0) {
           canvas.rotate(mOrientation, this.getWidth() / 2, this.getHeight() / 2);
        }
        super.onDraw(canvas);
        canvas.restore();
    }

    public int getOrientation() {
        return mOrientation;
    }

}
