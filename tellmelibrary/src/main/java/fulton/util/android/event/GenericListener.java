package fulton.util.android.event;

import android.os.Bundle;

/**
 * Created by 13774 on 7/16/2017.
 */

/**
 * 可以监听多个通知器，所以必须对来源加以区分
 */
public interface GenericListener{
    void applyListen(int listenerId, int eventType, Object... args);
}
