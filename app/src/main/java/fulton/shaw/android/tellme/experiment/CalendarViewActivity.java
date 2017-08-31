package fulton.shaw.android.tellme.experiment;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Calendar;

import fulton.shaw.android.tellme.R;
import fulton.util.android.utils.SqlUtil;
import fulton.util.android.event.GenericListener;
import fulton.shaw.android.tellme.experiment.fragments.SharedArgument;
import fulton.util.android.aware.ContextSQLiteAware;
import fulton.shaw.android.tellmelibrary.sql.SqliteHelper;
import fulton.shaw.android.tellme.experiment.viewhelpers.NumberSelectViewHelper;

/**
 * 在发生下面事件时，必须重新绘制calendar
 */
public class CalendarViewActivity extends AppCompatActivity implements ContextSQLiteAware {

    private GridView mGridView;
    private CalendarAdapter mAdapter;
    private NumberSelectViewHelper mNsYear,mNsMonth;
    private SqliteHelper mSqliteHelper;
    private SQLiteDatabase mdb;
    private TextView[] mWeekdayHeader=new TextView[7];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grid_view);
        this.
        mWeekdayHeader[0]= (TextView) findViewById(R.id.mondayHeader);
        mWeekdayHeader[1]= (TextView) findViewById(R.id.tuesdayHeader);
        mWeekdayHeader[2]= (TextView) findViewById(R.id.wednesdayHeader);
        mWeekdayHeader[3]= (TextView) findViewById(R.id.thursdayHeader);
        mWeekdayHeader[4]= (TextView) findViewById(R.id.fridayHeader);
        mWeekdayHeader[5]= (TextView) findViewById(R.id.saturdayHeader);
        mWeekdayHeader[6]= (TextView) findViewById(R.id.sundayHeader);
        String[] weekdays=getResources().getStringArray(R.array.weekdays_ellipse);
        for(int i=0;i<mWeekdayHeader.length;i++)
            mWeekdayHeader[i].setText(weekdays[i]);

        Calendar today=Calendar.getInstance();
        mGridView = (GridView)findViewById(R.id.gridview);
        mNsYear=new NumberSelectViewHelper(this,findViewById(R.id.yearSelectView),today.get(Calendar.YEAR),1,30000);
        mNsMonth=new NumberSelectViewHelper(this,findViewById(R.id.monthSelectView),today.get(Calendar.MONTH)+1,1,12);

//        Util.logi("Year Picker Null?"+(mYearPicker==null));  //not null

        mSqliteHelper = new SqliteHelper(this);
        mdb = mSqliteHelper.getWritableDatabase();

        mAdapter = new CalendarAdapter(this,today);
        mGridView.setAdapter(mAdapter);


//NOT WORKING
//        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                TextView textView=(TextView)view.findViewById(R.id.date_number);
//                textView.setText("" + (position - 6) + "clicked");
//                Util.logi("grid view item clicked");
//            }
//        });

        mNsYear.setOnNotifyListener(-1, NumberSelectViewHelper.TYPE_INPUT_COMPLISHED, new GenericListener() {
            @Override
            public void applyListen(int listenerId, int eventType, final Object... args) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
//                        Util.logi("Year Input Complished");
                        mAdapter.setCurrentShownDate(mNsYear.getNumberOfView(),-1);
                        CalendarViewActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mAdapter.notifyDataSetChanged();
                            }
                        });
                    }
                }).start();

            }
        });
        mNsMonth.setOnNotifyListener(-1, NumberSelectViewHelper.TYPE_INPUT_COMPLISHED, new GenericListener() {
            @Override
            public void applyListen(int listenerId, int eventType, final Object... args) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
//                        Util.logi("Month Input Complished");
                        mAdapter.setCurrentShownDate(-1,mNsMonth.getNumberOfView()-1);
                        CalendarViewActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mAdapter.notifyDataSetChanged();
                            }
                        });
                    }
                }).start();
            }
        });



    }

    @Override
    public SQLiteDatabase getContextDB() {
        return mdb;
    }

    public static class CalendarAdapter extends BaseAdapter{
        private CalendarViewActivity mActivity;
        private LayoutInflater mInflater;
        private Calendar mCurrentShownDateFirstDayOfMonth;
        private int mFirstDayOfMonthInWeek;
        private int mDaysInCurrentMonth;
        /**
         * -1 to have no effect.This is used to mark the day in a calendar that is today
         */
        private int mMarkAsToday;
        private Calendar mCurrentSysDate;

        private static final String[] HEADER_TITLES=new String[]{"Monday","Tuesday","Wednesday","Thursday","Friday","Saturday","Sunday"};

        public static final int DATE_STATE_COLOR_TODAY=R.color.color_today;
        public static final int DATE_STATE_COLOR_OTHER_MONTH=R.color.color_other_month;
        public static final int DATE_STATE_COLOR_NORMAL=R.color.color_normal;

        public CalendarAdapter(CalendarViewActivity activity)
        {
            this(activity,Calendar.getInstance());
        }
        public CalendarAdapter(CalendarViewActivity activity,Calendar shownDate)
        {
            this(activity,shownDate.get(Calendar.YEAR),shownDate.get(Calendar.MONTH));
        }

        public CalendarAdapter(CalendarViewActivity activity,int year,int month)
        {
            mActivity = activity;
            mInflater = mActivity.getLayoutInflater();
            mCurrentShownDateFirstDayOfMonth = Calendar.getInstance();

            if(mCurrentSysDate==null)
                mCurrentSysDate = Calendar.getInstance();

            setCurrentShownDate(year,month);
        }



        @Override
        public int getCount() {

            return 7*6;
        }

        @Override
        public Object getItem(int position) {

            return null;
//            super.getItem(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {//如何确定某个具体的position会被调用呢？通过count设置
//            Util.logi("position="+position);//被多次调用
            boolean fromExist=true;
            final Calendar temp=Calendar.getInstance();
            int year=mCurrentShownDateFirstDayOfMonth.get(Calendar.YEAR);
            int month=mCurrentShownDateFirstDayOfMonth.get(Calendar.MONTH);
            int date=position- 5 - mFirstDayOfMonthInWeek+7;
            temp.set(year,month,date);
            final int qyear=temp.get(Calendar.YEAR);
            final int qmonth=temp.get(Calendar.MONTH);
            final int qdate=temp.get(Calendar.DAY_OF_MONTH);//used to query
            if(convertView==null)
            {
                convertView = mInflater.inflate(R.layout.calendar_subitem, parent,false);
                fromExist=false;
                /**
                 * Parent Click Event not called if this is set.However if it is not set,then parent-click happened
                 */
//                TextView dateNote= (TextView) convertView.findViewById(R.id.date_note);
//                dateNote.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        Util.logi("dateNote clicked:"+position);
//                    }
//                });

            }
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                        Util.logi("position:"+position+" clicked");
                    Intent intent=new Intent(mActivity, DateDetailActivity.class);
                    CalendarViewActivity activity= (CalendarViewActivity) mActivity;
                    intent.putExtra(SharedArgument.ARG_YEAR,qyear);
                    intent.putExtra(SharedArgument.ARG_MONTH,qmonth);
                    intent.putExtra(SharedArgument.ARG_DATE,qdate);
                    mActivity.startActivity(intent);
                }
            });

            TextView issueNumber= (TextView) convertView.findViewById(R.id.importantIssuesNumber);
            ImageView weatherIndicator= (ImageView) convertView.findViewById(R.id.weatherIndicator);
            TextView dateNumber = (TextView)convertView.findViewById(R.id.date_number);
            TextView festivalView= (TextView) convertView.findViewById(R.id.mainFestivalName);
            if(position < 7-7)
            {
                if(!fromExist) {
                    dateNumber.setText(HEADER_TITLES[position]);
                    issueNumber.setText("");
                    weatherIndicator.setImageDrawable(null);
                }
            }else{

                String sYear=String.valueOf(qyear),sMonth=String.valueOf(qmonth),sDate=String.valueOf(qdate);

                //=========issue number
                Cursor c=mActivity.getContextDB().query(
                    SqliteHelper.TABLE_ISSUE_LIST,
                        new String[]{"COUNT(*)"},
                    SqliteHelper.SELECTION_YEAR_MONTH_DATE+" and "+ SqlUtil.COL_TYPE+" in (?,?,?)",
                        new String[]{sYear,sMonth,sDate,SqliteHelper.TYPE_ONCE,SqliteHelper.TYPE_BY_MONTH,SqliteHelper.TYPE_BY_YEAR},
                        null,
                        null,
                        null
                );
                int issueCount=0;
                if(c.moveToNext())
                {
                    issueCount=c.getInt(0);

                }
                if(issueCount!=0)
                {
                    issueNumber.setText("" + issueCount);
                }
                else{
                    issueNumber.setText("");
                }
                c.close();

                //=====festival
                c=mActivity.getContextDB().query(
                  SqliteHelper.TABLE_SOLAR_CALENDAR_FESTIVALS,
                        new String[]{SqlUtil.COL_FESTNAME},
                        SqliteHelper.SELECTION_MONTH_DATE,
                        new String[]{sMonth,sDate},
                        null,
                        null,
                        null,
                        "1"
                );
                if(c.moveToNext())
                {
                    festivalView.setText(c.getString(c.getColumnIndex(SqlUtil.COL_FESTNAME)));
                    festivalView.setBackgroundColor(mActivity.getResources().getColor(R.color.background_color_when_has_fest));
                }else{
                    festivalView.setText("");
                    festivalView.setBackgroundColor(mActivity.getResources().getColor(R.color.background_color_when_no_fest));
                }
                c.close();

                //========weather
                c=mActivity.getContextDB().query(
                  SqliteHelper.TABLE_WEATHER,
                        new String[]{SqlUtil.COL_WEATHER_STATE},
                        SqliteHelper.SELECTION_YEAR_MONTH_DATE,
                        new String[]{sYear,sMonth,sDate},
                        null,
                        null,
                        null
                );
                if(c.moveToNext())
                {
                    weatherIndicator.setImageResource(SharedArgument.weatherStringToDrawable(c.getString(c.getColumnIndex(SqlUtil.COL_WEATHER_STATE))));
                }else{
                    weatherIndicator.setImageDrawable(null);
                }
                c.close();


                dateNumber.setText(""+qdate);
                if(position - 6 + 7>= mFirstDayOfMonthInWeek && position - 5 - mFirstDayOfMonthInWeek + 7 <= mDaysInCurrentMonth)
                {
                    if(position+7 - 5 - mFirstDayOfMonthInWeek != mMarkAsToday)
                        dateNumber.setBackgroundColor(mActivity.getResources().getColor(DATE_STATE_COLOR_NORMAL));
                    else
                        dateNumber.setBackgroundColor(mActivity.getResources().getColor(DATE_STATE_COLOR_TODAY));
                }else {
                    dateNumber.setBackgroundColor(mActivity.getResources().getColor(DATE_STATE_COLOR_OTHER_MONTH));
                }
            }

            return convertView;
        }

        public Calendar getCurrentShownDate() {
            return mCurrentShownDateFirstDayOfMonth;
        }

        @Deprecated
        public void setCurrentShownDate(Calendar currentShownDate) {
            setCurrentShownDate(currentShownDate.get(Calendar.YEAR),currentShownDate.get(Calendar.MONTH));
        }

        /**
         *
         * @param year                              if set to -1,then not modified
         * @param month start from 0,range up to 11,  if set to -1,then not modified
         */
        public void setCurrentShownDate(int year,int month)
        {
            if(year==-1 && month==-1)return;
            if(year==-1)year=mCurrentShownDateFirstDayOfMonth.get(Calendar.YEAR);
            if(month==-1)month=mCurrentShownDateFirstDayOfMonth.get(Calendar.MONTH);
            this.mCurrentShownDateFirstDayOfMonth.set(year,month,1);
//            Util.logi(mCurrentShownDateFirstDayOfMonth.toString());
            mFirstDayOfMonthInWeek = this.mCurrentShownDateFirstDayOfMonth.get(Calendar.DAY_OF_WEEK);

            if(mFirstDayOfMonthInWeek!=Calendar.SUNDAY)mFirstDayOfMonthInWeek--;
            else mFirstDayOfMonthInWeek=7;

//            Util.logi("First Day of Month In Week:"+mFirstDayOfMonthInWeek);

            mDaysInCurrentMonth = mCurrentShownDateFirstDayOfMonth.getActualMaximum(Calendar.DAY_OF_MONTH);
//            Util.logi("Days of Month:"+mDaysInCurrentMonth);

            if(mCurrentShownDateFirstDayOfMonth.get(Calendar.YEAR)==mCurrentSysDate.get(Calendar.YEAR) &&
                    mCurrentShownDateFirstDayOfMonth.get(Calendar.MONTH)==mCurrentSysDate.get(Calendar.MONTH))
            {
                mMarkAsToday = mCurrentSysDate.get(Calendar.DAY_OF_MONTH);
            }else{
                mMarkAsToday=-1;
            }
        }

        public static void HowToUse(CalendarViewActivity activity)
        {
            CalendarAdapter cadpter=new CalendarAdapter(activity,Calendar.getInstance());
        }
    }

}
