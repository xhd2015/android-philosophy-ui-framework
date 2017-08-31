package fulton.shaw.android.x.views;

import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatTextView;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

/**
 * Created by 13774 on 8/10/2017.
 */

public class DefinedLists {
    public static final Class<View>[] VIEW_CLASS_LIST =new Class[]{
            EditText.class,
            TextView.class,
            DatePickerDialogWithTextView.class,
            TimePickerDialogWithTextView.class,
            DateTimePickerDialogWithTextView.class,//4
            ColorIndicator.class,//5
            ImageIndicator.class,
            Spinner.class,
            RadioGroup.class,//8
            AppCompatEditText.class,//9
            AppCompatTextView.class,//10
    };
}
