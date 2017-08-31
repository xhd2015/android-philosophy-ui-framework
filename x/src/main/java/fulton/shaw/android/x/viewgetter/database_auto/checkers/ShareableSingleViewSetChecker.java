package fulton.shaw.android.x.viewgetter.database_auto.checkers;

import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.Calendar;

import fulton.shaw.android.x.views.ColorIndicator;
import fulton.shaw.android.x.views.DatePickerDialogWithTextView;
import fulton.shaw.android.x.views.DateTimePickerDialogWithTextView;
import fulton.shaw.android.x.views.ImageIndicator;
import fulton.shaw.android.x.views.TimePickerDialogWithTextView;

/**
 * Created by 13774 on 8/18/2017.
 */

public abstract class ShareableSingleViewSetChecker {
    public boolean check(View view,Object value)
    {
        if(view==null)
            throw new NullPointerException("view cannot be null for null is not a checkable view");
        else if(view instanceof CheckBox)
        {
            return checkCheckBox((CheckBox) view,(boolean)(Boolean)value);
        } else if(view instanceof TimePickerDialogWithTextView) {
            return checkTimePicker((TimePickerDialogWithTextView) view,(Calendar)value);
        } else if(view instanceof DatePickerDialogWithTextView) {
            return checkDatePicker((DatePickerDialogWithTextView) view, (Calendar) value);
        } else if(view instanceof DateTimePickerDialogWithTextView) {
            return checkDateTimePicker((DateTimePickerDialogWithTextView) view, (Calendar) value);
        } else if(view instanceof EditText) {
            return checkEditText((EditText) view, (String) value);
        }else if(view instanceof TextView){
            return checkTextView((TextView) view, (String) value);
        }else if(view instanceof Spinner){
            return checkSpinner((Spinner) view, (Integer) value);
        }else if(view instanceof RadioGroup){
            return checkRadioGroup((RadioGroup) view, (Integer) value);
        }else if(view instanceof ColorIndicator){
            return checkColorIndicator((ColorIndicator) view, (Integer) value);
        }else if(view instanceof ImageIndicator){
            return checkImageIndicator((ImageIndicator) view, (Integer) value);
        }else if(view instanceof RatingBar){
            return checkRatingBar((RatingBar) view, (Float) value);
        }else{
            throw new UnsupportedClassVersionError(view.getClass() + "is not supported");
        }
    }

    public abstract boolean checkCheckBox(CheckBox view,boolean value);
    public abstract boolean checkTimePicker(TimePickerDialogWithTextView view, Calendar value);
    public abstract boolean checkDatePicker(DatePickerDialogWithTextView view,Calendar value);
    public abstract boolean checkDateTimePicker(DateTimePickerDialogWithTextView view,Calendar value);
    public abstract boolean checkEditText(EditText view,String value);
    public abstract boolean checkTextView(TextView view,String value);
    public abstract boolean checkSpinner(Spinner view,int value);
    public abstract boolean checkRadioGroup(RadioGroup view,int value);
    public abstract boolean checkColorIndicator(ColorIndicator view,int value);
    public abstract boolean checkImageIndicator(ImageIndicator view,int value);
    public abstract boolean checkRatingBar(RatingBar view,float value);
}
