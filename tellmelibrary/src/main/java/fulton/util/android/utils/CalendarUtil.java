package fulton.util.android.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import fulton.util.android.notations.HelperMethods;
import fulton.util.android.notations.HelperStaticMethods;
import fulton.util.android.notations.MayConsumeTimePleaseUtilize;

/**
 * Created by 13774 on 8/8/2017.
 */

public class CalendarUtil {
    public static final long ONE_SECOND=1000;
    public static final long ONE_MINUTE=60*ONE_SECOND;
    public static final long ONE_HOUR=60*ONE_MINUTE;
    public static final long ONE_DAY=24*ONE_HOUR;
    public static final long ONE_WEEK=7*ONE_DAY;

    @MayConsumeTimePleaseUtilize("static init")
    public static final SimpleDateFormat SQLITE_DATE_FORMAT =new SimpleDateFormat("yyyy-MM-dd");
    public static final SimpleDateFormat SQLITE_TIME_FORMAT =new SimpleDateFormat("HH:mm:ss");
    public static final SimpleDateFormat SQLITE_DATETIME_FORMAT =new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");


    public static long computeDiffAsDays(Calendar c1,Calendar c2)
    {
        return computeDiff(c1,c2)/ONE_DAY;
    }
    public static long computeDiff(Calendar c1,Calendar c2)
    {
        return c1.getTimeInMillis()-c2.getTimeInMillis();
    }
    public static boolean isDateToDay(Calendar calendar)
    {

        Calendar calendarToday=getTodayBegin();
        long diff=computeDiff(calendar,calendarToday);
        return diff>=0 && diff<ONE_DAY;
    }
    public static Calendar getDateOfSqlString(String date) {
        Date d= null;
        try {
            d = SQLITE_DATE_FORMAT.parse(date);
        } catch (ParseException e) {
            throw new Error("Cannot Parse:"+date);
        }
        return new GregorianCalendar(d.getYear()+1900,d.getMonth(),d.getDate());
    }

    public static Calendar getDateOfString(String date) throws ParseException {
            Date d= SQLITE_DATE_FORMAT.parse(date);
            return new GregorianCalendar(d.getYear()+1900,d.getMonth(),d.getDate());
    }
    public static Calendar getTimeOfString(String time) throws ParseException {
            Date d= SQLITE_TIME_FORMAT.parse(time);
            return new GregorianCalendar(1970,0,1,d.getHours(),d.getMinutes(),d.getSeconds());
    }
    public static Calendar getDateTimeOfString(String datetime) throws ParseException {
            Date d= SQLITE_DATETIME_FORMAT.parse(datetime);
            return new GregorianCalendar(d.getYear()+1900,d.getMonth(),d.getDate(),d.getHours(),d.getMinutes(),d.getSeconds());
    }

    public static String getDateString(Calendar calendar)
    {
        return SQLITE_DATE_FORMAT.format(calendar.getTime());
    }
    public static String getDateTimeString(Calendar calendar)
    {
        return SQLITE_DATETIME_FORMAT.format(calendar.getTime());
    }
    public static String getTimeString(Calendar calendar)
    {
        return SQLITE_TIME_FORMAT.format(calendar.getTime());
    }






    @HelperMethods
    public static Calendar timeInMillisToCalendar(long time)
    {
        Calendar calendar=Calendar.getInstance();
        calendar.setTimeInMillis(time);
        return calendar;
    }

    @HelperMethods
    public static Calendar timeToDayBegin(Calendar calendar)
    {
        calendar.set(Calendar.HOUR_OF_DAY,0);
        calendar.set(Calendar.MINUTE,0);
        calendar.set(Calendar.SECOND,0);
        calendar.set(Calendar.MILLISECOND,0);
        return calendar;
    }
    public static Calendar timeToWeekBegin(Calendar calendar)
    {
        long theTime=calendar.getTimeInMillis();
        calendar.setFirstDayOfWeek(Calendar.MONDAY);//小米will not be correct.
        calendar.set(Calendar.DAY_OF_WEEK,Calendar.MONDAY);
        calendar.set(Calendar.HOUR_OF_DAY,0);
        calendar.set(Calendar.MINUTE,0);
        calendar.set(Calendar.SECOND,0);
        calendar.set(Calendar.MILLISECOND,0);
        if(theTime<calendar.getTimeInMillis())
            calendar.add(Calendar.DAY_OF_MONTH,-7);
        return calendar;
    }
    public static Calendar timeToDayEnd(Calendar calendar)
    {
        calendar.set(Calendar.HOUR_OF_DAY,23);
        calendar.set(Calendar.MINUTE,59);
        calendar.set(Calendar.SECOND,59);
        calendar.set(Calendar.MILLISECOND,999);
        return calendar;
    }
    public static Calendar timeToNextDayBegin(Calendar calendar)
    {
        calendar.set(Calendar.HOUR_OF_DAY,23);
        calendar.set(Calendar.MINUTE,59);
        calendar.set(Calendar.SECOND,60);
        calendar.set(Calendar.MILLISECOND,0);
        return calendar;
    }
    public static Calendar timeTrimMillseconds(Calendar calendar)
    {
        calendar.set(Calendar.MILLISECOND,0);
        return calendar;
    }


    @HelperStaticMethods
    public static Calendar calDiff(Calendar a,Calendar b)
    {
        Calendar calendar=Calendar.getInstance();
        calendar.setTimeInMillis(a.getTimeInMillis() - b.getTimeInMillis());
        return calendar;
    }

    @HelperMethods
    public static long calDiffAsDays(Calendar a,Calendar b)
    {
        return a.getTimeInMillis()/ONE_DAY - b.getTimeInMillis()/ONE_DAY;
    }

    @HelperMethods
    public static Calendar getTodayBegin()
    {
        Calendar calendar=Calendar.getInstance();
        timeToDayBegin(calendar);
        return calendar;
    }
    @HelperMethods
    public static Calendar getTodayEnd()
    {
        Calendar calendar=Calendar.getInstance();
        timeToDayEnd(calendar);
        return calendar;
    }

    public static Calendar getThisWeekBegin()
    {
        Calendar calendar=Calendar.getInstance();
        timeToWeekBegin(calendar);
        return calendar;
    }

    public static Calendar getNextDayBegin()
    {
        Calendar calendar=Calendar.getInstance();
        timeToNextDayBegin(calendar);
        return calendar;
    }

    @HelperMethods
    public static Calendar cloneCalendar(Calendar calendar)
    {
        GregorianCalendar gregorianCalendar = new GregorianCalendar(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH),
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                calendar.get(Calendar.SECOND)
        );
        gregorianCalendar.set(Calendar.MILLISECOND,calendar.get(Calendar.MILLISECOND));
        return gregorianCalendar;
    }
}
