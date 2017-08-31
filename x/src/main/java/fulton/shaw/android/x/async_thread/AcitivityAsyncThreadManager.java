package fulton.shaw.android.x.async_thread;

import android.app.Activity;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Objects;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import fulton.util.android.notations.Incomplete;
import fulton.util.android.notations.TestNote;
import fulton.util.android.thread.LockedObject;
import fulton.util.android.utils.Util;

/**
 * Created by 13774 on 8/19/2017.
 */

@TestNote(TestNote.STATE.TESTED_SUCCESS_WEAK)
/**
 *  test case:
 *
private int mA=-1;
 private int mB=-1;
 private int mC=-1;
 public void testAsyncManager()
 {
 AcitivityAsyncThreadManager manager=new AcitivityAsyncThreadManager(this);


 manager.startThread("set:mA=-2", new Runnable() {
@Override
public void run() {
mA=-2;
Util.logi("set:mA=-2 end");
}
});
 manager.asyncWaitThread("set:mA=-2", "set:mB=mA-1", new Runnable() {
@Override
public void run() {
mB=mA-1;
Util.logi("set:mB=mA-1 end");
}
});
 manager.asyncWaitThread("set:mB=mA-1", "set:mC=2*mB", new Runnable() {
@Override
public void run() {
mC=2*mB;
Util.logi("set:mC=2*mB end");
}
});
 Util.logi("hello");
 manager.asyncWaitThread(new String[]{
 "set:mA=-2", "set:mB=mA-1","set:mC=2*mB"
 }, "showAll", new Runnable() {
@Override
public void run() {
Util.logi("mA,mB,mC:"+mA+","+mB+","+mC);
}
});
 //onDestroy
 //manager.waitOnConditions(...[all conditions]...)
 }


 */
public class AcitivityAsyncThreadManager extends AsyncThreadManager {
    private Activity mActivity;

    public AcitivityAsyncThreadManager(Activity activity) {
        super();
        mActivity = activity;
    }

    /**
     * record the thread and getByGetter started.
     *
     * @param condition
     * @param r
     * @see {@link super#startThread(String, Runnable)}
     */
    public void startThreadOnUiThread(final String condition, final Runnable r) {
        mActivity.runOnUiThread(addThreadOnUiThread(condition, r));
    }

    @BlockingMethod
    public void waitThreadOnUiThread(String waitedCondition, String condition, Runnable r) {
        Runnable wrapper = addThreadOnUiThread(condition, r);
        waitOnCondition(waitedCondition);
        mActivity.runOnUiThread(wrapper);
    }

    /**
     * @param waitedCondition
     * @param condition
     * @param r               will ensure that the conditon exists at return.
     */
    public void asyncWaitThreadOnUiThread(final String waitedCondition, final String condition, final Runnable r) {
        final Runnable wrapper = addThreadOnUiThread(condition, r);
        new Thread(new Runnable() {
            @Override
            public void run() {
                waitOnCondition(waitedCondition);
                mActivity.runOnUiThread(wrapper);
            }
        }).start();
    }


    protected Runnable addThreadOnUiThread(final String condition, final Runnable r)
    {
        final LockedObject<Boolean,Condition[]> lock=getCondition(condition);
        Runnable wrapped=new Runnable() {
            @Override
            public void run() {
                r.run();
                lock.lock();
                lock.set_unsafe(true);
                lock.getAdditionalData()[COMPLISH_CONDITION].signalAll();
                lock.unlock();
            }
        };
        lock.lock();
        lock.getAdditionalData()[ADD_CONDITION].signalAll();
        lock.unlock();
        return wrapped;
    }
}
