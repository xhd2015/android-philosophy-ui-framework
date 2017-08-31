package fulton.util.android.interfaces.typetransfers;

import java.text.ParseException;
import java.util.Calendar;

import fulton.util.android.utils.CalendarUtil;

/**
 * Created by 13774 on 8/14/2017.
 */

public class TransferCalendarToSqliteDateTime implements ValueTypeTransfer<Calendar,String> {
    @Override
    public String transferPositive(Calendar calendar) {
        if(calendar!=null)
            return CalendarUtil.getDateTimeString(calendar);
        else
            throw new NullPointerException();
    }

    @Override
    public Calendar transferNagetive(String s) {
        try {
            return CalendarUtil.getDateTimeOfString(s);
        } catch (ParseException e) {
            throw new Error(s+" cannot be parsed as calendar");
        }
    }
}
