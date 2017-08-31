package fulton.shaw.android.x.viewgetter.database_auto.valuegetters;

import fulton.util.android.interfaces.ValueGetter;

/**
 * Created by 13774 on 8/19/2017.
 */

public class ValueGetterCurrentDateTimeLong extends ValueGetter<Object,Long> {
    @Override
    public Long getValue(Object o) {
        return System.currentTimeMillis();
    }
}
