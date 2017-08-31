package fulton.shaw.android.x.viewgetter.transferview.viewadapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import fulton.shaw.android.x.R;
import fulton.shaw.android.x.views.ColorIndicator;

/**
 * Created by 13774 on 8/10/2017.
 */

public class ColorIndicatorIntegerAdapter extends ViewDatabaseAdapter<ColorIndicator,Integer> {

    public ColorIndicatorIntegerAdapter(ColorIndicator view) {
        super(view);
    }

    @Override
    public Integer getValue() {
        return mView.getCurrentSelected();
    }

    @Override
    public void setValue(Integer value) {
        if(value!=null)
            mView.setCurrentSelected(value);
    }

}
