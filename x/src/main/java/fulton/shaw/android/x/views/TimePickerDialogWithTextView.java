package fulton.shaw.android.x.views;

import android.app.TimePickerDialog;
import android.content.Context;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TimePicker;

import java.util.Calendar;

import fulton.util.android.utils.StringFormatters;
import fulton.shaw.android.x.views.interfaces.ViewModifiable;
import fulton.util.android.views.interfaces.ViewTimeAware;
import fulton.util.android.utils.CalendarUtil;

/**
 * Created by 13774 on 8/9/2017.
 */

public class TimePickerDialogWithTextView extends AppCompatTextView implements ViewModifiable,ViewTimeAware {
    public Calendar getTime() {
        return mTime;
    }

    public void setTime(Calendar time) {
        mTime = time;
        updateAppearance();
    }

    protected Calendar mTime;

    protected boolean mPopup=true;

    public TimePickerDialogWithTextView(Context context) {
        super(context);
        init();
    }

    public TimePickerDialogWithTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public TimePickerDialogWithTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }


    protected void init()
    {
        mTime=Calendar.getInstance();
        CalendarUtil.timeTrimMillseconds(mTime);
        updateAppearance();
        this.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!mPopup)return;
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
        });
    }


    @Override
    public void setModifiable(boolean modifiable) {
        mPopup=modifiable;
    }

    @Override
    public boolean isModifiable() {
        return mPopup;
    }
    public void updateAppearance()
    {

        setText(StringFormatters.formatTimeWithCalendar(mTime));
    }
}
