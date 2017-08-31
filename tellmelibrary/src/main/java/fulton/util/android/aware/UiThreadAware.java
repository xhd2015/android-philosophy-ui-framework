package fulton.util.android.aware;

/**
 * Created by 13774 on 8/5/2017.
 */

public interface UiThreadAware {
    Thread getUiThread();
    void runOnUiThread(Runnable runnable);
}
