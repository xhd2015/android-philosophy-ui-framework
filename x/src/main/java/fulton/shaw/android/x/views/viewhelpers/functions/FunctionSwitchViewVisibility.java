package fulton.shaw.android.x.views.viewhelpers.functions;

import android.widget.AdapterView;

import fulton.shaw.android.x.views.viewhelpers.DatabaseAdapterView;

/**
 * Created by 13774 on 8/19/2017.
 */

public class FunctionSwitchViewVisibility<V extends AdapterView> extends AdapterViewFunction<V> {
    @Override
    public void apply(DatabaseAdapterView<V> wrapper, int position) {
        wrapper.setShown(!wrapper.isShown());
    }
}
