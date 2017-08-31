package fulton.shaw.android.x.views.viewhelpers.functions;

import android.widget.AdapterView;

import java.util.ArrayList;

import fulton.shaw.android.x.views.viewhelpers.DatabaseAdapterView;
import fulton.util.android.utils.Util;

/**
 * Created by 13774 on 8/20/2017.
 */

public class FunctionClearAdderView<V extends AdapterView> extends AdapterViewFunction<V>{
    private ArrayList mValues;

    public FunctionClearAdderView(ArrayList values)
    {
        mValues=values;
    }

    @Override
    public void apply(DatabaseAdapterView<V> wrapper, int position) {
        wrapper.fillAdderValue(mValues);
    }
}
