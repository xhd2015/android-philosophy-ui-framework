package fulton.shaw.android.x.views.interfaces;

import fulton.util.android.notations.CoreMethodOfThisClass;
import fulton.util.android.notations.OverrideToCustomBehaviour;

/**
 * Created by 13774 on 8/17/2017.
 */

public abstract class LinkedListener<ArgType> {
    protected LinkedListener mLastListener;
    protected boolean mCancelled;


    /**
     *   this is the right method where a class that owns the listener as a field must call when some event is triggered.
     * @param arg
     */
    @CoreMethodOfThisClass
    public void doFunction(ArgType arg)
    {
        if(!mCancelled)
        {
            sublistenerFunction(arg);
        }
        if(mLastListener!=null)
            mLastListener.doFunction(arg);
    }
    @OverrideToCustomBehaviour
    protected abstract void sublistenerFunction(ArgType arg);

    public LinkedListener<ArgType> getLastListener() {
        return mLastListener;
    }

    public void setLastListener(LinkedListener<ArgType> lastListener) {
        mLastListener=lastListener;
    }

    public boolean isCancelled() {
        return mCancelled;
    }

    public void setCancelled(boolean cancelled) {
        mCancelled = cancelled;
    }
}
