package fulton.shaw.android.x.viewgetter.database_auto.checkers;

import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;

import fulton.shaw.android.x.views.ColorIndicator;
import fulton.shaw.android.x.views.DatePickerDialogWithTextView;
import fulton.shaw.android.x.views.DateTimePickerDialogWithTextView;
import fulton.shaw.android.x.views.ImageIndicator;
import fulton.shaw.android.x.views.TimePickerDialogWithTextView;
import fulton.util.android.utils.ViewUtil;

/**
 * Created by 13774 on 8/18/2017.
 */

public abstract class ShareableSingleViewGetChecker {
    public boolean check(View view)
    {
        if(view==null)
            throw new NullPointerException("view cannot be null for null is not a checkable view");
        else if(view instanceof CheckBox)
        {
            return checkCheckBox((CheckBox) view);
        } else if(view instanceof TimePickerDialogWithTextView) {
            return checkTimePicker((TimePickerDialogWithTextView) view);
        } else if(view instanceof DatePickerDialogWithTextView) {
            return checkDatePicker((DatePickerDialogWithTextView) view);
        } else if(view instanceof DateTimePickerDialogWithTextView) {
            return checkDateTimePicker((DateTimePickerDialogWithTextView) view);
        } else if(view instanceof EditText) {
            return checkEditText((EditText) view);
        }else if(view instanceof TextView){
            return checkTextView((TextView) view);
        }else if(view instanceof Spinner){
            return checkSpinner((Spinner) view);
        }else if(view instanceof RadioGroup){
            return checkRadioGroup((RadioGroup) view);
        }else if(view instanceof ColorIndicator){
            return checkColorIndicator((ColorIndicator) view);
        }else if(view instanceof ImageIndicator){
            return checkImageIndicator((ImageIndicator) view);
        }else if(view instanceof RatingBar){
            return checkRatingBar((RatingBar) view);
        }else{
            throw new UnsupportedClassVersionError(view.getClass() + "is not supported");
        }
    }

    public abstract boolean checkCheckBox(CheckBox view);
    public abstract boolean checkTimePicker(TimePickerDialogWithTextView view);
    public abstract boolean checkDatePicker(DatePickerDialogWithTextView view);
    public abstract boolean checkDateTimePicker(DateTimePickerDialogWithTextView view);
    public abstract boolean checkEditText(EditText view);
    public abstract boolean checkTextView(TextView view);
    public abstract boolean checkSpinner(Spinner view);
    public abstract boolean checkRadioGroup(RadioGroup view);
    public abstract boolean checkColorIndicator(ColorIndicator view);
    public abstract boolean checkImageIndicator(ImageIndicator view);
    public abstract boolean checkRatingBar(RatingBar view);
}
