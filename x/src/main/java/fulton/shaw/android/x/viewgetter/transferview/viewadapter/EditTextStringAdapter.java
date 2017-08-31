package fulton.shaw.android.x.viewgetter.transferview.viewadapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.EditText;

import fulton.shaw.android.x.R;

/**
 * Created by 13774 on 8/9/2017.
 */

public class EditTextStringAdapter extends ViewDatabaseAdapter<EditText,String> {

    public EditTextStringAdapter(EditText view) {
        super(view);
    }

    @Override
    public String getValue() {
        return mView.getText().toString();
    }

    @Override
    public void setValue(String value) {
            mView.setText(value);
    }

}
