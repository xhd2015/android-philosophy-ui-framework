package fulton.shaw.android.x.fragments.filter_conditions;

import java.util.Calendar;
import java.util.GregorianCalendar;

import fulton.util.android.utils.CalendarUtil;
import fulton.util.android.utils.SqlUtil;
import fulton.util.android.utils.Util;

/**
 * Created by 13774 on 8/23/2017.
 */

public class FilterConditionByShownDate extends FilterConditionBase{
    @SqlUtil.DateModelType private int mDateModel;
    private Calendar mStartDate;
    private Calendar mEndDate;
    private int mNumDays;
    private String mDateCol;

    public FilterConditionByShownDate( String dateCol,int dateModel, Calendar startDate, Calendar endDate, int numDays) {
        mDateModel = dateModel;
        mStartDate = startDate;
        mEndDate = endDate;
        setNumDays(numDays);
        mDateCol = dateCol;
    }

    public String getDateCol() {
        return mDateCol;
    }

    public void setDateCol(String dateCol) {
        mDateCol = dateCol;
    }

    @Override
    public void updateCachedResult() {
        String sql=null;
        String[] sqlArgs=null;
        if(mDateModel ==SqlUtil.DATE_MODEL_ALL_DATE)
        {
        }else if(mDateModel==SqlUtil.DATE_MODEL_FROM_START){
            sql=mDateCol+">=?";
            sqlArgs=new String[]{CalendarUtil.getDateString(mStartDate)};
        }else if(mDateModel==SqlUtil.DATE_MODEL_TO_END){
            sql=mDateCol+"<?";
            sqlArgs=new String[]{CalendarUtil.getDateString(mEndDate)};
        }else if(mDateModel==SqlUtil.DATE_MODEL_JUST_DAYS_FROM_START){
            sql=mDateCol+">=? and "+mDateCol+"<?";
            Calendar temp=CalendarUtil.timeInMillisToCalendar(mStartDate.getTimeInMillis()+CalendarUtil.ONE_DAY*mNumDays);
            sqlArgs=new String[]{CalendarUtil.getDateString(mStartDate),CalendarUtil.getDateString(temp)};
        }else if(mDateModel==SqlUtil.DATE_MODEL_FROM_START_TO_END){
            sql=mDateCol+">=? and "+mDateCol+"<?";
            sqlArgs=new String[]{CalendarUtil.getDateString(mStartDate),CalendarUtil.getDateString(mEndDate)};
        }else if(mDateModel==SqlUtil.DATE_MODEL_THIS_WEEK_TO_TODAY){
            sql=mDateCol+">=? and "+mDateCol+"<?";
            Calendar tmp=CalendarUtil.getNextDayBegin();
            sqlArgs=new String[]{null,CalendarUtil.getDateString(tmp)};
            if(tmp.get(Calendar.DAY_OF_WEEK)==Calendar.MONDAY)
                tmp.add(Calendar.DAY_OF_MONTH,-7);
            else
                CalendarUtil.timeToWeekBegin(tmp);
            sqlArgs[0]=CalendarUtil.getDateString(tmp);
            Util.logi(sqlArgs);
        }else if(mDateModel==SqlUtil.DATE_MODEL_JUST_DAYS_TO_TODAY){
            sql=mDateCol+">=? and "+mDateCol+"<?";
            Calendar tmp=CalendarUtil.getNextDayBegin();
            sqlArgs=new String[]{null,CalendarUtil.getDateString(tmp)};// --> today
            tmp.add(Calendar.DAY_OF_MONTH,mNumDays);
            sqlArgs[0]=CalendarUtil.getDateString(tmp);
        }
        setCachedSql(sql);
        setCachedArgs(sqlArgs);
    }

    public @SqlUtil.DateModelType int getDateModel() {
        return mDateModel;
    }

    public void setDateModel(@SqlUtil.DateModelType int dateModel) {
        mDateModel = dateModel;
    }

    public Calendar getStartDate() {
        return mStartDate;
    }

    public void setStartDate(Calendar startDate) {
        mStartDate = startDate;
    }

    public Calendar getEndDate() {
        return mEndDate;
    }

    public void setEndDate(Calendar endDate) {
        mEndDate = endDate;
    }

    public int getNumDays() {
        return mNumDays;
    }

    public void setNumDays(int numDays) {
        mNumDays = Math.abs(numDays);
        if(mNumDays==0)mNumDays=1;
    }

}
