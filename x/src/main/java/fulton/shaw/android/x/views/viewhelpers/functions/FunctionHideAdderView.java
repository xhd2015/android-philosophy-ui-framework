package fulton.shaw.android.x.views.viewhelpers.functions;

import android.widget.AdapterView;

import java.util.ArrayList;

import fulton.shaw.android.x.views.viewhelpers.DatabaseAdapterView;

/**
 * Created by 13774 on 8/18/2017.
 */

/**
 *   hide the adderview,and clear the value if clear value is given and index indicating that in updating mode.
 * @param <V>
 */
public class FunctionHideAdderView<V extends AdapterView> extends AdapterViewFunction<V> {
    private ArrayList mClearValues;

    public FunctionHideAdderView(ArrayList clearValues) {
        mClearValues = clearValues;
    }
    public FunctionHideAdderView(){}

    @Override
    public void apply(DatabaseAdapterView<V> wrapper, int position) {
        if(mClearValues!=null && wrapper.getArgOpViewIndex()!=-1)
        {
            wrapper.setArgOpViewIndex(-1);
            wrapper.fillAdderValue(mClearValues);
        }
        wrapper.setAdderShown(false);
    }
}
