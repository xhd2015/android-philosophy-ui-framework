package fulton.shaw.android.x.views.interfaces.proxiers;

import fulton.shaw.android.x.views.interfaces.LinkedListener;
import fulton.shaw.android.x.views.interfaces.LinkedListenerAware;

/**
 * Created by 13774 on 8/17/2017.
 */

public class LinkedListenerAwareProxy<ArgType,V extends LinkedListener<ArgType>> implements LinkedListenerAware<ArgType,V> {
    protected V mListener;

    @Override
    public void seOnEventLinkedListener(V listener) {
        listener.setLastListener(mListener);
        mListener=listener;
    }

    @Override
    public V getOnEventLinkedListener() {
        return mListener;
    }
}
