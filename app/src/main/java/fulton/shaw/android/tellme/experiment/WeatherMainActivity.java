package fulton.shaw.android.tellme.experiment;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.SimpleExpandableListAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;

import fulton.shaw.android.tellme.R;
import fulton.util.android.utils.SqlUtil;
import fulton.util.android.event.GenericListener;
import fulton.shaw.android.tellme.experiment.fragments.SharedArgument;
import fulton.shaw.android.tellme.experiment.service.GetWeatherInfoHandler;
import fulton.shaw.android.tellme.experiment.service.GetWeatherInfoService;
import fulton.shaw.android.tellme.experiment.service.WeatherInfoProvider;
import fulton.util.android.aware.ContextSQLiteAware;
import fulton.shaw.android.tellmelibrary.sql.SqliteHelper;
import fulton.util.android.utils.Util;

/**
 * 向service传递参数的唯一办法是使Activity 可序列化， 带来的问题是那些其中的变量都很难再维持。
 *
 * 结论：复杂的Activity无法被作为参数传递到service。避免使用intent。
 *
 * open arguments:
 *      year,month,date
 *      provIndex,cityIndex,  if provIndex=-1,cityIndex=-1, it means use current location
 *
 */
public class WeatherMainActivity extends AppCompatActivity implements GenericListener,ContextSQLiteAware {

    //state related variable
    private int mYear,mMonth,mDate;// it cannot be other days.
    int mProvIndex, mCityIndex;
    private boolean mDialogDefaultValue;


    private boolean mSaveLocationOnExit;


    protected static final int NEASERST_DAY_SHOWN_NUM=2;
    protected static final int PLAN_SHOWN_DAY_NUM=4;

    private ImageView mSwitchButton;//dynamically findViewById
    private GetWeatherInfoHandler mHandler;
    private SqliteHelper mSqliteHelper;
    private SQLiteDatabase mdb;
    private ArrayList<Object[]> mProvinces;
    private HashMap<Integer,ArrayList<Object[]>> mCities;
    private ArrayList<Object[]> mCurCityList;

    //on dialog
    private Spinner mProvSpinner;
    private Spinner mCitySpinner;
    private ImageView mDialogCancelButton;
    private CheckBox mDefaultChoosedCheckBox;

    //on main UI
    private TextView mYearView,mMonthView,mDateView;
    private TextView mProvView,mCityView;
    private TextView mTemperatureView, mWeatherTextView,mUVView,mWindScaleView,mWindDirectionView,mLastUpdateTimeView;
    private TextView mCarWasingView,mDressingView,mFluView,mSportView,mTravelView;
    private ImageView mWeatherIndicator;
    private ExpandableListView mNearstDaysPlanView;
    private SimpleExpandableListAdapter mDaysPlanAdapter;
    private ViewGroup[] mNearestDaysWeatherInfoViews;

    private ArrayList<HashMap<String,String>> mGroupsData;
    private ArrayList<ArrayList<HashMap<String,String>>> mDaysPlanData;//0,1,2,3 today,nextday,...,...

    protected final String STATE_PROV_INDEX="prov_index";
    protected final String STATE_CITY_INDEX="city_index";
    protected final String STATE_DIALOG_DEFAULT="dialog_default";

    protected final String MONTH_DATE_FORMATTER="%02d.%02d";
    protected final String MAX_MIN_TEMPERATURE_FORMATTER="%s/%s℃";

    private boolean mDoInitTablse;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Util.logi("onCreate,bundle==null?"+(savedInstanceState==null));
        mSqliteHelper=new SqliteHelper(WeatherMainActivity.this);
        mDoInitTablse=false;
        if(savedInstanceState==null)//read some saved state from file
        {
            SharedPreferences preferences=getSharedPreferences(SharedArgument.CONFIG_SHARED_PREF_FILE,MODE_PRIVATE);
            mDialogDefaultValue=preferences.getBoolean(SharedArgument.ARG_DEFAULT_DIALOG_CHECKED,false);
            mProvIndex=preferences.getInt(SharedArgument.ARG_PROV_INDEX,8);//黑龙江
            mCityIndex=preferences.getInt(SharedArgument.ARG_CITY_INDEX,0);

            if(!preferences.contains(SharedArgument.ARG_INIT_DATABASES))
            {
                mDoInitTablse=true;
                SharedPreferences.Editor editor = preferences.edit();
                editor.putBoolean(SharedArgument.ARG_INIT_DATABASES,true);
                editor.commit();
            }

        }
        mSaveLocationOnExit=false;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather_main);
        mSwitchButton = (ImageView) findViewById(R.id.switchButton);
        mYearView= (TextView) findViewById(R.id.yearView);
        mMonthView= (TextView) findViewById(R.id.monthView);
        mDateView= (TextView) findViewById(R.id.dateView);
        mCityView = (TextView) findViewById(R.id.cityStringView);
        mProvView= (TextView) findViewById(R.id.provinceView);
        mLastUpdateTimeView= (TextView) findViewById(R.id.lastUpdateTimeView);
        mTemperatureView= (TextView) findViewById(R.id.temperatureView);
        mWeatherTextView = (TextView) findViewById(R.id.weatherTextView);
        mUVView= (TextView) findViewById(R.id.uvView);
        mWindDirectionView= (TextView) findViewById(R.id.windDirectionView);
        mWindScaleView= (TextView) findViewById(R.id.windScaleView);
        mWeatherIndicator = (ImageView) findViewById(R.id.weatherIndicator);
        mCarWasingView= (TextView) findViewById(R.id.carWashingView);
        mDressingView= (TextView) findViewById(R.id.dressingView);
        mFluView= (TextView) findViewById(R.id.fluView);
        mSportView= (TextView) findViewById(R.id.sportView);
        mTravelView= (TextView) findViewById(R.id.travelView);
        mNearstDaysPlanView = (ExpandableListView) findViewById(R.id.nearDaysTravelPlan);
        mNearestDaysWeatherInfoViews = new ViewGroup[]{
                (ViewGroup) findViewById(R.id.includeView1),
                (ViewGroup) findViewById(R.id.includeView2)
        };


        mGroupsData =new ArrayList<HashMap<String,String>>(){{
            add(new HashMap<String, String>(){{
                put(SharedArgument.ARG_TITLE,getResources().getString(R.string.today));
                put(SharedArgument.ARG_NUM,"1");
            }});
            add(new HashMap<String, String>(){{
                put(SharedArgument.ARG_TITLE,"1"+getResources().getString(R.string.after_days));
                put(SharedArgument.ARG_NUM,"");
            }});
            add(new HashMap<String, String>(){{
                put(SharedArgument.ARG_TITLE,"2"+getResources().getString(R.string.after_days));
                put(SharedArgument.ARG_NUM,"");
            }});
            add(new HashMap<String, String>(){{
                put(SharedArgument.ARG_TITLE,"3"+getResources().getString(R.string.after_days));
                put(SharedArgument.ARG_NUM,"");
            }});
        }};
        mDaysPlanData=new ArrayList<ArrayList<HashMap<String,String>>>(){{
            for(int i=0;i<PLAN_SHOWN_DAY_NUM;i++) {
                add(new ArrayList<HashMap<String, String>>());
            }
        }};
//        mDaysPlanData.getByGetter(0).add(new HashMap<String, String>(){{
//            put(SharedArgument.ARG_SRC_PROV_NAME,"黑龙江省");
//            put(SharedArgument.ARG_SRC_CITY_NAME,"哈尔滨市");
//            put(SharedArgument.ARG_DST_PROV_NAME,"北京市");
//            put(SharedArgument.ARG_DST_CITY_NAME,"东城区");
//        }});
        mDaysPlanAdapter = new SimpleExpandableListAdapter(this,
                mGroupsData,
                R.layout.activity_weather_main_nearst_daysplan_group_item,
                new String[]{SharedArgument.ARG_TITLE, SharedArgument.ARG_NUM},new int[]{R.id.groupTitle,R.id.numberIndicater},
                mDaysPlanData,
                R.layout.activity_weather_main_nearst_daysplan_child_item,
                new String[]{SharedArgument.ARG_SRC_PROV_NAME,SharedArgument.ARG_SRC_CITY_NAME,SharedArgument.ARG_DST_PROV_NAME,SharedArgument.ARG_DST_CITY_NAME},
                new int[]{R.id.srcProvView,R.id.srcCityView,R.id.dstProvView,R.id.dstCityView}
                );
        mNearstDaysPlanView.setAdapter(mDaysPlanAdapter);
        mNearstDaysPlanView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                ImageView indicator= (ImageView) v.findViewById(R.id.groupIndicator);
                indicator.setRotation(90 - indicator.getRotation());
                return false;
            }
        });


        mHandler = new GetWeatherInfoHandler();

        Intent intent=getIntent();
        Calendar current=Calendar.getInstance();
        mYear = intent.getIntExtra(SharedArgument.ARG_YEAR,current.get(Calendar.YEAR));
        mMonth = intent.getIntExtra(SharedArgument.ARG_MONTH,current.get(Calendar.MONTH));
        mDate = intent.getIntExtra(SharedArgument.ARG_DATE,current.get(Calendar.DAY_OF_MONTH));
        mProvIndex=intent.getIntExtra(SharedArgument.ARG_PROV_INDEX,mProvIndex);//if not opened by others,use default saved value.
        mCityIndex=intent.getIntExtra(SharedArgument.ARG_CITY_INDEX,mCityIndex);



        new Thread(new Runnable() {
            @Override
            public void run() {
                mdb=mSqliteHelper.getWritableDatabase();
                {//must init
//                    SqliteHelper.recreate(mdb,SqliteHelper.TABLE_WEATHER);
                }
                if(mDoInitTablse)
                {
                    SqliteHelper.initProvinces(mdb);
                    SqliteHelper.initCities(mdb);
                    SqliteHelper.initFestivals(mdb);
                }
                mProvinces = SqliteHelper.getProvincesAsArrayList(mdb);
                mCities = SqliteHelper.getCitiesAsHashMap(mdb);

//                SqliteHelper.recreate(mdb,SqliteHelper.TABLE_TRAVEL_PLAN);
//                SqliteHelper.recreate(mdb,SqliteHelper.TABLE_WEATHER);
                requestContentPrepare();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setShownDateSync(mYear,mMonth,mDate);//at this time
                        setCitySync(mProvIndex,mCityIndex);//will wait db to be ready


                        updateNearstThreedaysInfoToViewsAsync();
                        updateDaysInfoToViewsAsync();//this must be second

                        updateNearestDaysPlanInfoToViewAsync();
                    }
                });
//                Util.logi("prov size="+SqliteHelper.getProvincesAsArrayList(mdb).size());
//                Util.logi("one city size="+SqliteHelper.getCitiesAsHashMap(mdb).getByGetter(SqliteHelper.getProvincesAsArrayList(mdb).getByGetter(0)[0]));
            }
        }).start();

    }

    public void requestContentPrepare()
    {
        WeatherInfoProvider.requestPrepare(getContentResolver(),(int)mProvinces.get(mProvIndex)[0],
                (int)mCities.get((Integer) mProvinces.get(mProvIndex)[0]).get(mCityIndex)[0],
                NEASERST_DAY_SHOWN_NUM);
    }

    public void setShownDateSync(int year, int month, int date)
    {
        mYear=year;
        mMonth=month;
        mDate=date;

        mYearView.setText(""+year);
        mMonthView.setText(""+(month+1));
        mDateView.setText(""+date);
    }

    public void setCitySync(int provIndex, int cityIndex)
    {
        if(provIndex==-1 && cityIndex==-1)
        {
            //set to current location
        }
        mProvIndex=provIndex;
        mCityIndex=cityIndex;

        mProvView.setText((String)mProvinces.get(provIndex)[1]);
        mCityView.setText((String)mCities.get(mProvinces.get(provIndex)[0]).get(cityIndex)[1]);
    }

    /**
     * a new worker thread to do this
     */
    public void updateNearstThreedaysInfoToViewsAsync()
    {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String uri=String.format(WeatherInfoProvider.CONTENT_URI_NEXT_FEW_DAYS_FORMATTER,
                        mProvinces.get(mProvIndex)[0],
                        mCities.get((Integer) mProvinces.get(mProvIndex)[0]).get(mCityIndex)[0],
                        NEASERST_DAY_SHOWN_NUM);
                final Cursor cursor=getContentResolver().query(Uri.parse(uri),null,null,null,null);
                final Object[][] params=new Object[mNearestDaysWeatherInfoViews.length][3];

                for(int i=0;i<mNearestDaysWeatherInfoViews.length;i++)
                {
                    if(cursor!=null && cursor.moveToNext())
                    {
                        params[i][0]= SharedArgument.weatherStringToDrawable(cursor.getString(cursor.getColumnIndex(SqlUtil.COL_WEATHER_STATE)));
                        params[i][1]= cursor.isNull(cursor.getColumnIndex(SqlUtil.COL_MAX_TEMPERATURE))?null:cursor.getInt(cursor.getColumnIndex(SqlUtil.COL_MAX_TEMPERATURE));
                        params[i][2]= cursor.isNull(cursor.getColumnIndex(SqlUtil.COL_MIN_TEMPERATURE))?null:cursor.getInt(cursor.getColumnIndex(SqlUtil.COL_MIN_TEMPERATURE));
//                        Util.logi("params["+i+"]="+params[i][0]+","+params[i][1]+","+params[i][2]);
                    }
                }
                if(cursor!=null && cursor.getCount() > 0)
                {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            updateNearstThreedaysInfoToViewsByParametersSync(params);
                        }
                    });
                }else{
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            updateNearstThreedaysInfoToViewsAsNoInfoSync();
                        }
                    });
                }
            }
        }).start();
    }

    public void updateNearstThreedaysInfoToViewsAsNoInfoSync()
    {
        String unknown=getResources().getString(R.string.unknown);
        for(int i=0;i<mNearestDaysWeatherInfoViews.length;i++)
        {
            ((TextView)mNearestDaysWeatherInfoViews[i].findViewById(R.id.monthDateView)).setText(String.format(MONTH_DATE_FORMATTER,mMonth+1,mDate+i+1));
            ((ImageView)mNearestDaysWeatherInfoViews[i].findViewById(R.id.weatherIndicator)).setImageDrawable(null);
            ((TextView)mNearestDaysWeatherInfoViews[i].findViewById(R.id.maxMinTemperature)).setText(String.format(MAX_MIN_TEMPERATURE_FORMATTER,unknown,unknown));
        }
    }

    /**
     *
     * @param params each parameter is [weatherImage,max temperature,min temperature]
     */
    public void updateNearstThreedaysInfoToViewsByParametersSync(Object[][] params)
    {
        String cannotAccess=getString(R.string.cannotAccess);
        for(int i=0;i<mNearestDaysWeatherInfoViews.length;i++)
        {
            ((TextView)mNearestDaysWeatherInfoViews[i].findViewById(R.id.monthDateView)).setText(String.format(MONTH_DATE_FORMATTER,mMonth+1,mDate+i+1));
            if(params[i][0]!=null && (Integer)params[i][0]!=-1)
                ((ImageView)mNearestDaysWeatherInfoViews[i].findViewById(R.id.weatherIndicator)).setImageResource((Integer) params[i][0]);
            else
                ((ImageView)mNearestDaysWeatherInfoViews[i].findViewById(R.id.weatherIndicator)).setImageDrawable(null);

            ((TextView)mNearestDaysWeatherInfoViews[i].findViewById(R.id.maxMinTemperature)).setText(
                    (params[i][1]==null||params[i][2]==null)?cannotAccess:
                    String.format(MAX_MIN_TEMPERATURE_FORMATTER,""+params[i][1],""+params[i][2]));
        }
    }

    public void updateDaysInfoToViewsAsync()
    {
        //after set the result cursor
        new Thread(new Runnable() {
            @Override
            public void run() {
                String uri=String.format(WeatherInfoProvider.CONTENT_URI_TODAY_FORMATTER,
                        mProvinces.get(mProvIndex)[0],
                        mCities.get((Integer) mProvinces.get(mProvIndex)[0]).get(mCityIndex)[0]
                );
                Util.logi("uri="+uri);
                final Cursor cursor=getContentResolver().query(Uri.parse(uri),null,null,null,null);

                if(cursor!=null && cursor.moveToNext())
                {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            updateDaysInfoViewsByParametersSync(SharedArgument.weatherStringToDrawable(
                                    cursor.getString(cursor.getColumnIndex(SqlUtil.COL_WEATHER_STATE))
                            ),
                            cursor.isNull(cursor.getColumnIndex(SqlUtil.COL_CUR_TEMPERATURE))?null:cursor.getInt(cursor.getColumnIndex(SqlUtil.COL_CUR_TEMPERATURE)),
                                    cursor.getString(cursor.getColumnIndex(SqlUtil.COL_WEATHER_TEXT)),
                                    cursor.getString(cursor.getColumnIndex(SqlUtil.COL_SUGGESTION_UV)),
                                    cursor.isNull(cursor.getColumnIndex(SqlUtil.COL_WIND_SCALE))?null:cursor.getInt(cursor.getColumnIndex(SqlUtil.COL_WIND_SCALE)),
                                    cursor.getString(cursor.getColumnIndex(SqlUtil.COL_WIND_DIRECTION)),
                                    cursor.isNull(cursor.getColumnIndex(SqlUtil.COL_LAST_UPDATE_HOUR))?null:cursor.getInt(cursor.getColumnIndex(SqlUtil.COL_LAST_UPDATE_HOUR)),
                                    cursor.isNull(cursor.getColumnIndex(SqlUtil.COL_LAST_UPDATE_MINUTE))?null:cursor.getInt(cursor.getColumnIndex(SqlUtil.COL_LAST_UPDATE_MINUTE)),
                                                    cursor.getString(cursor.getColumnIndex(SqlUtil.COL_SUGGESTION_CAR_WASHING)),
                                                    cursor.getString(cursor.getColumnIndex(SqlUtil.COL_SUGGESTION_DRESSING)),
                                                    cursor.getString(cursor.getColumnIndex(SqlUtil.COL_SUGGESTION_FLU)),
                                                    cursor.getString(cursor.getColumnIndex(SqlUtil.COL_SUGGESTION_SPORT)),
                                                    cursor.getString(cursor.getColumnIndex(SqlUtil.COL_SUGGESTION_TRAVEL))
                            );
                        }
                    });
                }else{
//                    Util.logi("cannot getByGetter information");
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            updateDaysInfoViewsAsNoInfoSync();
                        }
                    });
                }
            }
        }).start();
    }

    public void updateDaysInfoViewsAsNoInfoSync()
    {
//        String cannotAccess=getResources().getString(R.string.cannotAccess);
        mWeatherIndicator.setImageDrawable(null);
        mTemperatureView.setText(R.string.cannotAccess);
        mWeatherTextView.setText(R.string.cannotAccess);
        mUVView.setText(R.string.cannotAccess);
        mWindDirectionView.setText(R.string.cannotAccess);
        mWindScaleView.setText(R.string.cannotAccess);

    }
    public void updateDaysInfoViewsByParametersSync(Integer weatherDrawable, Integer temperature, String weatherText, String uv, Integer windScale,String windDirection,
                                                    Integer lastUpdateHour, Integer lastUpdateMinute,
                                                    String carWashing,
                                                    String dressing, String flu, String sport, String travle
    )
    {
        String cannotAccess=getString(R.string.cannotAccess);
        if(weatherDrawable!=null && weatherDrawable!=-1)
            mWeatherIndicator.setImageResource(weatherDrawable);

        mTemperatureView.setText(""+(temperature==null?cannotAccess:temperature));
        mWindScaleView.setText(""+(windScale==null?cannotAccess:windScale));
        mWindDirectionView.setText(windDirection==null?cannotAccess:windDirection);
        mWeatherTextView.setText(weatherText==null?cannotAccess:weatherText);
        mUVView.setText(uv==null?cannotAccess:uv);
        mLastUpdateTimeView.setText((lastUpdateHour==null||lastUpdateMinute==null)?cannotAccess:String.format("%02d:%02d",lastUpdateHour,lastUpdateMinute));
        mCarWasingView.setText(carWashing==null?cannotAccess:carWashing);
        mDressingView.setText(dressing==null?cannotAccess:dressing);
        mFluView.setText(flu==null?cannotAccess:flu);
        mSportView.setText(sport==null?cannotAccess:sport);
        mTravelView.setText(travle==null?cannotAccess:travle);
    }

    public void setDateViewToCurrentDay()
    {
        Calendar calendar=Calendar.getInstance();
        setShownDateSync(calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH));

        updateDaysInfoToViewsAsync();
    }

    public void updateNearestDaysPlanInfoToViewAsync()  {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Calendar calendar = new GregorianCalendar(mYear, mMonth, mDate);
                String selection = SqlUtil.COL_YEAR + "=? and " + SqlUtil.COL_MONTH + "=? and " + SqlUtil.COL_DATE + "=?";
                String[] selectionArgs = new String[3];
                String[] projection = new String[]{SqlUtil.COL_SRC_PROV_ID, SqlUtil.COL_SRC_CITY_ID, SqlUtil.COL_DST_PROV_ID, SqlUtil.COL_DST_CITY_ID};

                final String undeterminded = getResources().getString(R.string.undetermined);
                for (int i = 0; i < PLAN_SHOWN_DAY_NUM; i++) {
                    mDaysPlanData.get(i).clear();
                    selectionArgs[0] = String.valueOf(calendar.get(Calendar.YEAR));
                    selectionArgs[1] = String.valueOf(calendar.get(Calendar.MONTH));
                    selectionArgs[2] = String.valueOf(calendar.get(Calendar.DAY_OF_MONTH));
                    final Cursor c = mdb.query(SqliteHelper.TABLE_TRAVEL_PLAN, projection, selection, selectionArgs, null, null, null);
                    while (c.moveToNext()) {
                        mDaysPlanData.get(i).add(new HashMap<String, String>() {{

                            put(SharedArgument.ARG_SRC_PROV_NAME,  fulton.shaw.android.tellmelibrary.config.ShareableArguments.nonNullOrDefault(
                                    SqliteHelper.getProvinceIDToNameAsHashMap(mdb).get(c.getInt(c.getColumnIndex(SqlUtil.COL_SRC_PROV_ID))), undeterminded));
                            put(SharedArgument.ARG_SRC_CITY_NAME, fulton.shaw.android.tellmelibrary.config.ShareableArguments.nonNullOrDefault(
                                    SqliteHelper.getCityIDToNameAsHashMap(mdb).get(c.getInt(c.getColumnIndex(SqlUtil.COL_SRC_CITY_ID))), undeterminded));
                            put(SharedArgument.ARG_DST_PROV_NAME, fulton.shaw.android.tellmelibrary.config.ShareableArguments.nonNullOrDefault(
                                    SqliteHelper.getProvinceIDToNameAsHashMap(mdb).get(c.getInt(c.getColumnIndex(SqlUtil.COL_DST_PROV_ID))), undeterminded));
                            put(SharedArgument.ARG_DST_CITY_NAME, fulton.shaw.android.tellmelibrary.config.ShareableArguments.nonNullOrDefault(
                                    SqliteHelper.getCityIDToNameAsHashMap(mdb).get(c.getInt(c.getColumnIndex(SqlUtil.COL_DST_CITY_ID))), undeterminded));
                        }});

                    }
                    int nsize = mDaysPlanData.get(i).size();
                    if (nsize == 0) mGroupsData.get(i).put(SharedArgument.ARG_NUM, "");
                    else
                        mGroupsData.get(i).put(SharedArgument.ARG_NUM, "" + mDaysPlanData.get(i).size());
                    c.close();
                    calendar.add(Calendar.DAY_OF_MONTH, 1);
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mDaysPlanAdapter.notifyDataSetChanged();
                    }
                });
            }
        }).start();
    }

    @Override
    protected void onStart() {
        Util.logi("onStart");
        super.onStart();
    }

    @Override
    protected void onStop() {
        Util.logi("onStop");
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        Util.logi("onDestroy");
        Util.logi("savePreferences");
        SharedPreferences preferences=getSharedPreferences(SharedArgument.CONFIG_SHARED_PREF_FILE,MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        if(mSaveLocationOnExit) {
            editor.putInt(SharedArgument.ARG_PROV_INDEX, mProvIndex);
            editor.putInt(SharedArgument.ARG_CITY_INDEX,mCityIndex);
        }
        editor.putBoolean(SharedArgument.ARG_DEFAULT_DIALOG_CHECKED,mDialogDefaultValue);
        editor.commit();

        super.onDestroy();
    }

    @Override
    protected void onResume() {
        Util.logi("onResume");

        if(mdb!=null)
            updateNearestDaysPlanInfoToViewAsync();
        super.onResume();
    }

    @Override
    protected void onPause() {
        Util.logi("onPause");
        super.onPause();
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        Util.logi("onRestoreInstanceState");
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    protected void onRestart() {
        Util.logi("onRestart");
        super.onRestart();
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        Util.logi("onSaveInstance");
        outState.putInt(STATE_PROV_INDEX,mProvIndex);
        outState.putInt(STATE_CITY_INDEX,mCityIndex);
        outState.putBoolean(STATE_DIALOG_DEFAULT,mDialogDefaultValue);
        super.onSaveInstanceState(outState);
    }

    public void onClickViewCalendarButton(View v)
    {
        Intent intent=new Intent(this,CalendarViewActivity.class);
        startActivity(intent);
    }

    public void onClickSelectCityButton(View v)
    {
        float basex=v.getX();
        float basey=v.getY();
//        Util.logi("basex="+basex+" basey="+basey);
        final Dialog d=new Dialog(this){
            @Override
            public boolean onTouchEvent(MotionEvent event)
            {
                if(event.getAction() == MotionEvent.ACTION_OUTSIDE){//sured
                    mProvIndex=mProvSpinner.getSelectedItemPosition();
                    mCityIndex=mCitySpinner.getSelectedItemPosition();
                    final Object[] objects= mCurCityList.get(mCityIndex);
                    final String queryId= (String)objects[2];
                    WeatherMainActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            setCitySync(mProvIndex,mCityIndex);
                        }
                    });

                    if(queryId==null)
                    {
                        Toast.makeText(WeatherMainActivity.this,"Sorry,Currently we do not have info about this city.",Toast.LENGTH_LONG).show();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                updateDaysInfoViewsAsNoInfoSync();
                                updateNearstThreedaysInfoToViewsAsNoInfoSync();
                            }
                        });
                    }else{
                        Util.logi("to update");
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                requestContentPrepare();
                                updateDaysInfoToViewsAsync();
                                updateNearstThreedaysInfoToViewsAsync();
                            }
                        }).start();
                    }
                    mSaveLocationOnExit = mDefaultChoosedCheckBox.isChecked();
                    mDialogDefaultValue = mSaveLocationOnExit;

                    this.dismiss();
                    return true;
                }
                return false;
            }
        };

        Window window=d.getWindow();
        window.requestFeature(Window.FEATURE_NO_TITLE);

        d.setContentView(R.layout.activity_weather_main_select_city);
        mProvSpinner = (Spinner) d.findViewById(R.id.spinnerProvince);
        mCitySpinner =(Spinner)d.findViewById(R.id.spinnerCity);
        mDialogCancelButton = (ImageView) d.findViewById(R.id.cancelButton);
        mDefaultChoosedCheckBox = (CheckBox) d.findViewById(R.id.chooseIfDefault);
        mDefaultChoosedCheckBox.setChecked(mDialogDefaultValue);



//        ArrayAdapter<T> 中T的类型用于获取text,

        final ArrayAdapter<String> provAdapter=new ArrayAdapter<String>(WeatherMainActivity.this,android.R.layout.simple_spinner_item){

            @Override
            public int getCount() {
                return mProvinces.size();
            }

            @Nullable
            @Override
            public String getItem(int position) {
                return (String) mProvinces.get(position)[1];
            }
        };

        final ArrayAdapter<String> cityAdapter=new ArrayAdapter<String>(WeatherMainActivity.this,android.R.layout.simple_spinner_item){
            @Override
            public int getCount() {
                return mCurCityList ==null?0: mCurCityList.size();
            }

            @Nullable
            @Override
            public String getItem(int position) {
                return (String) mCurCityList.get(position)[1];
            }
        };

        mProvSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                int curProvCode= (int) mProvinces.get(position)[0];
                mCurCityList = mCities.get(curProvCode);
                cityAdapter.notifyDataSetChanged();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                if(mCurCityList !=null) {
                    mCurCityList = null;
                    cityAdapter.notifyDataSetChanged();
                }
            }

        });

        mDialogCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                d.cancel();
            }
        });


        mProvSpinner.setAdapter(provAdapter);
        mCitySpinner.setAdapter(cityAdapter);


        mProvSpinner.setSelection(mProvIndex);
        mCitySpinner.setSelection(mCityIndex);

        window.addFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL);
        window.addFlags(WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH);
        window.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);//when show this,will the background be dimmed?
        WindowManager.LayoutParams wmlp = window.getAttributes();
//        wmlp.x=(int)basex;
//        wmlp.y =(int)basey;
        wmlp.gravity = Gravity.TOP | Gravity.LEFT;
        wmlp.x= (int) basex;
        wmlp.y= (int) basey;




        d.show();

    }
        @Override
        public void applyListen(int listenerId, int eventType, Object... args) {
            switch (eventType)
            {
                case GetWeatherInfoService.EVENT_DONE_GET_WEATHER_INFO:
                    Bundle result= (Bundle) args[0];
                    Util.logi("temperature="+result.getInt(SharedArgument.ARG_TEMPERATURE));
                    break;
            }
        }

    public void onClickSeeDetailButton(View v)
    {
        Intent intent=new Intent(this,DateDetailActivity.class);
        intent.putExtra(SharedArgument.ARG_PAGE_NUM,DateDetailActivity.PAGE_TRAVEL_PLAN);
        intent.putExtra(SharedArgument.ARG_YEAR,Integer.parseInt(mYearView.getText().toString()));
        intent.putExtra(SharedArgument.ARG_MONTH,Integer.parseInt(mMonthView.getText().toString())-1);
        intent.putExtra(SharedArgument.ARG_DATE,Integer.parseInt(mDateView.getText().toString()));
        startActivity(intent);
    }
    public void onClickShareButton(View v)
    {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("sms:"));
        intent.putExtra("sms_body", String.format("%s,%s  %d年%02d月%02d日,天气:%s   紫外线:%s\n\n 此短信由日程通App发送.",
                mProvView.getText().toString(),
                mCityView.getText().toString(),
                mYear,
                mMonth+1,
                mDate,
                mWeatherTextView.getText().toString(),
                mUVView.getText().toString()
        ));
        startActivity(intent);
    }

    public void onClickUpdateButton(View v)
    {
        Toast.makeText(this,R.string.updating,Toast.LENGTH_SHORT).show();

        new Thread(new Runnable() {
            @Override
            public void run() {
                requestContentPrepare();
                updateDaysInfoToViewsAsync();
                updateNearstThreedaysInfoToViewsAsync();
            }
        }).start();
    }
    @Override
    public SQLiteDatabase getContextDB() {
        return mdb;
    }

}
