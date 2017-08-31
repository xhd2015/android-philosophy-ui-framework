package fulton.shaw.android.x.viewgetter.database_auto.valuegetters;

import java.util.Date;

import fulton.util.android.interfaces.ValueGetter;
import fulton.util.android.utils.CalendarUtil;

/**
 * Created by 13774 on 8/19/2017.
 */

public class ValueGetterCurrentSqliteDateTime extends ValueGetter<Object,String> {
    @Override
    public String getValue(Object o) {
        return CalendarUtil.SQLITE_DATETIME_FORMAT.format(new Date());
    }
}
