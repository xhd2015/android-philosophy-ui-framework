package fulton.shaw.android.x.viewgetter.database_auto.checkers;

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

/**
 * Created by 13774 on 8/18/2017.
 */

public class CheckerGetNotNull extends ShareableSingleViewGetChecker {

    @Override
    public boolean checkCheckBox(CheckBox view) {
        return true;
    }

    @Override
    public boolean checkTimePicker(TimePickerDialogWithTextView view) {
        return view.getTime()!=null;
    }

    @Override
    public boolean checkDatePicker(DatePickerDialogWithTextView view) {
        return view.getTime()!=null;
    }

    @Override
    public boolean checkDateTimePicker(DateTimePickerDialogWithTextView view) {
        return view.getTime()!=null;
    }

    @Override
    public boolean checkEditText(EditText view) {
        return view.length()!=0;
    }

    @Override
    public boolean checkTextView(TextView view) {
        return view.length()!=0;
    }

    @Override
    public boolean checkSpinner(Spinner view) {
        return view.getSelectedItemPosition()!=Spinner.INVALID_POSITION;
    }

    @Override
    public boolean checkRadioGroup(RadioGroup view) {
        return view.getCheckedRadioButtonId()!=-1;
    }

    @Override
    public boolean checkColorIndicator(ColorIndicator view) {
        return view.getCurrentSelected()!=-1;
    }

    @Override
    public boolean checkImageIndicator(ImageIndicator view) {
        return view.getCurrentSelected()!=-1;
    }

    @Override
    public boolean checkRatingBar(RatingBar view) {
        return true;
    }
}
