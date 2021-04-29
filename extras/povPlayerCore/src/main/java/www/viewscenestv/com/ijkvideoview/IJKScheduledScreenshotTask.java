package www.viewscenestv.com.ijkvideoview;

/**
 * Created by Doublel on 09/11/2017.
 */

public class IJKScheduledScreenshotTask {
    private static int sLastId = 0;

    private final int mId;
    private final long mJsvt;
    private final String mPath;
    private final IJKCallbacks.ScheduledScreenshotCallback mCallback;

    public IJKScheduledScreenshotTask(final long jsvt, final String path,
                                   final IJKCallbacks.ScheduledScreenshotCallback callback) {
        mId = allocId();
        mJsvt = jsvt;
        mPath = path;
        mCallback = callback;
    }

    private static synchronized int allocId() {
        sLastId++;
        return sLastId;
    }

    public int getId() {
        return mId;
    }

    public String getPath() {
        return mPath;
    }

    public void onSuccess() {
        mCallback.onSuccess(mId, mPath);
    }

    public void onCancel() {
        mCallback.onCancel(mId);
    }

    public void onFail(Throwable throwable) {
        mCallback.onFail(mId, throwable);
    }

}
