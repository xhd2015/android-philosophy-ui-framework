package fulton.shaw.android.x.views.interfaces;

import android.support.annotation.NonNull;

/**
 * Created by 13774 on 8/17/2017.
 */

public interface LinkedListenerAware<ArgType,V extends LinkedListener<ArgType>> {
    void seOnEventLinkedListener(V listener);
    V getOnEventLinkedListener();
}
