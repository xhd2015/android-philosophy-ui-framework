package fulton.shaw.android.tellme.experiment.thread;

import java.util.concurrent.locks.Condition;

import fulton.util.android.thread.LockedObject;

/**
 * Created by 13774 on 7/16/2017.
 */

public abstract class LooperThread extends Thread{
    protected LockedObject<Boolean,Object> mCancelled=new LockedObject<>(false);//shared between multiple threads,needs synchronize
    protected LockedObject<Boolean,Object> mPaused = new LockedObject<>(false);
    protected Condition mPausedSetToFalse = mPaused.getLock().newCondition();


    @Override
    public void run()
    {
        beforeRun();
        while (!mCancelled.get())
        {
            mPaused.getLock().lock();
            while (mPaused.get_unsafe()==true)
            {
                try {
                    mPausedSetToFalse.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            mPaused.getLock().unlock();
            duringRun();
        }
        afterRun();
    }

    protected void beforeRun(){}
    protected abstract void duringRun();
    protected void afterRun(){}

    public void cancel()
    {
        mCancelled.set(true);
        if(mPaused.get()==true) {
            resumeRunning();
        }
    }

    public void pauseRunning()
    {
        mPaused.set(true);
    }

    public boolean paused()
    {
        return mPaused.get();
    }
    public boolean cancelled()
    {
        return mCancelled.get();
    }

    public void resumeRunning()
    {
        mPaused.getLock().lock();
        mPaused.set_unsafe(false);
        mPausedSetToFalse.signalAll();
        mPaused.getLock().unlock();
    }



}
