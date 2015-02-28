package timersassignment.simplegallery.image;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.Checkable;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import timersassignment.simplegallery.R;

/**
 *
 * Contains Image Informations and is applied to GridView
 *
 * @author Byungchul, Cho
 * @version 1.0
 */
public class ImageGridItemView extends FrameLayout implements Checkable{

    private ImageView mPhotoView;
    private TextView mTitleView;
    private CheckBox mCheckBox;

    public ImageGridItemView(Context context) {
        super(context);
    }

    public ImageGridItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ImageGridItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setTitle(String title) {
        boolean valid = !TextUtils.isEmpty(title);
        if(mTitleView == null) {
            mTitleView = getTitleView();
            addView(mTitleView);
        }
        mTitleView.setText(title);
        mTitleView.setVisibility(valid ? View.VISIBLE : View.GONE);
        mTitleView.bringToFront();
        if(valid) {
            mTitleView.setText(title);
        }
    }

    public void setCheckBox(boolean show) {
        if(mCheckBox == null) {
            mCheckBox = new CheckBox(getContext());
            mCheckBox.setClickable(false);
            mCheckBox.setFocusable(false);
            int size = getResources().getDimensionPixelSize(R.dimen.common_checkbox_size);
            int margin = getResources().getDimensionPixelSize(R.dimen.common_checkbox_padding);
            LayoutParams params = new LayoutParams(size, size);
            params.rightMargin = margin;
            params.topMargin = margin;
            params.gravity = Gravity.TOP | Gravity.RIGHT;
            mCheckBox.setLayoutParams(params);
            addView(mCheckBox);
        }
        mCheckBox.bringToFront();
        mCheckBox.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    public void setPhoto(String path, int orientation) {
        if(mPhotoView == null) {
            mPhotoView = getImageView();
            addView(mPhotoView);
        }
        CachedImageLoader.getInstance().loadImage(mPhotoView, path, CachedImageLoader.CACHE_ID_TILE);
    }

    private ImageView getImageView() {
        ImageView imageView = new CanvasRotateImageView(getContext());
        imageView.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        return imageView;
    }

    private TextView getTitleView() {
        int padding = getResources().getDimensionPixelSize(R.dimen.image_title_padding);

        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.BOTTOM;

        TextView textView = new TextView(getContext());
        textView.setLayoutParams(params);
        textView.setPadding(padding, padding, padding, padding);
        textView.setBackgroundColor(0x55000000);
        textView.setTextColor(Color.WHITE);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.image_title_text_size));
        return textView;
    }

    @Override
    public void setChecked(boolean checked) {
        mCheckBox.setChecked(checked);
    }

    @Override
    public boolean isChecked() {
        return mCheckBox.isChecked();
    }

    @Override
    public void toggle() {
        mCheckBox.toggle();;
    }
}
