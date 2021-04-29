package com.easefun.povplayer.core.gifmaker;

import android.graphics.Bitmap;
import android.graphics.Matrix;

import com.easefun.povplayer.core.util.PolyvLog;

import java.io.ByteArrayOutputStream;
import java.util.List;

public class GifMaker {
    private static final String TAG = GifMaker.class.getSimpleName();
    private OnGifListener mGifListener = null;
    private AnimatedGifEncoder mEncoder = null;

    public void makeGif(List<Bitmap> source, float sx, float sy, float ro) {
        mEncoder = new AnimatedGifEncoder();
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        mEncoder.start(bos);
        mEncoder.setRepeat(0);
        mEncoder.setFrameRate(10);
        long startTime = System.currentTimeMillis();
        final int length = source.size();
        int w = 0, h = 0;
        for (int i = 0; i < length; i++) {
            if (source.size() == 0)
                break;
            Bitmap bmp = source.get(i);
            if (bmp == null) {
                continue;
            }
            Bitmap newBitmap = null;
            try {
                long startTime1 = System.currentTimeMillis();
                Matrix matrix = new Matrix();
                matrix.postScale(sx, sy);
                matrix.postRotate(ro);
                newBitmap = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), matrix, false);
                if (w == 0 || h == 0) {
                    w = newBitmap.getWidth();
                    h = newBitmap.getHeight();
                }
                mEncoder.addFrame(newBitmap);
                if (mGifListener != null) {
                    mGifListener.onMake(i + 1, length, (int) (System.currentTimeMillis() - startTime1));
                }
            } catch (Exception e) {
                PolyvLog.i(TAG, "makeGif", e);
                System.gc();
                break;
            } finally {
                if (newBitmap != null && !newBitmap.isRecycled()) {
                    newBitmap.recycle();
                }
            }
        }
        if (!mEncoder.started)
            return;
        mEncoder.finish();
        if (mGifListener != null)
            mGifListener.onFinish(bos.toByteArray(), w, h, (int) ((System.currentTimeMillis() - startTime) / 1000));
    }

    public void cancel() {
        if (mEncoder != null) {
            mEncoder.finish();
        }
    }

    public void setOnGifListener(OnGifListener listener) {
        mGifListener = listener;
    }

    public interface OnGifListener {
        /**
         * @param current 完成数
         * @param total   总数
         * @param makeMs  完成所需时间
         */
        public void onMake(int current, int total, int makeMs);

        public void onError(Throwable throwable);

        /**
         * @param data         gif图片数据
         * @param w            图片的宽
         * @param h            图片的高
         * @param finishSecond 完成所需时间
         */
        public void onFinish(byte[] data, int w, int h, int finishSecond);
    }
}
