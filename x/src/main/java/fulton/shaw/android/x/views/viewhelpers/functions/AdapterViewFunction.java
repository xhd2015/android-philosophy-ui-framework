package fulton.shaw.android.x.views.viewhelpers.functions;

import android.widget.AdapterView;

import fulton.shaw.android.x.views.viewhelpers.AdapterViewWrapper;
import fulton.shaw.android.x.views.viewhelpers.DatabaseAdapterView;

/**
 * Created by 13774 on 8/18/2017.
 */

public abstract class AdapterViewFunction<V extends AdapterView> {
    public abstract void apply( DatabaseAdapterView<V> wrapper, int position);

}
