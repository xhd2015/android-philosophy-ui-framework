package fulton.shaw.android.x.viewgetter.transferview.viewadapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.Calendar;

import fulton.shaw.android.x.R;
import fulton.shaw.android.x.views.TimePickerDialogWithTextView;

/**
 * Created by 13774 on 8/9/2017.
 */

public class TextViewTimeCalendarAdapter extends ViewDatabaseAdapter<TimePickerDialogWithTextView,Calendar> {


    public TextViewTimeCalendarAdapter(TimePickerDialogWithTextView view) {
        super(view);
    }

    @Override
    public Calendar getValue() {
        return mView.getTime();
    }

    @Override
    public void setValue(Calendar value) {
        if(value!=null)
            mView.setTime(value);
    }

}
