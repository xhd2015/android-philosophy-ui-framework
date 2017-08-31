package fulton.shaw.android.x.views.viewhelpers.functions;

import android.widget.AdapterView;

import java.util.ArrayList;

import fulton.shaw.android.x.viewgetter.database_auto.ViewGetter;
import fulton.shaw.android.x.views.viewhelpers.DatabaseAdapterView;

/**
 * Created by 13774 on 8/18/2017.
 */

public class FunctionShowAdderViewForUpdate<V extends AdapterView> extends AdapterViewFunction<V> {

    @Override
    public void apply(DatabaseAdapterView<V> wrapper, int position) {
        wrapper.fillAdderValue(wrapper.collectItemValueFromCursor(position,ArrayList.class,wrapper.getAdderViewGetters()));
        wrapper.setAdderShown(true);
    }
}

