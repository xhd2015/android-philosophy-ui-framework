package fulton.util.android.interfaces.typetransfers;

import java.util.Calendar;

import fulton.util.android.utils.CalendarUtil;

/**
 * Created by 13774 on 8/9/2017.
 */

public class TransferCalendarToLong implements ValueTypeTransfer<Calendar,Long> {
    @Override
    public Long transferPositive(Calendar calendar) {
        return calendar==null?null:calendar.getTimeInMillis();
    }

    @Override
    public Calendar transferNagetive(Long aLong) {
        return aLong==null?null: CalendarUtil.timeInMillisToCalendar(aLong);
    }
}
