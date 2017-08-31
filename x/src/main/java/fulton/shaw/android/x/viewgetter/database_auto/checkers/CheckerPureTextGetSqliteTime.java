package fulton.shaw.android.x.viewgetter.database_auto.checkers;

import android.support.annotation.NonNull;
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

public class CheckerPureTextGetSqliteTime extends ShareableSingleViewGetChecker {
    @Override
    public boolean checkCheckBox(CheckBox view) {
        throw new UnsupportedClassVersionError(view.getClass() + "is not supported");
    }

    @Override
    public boolean checkTimePicker(TimePickerDialogWithTextView view) {

        throw new UnsupportedClassVersionError(view.getClass() + "is not supported");
    }

    @Override
    public boolean checkDatePicker(DatePickerDialogWithTextView view) {

        throw new UnsupportedClassVersionError(view.getClass() + "is not supported");
    }

    @Override
    public boolean checkDateTimePicker(DateTimePickerDialogWithTextView view) {

        throw new UnsupportedClassVersionError(view.getClass() + "is not supported");
    }

    @Override
    public boolean checkEditText(EditText view) {
        return allAreDigits(view.getText());
    }

    @Override
    public boolean checkTextView(TextView view) {
        return allAreDigits(view.getText());
    }

    @Override
    public boolean checkSpinner(Spinner view) {

        throw new UnsupportedClassVersionError(view.getClass() + "is not supported");
    }

    @Override
    public boolean checkRadioGroup(RadioGroup view) {

        throw new UnsupportedClassVersionError(view.getClass() + "is not supported");
    }

    @Override
    public boolean checkColorIndicator(ColorIndicator view) {

        throw new UnsupportedClassVersionError(view.getClass() + "is not supported");
    }

    @Override
    public boolean checkImageIndicator(ImageIndicator view) {

        throw new UnsupportedClassVersionError(view.getClass() + "is not supported");
    }

    @Override
    public boolean checkRatingBar(RatingBar view) {
        throw new UnsupportedClassVersionError(view.getClass() + "is not supported");
    }

    public static boolean allAreDigits(@NonNull CharSequence s)
    {
        for(int i=0;i<s.length();i++)
        {
            char ch=s.charAt(i);
            if(ch>='0' && ch<='9')
                continue;
            return false;
        }
        return true;
    }
}
