package fulton.shaw.android.tellme.experiment;

import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;

import fulton.shaw.android.tellme.R;
import fulton.util.android.utils.SqlUtil;
import fulton.util.android.event.GenericListener;
import fulton.util.android.event.GenericNotifier;
import fulton.util.android.event.GenericNotifierPrincipal;
import fulton.util.android.aware.DateAware;
import fulton.shaw.android.tellme.experiment.fragments.DateIssueFragment;
import fulton.shaw.android.tellme.experiment.fragments.DateNoteFragment;
import fulton.shaw.android.tellme.experiment.fragments.DateTravelPlanFragment;
import fulton.shaw.android.tellme.experiment.fragments.SharedArgument;
import fulton.util.android.aware.ContextSQLiteAware;
import fulton.shaw.android.tellmelibrary.sql.SqliteHelper;
import fulton.shaw.android.tellme.experiment.viewhelpers.NumberSelectViewHelper;
import fulton.util.android.utils.Util;

/**
 * To Open This Activity,one must provide those arguments:
 *          year,month,date  all are integers
 *          page number
 *
 * This activity does not return anything.
 */
public class DateDetailActivity extends AppCompatActivity implements Serializable,GenericNotifierPrincipal,DateAware,ContextSQLiteAware {

    //set by dialog

    private ViewPager mPager;
    private PagerAdapter mPageAdapter;
    private NumberSelectViewHelper mNsYear, mNsMonth, mNsDate;
    private SqliteHelper mSqliteHelper;
    private SQLiteDatabase mdb;
    private TextView mFestivalView;
    private TextView mWeekdayView;
    private ImageView mWeatherIndicator;

    private Handler mFastUpdater;
    private String[] mWeekDaysName;

    public static final int EVENT_COUNT = 4;
    public static final int EVENT_ADD_ISSUE = 0;
    public static final int EVENT_ADD_NOTE = 1;
    public static final int EVENT_ADD_TRAVEL_PLAN=2;
    public static final int EVENT_DATE_CHANGED=3;//args:year,month,date

    public static final int PAGE_TODOLIST=0;
    public static final int PAGE_NOTE_LIST=1;
    public static final int PAGE_TRAVEL_PLAN=2;


    private GenericNotifier mGenericNotifier;
    private GenericListener mDateListener;

    private ArrayList<Object[]>[] mCurCityLists=new ArrayList[2];


    @Override
    protected void onDestroy() {
        mNsYear.destroyJoinAsync();
        mNsDate.destroyJoinAsync();
        mNsMonth.destroyJoinAsync();
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            final Intent openIntent = getIntent();
            mSqliteHelper = new SqliteHelper(this);
            mdb = mSqliteHelper.getWritableDatabase();

            setContentView(R.layout.activity_date_detail);

            mWeekDaysName=getResources().getStringArray(R.array.weekdays);

            mFestivalView= (TextView) findViewById(R.id.mainFestivalName);
            mWeekdayView= (TextView) findViewById(R.id.weekday);
            mWeatherIndicator= (ImageView) findViewById(R.id.weatherIndicator);
            mFastUpdater=new Handler();

            mPager = (ViewPager) findViewById(R.id.pager);
            mPageAdapter = new SlidePagerAdapter(this, getSupportFragmentManager());
            mPager.setAdapter(mPageAdapter);
            setCurrentPage(openIntent.getIntExtra(SharedArgument.ARG_PAGE_NUM,0));


            mGenericNotifier = new GenericNotifier(EVENT_COUNT);

            mNsYear = new NumberSelectViewHelper(DateDetailActivity.this, findViewById(R.id.selectYear), openIntent.getIntExtra(SharedArgument.ARG_YEAR, 2017), 0, 3000);
            mNsMonth = new NumberSelectViewHelper(DateDetailActivity.this, findViewById(R.id.selectMonth), openIntent.getIntExtra(SharedArgument.ARG_MONTH, 0) + 1, 1, 12);
            mNsDate = new NumberSelectViewHelper(DateDetailActivity.this, findViewById(R.id.selectDate), openIntent.getIntExtra(SharedArgument.ARG_DATE, 1), 1, 31);
            updateAllRelatedViews();
            mDateListener=new GenericListener() {
                @Override
                public void applyListen(int listenerId, int eventType, Object... args) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            updateAllRelatedViews();
                        }
                    });
                    mGenericNotifier.notifyListeners(EVENT_DATE_CHANGED,getYear(),getMonth(),getDate());
                }
            };
            mNsYear.setOnNotifyListener(-1,NumberSelectViewHelper.TYPE_INPUT_COMPLISHED,mDateListener);
            mNsMonth.setOnNotifyListener(-1,NumberSelectViewHelper.TYPE_INPUT_COMPLISHED,mDateListener);
            mNsDate.setOnNotifyListener(-1,NumberSelectViewHelper.TYPE_INPUT_COMPLISHED,mDateListener);
        }

    }

    public void updateAllRelatedViews()//as date selected by three inputs
    {
//        Util.logi("in update all,year");
        int year=mNsYear.getNumberOfView();
        int month=mNsMonth.getNumberOfView()-1;
        int date=mNsDate.getNumberOfView();

        //==========festival
        Cursor c=mdb.query(SqliteHelper.TABLE_SOLAR_CALENDAR_FESTIVALS,
                new String[]{SqlUtil.COL_FESTNAME},
                SqlUtil.COL_MONTH+"=? and "+ SqlUtil.COL_DATE+"=?",
                new String[]{String.valueOf(month),String.valueOf(date)},
                null,
                null,
                null,
                "1"
        );
        String festString="No Festival";

        if(c.moveToNext()){
            festString=c.getString(c.getColumnIndex(SqlUtil.COL_FESTNAME));

        }
        c.close();

        //========weather
        c=mdb.query(SqliteHelper.TABLE_WEATHER,
                        new String[]{SqlUtil.COL_WEATHER_STATE},
                        SqlUtil.COL_YEAR+"=? and "+ SqlUtil.COL_MONTH+"=? and "+ SqlUtil.COL_DATE+"=?",
                        new String[]{String.valueOf(year),String.valueOf(month),String.valueOf(date)},
                        null,
                        null,
                        null
                );
        String weatherState=null;
        if(c.moveToNext())
        {
            weatherState=c.getString(c.getColumnIndex(SqlUtil.COL_WEATHER_STATE));
        }
        c.close();

        //==========weekdays
        Calendar calendar=Calendar.getInstance();
        calendar.set(year,month,date);
        Util.logi("year="+year+" month="+month+" date="+date);
        int weekday=calendar.get(Calendar.DAY_OF_WEEK);//SUNDAY as 1
//        Util.logi("original value:"+weekday);
        if(weekday!=Calendar.SUNDAY)weekday-=2;
        else weekday=6;
//        Util.logi("updated value:"+weekday);

        //===============be final
        final int fweekday=weekday;
        final String fweatherState=weatherState;
        final String ffestString=festString;

        //====
        mFastUpdater.postAtFrontOfQueue(new Runnable() {
            @Override
            public void run() {
                mPageAdapter.notifyDataSetChanged();
                mWeekdayView.setText(mWeekDaysName[fweekday]);
                if(fweatherState==null)
                {
                    mWeatherIndicator.setImageDrawable(null);
                }else{
                    switch (fweatherState)
                    {
                        case SqliteHelper.WEATHER_RAINY:
                            mWeatherIndicator.setImageResource(SharedArgument.DATE_WEATHER_RAINY);
                            break;
                        case SqliteHelper.WEATHER_SUNNY:
                            mWeatherIndicator.setImageResource(SharedArgument.DATE_WEATHER_SUNNY);
                            break;
                        case SqliteHelper.WEATHER_THUNDER:
                            mWeatherIndicator.setImageResource(SharedArgument.DATE_WEATHER_THUNDER);
                            break;
                        case SqliteHelper.WEATHER_SNOW:
                            mWeatherIndicator.setImageResource(SharedArgument.DATE_WEATHER_SNOW);
                            break;
                    }
                }
                mFestivalView.setText(ffestString);
            }
        });
    }

    public void setCurrentPage(int i)
    {
        if(i>=0 && i<mPageAdapter.getCount())
        {
            mPager.setCurrentItem(i);
        }
    }

    @Override
    public void setYear(int year) {

    }

    @Override
    public void setMonth(int month) {

    }

    @Override
    public void setDate(int date) {

    }

    public int getYear() {
        return mNsYear.getNumberOfView();
    }

    public int getMonth()
    {
        return mNsMonth.getNumberOfView()-1;
    }
    public int getDate()
    {
        return mNsDate.getNumberOfView();
    }

    public void onClickAddButton(View v)
    {
        int curItem=mPager.getCurrentItem();
        if(curItem==0)
            onClickAddButtonWhenIssue(v);
        else if(curItem==1)
            onClickAddButtonWhenNote(v);
        else if(curItem==2)
            onClickAddButtonWhenTravelPlan(v);
    }

    /**
     *  open a dialog,then getByGetter data.If data not set(cancelled),then do no thing
     *  else do a insertation to the database
     * @param v
     */
    public void onClickAddButtonWhenIssue(View v)
    {
        if(mdb==null)
        {
            Toast.makeText(this,"Database Not Prepared",Toast.LENGTH_SHORT).show();
            Util.logi("Database : mdb not prepared");
            return;
        }
        final Dialog dialog=new Dialog(this);

        dialog.setContentView(R.layout.date_issue_fragment_add_issue_dialog);
        Button addButton= (Button) dialog.findViewById(R.id.addButton);
        Button cancelButton= (Button) dialog.findViewById(R.id.cancelButton);
        final TimePicker tp= (TimePicker) dialog.findViewById(R.id.timePicker);
        final Spinner tsp= (Spinner) dialog.findViewById(R.id.spinnerType);
        final EditText issueText= (EditText) dialog.findViewById(R.id.issue);

        final ContentValues result=new ContentValues();



        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String issueContent=issueText.getText().toString();
                if(issueContent.length()==0)
                {
                    Toast.makeText(DateDetailActivity.this,"Please input something",Toast.LENGTH_LONG).show();
                    return;
                }

                result.put(SqlUtil.COL_HOUR,tp.getCurrentHour());//-1
                result.put(SqlUtil.COL_MINUTE,tp.getCurrentMinute());//-1
                result.put(SqlUtil.COL_TYPE,SqliteHelper.ALLOWED_TYPES[tsp.getSelectedItemPosition()]);
                result.put(SqlUtil.COL_ISSUE,issueContent);


                //dismiss as soon as the views needed are all accessed
                dialog.dismiss();

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        result.put(SqlUtil.COL_YEAR,mNsYear.getNumberOfView());//+0
                        result.put(SqlUtil.COL_MONTH,mNsMonth.getNumberOfView()-1);//-1
                        result.put(SqlUtil.COL_DATE,mNsDate.getNumberOfView());//+0

                        long id=mdb.insert(SqliteHelper.TABLE_ISSUE_LIST,null,result);
                        if(id==-1)
                        {
                            Util.logi("insert failed");
                        }else{
                            Util.logi("insert succeed");
                            result.put(SqlUtil.COL_ID,id);
                            mGenericNotifier.notifyListeners(EVENT_ADD_ISSUE,result);
                        }
                    }
                }).start();
            }
        });
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });

       dialog.setTitle(R.string.addIssue);
        dialog.show();

    }
    public void onClickAddButtonWhenNote(View v)
    {
        if(mdb==null)
        {
            Toast.makeText(this,"Database Not Prepared",Toast.LENGTH_SHORT).show();
            Util.logi("Database : mdb not prepared");
            return;
        }
        final Dialog dialog=new Dialog(this);

        dialog.setContentView(R.layout.date_issue_fragment_add_note_dialog);
        Button addButton= (Button) dialog.findViewById(R.id.addButton);
        Button cancelButton= (Button) dialog.findViewById(R.id.cancelButton);
        final EditText noteText= (EditText) dialog.findViewById(R.id.note);

        final ContentValues result=new ContentValues();



        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String noteContent=noteText.getText().toString();
                if(noteContent.length()==0)
                {
                    Toast.makeText(DateDetailActivity.this,"Please input something",Toast.LENGTH_LONG).show();
                    return;
                }

                result.put(SqlUtil.COL_NOTE,noteContent);


                //dismiss as soon as the views needed are all accessed
                dialog.dismiss();

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        result.put(SqlUtil.COL_YEAR,mNsYear.getNumberOfView());//+0
                        result.put(SqlUtil.COL_MONTH,mNsMonth.getNumberOfView()-1);//-1
                        result.put(SqlUtil.COL_DATE,mNsDate.getNumberOfView());//+0
                        result.put(SqlUtil.COL_HOUR, Calendar.getInstance().get(Calendar.HOUR_OF_DAY));//+0



                        long id=mdb.insert(SqliteHelper.TABLE_NOTE,null,result);
                        if(id==-1)
                        {
                            Util.logi("insert into note failed");
//                            mdb.execSQL("Drop Table If Exists "+SqliteHelper.TABLE_NOTE);
//                            mdb.execSQL(SqliteHelper.CREATE_TABLES[1]);
//                            id=mdb.insert(SqliteHelper.TABLE_NOTE,null,result);
//                            if(id==-1)
//                                Util.logi("Still failed");
                        }else{
                            Util.logi("insert into note succeed");
                            mGenericNotifier.notifyListeners(EVENT_ADD_NOTE,result);
                        }
                    }
                }).start();
            }
        });
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });

        dialog.setTitle(R.string.addNote);
        dialog.show();
    }


    public static Dialog buildTravelPlanDialog(final Context context,
                                               final ContextSQLiteAware dbGetter,
                                               final DateAware dateGetter,//to getByGetter date
                                               final ArrayList<Object[]>[] curCityList,//in the adapter,src & dst
                                               final GenericNotifier notifierOnAdd,
                                               final int eventAddCode,
                                               String title,
                                               String positiviButtonText,
                                               int initSrcProvIndex, int initSrcCityIndex, int initDstProvIndex, int initDstCityIndex,
                                               ContentValues initValues)
    {
        final Dialog dialog=new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.date_issue_fragment_add_travel_plan_dialog);

        Button addButton= (Button) dialog.findViewById(R.id.addButton);
        Button cancelButton= (Button) dialog.findViewById(R.id.cancelButton);

        final Spinner srcProvSpinner= (Spinner) dialog.findViewById(R.id.srcProvSpinner);
        final Spinner srcCitySpinner= (Spinner) dialog.findViewById(R.id.srcCitySpinner);
        final Spinner dstProvSpinner= (Spinner) dialog.findViewById(R.id.dstProvSpinner);
        final Spinner dstCitySpinner= (Spinner) dialog.findViewById(R.id.dstCitySpinner);

        final ArrayAdapter<String> srcProvAdapter=new ArrayAdapter<String>(context,android.R.layout.simple_spinner_item){

            @Override
            public int getCount() {
//               Util.logi("srcProvAdapter:getCount");
                return SqliteHelper.getProvincesAsArrayList(dbGetter.getContextDB()).size()+1;
            }

            @Nullable
            @Override
            public String getItem(int position) {
//               Util.logi("srcProvAdapter:getItem at "+position);
                if(position==0)return context.getResources().getString(R.string.undetermined);
                else return (String) SqliteHelper.getProvincesAsArrayList(dbGetter.getContextDB()).get(position-1)[1];
            }
        };
        final ArrayAdapter<String> dstProvAdapter=new ArrayAdapter<String>(context,android.R.layout.simple_spinner_item){

            @Override
            public int getCount() {
                return SqliteHelper.getProvincesAsArrayList(dbGetter.getContextDB()).size();
            }

            @Nullable
            @Override
            public String getItem(int position) {
                return (String) SqliteHelper.getProvincesAsArrayList(dbGetter.getContextDB()).get(position)[1];
            }
        };

        final ArrayAdapter<String> srcCityAdapter=new ArrayAdapter<String>(context,android.R.layout.simple_spinner_item){
            @Override
            public int getCount() {
                return curCityList[0] ==null?0: curCityList[0].size();
            }

            @Nullable
            @Override
            public String getItem(int position) {
                return (String) curCityList[0].get(position)[1];
            }
        };
        final ArrayAdapter<String> dstCityAdapter=new ArrayAdapter<String>(context,android.R.layout.simple_spinner_item){
            @Override
            public int getCount() {
                return curCityList[1] ==null?0: curCityList[1].size();
            }

            @Nullable
            @Override
            public String getItem(int position) {
                return (String) curCityList[1].get(position)[1];
            }
        };

        srcProvSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//               Util.logi("dstProv:onItemSelected at "+position);
                if(position==0)
                {
                    curCityList[0]=new ArrayList<Object[]>();
                    curCityList[0].add(new Object[]{null,context.getResources().getString(R.string.undetermined)});
                }else {
                    int curProvCode = (int) SqliteHelper.getProvincesAsArrayList(dbGetter.getContextDB()).get(position-1)[0];
                    curCityList[0]= SqliteHelper.getCitiesAsHashMap(dbGetter.getContextDB()).get(curProvCode);
                }
                srcCityAdapter.notifyDataSetChanged();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
//               Util.logi("srcProv:onNothingSelected");
                if(curCityList[0] !=null) {
                    curCityList[0] = null;
                    srcCityAdapter.notifyDataSetChanged();
                }
            }
        });

        dstProvSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                int curProvCode= (int) SqliteHelper.getProvincesAsArrayList(dbGetter.getContextDB()).get(position)[0];
                curCityList[1] = SqliteHelper.getCitiesAsHashMap(dbGetter.getContextDB()).get(curProvCode);
                dstCityAdapter.notifyDataSetChanged();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

                if(curCityList[1] !=null) {
                    curCityList[1] = null;
                    dstCityAdapter.notifyDataSetChanged();
                }
            }
        });

        srcProvSpinner.setAdapter(srcProvAdapter);
        srcCitySpinner.setAdapter(srcCityAdapter);
        dstProvSpinner.setAdapter(dstProvAdapter);
        dstCitySpinner.setAdapter(dstCityAdapter);

        srcProvSpinner.setSelection(initSrcProvIndex);
        srcCitySpinner.setSelection(initSrcCityIndex);
        dstProvSpinner.setSelection(initDstProvIndex);
        dstCitySpinner.setSelection(initDstCityIndex);


        final ContentValues result=new ContentValues();
        if(initValues!=null)
        {
            result.putAll(initValues);
        }


        if(positiviButtonText!=null)
            addButton.setText(positiviButtonText);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int srcProvPos = srcProvSpinner.getSelectedItemPosition();
                if (srcProvPos != 0) {
                    Object[] data = SqliteHelper.getProvincesAsArrayList(dbGetter.getContextDB()).get(srcProvPos - 1);
                    result.put(SqlUtil.COL_SRC_PROV_ID, (Integer) data[0]);
                    int srcCityPos = srcCitySpinner.getSelectedItemPosition();
                    result.put(SqlUtil.COL_SRC_CITY_ID, (Integer) SqliteHelper.getCitiesAsHashMap(dbGetter.getContextDB()).get(data[0]).get(srcCityPos)[0]);
                }
                Object[] dstData = SqliteHelper.getProvincesAsArrayList(dbGetter.getContextDB()).get(dstProvSpinner.getSelectedItemPosition());
                result.put(SqlUtil.COL_DST_PROV_ID, (Integer) dstData[0]);
                result.put(SqlUtil.COL_DST_CITY_ID, (Integer) SqliteHelper.getCitiesAsHashMap(dbGetter.getContextDB()).get(dstData[0]).get(dstCitySpinner.getSelectedItemPosition())[0]);
                result.put(SqlUtil.COL_YEAR, dateGetter.getYear());
                result.put(SqlUtil.COL_MONTH, dateGetter.getMonth());
                result.put(SqlUtil.COL_DATE, dateGetter.getDate());


                //dismiss as soon as the views needed are all accessed

                dialog.dismiss();
                long id = dbGetter.getContextDB().replace(SqliteHelper.TABLE_TRAVEL_PLAN, null, result);
                if (id == -1) {
                    Util.logi("insert into travel_plan failed");
                } else {
                    Util.logi("insert into travel_plan succeed,rowid="+id);
//                    Cursor cursor = dbGetter.getContextDB().query(SqliteHelper.TABLE_TRAVEL_PLAN, new String[]{SqliteHelper.COL_ID}, null, null, null, null, null);
//                    while(cursor.moveToNext())
//                    {
//                        Util.logi("id:"+cursor.getInt(cursor.getColumnIndex(SqliteHelper.COL_ID)));
//                    }
                    notifierOnAdd.notifyListeners(eventAddCode, result);
                }
            }
        });
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });

        TextView titleView= (TextView) dialog.findViewById(R.id.dialogTitle);
        if(title!=null)
            titleView.setText(title);

        return dialog;
    }
   public void onClickAddButtonWhenTravelPlan(View v)
   {
       if(mdb==null)
       {
           Toast.makeText(this,"Database Not Prepared",Toast.LENGTH_SHORT).show();
           Util.logi("Database : mdb not prepared");
           return;
       }
        Dialog dialog=buildTravelPlanDialog(this,this,this,
                            mCurCityLists,
                            mGenericNotifier,EVENT_ADD_TRAVEL_PLAN,
                getResources().getString(R.string.add_travel_plan_title),null,0,0,0,0,null);
       dialog.show();
   }

    public SqliteHelper getSqliteHelper() {
        return mSqliteHelper;
    }

    public SQLiteDatabase getDb() {
        return mdb;
    }

    public ArrayList<Object[]>[] getDialogCityList()
    {
        return mCurCityLists;
    }


    @Override
    public GenericNotifier getProxyNotifier() {
        return mGenericNotifier;
    }

    @Override
    public SQLiteDatabase getContextDB() {
        return mdb;
    }

    public static class SlidePagerAdapter extends FragmentPagerAdapter
    {
        private DateDetailActivity mActivity;

        public SlidePagerAdapter(DateDetailActivity activity,FragmentManager fm)
        {
            super(fm);
            mActivity=activity;
        }

        @Override
        public Fragment getItem(int position) {
            Util.logi("fragment getItem:"+position);
            Fragment fragment=null;
            Bundle args=new Bundle();
            switch (position)
            {
                case 0:
                    fragment=new DateIssueFragment();
                    break;
                case 1:
                    fragment=new DateNoteFragment();
                    break;
                case 2:
                    fragment=new DateTravelPlanFragment();
                    break;
            }
            args.putSerializable(SharedArgument.ARG_ACTIVITY,mActivity);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public int getCount() {
            return 3;
        }

    }


}
