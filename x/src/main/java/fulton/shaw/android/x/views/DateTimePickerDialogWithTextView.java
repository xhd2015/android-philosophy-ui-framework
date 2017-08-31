package fulton.shaw.android.x.views;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.Calendar;

import fulton.shaw.android.x.R;
import fulton.util.android.utils.StringFormatters;
import fulton.shaw.android.x.views.interfaces.ViewModifiable;
import fulton.shaw.android.x.views.interfaces.ViewPopupDialog;
import fulton.util.android.views.interfaces.ViewTimeAware;
import fulton.util.android.utils.CalendarUtil;

/**
 * Created by 13774 on 8/9/2017.
 */

public class DateTimePickerDialogWithTextView extends LinearLayout implements ViewTimeAware,ViewModifiable,ViewPopupDialog{
    protected Calendar mTime;
    protected boolean mModifiable;
    protected TextView mDateView,mTimeView;

    public DateTimePickerDialogWithTextView(Context context) {
        super(context);
        //automatically add two textview and set thier listener
        init(context,null);
    }

    public DateTimePickerDialogWithTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context,attrs);
    }

    public DateTimePickerDialogWithTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context,attrs);
    }

    public DateTimePickerDialogWithTextView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context,attrs);
    }


    /**
     * orientation
     */
    protected void init(Context context,AttributeSet attrs)
    {
        //set two click listners
        mTime=Calendar.getInstance();
        CalendarUtil.timeTrimMillseconds(mTime);
        //default oritentation is horizontal
        int customDateId=0,customTimeId=0;
        mModifiable=true;
        if(attrs!=null)
        {
            TypedArray a=context.getTheme().obtainStyledAttributes(attrs, R.styleable.CustomViews,0,0);
            try{
               customDateId=a.getResourceId(R.styleable.CustomViews_customDateViewId,0);
                customTimeId=a.getResourceId(R.styleable.CustomViews_customTimeViewId,0);
                mModifiable=a.getBoolean(R.styleable.CustomViews_modifiable,true);
            }finally {
                a.recycle();
            }
        }
        if(customDateId==0)
        {
            mDateView=new TextView(context);
            addView(mDateView);
        }else{
            mDateView= (TextView) findViewById(customDateId);
        }
        if(customTimeId==0)
        {
            mTimeView=new TextView(context);
            addView(mTimeView);
        }else{
            mTimeView= (TextView) findViewById(customTimeId);
        }

        OnClickListener listener=new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!mModifiable)return;
                if(v==mDateView)
                {
                    final DatePickerDialog dialog=new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                            mTime.set(year,month,dayOfMonth);
                            updateAppearance();
                        }
                    },mTime.get(Calendar.YEAR),mTime.get(Calendar.MONTH),mTime.get(Calendar.DAY_OF_MONTH));
                    dialog.show();
                }else if(v==mTimeView){
                    final TimePickerDialog dialog=new TimePickerDialog(getContext(),new TimePickerDialog.OnTimeSetListener(){
                        @Override
                        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                            mTime.set(Calendar.HOUR_OF_DAY,hourOfDay);
                            mTime.set(Calendar.MINUTE,minute);
                            updateAppearance();
                        }
                    },mTime.get(Calendar.HOUR_OF_DAY),mTime.get(Calendar.MINUTE),true);
                    dialog.show();
                }
            }
        };
        mDateView.setOnClickListener(listener);
        mTimeView.setOnClickListener(listener);

        updateAppearance();
    }

    @Override
    public void setModifiable(boolean modifiable) {
        mModifiable=modifiable;
    }

    @Override
    public boolean isModifiable() {
        return mModifiable;
    }

    @Override
    public void setTime(Calendar calendar) {
        mTime=calendar;
        updateAppearance();
    }

    @Override
    public Calendar getTime() {
        return mTime;
    }

    public void updateAppearance()
    {
        this.mDateView.setText(StringFormatters.formatDateWithCalendar(mTime));
        this.mTimeView.setText(StringFormatters.formatTimeWithCalendar(mTime));
    }
}
