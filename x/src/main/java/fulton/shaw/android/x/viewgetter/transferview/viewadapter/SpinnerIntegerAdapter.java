package fulton.shaw.android.x.viewgetter.transferview.viewadapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Spinner;

/**
 * Created by 13774 on 8/9/2017.
 */

public class SpinnerIntegerAdapter extends ViewDatabaseAdapter<Spinner,Integer> {

    public SpinnerIntegerAdapter(Spinner view) {
        super(view);
    }

    @Override
    public Integer getValue() {
        return mView.getSelectedItemPosition();
    }

    @Override
    public void setValue(Integer value) {
        if(value!=null)
            mView.setSelection(value);
    }

}
