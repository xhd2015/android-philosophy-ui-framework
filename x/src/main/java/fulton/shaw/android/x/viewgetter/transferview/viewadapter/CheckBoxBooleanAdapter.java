package fulton.shaw.android.x.viewgetter.transferview.viewadapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.CheckBox;

import fulton.shaw.android.x.R;

/**
 * Created by 13774 on 8/10/2017.
 */

public class CheckBoxBooleanAdapter extends ViewDatabaseAdapter<CheckBox,Boolean> {

    public CheckBoxBooleanAdapter(CheckBox view) {
        super(view);
    }

    @Override
    public Boolean getValue() {
        return mView.isChecked();
    }

    @Override
    public void setValue(Boolean value) {
        if(value!=null)
            mView.setChecked(value);
    }

}
