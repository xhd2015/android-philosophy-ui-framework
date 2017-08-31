package fulton.shaw.android.x.views;

import android.app.DatePickerDialog;
import android.content.Context;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;
import android.view.View;
import android.widget.DatePicker;

import java.util.Calendar;

import fulton.util.android.utils.StringFormatters;
import fulton.shaw.android.x.views.interfaces.ViewModifiable;
import fulton.util.android.views.interfaces.ViewTimeAware;
import fulton.util.android.utils.CalendarUtil;

/**
 * Created by 13774 on 8/9/2017.
 */

public class DatePickerDialogWithTextView extends AppCompatTextView implements ViewModifiable,ViewTimeAware {
    public Calendar getTime() {
        return mTime;
    }

    public void setTime(Calendar time) {
        mTime = time;
        setText(StringFormatters.formatDateWithCalendar(mTime));
    }


    protected boolean mPopup=true;
    protected Calendar mTime;
    public DatePickerDialogWithTextView(Context context) {
        super(context);
        init();
    }

    public DatePickerDialogWithTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public DatePickerDialogWithTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    protected void init()
    {
        mTime= CalendarUtil.getTodayBegin();
        mTime=Calendar.getInstance();
        updateAppearance();
        this.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!mPopup)return;
                final DatePickerDialog dialog=new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        mTime.set(year,month,dayOfMonth);
                        updateAppearance();
                    }
                },mTime.get(Calendar.YEAR),mTime.get(Calendar.MONTH),mTime.get(Calendar.DAY_OF_MONTH));
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
        setText(StringFormatters.formatDateWithCalendar(mTime));
    }
}
