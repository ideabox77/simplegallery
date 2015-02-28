package timersassignment.simplegallery.detail;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
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
    private Matrix matrix = new Matrix();
    private Matrix moveMatrix = new Matrix();

    private static final int NONE = 0;
    private static final int DRAG = 1;
    private static final int ZOOM = 2;
    private int mode = NONE;

    private PointF start = new PointF();
    private PointF mid = new PointF();
    private float oldDist = 1f;
    private static final int WIDTH = 0;
    private static final int HEIGHT = 1;

    public static final int VALUE_INDEX_SCALE_RATE_X = 0;
    public static final int VALUE_INDEX_SCALE_RATE_Y = 4;
    public static final int VALUE_INDEX_MOVED_DISTANCE_X = 2;
    public static final int VALUE_INDEX_MOVED_DISTANCE_Y = 5;

    private float[] value = new float[9];
    private Drawable drawable;
    private int width;
    private int height;
    private int imageWidth;
    private int imageHeight;
    private int scaledImageWidth;
    private int scaledImageHeight;

    private boolean mIsCanvasRotated;
    private float mRotatedImageRate = (float)1.0;
    private float offsetX;
    private float offsetY;

    private DetailViewFragment.ZoomCallbacks mListener;

    public PinchToZoomImageView(Context context) {
        this(context, null);
        setOnTouchListener(this);
        setScaleType(ScaleType.MATRIX);
    }

    public PinchToZoomImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
        setOnTouchListener(this);
        setScaleType(ScaleType.MATRIX);
    }

    public PinchToZoomImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setOnTouchListener(this);
        setScaleType(ScaleType.MATRIX);
    }

    void setZoomListener(DetailViewFragment.ZoomCallbacks zoomListener) {
        mListener = zoomListener;
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        init();
    }

    protected void init() {
        this.matrix.getValues(value); // 매트릭스 값
        width = this.getWidth(); // 뷰크기
        height = this.getHeight();
        drawable = this.getDrawable();
        if(drawable == null) {
            return;
        }
        mIsCanvasRotated = getOrientation() != 0 && getOrientation() != 180;
        if(mIsCanvasRotated) {
            imageWidth = drawable.getIntrinsicHeight(); //실제 이미지 너비
            imageHeight = drawable.getIntrinsicWidth(); //실제 이미지 높이

            if(imageHeight > imageWidth) {
                mRotatedImageRate = (float)imageHeight / (float)imageWidth;
            } else{
                mRotatedImageRate = (float)imageWidth / (float)imageHeight;
            }
        } else {
            imageWidth = drawable.getIntrinsicWidth();
            imageHeight = drawable.getIntrinsicHeight();
        }
        if (imageWidth > width || imageHeight > height) {
            setImageFitOnView();
        }
        setCenter(true);
        matrix.setValues(value);
        setImageMatrix(matrix);
    }

    private void setImageFitOnView() {

        int target = imageWidth > imageHeight ? WIDTH : HEIGHT;
        if (target == WIDTH) {
            value[VALUE_INDEX_SCALE_RATE_Y] = (float)width / (float)imageWidth;
            value[VALUE_INDEX_SCALE_RATE_X] = value[VALUE_INDEX_SCALE_RATE_Y];
        } else if (target == HEIGHT) {
            value[VALUE_INDEX_SCALE_RATE_Y] = (float)height / (float)imageHeight;
            value[VALUE_INDEX_SCALE_RATE_X] = value[VALUE_INDEX_SCALE_RATE_Y];
        }

        scaledImageWidth = (int) (imageWidth * value[VALUE_INDEX_SCALE_RATE_X]);
        scaledImageHeight = (int) (imageHeight * value[VALUE_INDEX_SCALE_RATE_Y]);

    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        ImageView view = (ImageView) v;
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                moveMatrix.set(matrix);
                start.set(event.getX(), event.getY());
                mode = DRAG;
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
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
            case MotionEvent.ACTION_MOVE:
                if (mode == DRAG) {
                    matrix.set(moveMatrix);

                    float xTrans = mIsCanvasRotated ? event.getY() - start.y : event.getX() - start.x;
                    float yTrans = mIsCanvasRotated ? start.x  - event.getX() : event.getY() - start.y;

                    matrix.postTranslate(xTrans, yTrans);
                } else if (mode == ZOOM) {
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

    private void setCenter(boolean init) {
        scaledImageWidth = (int) (imageWidth * value[VALUE_INDEX_SCALE_RATE_X]);
        scaledImageHeight = (int) (imageHeight * value[VALUE_INDEX_SCALE_RATE_Y]);

            if(scaledImageWidth <= width) {
                float scaleOffset = mIsCanvasRotated ?
                        (float)(scaledImageHeight / 2) - (float)(scaledImageWidth / 2) : 0;

                value[VALUE_INDEX_MOVED_DISTANCE_X] =
                        (float)(width / 2) - (float)(scaledImageWidth / 2) - scaleOffset;
                if(init) {
                    offsetX = mIsCanvasRotated ? value[VALUE_INDEX_MOVED_DISTANCE_X] : 0;
                }
            }
            if(scaledImageHeight <= height) {
                float scaleOffset = mIsCanvasRotated ?
                        (float)(scaledImageWidth / 2) - (float)(scaledImageHeight / 2): 0;
                value[VALUE_INDEX_MOVED_DISTANCE_Y] =
                        (float)(height / 2) - (float)(scaledImageHeight / 2) - scaleOffset;
                if(init) {
                    offsetY = mIsCanvasRotated ? value[VALUE_INDEX_MOVED_DISTANCE_Y] : 0;
                }
        }
    }

    private void changeMatrixValue(Matrix matrix, ImageView view) {
        matrix.getValues(value);
        if (drawable == null)  return;

        int x = mIsCanvasRotated ? VALUE_INDEX_MOVED_DISTANCE_Y : VALUE_INDEX_MOVED_DISTANCE_X;
        int y = mIsCanvasRotated ? VALUE_INDEX_MOVED_DISTANCE_X : VALUE_INDEX_MOVED_DISTANCE_Y;

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

        if (value[VALUE_INDEX_SCALE_RATE_X] > 1 * mRotatedImageRate
                || value[VALUE_INDEX_SCALE_RATE_Y] > 1 * mRotatedImageRate) {
            value[VALUE_INDEX_SCALE_RATE_X] = 1 * mRotatedImageRate;
            value[VALUE_INDEX_SCALE_RATE_Y] = 1 * mRotatedImageRate;
        }

        if (imageWidth > width || imageHeight > height) {
            double scaleRate = (double)scaledImageWidth / (double)width;
            if(mListener != null) {
                if(scaleRate < 1.0) {
                    mListener.onZoomOut(scaleRate);
                }
            }

            if (scaledImageWidth < width && mode == NONE) {
                if(mListener != null) {
                    mListener.onZoomBackOn();
                }
                setImageFitOnView();
            }
        } else {
            if (value[VALUE_INDEX_SCALE_RATE_X] < 1) value[VALUE_INDEX_SCALE_RATE_X] = 1;
            if (value[VALUE_INDEX_SCALE_RATE_Y] < 1) value[VALUE_INDEX_SCALE_RATE_Y] = 1;
        }
        setCenter(false);

        matrix.setValues(value);
        setImageMatrix(matrix);
    }

    private float spacing(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return FloatMath.sqrt(x * x + y * y);
    }

    private void midPoint(PointF point, MotionEvent event) {
        float x = event.getX(0) + event.getX(1);
        float y = event.getY(0) + event.getY(1);
        point.set(x / 2, y / 2);
    }

}