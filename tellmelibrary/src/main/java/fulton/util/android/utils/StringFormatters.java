package fulton.util.android.utils;

import java.util.Calendar;

import fulton.util.android.notations.HelperMethods;

/**
 * Created by 13774 on 8/1/2017.
 */

public class StringFormatters {
    public static final String DATE="%d年%02d月%02d日";
    public static final String TIME="%02d时%02d分";



    //============
    @HelperMethods
    public static String formatDateWithCalendar(Calendar calendar)
    {
        return String.format(StringFormatters.DATE,calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH)+1,calendar.get(Calendar.DAY_OF_MONTH));
    }
    public static String formatTimeWithCalendar(Calendar calendar)
    {
        return String.format(StringFormatters.TIME,calendar.get(Calendar.HOUR_OF_DAY),calendar.get(Calendar.MINUTE));
    }
    public static String formatTimeWithTimeDouble(double time)
    {
        return formatTimeWithCalendar(CalendarUtil.timeInMillisToCalendar((long)(double)time));
    }
    public static String formatDateTimeWithCalendar(Calendar calendar,String sep)
    {
        return formatDateWithCalendar(calendar)+sep+formatTimeWithCalendar(calendar);
    }
    public static String formatDateTimeWithCalendar(Calendar calendar)
    {
        return formatDateTimeWithCalendar(calendar," ");
    }

}
