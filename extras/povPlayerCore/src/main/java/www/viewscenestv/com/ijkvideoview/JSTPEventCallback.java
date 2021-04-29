package www.viewscenestv.com.ijkvideoview;


import androidx.annotation.Keep;

import java.util.HashMap;
import java.util.Map;

@Keep
public class JSTPEventCallback {
    private static final Map<Integer, Callback> CALLBACK_MAP = new HashMap<>();
    private static int sHandler = 1;

    public static synchronized int register(Callback callback) {
        int handler = sHandler;
        CALLBACK_MAP.put(handler, callback);
        sHandler++;

        return handler;
    }

    public static synchronized void unregister(int handler) {
        CALLBACK_MAP.remove(handler);
    }

    static void onJSTPEvent(int handler, int cmd, int data, Params params) {
        Callback callback;
        synchronized (JSTPEventCallback.class) {
            callback = CALLBACK_MAP.get(handler);
        }
        if (callback != null) {
            callback.onJSTPEvent(cmd, data, params);
        }
    }

    @Keep
    public interface Callback {
        void onJSTPEvent(int cmd, int data, Params params);
    }

    @Keep
    public static class Params {
        private final int mInt;
        private final String mStr;

        public Params(final int anInt, final String str) {
            mInt = anInt;
            mStr = str;
        }

        @Override
        public String toString() {
            return "{"
                   + "mInt: " + mInt
                   + ", mStr: " + mStr
                   + "}";
        }
    }
}
