package fulton.shaw.android.x.viewgetter.transferview.viewadapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import fulton.shaw.android.x.R;

/**
 * Created by 13774 on 8/9/2017.
 */

public class TextViewStringAdapter extends ViewDatabaseAdapter<TextView,String> {


    public TextViewStringAdapter(TextView view) {
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
