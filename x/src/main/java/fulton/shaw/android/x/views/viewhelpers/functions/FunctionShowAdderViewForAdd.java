package fulton.shaw.android.x.views.viewhelpers.functions;

import android.widget.AdapterView;

import fulton.shaw.android.x.views.viewhelpers.AdapterViewWrapper;
import fulton.shaw.android.x.views.viewhelpers.DatabaseAdapterView;

/**
 * Created by 13774 on 8/18/2017.
 */

public class FunctionShowAdderViewForAdd<V extends AdapterView> extends AdapterViewFunction<V> {
    @Override
    public void apply(DatabaseAdapterView<V> wrapper, int position) {
        wrapper.setArgOpViewIndex(-1);//==next position argument
        wrapper.setAdderShown(true);
    }
}
