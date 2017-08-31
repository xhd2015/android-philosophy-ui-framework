package fulton.util.android.interfaces.typetransfers;

import java.util.Calendar;

import fulton.util.android.utils.StringFormatters;


/**
 * Created by 13774 on 8/14/2017.
 */

public class TransferCalendarToString implements ValueTypeTransfer<Calendar,String> {
    @Override
    public String transferPositive(Calendar calendar) {
        if(calendar!=null)
            return StringFormatters.formatDateTimeWithCalendar(calendar);
        else
            return "Null Calendar";
    }

    @Override
    public Calendar transferNagetive(String s) {
        throw new UnsupportedOperationException(" String to Calendar not supported.");
    }
}
