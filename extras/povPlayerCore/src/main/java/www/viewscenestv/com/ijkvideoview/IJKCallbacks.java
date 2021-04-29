package www.viewscenestv.com.ijkvideoview;
import androidx.annotation.Keep;
/**
 * Created by Doublel on 09/11/2017.
 */

@Keep
public interface IJKCallbacks {
    @Keep
    interface ScheduledScreenshotCallback {
        void onSuccess(int id, String path);

        void onCancel(int id);

        void onFail(int id, Throwable throwable);
    }
}
