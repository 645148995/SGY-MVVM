package www.viewscenestv.com.ijkvideoview;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by viewscenes on 19/04/2017.
 */
public abstract class CompositeCallback<I> {
    protected final List<I> mCallbacks = new CopyOnWriteArrayList<>();

    public void register(I callback) {
        mCallbacks.add(callback);
    }

    public void unregister(I callback) {
        mCallbacks.remove(callback);
    }

    void clear() {
        mCallbacks.clear();
    }
}
