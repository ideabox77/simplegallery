package timersassignment.simplegallery.detail;

import android.content.Context;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.FloatMath;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import timersassignment.simplegallery.image.CanvasRotateImageView;

/**
 *
 * Contains Pinch To Zoom functions
 *
 * @author Byungchul, Cho
 * @version 1.0
 */
public class PinchToZoomImageView extends CanvasRotateImageView implements View.OnTouchListener {
    private static final String TAG = "PinchToZoomImageView";

    private Matrix matrix = new Matrix();
    private Matrix moveMatrix = new Matrix();

    public static final float ZOOM_OUT_LIMIT_RATE = (float)0.4;

    /*
     * user action status mode when user do noting
     */
    private static final int NONE = 0;
    /*
    * user action status mode when user move the image
    */
    private static final int DRAG = 1;
    /*
    * user action status mode when user do pinch actions
    */
    private static final int ZOOM = 2;
    private int mode = NONE;

    private PointF start = new PointF();
    private PointF mid = new PointF();
    private float oldDist = 1f;
    private static final int WIDTH = 0;
    private static final int HEIGHT = 1;

    /*
     * image matrix array values
     */
    public static final int VALUE_INDEX_SCALE_RATE_X = 0;
    public static final int VALUE_INDEX_SCALE_RATE_Y = 4;
    public static final int VALUE_INDEX_MOVED_DISTANCE_X = 2;
    public static final int VALUE_INDEX_MOVED_DISTANCE_Y = 5;

    private float[] value = new float[9];
    private Drawable drawable;
    /*
     * view width, height
     */
    private int width;
    private int height;
    /*
     * actual image width, height
     */
    private int imageWidth;
    private int imageHeight;
    /*
     * image width, height values that are shown to user
     */
    private int scaledImageWidth;
    private int scaledImageHeight;

    private int fitScaledImageWidth;
    private int fitScaledImageHeight;

    /*
     * whether current canvas is rotated and should be swapped x for b
     */
    private boolean mIsCanvasRotated;

    /*
     * initial offset distance if the Canvas is rotated
     */
    private float offsetX;
    private float offsetY;

    private boolean pressed;

    private DetailViewFragment.ZoomCallbacks mListener;

    public PinchToZoomImageView(Context context) {
        this(context, null);
        setOnTouchListener(this);
        setScaleType(ScaleType.MATRIX); // Scale type should be matrix
    }

    public PinchToZoomImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
        setOnTouchListener(this);
        setScaleType(ScaleType.MATRIX); // Scale type should be matrix
    }

    public PinchToZoomImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setOnTouchListener(this);
        setScaleType(ScaleType.MATRIX); // Scale type should be matrix
    }

    void setZoomListener(DetailViewFragment.ZoomCallbacks zoomListener) {
        mListener = zoomListener;
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        init();
    }

    /*
     * init the image
     * initialize values and fit image on view at the center of the view
     */
    protected void init() {
        this.matrix.getValues(value);
        width = this.getWidth();
        height = this.getHeight();
        drawable = this.getDrawable();
        if(drawable == null) {
            return;
        }
        mIsCanvasRotated = getOrientation() != 0 && getOrientation() != 180;
        Log.v(TAG, "image orientation : " + getOrientation());
        if(mIsCanvasRotated) {
            imageWidth = drawable.getIntrinsicHeight();
            imageHeight = drawable.getIntrinsicWidth();
        } else {
            imageWidth = drawable.getIntrinsicWidth();
            imageHeight = drawable.getIntrinsicHeight();
        }
        setImageFitOnView();
        fitScaledImageWidth = scaledImageWidth;
        fitScaledImageHeight = scaledImageHeight;
        setCenter(true);
        matrix.setValues(value);
        setImageMatrix(matrix);
    }
    /*
     * adjust image size to fit for view
     */
    private void setImageFitOnView() {
        value[VALUE_INDEX_SCALE_RATE_Y] = (float)width / (float)imageWidth;
        value[VALUE_INDEX_SCALE_RATE_X] = value[VALUE_INDEX_SCALE_RATE_Y];

        setScaledImageSize();

        if(width < scaledImageWidth || height < scaledImageHeight) {
            value[VALUE_INDEX_SCALE_RATE_Y] = (float)height / (float)imageHeight;
            value[VALUE_INDEX_SCALE_RATE_X] = value[VALUE_INDEX_SCALE_RATE_Y];
            setScaledImageSize();
        }

    }

    private void setScaledImageSize() {
        scaledImageWidth = (int) (imageWidth * value[VALUE_INDEX_SCALE_RATE_X]);
        scaledImageHeight = (int) (imageHeight * value[VALUE_INDEX_SCALE_RATE_Y]);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        ImageView view = (ImageView) v;
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                pressed = true;
                moveMatrix.set(matrix);
                // When move, set start point
                start.set(event.getX(), event.getY());
                mode = DRAG;
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                // handle multi touch event
                oldDist = spacing(event);
                if (oldDist > 20f) {
                    moveMatrix.set(matrix);
                    midPoint(mid, event);
                    mode = ZOOM;
                }
                break;
            case MotionEvent.ACTION_POINTER_UP:
                mode = NONE;
                break;
            case MotionEvent.ACTION_UP:
                if(mListener != null && pressed) {
                    mListener.onClick();
                }
                pressed = false;
                break;
            case MotionEvent.ACTION_MOVE:
                if (mode == DRAG) {
                    matrix.set(moveMatrix);
                    float xTrans = getRotateX(event.getX() - start.x, event.getY() - start.y, getOrientation());
                    float yTrans = getRotateY(event.getX() - start.x, event.getY() - start.y, getOrientation());

                    float space = spacing(xTrans, yTrans);
                    if(space > 0) {
                        pressed = false;
                    }
                    matrix.postTranslate(xTrans, yTrans);
                } else if (mode == ZOOM) {
                    pressed = false;
                    float newDist = spacing(event);
                    if (newDist > 5f) {
                        matrix.set(moveMatrix);
                        float scale = newDist / oldDist;
                        matrix.postScale(scale, scale, mid.x, mid.y);
                    }
                }
                break;
        }
        changeMatrixValue(matrix, view);
//        view.setImageMatrix(matrix);
        return true;
    }


    /**
     * when canvas is rotated, x value should be changeds
     *
     * @param x x value
     * @param y y value
     * @return recalculated value
     */
    private float getRotateX(float x, float y, int degree) {
        switch(degree) {
            case 0:
                return x;
            case 90:
                return y;
            case 180:
                return -x;
            case 270:
                return -y;
        }
        return x;
    }
    /**
     * when canvas is rotated, x value should be changeds
     *
     * @param x x value
     * @param y y value
     * @return recalculated value
     */
    private float getRotateY(float x, float y, int degree) {
        switch(degree) {
            case 0:
                return y;
            case 90:
                return -x;
            case 180:
                return -y;
            case 270:
                return x;
        }
        return y;
    }

    /**
     * set image to center of this view
     *
     * @param init if true value is set for initialize
     * @return recalculated value
     */
    private void setCenter(boolean init) {
        setScaledImageSize();

        if(scaledImageWidth <= width) {
            float scaleOffset = getScaledOffsetX(getOrientation());
            value[VALUE_INDEX_MOVED_DISTANCE_X] =
                    (float)(width / 2) - (float)(scaledImageWidth / 2) - scaleOffset;
            if(init) {
                offsetX = mIsCanvasRotated ? value[VALUE_INDEX_MOVED_DISTANCE_X] : 0;
            }
        }
        if(scaledImageHeight <= height) {
            float scaleOffset = getScaledOffsetY(getOrientation());
            value[VALUE_INDEX_MOVED_DISTANCE_Y] =
                    (float)(height / 2) - (float)(scaledImageHeight / 2) - scaleOffset;
            if(init) {
                offsetY = mIsCanvasRotated ? value[VALUE_INDEX_MOVED_DISTANCE_Y] : 0;
            }
        }
    }

    private float getScaledOffsetX(int degrees) {
        switch(degrees) {
            case 0:
                return 0;
            case 90:
                return (float)(scaledImageHeight / 2) - (float)(scaledImageWidth / 2);
            case 180:
                return 0;
            case 270:
                return (float)(scaledImageHeight / 2) - (float)(scaledImageWidth / 2);
            default:
                return 0;
        }
    }

    private float getScaledOffsetY(int degrees) {
        switch(degrees) {
            case 0:
                return 0;
            case 90:
                return (float)(scaledImageWidth / 2) - (float)(scaledImageHeight / 2);
            case 180:
                return 0;
            case 270:
                return (float)(scaledImageWidth / 2) - (float)(scaledImageHeight / 2);
            default:
                return 0;
        }
    }

    /**
     * adjust image matrix as user drags or pinches
     */
    private void changeMatrixValue(Matrix matrix, ImageView view) {
        matrix.getValues(value);
        if (drawable == null)  return;

        int x = mIsCanvasRotated ? VALUE_INDEX_MOVED_DISTANCE_Y : VALUE_INDEX_MOVED_DISTANCE_X;
        int y = mIsCanvasRotated ? VALUE_INDEX_MOVED_DISTANCE_X : VALUE_INDEX_MOVED_DISTANCE_Y;

        // prevent view from getting out of image
        if (value[x] < width - scaledImageWidth - offsetX) {
            value[x] = width - scaledImageWidth - offsetX;
        }
        if (value[y] < height - scaledImageHeight - offsetY) {
            value[y] = height - scaledImageHeight - offsetY;
        }

        if (value[VALUE_INDEX_MOVED_DISTANCE_X] > 0 + offsetX) {
            value[VALUE_INDEX_MOVED_DISTANCE_X] = 0 + offsetX;
        }
        if (value[VALUE_INDEX_MOVED_DISTANCE_Y] > 0 + offsetY) {
            value[VALUE_INDEX_MOVED_DISTANCE_Y] = 0 + offsetY;
        }

        // set limits on expansion
        if (value[VALUE_INDEX_SCALE_RATE_X] > 2
                || value[VALUE_INDEX_SCALE_RATE_Y] > 2) {
            value[VALUE_INDEX_SCALE_RATE_X] = 2;
            value[VALUE_INDEX_SCALE_RATE_Y] = 2;
        }


        // if (imageWidth > width || imageHeight > height) {
        // if image is downscaled, call callbacks
        double scaleRate = (double)scaledImageWidth / fitScaledImageWidth;
        if(mListener != null && scaleRate < 1.0) {
            mListener.onZoomOut(scaleRate);
        }
        if (ZOOM_OUT_LIMIT_RATE < scaleRate &&
                scaledImageWidth < fitScaledImageWidth && mode == NONE) {
            if(mListener != null) {
                mListener.onZoomBackOn();
            }
            setImageFitOnView();
        }
        setCenter(false);

        matrix.setValues(value);
        setImageMatrix(matrix);
    }

    /**
     * get the space between to fingers
     * @param event User Motion event
     */
    private float spacing(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return FloatMath.sqrt(x * x + y * y);
    }

    private float spacing(float xTrans, float yTrans) {
        return FloatMath.sqrt(xTrans * xTrans + yTrans * yTrans);
    }

    /**
     *
     * get the middle point of two fingers
     *
     * @param point
     * @param event
     */
    private void midPoint(PointF point, MotionEvent event) {
        float x = event.getX(0) + event.getX(1);
        float y = event.getY(0) + event.getY(1);
        point.set(x / 2, y / 2);
    }

}