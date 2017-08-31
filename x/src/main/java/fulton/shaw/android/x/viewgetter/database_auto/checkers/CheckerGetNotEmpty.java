package fulton.shaw.android.x.viewgetter.database_auto.checkers;

import android.widget.EditText;
import android.widget.TextView;

/**
 * Created by 13774 on 8/18/2017.
 */

/**
 *  this is
 */
public class CheckerGetNotEmpty extends CheckerGetNotNull {
    @Override
    public boolean checkEditText(EditText view) {
        return view.length()>0;
    }

    @Override
    public boolean checkTextView(TextView view) {
        return view.length()>0;
    }
}
