package fulton.util.android.interfaces.typetransfers;

import fulton.util.android.utils.CalendarUtil;
import fulton.util.android.utils.StringFormatters;

/**
 * Created by 13774 on 8/19/2017.
 */

public class TransferStringToLongDateTime implements ValueTypeTransfer<String,Long> {
    @Override
    public Long transferPositive(String s) {
        throw new UnsupportedOperationException("Cannot transfer from String to Long Time");
    }

    @Override
    public String transferNagetive(Long aLong) {
        return aLong==null?"NULL Time":
                StringFormatters.formatDateTimeWithCalendar(CalendarUtil.timeInMillisToCalendar(aLong));
    }
}
