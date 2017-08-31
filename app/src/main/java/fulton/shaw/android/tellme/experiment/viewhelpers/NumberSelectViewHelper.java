package fulton.shaw.android.tellme.experiment.viewhelpers;

import android.app.Activity;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import fulton.shaw.android.tellme.R;
import fulton.util.android.event.GenericListener;
import fulton.util.android.event.GenericNotifier;
import fulton.util.android.thread.LockedObject;
import fulton.shaw.android.tellme.experiment.thread.LooperThread;
import fulton.util.android.utils.Util;

/**
 * Created by 13774 on 7/16/2017.
 */

public class NumberSelectViewHelper extends GenericNotifier{
    private Activity mActivity;
    private LayoutInflater mInflater;
    private View mNumberView;
    private Handler mFastUpdater;

    private LooperThread mWatchInputComplishment;
    private LockedObject<Long,Object> mLastChangeTime=new LockedObject<>();

    private int mMaxValue,mMinValue;

    private ImageButton mUpButton,mDownButton;
    private EditText  mInputView;
    private boolean mInitStatus;

    /**
     * When input is complished, this event happens
     */
    public static final int  TYPE_INPUT_COMPLISHED=0;
    public static final  int TYPE_COUNT=3;


    public static final long WATCH_COMPLISHMENT_MIN_TIME=300;

    public NumberSelectViewHelper(Activity activity,View numberView,final int initValue,int min,int max){
        this(activity,numberView,initValue);
        this.setMaxValue(max);
        this.setMinValue(min);
    }
    public NumberSelectViewHelper(Activity activity,View numberView,final int initValue) {
        super(TYPE_COUNT);

        mInitStatus=true;

        mActivity=activity;
        mInflater=activity.getLayoutInflater();
        mFastUpdater = new Handler(mActivity.getMainLooper());
        mLastChangeTime.set(System.currentTimeMillis());

        mWatchInputComplishment=new LooperThread() {
            @Override
            protected void duringRun() {
                if(mInitStatus)return;
                long curTime=System.currentTimeMillis();
                if(curTime - mLastChangeTime.get() >= WATCH_COMPLISHMENT_MIN_TIME)
                {
                    notifyListeners(TYPE_INPUT_COMPLISHED,NumberSelectViewHelper.this);
                    this.pauseRunning();
                }
                try {
                    Thread.sleep(WATCH_COMPLISHMENT_MIN_TIME);
                }catch (InterruptedException e){

                }

            }
        };


        mNumberView = numberView;
        mUpButton = (ImageButton) numberView.findViewById(R.id.upButton);
        mDownButton = (ImageButton) numberView.findViewById(R.id.downButton);
        mInputView = (EditText) numberView.findViewById(R.id.number);

        updateNumberOfView(initValue);

        mUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        int numberOfView=getNumberOfView();
                        if(numberOfView >= mMaxValue)
                            numberOfView=mMinValue;
                        else
                            numberOfView++;

                        updateNumberOfViewAsSoonAsPossible(numberOfView);
                    }
                }).start();

            }
        });
        mDownButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        int numberOfView=getNumberOfView();
                        if(numberOfView <= mMinValue)
                            numberOfView=mMaxValue;
                        else
                            numberOfView--;
                        updateNumberOfViewAsSoonAsPossible(numberOfView);
                    }
                }).start();

            }
        });

        mInputView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(final View v,final boolean hasFocus) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        if(!hasFocus)
                        {
                            mWatchInputComplishment.pauseRunning();
                            notifyListeners(TYPE_INPUT_COMPLISHED,NumberSelectViewHelper.this);
                        }
                    }
                }).start();

            }
        });

        mInputView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(final Editable s) { //主要是多线程的模型，如果无focus，则直接通知完成；有focus，什么也不做
                    mInitStatus=false;
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            mLastChangeTime.set(System.currentTimeMillis());
                            mWatchInputComplishment.resumeRunning();
                        }
                    }).start();


            }
        });


        mWatchInputComplishment.start();
        mWatchInputComplishment.pauseRunning();
     }

    /**
     * sometimes make long.do not use in intializing
     * @param newNumber
     */
     protected void updateNumberOfViewAsSoonAsPossible(final int newNumber)
     {
         mFastUpdater.postAtFrontOfQueue(new Runnable() {
             @Override
             public void run() {
            updateNumberOfView(newNumber);
             }
         });
     }

     public void updateNumberOfView(final int number)
     {
         mInputView.setText("" + number);
         mInputView.setSelection(mInputView.getText().length());
     }


    public void setMaxValue(int maxValue)
    {
        mMaxValue = maxValue;
    }
    public void setMinValue(int minValue)
    {
        mMinValue = minValue;
    }

    /**
     *  based on the maxium value & minimum value,return a valid value
     * @return
     */
    public int getNumberOfView(){
        String text=mInputView.getText().toString();
//        Util.logi("text="+text);
        int trueValue=0;
        if(text.length()>0)
            trueValue=Integer.parseInt(text);
//        Util.logi("trueValue="+trueValue);
//        Util.logi("min="+mMinValue);
//        Util.logi("max="+mMaxValue);
        if(trueValue<mMinValue)
            trueValue=mMinValue;
        if(trueValue>mMaxValue)
            trueValue=mMaxValue;
//        Util.logi("returned value="+trueValue);
        return trueValue;
    }

    public void destroyJoin()
    {
        mWatchInputComplishment.cancel();
        try {
            mWatchInputComplishment.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    public void destroyJoinAsync()
    {
        new Thread(new Runnable() {
            @Override
            public void run() {
                destroyJoin();
            }
        }).start();
    }


    //================How To Use Method
    public static void HowToUse(Activity activity,View numberView)
    {
        NumberSelectViewHelper ns=new NumberSelectViewHelper(activity,numberView,2017);

        ns.setMaxValue(3000);
        ns.setMinValue(1);

        ns.setOnNotifyListener(-1, NumberSelectViewHelper.TYPE_INPUT_COMPLISHED, new GenericListener() {
            @Override
            public void applyListen(int listenerId, int eventType, Object... args) {
                Util.logi("Input finished[HowToUse]");
            }
        });
    }

}
