package timersassignment.simplegallery.image;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.LruCache;
import android.widget.ImageView;

import java.lang.ref.WeakReference;

import timersassignment.simplegallery.R;

/**
 *
 * Singletone cache image loader
 *
 * @author Byungchul, Cho
 * @version 1.0,
 */
public class CachedImageLoader {
    private static CachedImageLoader mInstance;
    private LruCache<String, Bitmap> mMemoryCache;
    private Bitmap mPlaceHolderBitmap;
    public static final long CACHE_ID_TILE = 1;
    public static final long CACHE_ID_DETAIL = 2;

    private CachedImageLoader() {
    }

    public static CachedImageLoader getInstance() {
        if(mInstance == null) {
            mInstance = new CachedImageLoader();
        }
        return mInstance;
    }
    public void loadImage(ImageView imageView, String path, long cacheId) {
        if(imageView == null || TextUtils.isEmpty(path)) {
            return;
        }
        ensureMemoryCache();
        if(cancelPotentialWork(path, imageView)) {
            String cacheKey = getCacheKey(cacheId, path);
            final Bitmap bitmap = getBitmapFromMemCache(cacheKey);
            if (bitmap != null) {
                if(imageView instanceof CanvasRotateImageView) {
                    String[] keyValues = cacheKey.split(",");
                    ((CanvasRotateImageView)imageView).setImageBitmap(bitmap,
                            keyValues[0]);
                } else {
                    imageView.setImageBitmap(bitmap);
                }
            } else {
                Resources res = imageView.getContext().getResources();
                initPlaceHolderBitmap(res, imageView.getMeasuredWidth(), imageView.getMeasuredHeight());
                final BitmapWorkerTask task = new BitmapWorkerTask(imageView);
                final AsyncDrawable asyncDrawable =
                        new AsyncDrawable(res, mPlaceHolderBitmap, task);
                if(imageView instanceof CanvasRotateImageView) {
                   ((CanvasRotateImageView)imageView).setImageDrawable(asyncDrawable, null);
                } else {
                    imageView.setImageDrawable(asyncDrawable);
                }
                task.execute(path, String.valueOf(cacheId));
            }
        }
    }

    private void initPlaceHolderBitmap(Resources res, int reqWidth, int reqHeight) {
        if(mPlaceHolderBitmap == null) {
            mPlaceHolderBitmap = //BitmapFactory.decodeResource(res, R.drawable.placeholder);
                    ImageUtils.decodeSampledBitmapFromResource(res, R.drawable.placeholder, 360, 360);
        }
    }

    private void ensureMemoryCache() {
        if(mMemoryCache == null) {
            final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
            final int cacheSize = maxMemory / 4;
            mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
                @Override
                protected int sizeOf(String key, Bitmap bitmap) {
                    // The cache size will be measured in kilobytes rather than
                    // number of items.
                    return bitmap.getByteCount() / 1024;
                }
            };
        }
    }

    private void addBitmapToMemoryCache(String key, Bitmap bitmap) {
        if (getBitmapFromMemCache(key) == null) {
            mMemoryCache.put(key, bitmap);
        }
    }

    private Bitmap getBitmapFromMemCache(String key) {
        return mMemoryCache.get(key);
    }

    // to handle Concurrency

    private class BitmapWorkerTask extends AsyncTask<String, Void, Bitmap> {
        private final WeakReference<ImageView> imageViewReference;
        private String path;
        long cacheId;

        public BitmapWorkerTask(ImageView imageView) {
            // Use a WeakReference to ensure the ImageView can be garbage collected
            imageViewReference = new WeakReference<ImageView>(imageView);
        }

        // Decode image in background.
        @Override
        protected Bitmap doInBackground(String... params) {
            if(imageViewReference == null) {
                return null;
            }
            final ImageView imageView = imageViewReference.get();
            if(imageView == null) {
                return null;
            }
            int width = imageView.getWidth();
            int height = imageView.getHeight();

            if(width == 0 || height == 0) {
                width = ImageUtils.getDisplayWidthPixel(imageView.getContext());
                height = ImageUtils.getDisplayHeightPixel(imageView.getContext());
            }

            path = params[0];
            cacheId = Long.valueOf(params[1]);

            return ImageUtils.decodeSampledBitmapFromPath(path, width, height);
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if (isCancelled()) {
                bitmap = null;
            }
            if (imageViewReference != null && bitmap != null) {
                final ImageView imageView = imageViewReference.get();
                final BitmapWorkerTask bitmapWorkerTask =
                        getBitmapWorkerTask(imageView);
                if (this == bitmapWorkerTask && imageView != null) {
                    String key = getCacheKey(cacheId, path);
                    addBitmapToMemoryCache(key, bitmap);
                    if(imageView instanceof CanvasRotateImageView) {
                        ((CanvasRotateImageView)imageView).setImageBitmap(bitmap, path);
                    } else {
                        imageView.setImageBitmap(bitmap);
                    }
                }
            }
        }
    }

    private static String getCacheKey(long cacheId, String path) {
        return path + "," +cacheId;
    }

    private static class AsyncDrawable extends BitmapDrawable {
        private final WeakReference<BitmapWorkerTask> bitmapWorkerTaskReference;

        public AsyncDrawable(Resources res, Bitmap bitmap, BitmapWorkerTask bitmapWorkerTask) {
            super(res, bitmap);
            bitmapWorkerTaskReference =
                    new WeakReference<BitmapWorkerTask>(bitmapWorkerTask);
        }

        public BitmapWorkerTask getBitmapWorkerTask() {
            return bitmapWorkerTaskReference.get();
        }
    }

    public static boolean cancelPotentialWork(String path, ImageView imageView) {
        final BitmapWorkerTask bitmapWorkerTask = getBitmapWorkerTask(imageView);

        if (bitmapWorkerTask != null) {
            final String bitmapData = bitmapWorkerTask.path;
            // If bitmapData is not yet set or it differs from the new data
            if (TextUtils.isEmpty(bitmapData) || bitmapData.equals(path)) {
                // Cancel previous task
                bitmapWorkerTask.cancel(true);
            } else {
                // The same work is already in progress
                return false;
            }
        }
        // No task associated with the ImageView, or an existing task was cancelled
        return true;
    }

    private static BitmapWorkerTask getBitmapWorkerTask(ImageView imageView) {
        if (imageView != null) {
            final Drawable drawable = imageView.getDrawable();
            if (drawable instanceof AsyncDrawable) {
                final AsyncDrawable asyncDrawable = (AsyncDrawable) drawable;
                return asyncDrawable.getBitmapWorkerTask();
            }
        }
        return null;
    }

}
