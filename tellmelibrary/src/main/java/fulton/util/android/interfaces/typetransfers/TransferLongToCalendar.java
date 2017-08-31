package fulton.util.android.interfaces.typetransfers;

import java.util.Calendar;

import fulton.util.android.utils.CalendarUtil;

/**
 * Created by 13774 on 8/9/2017.
 */

public class TransferLongToCalendar implements ValueTypeTransfer<Long,Calendar> {
    @Override
    public Calendar transferPositive(Long aLong) {
        return aLong==null?null:CalendarUtil.timeInMillisToCalendar(aLong);
    }

    @Override
    public Long transferNagetive(Calendar calendar) {
        return calendar==null?null:calendar.getTimeInMillis();
    }
}
