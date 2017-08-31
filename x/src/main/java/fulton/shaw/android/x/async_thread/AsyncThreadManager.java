package fulton.shaw.android.x.async_thread;

/**
 * Created by 13774 on 8/19/2017.
 */

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.HashMap;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import fulton.util.android.notations.TestNote;
import fulton.util.android.thread.LockedObject;

/**
 *   start at any time, call start
 *
 *   some operations wait on it to continue.call await()
 */

@TestNote(TestNote.STATE.TESTED_SUCCESS_WEAK)
/**
 *  we need provide a sync-hashmap
 *   hash
 */
public class AsyncThreadManager {

    /**
     * may block the current thread.
     */
    @Retention(RetentionPolicy.SOURCE)
    @Target(ElementType.METHOD)
    public @interface BlockingMethod{}

    private ReentrantLock mHashLock;
    private HashMap<String,LockedObject<Boolean,Condition[]>> mConditions;//String,Thread, when thread is null,we need to wait.
                                                //wait until Thread is put. when add,a thread,signalAll
    protected static final int ADD_CONDITION = 0;
    protected static final int COMPLISH_CONDITION = 1;



    public AsyncThreadManager()
    {
        mHashLock=new ReentrantLock();
        mConditions=new HashMap<>();
    }


    public void startThread(String conditionName,Runnable r)
    {
        addThread(conditionName,r).start();
    }

    /**
     *
     * @param waitedCondition
     * @param condition
     * @param r
     *
     *  if waitedCondition is null,it will be treated as completed.
     */
    @BlockingMethod
    public void waitThread(String waitedCondition,String condition,Runnable r)
    {
        Thread rThread=addThread(condition,r);
        waitOnCondition(waitedCondition);
        rThread.start();
    }
    @BlockingMethod
    public void waitThread(String[] conditions,String condition,Runnable r)
    {
        Thread rThread=addThread(condition,r);
        waitOnConditions(conditions);
        rThread.start();
    }
    public void asyncWaitThread(final String waitedCondition, final String condition, final Runnable r)
    {

        final Thread rThread=addThread(condition,r);
        new Thread(new Runnable() {
            @Override
            public void run() {
                waitOnCondition(waitedCondition);
                rThread.start();
            }
        }).start();
    }
    public void asyncWaitThread(final String[] waitedConditions, final String condition, final Runnable r)
    {

        final Thread rThread=addThread(condition,r);
        new Thread(new Runnable() {
            @Override
            public void run() {
                waitOnConditions(waitedConditions);
                rThread.start();
            }
        }).start();
    }




    /**
     *  if the spec condition does not exists,then it will be waited.
     * @param waitedCondition
     */
    @BlockingMethod
    public void waitOnCondition(String waitedCondition)
    {
        LockedObject<Boolean, Condition[]> lThread= getCondition(waitedCondition);
        Condition add=lThread.getAdditionalData()[ADD_CONDITION];
        Condition complish=lThread.getAdditionalData()[COMPLISH_CONDITION];
        lThread.lock();
        while (lThread.isNull_unsafe()) //wait for add
        {
            try {
                add.await();
            } catch (InterruptedException e) {
                //just gone
            }
        }
        while (!lThread.get_unsafe()) //wait for complishment
        {
            try {
                complish.await();
            } catch (InterruptedException e) {
                //just contine
            }
        }
        lThread.unlock();

    }

    @BlockingMethod
    public void waitOnConditions(String...conditions)
    {
        for(String con:conditions)
            waitOnCondition(con);
    }

    protected Thread addThread(String conditionName,final Runnable r)
    {
        final LockedObject<Boolean, Condition[]> lThread= getCondition(conditionName);
        Thread t=new Thread(new Runnable() {
            @Override
            public void run() {
                r.run();
                lThread.lock();
                lThread.set_unsafe(true);
                Condition complish=lThread.getAdditionalData()[COMPLISH_CONDITION];
                complish.signalAll();
                lThread.unlock();
            }
        });
        Condition add=lThread.getAdditionalData()[ADD_CONDITION];
        lThread.lock();
        lThread.set_unsafe(false);
        add.signalAll();//I have been added.
        lThread.unlock();
        return t;
    }

    /**
     *   if the key is not present,then a null object will be put.
     * @param conditionName
     * @return
     */
    protected   LockedObject<Boolean,Condition[]> getCondition(String conditionName)
    {
        //===must be atomic
        mHashLock.lock();
        LockedObject<Boolean,Condition[]> lStatus=mConditions.get(conditionName);
        if(lStatus==null)
        {
            lStatus=new LockedObject<>(null);
            mConditions.put(conditionName,lStatus);
            lStatus.setAdditionalData(new Condition[]{lStatus.newCondition(),lStatus.newCondition()});//on for add,one for remove
        }
        mHashLock.unlock();
        return lStatus;
    }

}
