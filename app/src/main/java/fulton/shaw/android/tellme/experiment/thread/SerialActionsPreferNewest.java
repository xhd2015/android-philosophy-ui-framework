package fulton.shaw.android.tellme.experiment.thread;

import java.util.concurrent.locks.Condition;

import fulton.util.android.utils.Util;
import fulton.util.android.thread.LockedObject;

/**
 * Created by 13774 on 7/14/2017.
 */

public class SerialActionsPreferNewest  extends  SerialActionDoer{

    private Runnable mCurRunning;
    private LockedObject<Runnable,Object> mNext=new LockedObject<>();//mNext should be observed
    private Condition mNextReadyCondition;
    private Thread mThreadToRunCurRunning;


    public static void HowToUse()
    {
        SerialActionsPreferNewest sa = new SerialActionsPreferNewest();
        sa.start();
        sa.putAction(new Runnable() {
            @Override
            public void run() {
               Util.logi("1 not shown");
            }
        });
        sa.putAction(new Runnable() {
            @Override
            public void run() {
                Util.logi("2 shown");
            }
        });
        try
        {
            Thread.sleep(100);
        }catch (Exception e)
        {

        }
        sa.putAction(new Runnable() {
            @Override
            public void run() {
                Util.logi("3 shown");
            }
        });
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        sa.cancel();


    }

    public SerialActionsPreferNewest()
    {
        mNextReadyCondition = mNext.getLock().newCondition();
    }

    @Override
    public void putAction(Runnable next)
    {
//        Util.logi("Put Action");
        mNext.getLock().lock();
        mNext.set_unsafe(next);
//        mNextReady.signalAll();//
        mNextReadyCondition.signalAll();
        mNext.getLock().unlock();
//        synchronized (mNextReady)
//        {
//            mNextReady.notifyAll();
//        }
    }


    @Override
    protected void duringRun() {
        if(mCurRunning!=null)//1. complish current running
        {
            while(mThreadToRunCurRunning.isAlive()) {
                try {
                    mThreadToRunCurRunning.join();
                } catch (InterruptedException e) {
                    //Log.d("THREAD INT", e.toString());
                }
            }
            //Util.logi("ThreadToRunCur stopped");
            mCurRunning = null;
//                mThreadToRunCurRunning = null;
        }
        //2. if next is null,sleep until next is avalaible
        //3. next is available,do that

//            }
        //Util.logi("Wait for PutAction");
        mNext.getLock().lock();
        while (mNext.isNull_unsafe()) {
            try {
                mNextReadyCondition.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        mCurRunning = mNext.get_unsafe();
        mNext.set_unsafe(null);
        mNext.getLock().unlock();

        mThreadToRunCurRunning = new Thread(mCurRunning);
        mThreadToRunCurRunning.start();
    }


}
