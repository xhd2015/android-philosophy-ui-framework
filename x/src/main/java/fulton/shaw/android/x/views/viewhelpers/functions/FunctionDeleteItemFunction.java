package fulton.shaw.android.x.views.viewhelpers.functions;

import android.widget.AdapterView;

import fulton.shaw.android.x.views.viewhelpers.DatabaseAdapterView;

/**
 * Created by 13774 on 8/18/2017.
 */

public class FunctionDeleteItemFunction<V extends AdapterView> extends AdapterViewFunction<V> {
    @Override
    public void apply(DatabaseAdapterView<V> wrapper, int position) {
        if(!wrapper.deleteItem(position))
            wrapper.onCommitDatabaseFailed();
    }
}
