package fulton.shaw.android.x.activities;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.ContextMenu;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.GregorianCalendar;
import java.util.HashMap;

import fulton.shaw.android.x.R;
import fulton.shaw.android.x.public_interfaces.ActivityUtil;
import fulton.shaw.android.x.public_interfaces.AppConfig;
import fulton.shaw.android.x.public_interfaces.SqliteHelper;
import fulton.util.android.utils.StringFormatters;
import fulton.util.android.interfaces.ValueGetter;
import fulton.util.android.varman.StateVariableManager;
import fulton.shaw.android.x.views.DatePickerDialogWithTextView;
import fulton.shaw.android.x.views.TextSwitchableTextView;
import fulton.util.android.interfaces.tuples.Triple;
import fulton.util.android.notations.CoreMethodOfThisClass;
import fulton.util.android.notations.TestNote;
import fulton.util.android.utils.CalendarUtil;
import fulton.util.android.utils.SqlUtil;
import fulton.util.android.utils.Util;
import fulton.util.android.aware.ContextSQLiteAware;
import fulton.util.android.aware.PrefSettingsAware;
import fulton.util.android.aware.UiThreadAware;
import fulton.util.android.notations.HelperMethods;
import fulton.util.android.notations.MayConsumeTimePleaseUtilize;
import fulton.util.android.utils.ViewUtil;

/**
 *  you cannot change the mode at activity alive
 */
@MayConsumeTimePleaseUtilize("Static initialization")
@Deprecated
public class ViewAsListActivity extends AppCompatActivity implements ContextSQLiteAware,PrefSettingsAware,UiThreadAware{
    public static final int DEFAULT_DATE_LAYOUT=R.layout.show_list_date;
    public static final int DEFAULT_TRAVEL_NOTE_LAYOUT=R.layout.show_list_travel_note;
    public static final int DEFAULT_PLAN_LAYOUT=R.layout.show_list_plan;
    public static final int DEFAULT_GENERAL_RECORD_LAYOUT=0;
    public static final int DEFAULT_GENERAL_RECORD_TAG_THOUGHT_LAYOUT=0;
    public static final int DEFAULT_GENERAL_RECORD_TAG_DIARY_LAYOUT=0;
    public static final int DEFAULT_GENERAL_RECORD_TAG_PROBLEM_LAYOUT=0;
    public static final int DEFAULT_GENERAL_RECORD_TAG_COMMON_SENSE_LAYOUT=0;

    public static final String KEY_DATE="DATE";
    public static final String KEY_TRAVEL_NOTE=SqliteHelper.TABLE_TRAVEL_NOTE;
    public static final String KEY_PLAN=SqliteHelper.TABLE_PLAN;
    public static final String KEY_GENERAL_RECORD=SqliteHelper.TABLE_GENERAL_RECORDS;
    public static final String KEY_GENERAL_RECORD_THOUGHT=SqliteHelper.TABLE_GENERAL_RECORDS+SqlUtil.TAG_THOUGHT;
    public static final String KEY_GENERAL_RECORD_DIARY=SqliteHelper.TABLE_GENERAL_RECORDS+SqlUtil.TAG_DIARY;
    public static final String KEY_GENERAL_RECORD_PROBLEM=SqliteHelper.TABLE_GENERAL_RECORDS+SqlUtil.TAG_PROBLEM;
    public static final String KEY_GENERAL_RECORD_COMMON_SENSE=SqliteHelper.TABLE_GENERAL_RECORDS+SqlUtil.TAG_COMMON_SENSE;


    public static final HashMap<String,Integer> DEFAULT_LAYOUT_MAP=new HashMap<String, Integer>(){{
       put(KEY_DATE,DEFAULT_DATE_LAYOUT);
        put(KEY_TRAVEL_NOTE,DEFAULT_TRAVEL_NOTE_LAYOUT);
        put(KEY_PLAN,DEFAULT_PLAN_LAYOUT);
        put(KEY_GENERAL_RECORD,DEFAULT_GENERAL_RECORD_LAYOUT);
        put(KEY_GENERAL_RECORD_THOUGHT,DEFAULT_GENERAL_RECORD_TAG_THOUGHT_LAYOUT);
        put(KEY_GENERAL_RECORD_DIARY,DEFAULT_GENERAL_RECORD_TAG_DIARY_LAYOUT);
        put(KEY_GENERAL_RECORD_PROBLEM,DEFAULT_GENERAL_RECORD_TAG_PROBLEM_LAYOUT);
        put(KEY_GENERAL_RECORD_COMMON_SENSE,DEFAULT_GENERAL_RECORD_TAG_COMMON_SENSE_LAYOUT);
    }};

    public static final Class[] MAPPED_ACTIVITY=new Class[]{
            AddPlanActivity.class,
            AddTravelNoteAcitivity.class,
            AddGeneralRecordTagDiaryActivity.class,
            AddGeneralRecordTagBlogActivity.class,
            AddGeneralRecordTagProblemActivity.class,

            null,
            null,
            AddThingsPriceActivity.class,
            null,
            null,

            null,
//            AddGeneralRecordTagSaySomethingActivity.class
            AddGeneralRecordTagSaySomethingVeryNewActivity.class
    };
    public static final int NUM_EACH_ROW_IN_DIALOG=3;

    //==helper fields
    protected StateVariableManager mVarMan;
    protected SqliteHelper mSqliteHelper;

    private int mTitleRes=R.array.support_types;
    private String[] mDialogTitles;//String or Int
    private Object[] mDialogIcons;

    protected ArrayList<String> mFilterTags;

    protected int               mFilterCondition;

    private SQLiteDatabase mDb;
    private ListView mShowListView;
    private ShowListAdapter mAdapter;
    private DatabaseOperationHelper mDbHelper;
    private ArrayList<Object[]> mListData;// [Calendar, ArrayList[Object<Table,ContentValues>] ]
    private ArrayList<Object[]> mListDataPositionMap;//position-->i,j, if j==-1,then that means the start or a view
    private SharedPreferences mActivityPref;
    private SharedPreferences.Editor mActivityPrefEditor;
    private Thread mUiThread;

    protected String[] mTables;

    //===popup dialog
    protected TagFilterDialogWrapper mDialogWrapper;

    //====intent open args
    public static final int FUNCTION_NORMAL=0;
    public static final int FUNCTION_SELECT=1;

    public static final String ARG_FUNCTION="function";

    //===used for {@link #FUNCTION_SELECT}
    public static final String ARG_TABLE_NAME="tableName";
    public static final String ARG_INNER_ID="innerId";

    private Intent mArgIntent;
    private int mArgFunction;

    //==refTable,refId, present.
    private static final int TYPE_ORIGINAL_DELETED=0;
    private static final int TYPE_ORIGINAL_PRESENT=1;
    private static final int TYPE_NEWLY_ADDED=2;


    private ArrayList<Triple<String,Long,Integer>> mSelectedData;//原来的是有内容的，其他的没有.  分类
                // 原来的， 已删除
                //原来的，未删除
                //新增的

    private void initIntentArgs()
    {
        mArgIntent=getIntent();
        mArgFunction=mArgIntent.getIntExtra(ARG_FUNCTION,FUNCTION_NORMAL);
        if(mArgFunction==FUNCTION_SELECT)
        {
            String table=mArgIntent.getStringExtra(ARG_TABLE_NAME);
            long id=mArgIntent.getLongExtra(ARG_INNER_ID,-1);
            mSelectedData=new ArrayList<>();
            Cursor cursor=mSqliteHelper.getCursorByRefId(SqliteHelper.TABLE_REFERENCE,table,id);
            while (cursor.moveToNext())
            {
               mSelectedData.add(new Triple<String, Long,Integer>(
                       SqlUtil.getCursorValue(cursor,SqlUtil.COL_REF_TABLE_NAME,String.class),
                        SqlUtil.getCursorValue(cursor,SqlUtil.COL_REF_INNER_ID,Long.class),
                       TYPE_ORIGINAL_PRESENT
                       ));
            }
            cursor.close();
        }
    }

    private int findIndexOfSelected(String table,long id)
    {
        for(int i=0;i<mSelectedData.size();i++)
        {
            if(mSelectedData.get(i).first.equals(table) && mSelectedData.get(i).second==id)
                return i;
        }
        return -1;
    }

    /**
     *
     * @param selected define 2 colors, the original, and the red_litte
     */
    private void setSelected(View v,boolean selected)
    {
        if(selected)
            v.setBackgroundColor(getResources().getColor(R.color.red_little));
        else
            v.setBackgroundColor(getResources().getColor(R.color.transparent));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        FirstTimeAppRunning.runIfTheFirstTime(this);

        setContentView(R.layout.activity_view_as_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //==getByGetter state variables
        mActivityPref=getPreferences(MODE_PRIVATE);
        mActivityPrefEditor = mActivityPref.edit();
        mVarMan=new StateVariableManager(mActivityPref);
        mVarMan.load();
        mVarMan.setDefaultValue(AppConfig.CONFIG_KEY_COMMON_TAGS_LIST,new ArrayList<>());

        //====
        mSqliteHelper=new SqliteHelper(this);
        mDb= mSqliteHelper.getContextDB();
        initIntentArgs();

        mTables=new String[]{SqliteHelper.TABLE_TRAVEL_NOTE, SqliteHelper.TABLE_PLAN, SqliteHelper.TABLE_GENERAL_RECORDS};


        mVarMan.setDefaultValue(AppConfig.CONFIG_KEY_FILTER_CONDITION, SqlUtil.FILTER_ANY);

        mFilterTags=new ArrayList<>();


        mUiThread=Thread.currentThread();

        mDialogTitles = getResources().getStringArray(mTitleRes);


        mShowListView = (ListView) findViewById(R.id.showList);
        mDbHelper = new DatabaseOperationHelper();
        mAdapter = new ShowListAdapter();


        mListDataPositionMap=new ArrayList<>();
        mShowListView.setAdapter(mAdapter);


        this.registerForContextMenu(mShowListView);
        mShowListView.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
            @Override
            public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
                getMenuInflater().inflate(R.menu.context_menu_for_show_list_item,menu);
            }
        });
        if(mArgFunction==FUNCTION_NORMAL) {
            mShowListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    viewDetails(position);
                }
            });
        }else if(mArgFunction==FUNCTION_SELECT){
            mShowListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Object[] pos=mListDataPositionMap.get(position);
                    int i=(Integer)pos[0];
                    int j=(Integer)pos[1];
                    if(j==-1)return;

                    ArrayList<Object[]> data= (ArrayList<Object[]>) mListData.get(i)[1];
                    String table=(String)data.get(j)[0];
                    long innerId=((ContentValues)data.get(j)[1]).getAsLong(SqlUtil.COL_ID);
                    int index=findIndexOfSelected(table,innerId);
                    String argTable=mArgIntent.getStringExtra(ARG_TABLE_NAME);
                    long argId=mArgIntent.getLongExtra(ARG_INNER_ID,-1);
                    if(argId==-1)
                    {
                        Toast.makeText(ViewAsListActivity.this,"Id参数不能为-1,请退出重试",Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if(index==-1) {
                        long insertId = mSqliteHelper.insertBasedOnKeys(SqliteHelper.TABLE_REFERENCE,
                                SqlUtil.COL_TABLE_NAME, argTable,
                                SqlUtil.COL_INNER_ID, argId,
                                SqlUtil.COL_REF_TABLE_NAME, table,
                                SqlUtil.COL_REF_INNER_ID, innerId
                        );
                        if (insertId == -1) {
                            Toast.makeText(ViewAsListActivity.this, "不能插入数据库，请退出重试或更新版本", Toast.LENGTH_SHORT).show();
                        } else {
                            mSelectedData.add(new Triple<String, Long, Integer>(table, innerId, TYPE_NEWLY_ADDED));
                            setSelected(view, true);
                        }
                    }else if(mSelectedData.get(index).third==TYPE_NEWLY_ADDED){
                        mSqliteHelper.execSQL("Delete From "+SqliteHelper.TABLE_REFERENCE+" Where "+SqlUtil.COL_TABLE_NAME+"=? and " +
                                        SqlUtil.COL_INNER_ID+"=? and "+SqlUtil.COL_REF_TABLE_NAME+"=? and "+
                                        SqlUtil.COL_REF_INNER_ID+"=?",
                                new String[]{argTable,""+argId,table,""+innerId}
                        );
                        do {
                            mSelectedData.remove(index);
                        }while((index=findIndexOfSelected(table,innerId))!=-1  && mSelectedData.get(index).third==TYPE_NEWLY_ADDED);
                        setSelected(view,false);
                    }else{
                        mSelectedData.get(index).third=1-mSelectedData.get(index).third;
                        setSelected(view,mSelectedData.get(index).third==TYPE_ORIGINAL_PRESENT?true:false);
                    }
                }
            });
        }

        //===========adjust views
        updateViews();


        //do the test
//        doTest();
    }
    @Override
    protected void onDestroy() {
        if(mDialogWrapper!=null)
            mDialogWrapper.onDestroy();
        mVarMan.save();
        mActivityPrefEditor.commit();
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        if(mArgFunction==FUNCTION_SELECT)
        {
            setResultForSelect();
            this.finish();
        }else{
            super.onBackPressed();
        }
    }

    public void setResultForSelect()
    {
        setResult(RESULT_OK);
        String argTable=mArgIntent.getStringExtra(ARG_TABLE_NAME);
        long argId=mArgIntent.getLongExtra(ARG_INNER_ID,-1);
        if(argId==-1)return;
        for(Triple<String, Long, Integer> data:mSelectedData)
        {
            if(data.third==TYPE_ORIGINAL_DELETED)
            {
                mSqliteHelper.execSQL("Delete From "+SqliteHelper.TABLE_REFERENCE+" Where "+SqlUtil.COL_TABLE_NAME+"=? and " +
                                SqlUtil.COL_INNER_ID+"=? and "+SqlUtil.COL_REF_TABLE_NAME+"=? and "+
                                SqlUtil.COL_REF_INNER_ID+"=?",
                        new String[]{argTable,""+argId,data.first,""+data.second});
            }
        }
    }

    @HelperMethods
    public static void setIndicater(View v, String status, Resources res)
    {
        if(SqlUtil.STATUS_COMPLISHED.equals(status))
        {
            v.setBackgroundColor(res.getColor(R.color.statusComplished));
        }else if(SqlUtil.STATUS_NOT_COMPLISHED.equals(status))
        {
            v.setBackgroundColor(res.getColor(R.color.statusNotComplished));
        }
    }


    @Override
    protected void onResume() {
//        Util.logi("onResume");
        super.onResume();
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.viewDetails: {
                AdapterView.AdapterContextMenuInfo menuInfo= (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
                viewDetails(menuInfo.position);
                return true;
            }
            case R.id.delete://build delete dialog
            {
                AdapterView.AdapterContextMenuInfo menuInfo= (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
                showDeleteConfirmDialog(menuInfo.position);
                return true;
            }
            case R.id.action_hide:
            {

                return true;
            }
            case R.id.deleteTag:
            {
                Util.logi("in context,deleteTag case");
                return true;
            }
        }
        return false;
    }
    public void viewDetails(int position)
    {
        Object[] pos=mListDataPositionMap.get(position);
        int i=(Integer)pos[0];
        int j=(Integer)pos[1];
        if(j==-1)return;

        ArrayList<Object[]> data= (ArrayList<Object[]>) mListData.get(i)[1];
        ActivityUtil.startViewDetailActivity(this,(String)data.get(j)[0],((ContentValues)data.get(j)[1]).getAsLong(SqlUtil.COL_ID));
    }

    @TestNote
    @HelperMethods
    protected void showDeleteConfirmDialog(int position) {
        Object[] pos = mListDataPositionMap.get(position);
        final int i = (int) pos[0], j = (int) pos[1];
        if (j == -1) {
            //do nothing
        } else {
            new AlertDialog.Builder(ViewAsListActivity.this).setMessage("是否删除?").
                    setNegativeButton("否", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Util.logi("clicked 否");
                        }
                    }).
                    setPositiveButton("是", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Util.logi("clicked 是");
                            ArrayList<Object[]> data = (ArrayList<Object[]>) mListData.get(i)[1];
                            String table = (String) data.get(j)[0];
                            ContentValues values1 = (ContentValues) data.get(j)[1];
                            int id = values1.getAsInteger(SqlUtil.COL_ID);
                            boolean deleted=mSqliteHelper.purgeModel(table,(long)id);
                            if (!deleted) {
                                Toast.makeText(ViewAsListActivity.this, "Delete falied", Toast.LENGTH_SHORT);
                            } else {
                                Util.logi("update views");
                                updateViews();
                            }
                        }
                    }).show();
        }
    }

    public void prepareData()
    {
        if(mListData!=null)
            mListData.clear();
        mListDataPositionMap.clear();
        ArrayList<Object[]> allDataFromDatabase = mDbHelper.getAllDataFromDatabase(
                mTables,
                mVarMan.get(AppConfig.CONFIG_KEY_DATE_MODEL_INDEX,0),
                mVarMan.getByGetter(AppConfig.CONFIG_KEY_START_CALENDAER, ValueGetter.ValueGetterOfCalendarNow.getIntance()),
                mVarMan.getByGetter(AppConfig.CONFIG_KEY_END_CALENDAER,ValueGetter.ValueGetterOfCalendarNow.getIntance()),
                mVarMan.get(AppConfig.CONFIG_KEY_SHOWN_DAYS,7),
                Util.listToArray(mFilterTags,String.class),
                mVarMan.get(AppConfig.CONFIG_KEY_FILTER_CONDITION,Integer.class)
                );
        mListData=mDbHelper.sortAndGroupAllData(allDataFromDatabase);
        int i=-1;
        int j=-1;
        for(Object[] objects:mListData)
        {
            i+=1;
            j=-1;
            mListDataPositionMap.add(new Object[]{i,j});
            ArrayList<Object[]> data= (ArrayList<Object[]>) objects[1];
            for(Object[] objects1:data)
            {
                j++;
                mListDataPositionMap.add(new Object[]{i,j});
            }
        }

    }

    @Override
    public Thread getUiThread() {
        return mUiThread;
    }

    public void updateViews()
    {
        prepareData();
        mAdapter.notifyDataSetChanged();
        //1ms is ok
        Util.runOnUiThreadDelay(this,1, new Runnable() {
            @Override
            public void run() {
                mShowListView.setSelection(mAdapter.getCount()-1);//automatically notify
            }
        });
    }

    @Override
    public SharedPreferences getActivityPref() {
        return mActivityPref;
    }

    @Override
    public SharedPreferences.Editor getActivityPrefEditor() {
        return mActivityPrefEditor;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_view_as_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        final int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_set_arguments) {//show a simple input dialog
            Toast.makeText(this,"set time no longer supported",Toast.LENGTH_SHORT).show();
            //return true already exists.
            ;
//            final SequenceViewFieldInputterAdapter adapter=new SequenceViewFieldInputterAdapter(this);
//            adapter.addAll(new Object[][]{
//                    {"",AppConfig.CONFIG_KEY_START_DATE,null,"起始日期",AdapterFieldsInfo.PROCESS_TYPE_DATE_TIME_DIALOGS_TIME,null,null,mStartDate.getTimeInMillis()},
//                    {"",AppConfig.CONFIG_KEY_NUM_DAYS,null,"天数",AdapterFieldsInfo.PROCESS_TYPE_EDITTEXT,AdapterFieldsInfo.VALUE_TYPE_INT,null,mNumDays},
//                    {"",AppConfig.CONFIG_KEY_SHOW_TODAY,null,"总是显示至当前",AdapterFieldsInfo.PROCESS_TYPE_CHECKBOX,null,null,mShowToday}
//            });
//            AlertDialog.Builder builder=new AlertDialog.Builder(this).setMessage("设置起始日期和连续的天数")
//                    .setNegativeButton("取消",null)
//                    .setPositiveButton("确认", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            final HashMap<String, ArrayList<ContentValues>> data = adapter.collectAsContentValues();
//                            ContentValues value=data.getByGetter("").getByGetter(0);
//                            Util.logi(value);
//                            mStartDate.setTimeInMillis((long)(double)value.getAsDouble(AppConfig.CONFIG_KEY_START_DATE));
//                            mShowToday=value.getAsBoolean(AppConfig.CONFIG_KEY_SHOW_TODAY);
//                            if(mShowToday)
//                                mNumDays = (int) CalendarUtil.calDiffAsDays(Calendar.getInstance(),mStartDate)+1;
//                            else
//                                mNumDays=value.getAsInteger(AppConfig.CONFIG_KEY_NUM_DAYS);
//                            updateViews();
//                        }
//                    });
//            AlertDialog dialog=builder.create();
//
//            LinearLayout holder=new LinearLayout(dialog.getContext());
//            holder.setOrientation(LinearLayout.VERTICAL);
//            holder.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
//            adapter.feedToViewGroup(holder);
//            dialog.setView(holder);
//
//            dialog.show();

            return true;
        }else if(id == R.id.action_see_price){
            Intent intent=new Intent(this,SeePriceActivity.class);
            startActivity(intent);
            return true;
        }else if(id == R.id.action_test){
            Intent intent=new Intent(this,TestActivity.class);
            startActivity(intent);
        }else if(id==R.id.action_view_date_theme){
            Intent intent=new Intent(this,ViewDateThemeActivity.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    private class TagFilterDialogWrapper{
        public ViewAsListActivity mActivity;
        public View mCustomDialogView;
        public AlertDialog mDialog;

        public ArrayAdapter<String> mSelectedListAdapter;
        public ArrayAdapter<String> mCommonAdaper;
        public BaseAdapter mTagSetAdapter;

        //====views
        public ArrayList<ArrayList<ContentValues>> mTagSets;
        public ScrollView mTagFilterDialogScroller;


        public GridView mSelectedList;
        public ListView mTagSetList;
        public GridView mCommonTagList;
        public GridView mAllTagList;
        TextSwitchableTextView mAllTagViewTitle;
        TextSwitchableTextView mTagSetViewTitle;
        TextSwitchableTextView mCommonTitle;

        //==adder view filed
        public EditText mContentInput;
        public Spinner mFilterTypeSpinner;
        public Spinner mDateModelTypeSpinner;
        public EditText mDaysEditText;
        public DatePickerDialogWithTextView mStartDate;
        public DatePickerDialogWithTextView mEndDate;

        public Cursor mAllTagListCursor;

        public CursorAdapter mAllTagListCursorAdapter;


        public int mAdderviewMode;//-1 normal, 0~n modifying


        public TagFilterDialogWrapper()
        {
            initNonviewFields();
            initViews();
            initAdapters();
            initContextMenuFunctions();
            initDialog();
            doCorrections();
        }
        private void initNonviewFields()
        {
            mAdderviewMode=-1;
            mTagSets=mSqliteHelper.getAllTagSet();
            mActivity=ViewAsListActivity.this;
        }
        private Cursor getAllTagListCursor()
        {
            if(mAllTagListCursor==null)
            {
                mAllTagListCursor=mDb.rawQuery("Select Distinct "+SqlUtil.COL_ID+","+SqlUtil.COL_CONTENT+" From "+SqliteHelper.TABLE_TAG+" " +
                        "Group By "+SqlUtil.COL_CONTENT,null);
                mAllTagListCursorAdapter =new CursorAdapter(ViewAsListActivity.this,mAllTagListCursor,false) {
                    @Override
                    public View newView(Context context, Cursor cursor, ViewGroup parent) {
                        return ViewAsListActivity.this.getLayoutInflater().inflate(R.layout.show_list_detail_tag_item,parent,false);
                    }

                    @Override
                    public void bindView(View view, Context context, Cursor cursor) {
                        TextView textView= (TextView) view.findViewById(R.id.content);
                        textView.setText(cursor.getString(cursor.getColumnIndex(SqlUtil.COL_CONTENT)));
                    }
                };
                mAllTagList.setAdapter(mAllTagListCursorAdapter);
                mAllTagList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    mAllTagListCursor.moveToPosition(position);
                    mFilterTags.add(mAllTagListCursor.getString(mAllTagListCursor.getColumnIndex(SqlUtil.COL_CONTENT)));
                    mSelectedListAdapter.notifyDataSetChanged();
                }
            });
            }
            return mAllTagListCursor;
        }

        private void initViews()
        {
            mCustomDialogView=getLayoutInflater().inflate(R.layout.show_list_detail_dialog_filter_by_tag,null,false);
            mTagFilterDialogScroller= (ScrollView) mCustomDialogView.findViewById(R.id.scroller);

            //==find views and set switch functions
            mSelectedList = (GridView) mCustomDialogView.findViewById(R.id.filterTagView);
            mCommonTagList = (GridView) mCustomDialogView.findViewById(R.id.ctagView);
            mTagSetList = (ListView) mCustomDialogView.findViewById(R.id.tagSetListView);
            mAllTagList=(GridView)mCustomDialogView.findViewById(R.id.allTagView);
             mAllTagViewTitle= (TextSwitchableTextView) mCustomDialogView.findViewById(R.id.allTagSwitchButton);
            mTagSetViewTitle= (TextSwitchableTextView) mCustomDialogView.findViewById(R.id.tagSetSwitchButton);
            mCommonTitle= (TextSwitchableTextView) mCustomDialogView.findViewById(R.id.commonListTitle);
            mAllTagViewTitle.setTag(mAllTagList);
            mTagSetViewTitle.setTag(mTagSetList);
            mCommonTitle.setTag(mCommonTagList);
            TextSwitchableTextView.OnSwitchTitleListener listener=new TextSwitchableTextView.OnSwitchTitleListener() {
                @Override
                public void onSwitchTitle(TextSwitchableTextView view) {
                    View relatedView= (View) view.getTag();
                    ViewUtil.setShown(relatedView,view.getTitleSelection()==0?false:true);
                    if(view==mAllTagViewTitle) {
                        getAllTagListCursor().requery();
                        mAllTagListCursorAdapter.notifyDataSetChanged();
                    }
                }
            };
            mAllTagViewTitle.setOnSwitchListener(listener);
            mTagSetViewTitle.setOnSwitchListener(listener);
            mCommonTitle.setOnSwitchListener(listener);


            //===find adderview and set its mode
            View adderView=mCustomDialogView.findViewById(R.id.adderViewContainer);
            mFilterTypeSpinner = (Spinner) adderView.findViewById(R.id.filterConditionSpinner);
            mDateModelTypeSpinner= (Spinner) adderView.findViewById(R.id.dateModeSpinner);
            mContentInput = (EditText) adderView.findViewById(R.id.content);
            mDaysEditText= (EditText) adderView.findViewById(R.id.daysEditText);
            mStartDate= (DatePickerDialogWithTextView) adderView.findViewById(R.id.startTimeTextView);
            mEndDate= (DatePickerDialogWithTextView) adderView.findViewById(R.id.endTimeTextView);


            //===set addder functions
            View.OnClickListener adderviewClickListener=new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(mContentInput.length()==0)
                    {
                        Toast.makeText(ViewAsListActivity.this,"标签不能为空",Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if(v.getId()==R.id.positiveButton)
                    {
                        mFilterTags.add(mContentInput.getText().toString());
                        mSelectedListAdapter.notifyDataSetChanged();
                        mContentInput.setText("");
                    }else if(v.getId()==R.id.nagetiveButton){
                        mVarMan.get(AppConfig.CONFIG_KEY_COMMON_TAGS_LIST,ArrayList.class).add(mContentInput.getText().toString());
                        mCommonAdaper.notifyDataSetChanged();
                        mContentInput.setText("");
                    }else if(v.getId()==R.id.thirdButton){
                        int dateModel=mDateModelTypeSpinner.getSelectedItemPosition();
//                        if(mDaysEditText.getText().length()==0)
//                        {
//                            if(dateModel==DATE_MODEL_JUST_DAYS_TO_TODAY||dateModel==DATE_MODEL_JUST_DAYS_FROM_START)
//                            {
//                                Toast.makeText(ViewAsListActivity.this,"你选择的时间类型必须输入天数",Toast.LENGTH_SHORT).show();
//                                return;
//                            }
//                        }
                        String contentString=mContentInput.getText().toString();
                        final String[] inputTags=contentString.split(" +");
                        ArrayList<String> tagList=new ArrayList<String>(){{
                            for(String s:inputTags)
                                add(s);
                        }};
                        if(mAdderviewMode<0)//add
                        {
                            mSqliteHelper.addTagSet(tagList, mFilterTypeSpinner.getSelectedItemPosition());
                        }else{//modify
                            mSqliteHelper.updateTagSet(mAdderviewMode,tagList, mFilterTypeSpinner.getSelectedItemPosition());
                            mAdderviewMode=-1;
                        }
                        //===set back the values
                        mFilterTypeSpinner.setSelection(mVarMan.get(AppConfig.CONFIG_KEY_FILTER_CONDITION, SqlUtil.FILTER_ALL));
                        mDateModelTypeSpinner.setSelection(mVarMan.get(AppConfig.CONFIG_KEY_DATE_MODEL_INDEX, SqlUtil.DATE_MODEL_ALL_DATE));
                        mTagSets=mSqliteHelper.getAllTagSet();
                        mTagSetAdapter.notifyDataSetChanged();
                        mContentInput.setText("");
                    }
                }
            };
            adderView.findViewById(R.id.positiveButton).setOnClickListener(adderviewClickListener);
            adderView.findViewById(R.id.nagetiveButton).setOnClickListener(adderviewClickListener);
            adderView.findViewById(R.id.thirdButton).setOnClickListener(adderviewClickListener);

        }
        private void initAdapters()
        {
            mSelectedListAdapter =new ArrayAdapter<String>(mActivity,R.layout.show_list_detail_tag_item,R.id.content,mFilterTags);
            mCommonAdaper=new ArrayAdapter<String>(mActivity,R.layout.show_list_detail_tag_item,R.id.content,
                    mVarMan.get(AppConfig.CONFIG_KEY_COMMON_TAGS_LIST,ArrayList.class));
            mTagSetAdapter=new BaseAdapter() {
                @Override
                public int getCount() {
                    return mTagSets.size();
                }

                @Override
                public Object getItem(int position) {
                    return null;
                }

                @Override
                public long getItemId(int position) {
                    return 0;
                }

                @Override
                public View getView(final int position, View convertView, ViewGroup parent) {
                    LinearLayout container=null;
                    if(convertView==null)
                    {
                        container=new LinearLayout(ViewAsListActivity.this);
                        container.setOrientation(LinearLayout.HORIZONTAL);
                        container.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    }else{
                        container=(LinearLayout)convertView;
                    }
                    container.removeViews(0,container.getChildCount());//remove all
                    for(int i=0;i<mTagSets.get(position).size();i++)
                    {
//                        Util.logi("pos,size:"+position+","+mTagSets.getByGetter(position).size());
                        View v=getLayoutInflater().inflate(R.layout.show_list_detail_tag_item,parent,false);
                        TextView  content= (TextView) v.findViewById(R.id.content);
                        content.setText(mTagSets.get(position).get(i).getAsString(SqlUtil.COL_CONTENT));
                        container.addView(v);
                        LinearLayout.LayoutParams lp= (LinearLayout.LayoutParams) v.getLayoutParams();
                        lp.setMarginEnd(getResources().getDimensionPixelSize(R.dimen.tagGap));
                    }
                    return container;
                }
            };

            mSelectedList.setAdapter(mSelectedListAdapter);
            mCommonTagList.setAdapter(mCommonAdaper);
            mTagSetList.setAdapter(mTagSetAdapter);
        }
        public void initContextMenuFunctions()
        {
            //===context menus
            final MenuItem.OnMenuItemClickListener listener = new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {//they are never called
                    AdapterView.AdapterContextMenuInfo menuInfo1 = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
                    switch (item.getItemId())
                    {
                        case R.id.deleteTag:
                            mFilterTags.remove(menuInfo1.position);
                            mSelectedListAdapter.notifyDataSetChanged();
                            break;
                        case R.id.deleteCommonTag:
                            mVarMan.get(AppConfig.CONFIG_KEY_COMMON_TAGS_LIST,ArrayList.class).remove(menuInfo1.position);
                            mCommonAdaper.notifyDataSetChanged();
                            break;
                        case R.id.addCommonTag:
                            mFilterTags.add((String) mVarMan.get(AppConfig.CONFIG_KEY_COMMON_TAGS_LIST,ArrayList.class).get(menuInfo1.position));
                            mSelectedListAdapter.notifyDataSetChanged();
                            break;
                        case R.id.modifyTagSet:
                            mContentInput.setText(Util.join(" ", mTagSets.get(menuInfo1.position), new Util.ArrayListStringGetter() {
                                @Override
                                public String getString(ArrayList list, int i) {
                                    return Util.cast(list.get(i),ContentValues.class).getAsString(SqlUtil.COL_CONTENT);
                                }
                            }));
                            mFilterTypeSpinner.setSelection(mTagSets.get(menuInfo1.position).get(0).getAsInteger(SqlUtil.COL_FILTER_CONDITION));
                            mDateModelTypeSpinner.setSelection(mTagSets.get(menuInfo1.position).get(0).getAsInteger(SqlUtil.COL_DATE_MODEL));
                            mAdderviewMode=menuInfo1.position;
                            break;
                        case R.id.deleteTagSet:
                            ArrayList<ContentValues> values=mTagSets.remove(menuInfo1.position);
                            mSqliteHelper.deleteTagSet(values.get(0).getAsLong(SqlUtil.COL_INNER_ID));
                            mTagSetAdapter.notifyDataSetChanged();
                            break;
                    }
                    return true;
                }
            };
            registerForContextMenu(mSelectedList);
            mSelectedList.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
                @Override
                public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
                    getMenuInflater().inflate(R.menu.context_menu_for_tags, menu);
                    menu.findItem(R.id.deleteTag).setOnMenuItemClickListener(listener);
                }
            });
            registerForContextMenu(mCommonTagList);
            mCommonTagList.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
                @Override
                public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
                    getMenuInflater().inflate(R.menu.context_menu_for_common_tags,menu);
                    menu.findItem(R.id.deleteCommonTag).setOnMenuItemClickListener(listener);
                    menu.findItem(R.id.addCommonTag).setOnMenuItemClickListener(listener);
                }
            });
            registerForContextMenu(mTagSetList);
            mTagSetList.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
                @Override
                public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
                    getMenuInflater().inflate(R.menu.context_menu_for_tag_set,menu);
                    menu.findItem(R.id.deleteTagSet).setOnMenuItemClickListener(listener);
                    menu.findItem(R.id.modifyTagSet).setOnMenuItemClickListener(listener);
                }
            });

            //====click functions
            mSelectedList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    mFilterTags.remove(position);
                    mSelectedListAdapter.notifyDataSetChanged();
                }
            });
            mCommonTagList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    mFilterTags.add((String) mVarMan.get(AppConfig.CONFIG_KEY_COMMON_TAGS_LIST,ArrayList.class).get(position));
                    mSelectedListAdapter.notifyDataSetChanged();
                }
            });
            mTagSetList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {mFilterTags.clear();
                    mFilterTypeSpinner.setSelection(mTagSets.get(position).get(0).getAsInteger(SqlUtil.COL_FILTER_CONDITION));
                    mDateModelTypeSpinner.setSelection(mTagSets.get(position).get(0).getAsInteger(SqlUtil.COL_DATE_MODEL));
                    for(int i=0;i<mTagSets.get(position).size();i++)
                        mFilterTags.add(mTagSets.get(position).get(i).getAsString(SqlUtil.COL_CONTENT));
                    mSelectedListAdapter.notifyDataSetChanged();
                    mDialog.getButton(DialogInterface.BUTTON_POSITIVE).performClick();
                }
            });
        }
        private void initDialog()
        {
            AlertDialog.Builder builder=new AlertDialog.Builder(mActivity);
            mDialog=builder.setTitle("添加标签")
                    .setView(mCustomDialogView)
                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if(mAdderviewMode>=0)
                            {
                                mAdderviewMode=-1;
                                mContentInput.setText("");
                            }
                        }
                    })
                    .setPositiveButton("确认",null)
                    .create();
            final View.OnClickListener okListener=new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(!checkDateModelAndDaysInput())
                    {
                        Toast.makeText(mActivity,"你所选的日期类型需要输入天数或者时间范围不正确",Toast.LENGTH_LONG).show();
                        return;
                    }else{
                        mDialog.hide();
                        mVarMan.set(AppConfig.CONFIG_KEY_DATE_MODEL_INDEX,mDateModelTypeSpinner.getSelectedItemPosition());
                        mVarMan.set(AppConfig.CONFIG_KEY_FILTER_CONDITION, mFilterTypeSpinner.getSelectedItemPosition());
                        mVarMan.set(AppConfig.CONFIG_KEY_SHOWN_DAYS,Integer.valueOf(mDaysEditText.getText().toString()));
                        mVarMan.set(AppConfig.CONFIG_KEY_START_CALENDAER,mStartDate.getTime());
                        mVarMan.set(AppConfig.CONFIG_KEY_END_CALENDAER,mEndDate.getTime());
                        updateViews();
                    }
                }
            };
            mDialog.setOnShowListener(new DialogInterface.OnShowListener() {
                @Override
                public void onShow(DialogInterface dialog) {
                    mFilterTypeSpinner.setSelection(mVarMan.get(AppConfig.CONFIG_KEY_CONDITION_TYPE_INDEX,0));
                    mDateModelTypeSpinner.setSelection(mVarMan.get(AppConfig.CONFIG_KEY_DATE_MODEL_INDEX,0));
                    mDialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener(okListener);
                }
            });
        }
        public boolean checkDateModelAndDaysInput()
        {
            int model=mDateModelTypeSpinner.getSelectedItemPosition();
            if(model== SqlUtil.DATE_MODEL_JUST_DAYS_TO_TODAY||model== SqlUtil.DATE_MODEL_JUST_DAYS_FROM_START)
            {
                if(mDaysEditText.getText().length()==0)
                    return false;
            }else if(model== SqlUtil.DATE_MODEL_FROM_START_TO_END){
                if(mStartDate.getTime().compareTo(mEndDate.getTime())>=0)
                    return false;
            }
            return true;
        }
        private void doCorrections()
        {
            //==retrive saved variables
            mContentInput.setText(mVarMan.get(AppConfig.CONFIG_KEY_SAVED_FILTER_DIALOG_TEXT,""));
            mTagFilterDialogScroller.setScrollY( mVarMan.get(AppConfig.CONFIG_KEY_SAVED_FILTER_DIALOG_SCROLL_Y,0));
            ViewUtil.setShown(mTagSetList,mVarMan.get(AppConfig.CONFIG_KEY_SAVED_FILTER_DIALOG_SAVED_SHOWNSTATE,false));
            ViewUtil.setShown(mCommonTagList,mVarMan.get(AppConfig.CONFIG_KEY_SAVED_FILTER_DIALOG_SAVED_COMMON_SHOWNSTATE,true));
            mCommonTitle.setTitleSelection(ViewUtil.isShown(mCommonTagList)?1:0);
            mDaysEditText.setText(""+mVarMan.get(AppConfig.CONFIG_KEY_SHOWN_DAYS,7));
            mStartDate.setTime(mVarMan.getByGetter(AppConfig.CONFIG_KEY_START_CALENDAER, ValueGetter.ValueGetterOfCalendarNow.getIntance()));
            mEndDate.setTime(mVarMan.getByGetter(AppConfig.CONFIG_KEY_END_CALENDAER,ValueGetter.ValueGetterOfCalendarNow.getIntance()));

        }

        public void onDestroy()
        {
            mDialog.dismiss();
            if(mAllTagListCursor!=null)
                mAllTagListCursor.close();
            mVarMan.set(AppConfig.CONFIG_KEY_SAVED_FILTER_DIALOG_SCROLL_Y,mTagFilterDialogScroller.getScrollY());
            mVarMan.set(AppConfig.CONFIG_KEY_SAVED_FILTER_DIALOG_TEXT, mContentInput.getText().toString());
            mVarMan.set(AppConfig.CONFIG_KEY_SAVED_FILTER_DIALOG_SAVED_SHOWNSTATE,ViewUtil.isShown(mTagSetList));
            mVarMan.set(AppConfig.CONFIG_KEY_SAVED_FILTER_DIALOG_SAVED_COMMON_SHOWNSTATE,ViewUtil.isShown(mCommonTagList));
        }
        public void show()
        {
            mDialog.show();
        }


    }
    public TagFilterDialogWrapper getTagFilterDialogWrapper()
    {
        if(mDialogWrapper==null)
        {
            mDialogWrapper=new TagFilterDialogWrapper();
        }
        return mDialogWrapper;
    }


    public void onClickSearchButton(View v)
    {
        getTagFilterDialogWrapper().show();
    }






    @Override
    public SQLiteDatabase getContextDB() {
        return mDb;
    }

    /**
     *   tables that used to show(model)
     *   tables that used to control(controller)
     *
     *   show all tables based on their
     */
    public class ShowListAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return mListDataPositionMap.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            //convertView will be directly abandoned,no reuse.
                Object[] pos=mListDataPositionMap.get(position);
                int i= (int) pos[0],j= (int) pos[1];
                Calendar date= (Calendar) mListData.get(i)[0];
                ArrayList<Object[]> data= (ArrayList<Object[]>) mListData.get(i)[1];
                if(j==-1)//date view
                {
                    convertView=getLayoutInflater().inflate(DEFAULT_DATE_LAYOUT,parent,false);
                    TextView dateTextView= (TextView)convertView.findViewById(R.id.dateTextView);
                    String dateText=StringFormatters.formatDateWithCalendar(date);
                    int weekDay=date.get(Calendar.DAY_OF_WEEK);
                    dateText+="  "+getResources().getStringArray(R.array.dayOfWeek)[weekDay];
                    if(date.get(Calendar.DAY_OF_WEEK)==Calendar.MONDAY)
                    {
                        dateText+="  第"+
                                date.get(Calendar.DAY_OF_YEAR)+"天  第"+date.get(Calendar.WEEK_OF_YEAR)+"周"
                        ;
                    }
                    /**
                     *  @see MayConsumeTimePleaseUtilize
                     *   because everytime it will generate the today.
                     */
                    if(CalendarUtil.isDateToDay(date))
                    {
                        dateText+="(今天)";
                    }
                    dateTextView.setText(dateText);
                }else{
                    String table= (String) data.get(j)[0];
                    ContentValues value= (ContentValues) data.get(j)[1];

                    if(table==SqliteHelper.TABLE_TRAVEL_NOTE)
                    {
                        convertView=getLayoutInflater().inflate(DEFAULT_TRAVEL_NOTE_LAYOUT,parent,false);
                        TextView srcTextView= (TextView) convertView.findViewById(R.id.srcTextView);
                        TextView dstTextView= (TextView) convertView.findViewById(R.id.dstTextView);
                        TextView startTimeTextView= (TextView) convertView.findViewById(R.id.startTimeTextView);
                        srcTextView.setText(value.getAsString(SqlUtil.COL_SRC));
                        dstTextView.setText(value.getAsString(SqlUtil.COL_DST));
                        startTimeTextView.setText(StringFormatters.formatTimeWithCalendar(CalendarUtil.timeInMillisToCalendar((long)(double)value.getAsDouble(SqlUtil.COL_START_TIME))));
                    }else if(table==SqliteHelper.TABLE_PLAN){
                        convertView = getLayoutInflater().inflate(DEFAULT_PLAN_LAYOUT,parent,false);//seelect something from issue
                        TextView nameView= (TextView) convertView.findViewById(R.id.nameTextView);
                        TextView timeView= (TextView) convertView.findViewById(R.id.timeTextView);
                        TextView placeView= (TextView) convertView.findViewById(R.id.placeTextView);
                        nameView.setText(value.getAsString(SqlUtil.COL_NAME));
                        timeView.setText(StringFormatters.formatTimeWithTimeDouble(value.getAsDouble(SqlUtil.COL_END_TIME)));
                        placeView.setText(value.getAsString(SqlUtil.COL_PLACE));

                        final ListView planIssueList= (ListView) convertView.findViewById(R.id.issueList);
                        View planIndicator=convertView.findViewById(R.id.statusIndicator);
                        setIndicater(planIndicator,value.getAsString(SqlUtil.COL_STATUS),getResources());
                        Cursor cursor=mDbHelper.getDataWithTableNameAndId(SqliteHelper.TABLE_ISSUE,SqliteHelper.TABLE_PLAN,value.getAsInteger(SqlUtil.COL_ID));

                        planIssueList.getLayoutParams().height = getResources().getDimensionPixelSize(R.dimen.issue_size)*cursor.getCount();
                        planIssueList.requestLayout();
//                        Util.logi("cursor count="+cursor.getCount());
                        CursorAdapter planIssueListAdapter=new CursorAdapter(
                                ViewAsListActivity.this,
                                cursor,
                                CursorAdapter.FLAG_AUTO_REQUERY
                                ) {
                            @Override
                            public View newView(Context context, Cursor cursor, ViewGroup parent) {
//                                Util.logi("getDecroView, cursor count,pos="+cursor.getCount()+","+cursor.getPosition());
                                View rootView=getLayoutInflater().inflate(R.layout.show_list_plan_issue, parent,false);
                                return rootView;
                            }

                            @Override
                            public void bindView(View view, Context context, Cursor cursor) {
                                TextView content= (TextView) view.findViewById(R.id.content);
                                View indicator=view.findViewById(R.id.issueIndicator);
                                content.setText(cursor.getString(cursor.getColumnIndex(SqlUtil.COL_CONTENT)));
                                String status=cursor.getString(cursor.getColumnIndex(SqlUtil.COL_STATUS));
                                setIndicater(indicator,status,getResources());
                            }
                        };
                        planIssueList.setAdapter(planIssueListAdapter);

                    }else if(table==SqliteHelper.TABLE_GENERAL_RECORDS){
                        //we still need to getByGetter tag of those things.
                        String mainTag=value.getAsString(SqlUtil.COL_MAIN_TAG);
                        if(SqlUtil.TAG_PROBLEM.equals(mainTag))
                        {
                            convertView = getLayoutInflater().inflate(R.layout.show_list_problem,parent,false);
                            TextView briefTextView= (TextView) convertView.findViewById(R.id.briefTextView);
                            TextView detailTextView= (TextView) convertView.findViewById(R.id.detailTextView);

                            briefTextView.setText(value.getAsString(SqlUtil.COL_BRIEF));
                            detailTextView.setText(value.getAsString(SqlUtil.COL_DETAIL));
                        }else if(SqlUtil.TAG_DIARY.equals(mainTag)){
                            convertView=getLayoutInflater().inflate(R.layout.show_list_diary,parent,false);
                            TextView briefTextView= (TextView) convertView.findViewById(R.id.briefTextView);
                            TextView detailTextView= (TextView) convertView.findViewById(R.id.detailTextView);
                            TextView weatherTextView= (TextView) convertView.findViewById(R.id.weatherTextView);
                            TextView placeTextView= (TextView) convertView.findViewById(R.id.placeTextView);

                            briefTextView.setText(value.getAsString(SqlUtil.COL_BRIEF));
                            detailTextView.setText(value.getAsString(SqlUtil.COL_DETAIL));
                            weatherTextView.setText(value.getAsString(SqlUtil.COL_WEATHER));
                            placeTextView.setText(value.getAsString(SqlUtil.COL_PLACE));
                        }else if(SqlUtil.TAG_SAY_SOMETHING.equals(mainTag)){
                            convertView=getLayoutInflater().inflate(R.layout.show_list_say_something,parent,false);
                            TextView detailTextView= (TextView) convertView.findViewById(R.id.detailTextView);
                            detailTextView.setText(value.getAsString(SqlUtil.COL_DETAIL));
                        }else if(SqlUtil.TAG_BLOG.equals(mainTag)){
                            convertView=getLayoutInflater().inflate(R.layout.show_list_blog,parent,false);
                            TextView briefTextView= (TextView) convertView.findViewById(R.id.briefTextView);
                            TextView detailTextView= (TextView) convertView.findViewById(R.id.detailTextView);


                            briefTextView.setText(value.getAsString(SqlUtil.COL_BRIEF));
                            detailTextView.setText(value.getAsString(SqlUtil.COL_DETAIL));
                        }

                    }else{
                        Util.logi("not implemented for table:"+table);
                    }
                    if(mArgFunction==FUNCTION_SELECT) {
                        Long id = value.getAsLong(SqlUtil.COL_ID);
                        if (id == null) {
                            Integer iid = value.getAsInteger(SqlUtil.COL_ID);
                            if (iid != null)
                                id = (long) iid;
                        }
                        if (id != null) {
                            int index=findIndexOfSelected(table,id);
                            if(index==-1)
                                setSelected(convertView,false);
                            else
                                setSelected(convertView,true);
                        }
                    }
                }
            return convertView;
        }
    }

    /**
     * query condition model:
     *      date model:
     *              start date
     *                  null -- no limit
     *                  one particular day
     *                  always this day
     *              end date
     *                  null -- no limit
     *                  one particular day
     *       filter model:
     *              any of them
     *              all of them
     *              at least one of them(how many)
     *
     *         given parameters:
     *            (dateModel,start,end,filter)
     *            if(dateModel==ALL_DATE)
     *            else if(dateModel==JUST_TODAY)
     *            else if(dateModel==TO_END)
     *            else if(dateModel==FROM_START)
     *            else if(dateModel==JUST_ONE_DAY)
     *
     *
     */
    public class DatabaseOperationHelper{


        /**
         *
         * @param calStartTime
         * @param days
         * @param tables
         * @return [<table,value>]
         */
        @MayConsumeTimePleaseUtilize("Database operation")
        public ArrayList<Object[]> getAllDataFromDatabase(Calendar calStartTime,int days,String[] tables)//String,ContentValues
        {
            return getAllDataFromDatabaseWithTags(calStartTime,days,tables,(String[])null, SqlUtil.FILTER_ANY);
        }
        @Deprecated
        public ArrayList<Object[]> getAllDataFromDatabaseWithTags(Calendar calStartTime,int days,String[] tables,ArrayList<String> tags,int filterCondition)//String,ContentValues
        {
            String[] tagsList=new String[tags==null?0:tags.size()];
            for(int i=0;i<tagsList.length;i++)
                tagsList[i]=tags.get(i);
            return getAllDataFromDatabaseWithTags(calStartTime,days,tables,tagsList, filterCondition);
        }
        @Deprecated
        public ArrayList<Object[]> getAllDataFromDatabaseWithTags(Calendar calStartTime, int days, String[] tables, String[] tags, int filterCondition)
        {
            Calendar dayZero=new GregorianCalendar(calStartTime.get(Calendar.YEAR),calStartTime.get(Calendar.MONTH),calStartTime.get(Calendar.DAY_OF_MONTH),0,0,0);
            long startTime = dayZero.getTimeInMillis();
            long endTime=startTime + days* CalendarUtil.ONE_DAY;

            ArrayList<Object[]> res=new ArrayList<>();

            int tagSize=tags==null?0:tags.length;
            String sqlRaw=makeRawQueryStringWithCreatedTimesTagsFormatter(tagSize,filterCondition);
            String[] sqlRawArgs=null;

            sqlRawArgs=new String[2+tagSize];
            sqlRawArgs[0]=String.valueOf(startTime);
            sqlRawArgs[1]=String.valueOf(endTime);
            for(int i=0;i<tagSize;i++)
            {
                sqlRawArgs[2+i]=tags[i];
            }

//            Util.logi("args=\n"+Arrays.deepToString(sqlRawArgs));

            for(String table:tables)
            {
                final String sql = String.format(sqlRaw, table, table,table);
                Cursor c=mDb.rawQuery(sql,sqlRawArgs);
                Util.logi("SQL="+sql);
                Util.logi("getByGetter all,cursor count="+c.getCount());
                ContentValues[] contentValues = SqlUtil.cursorToContentValues(c);
                for(ContentValues value:contentValues)
                {
                    res.add(new Object[]{table,value});
                }
            }
            return res;

        }

        @CoreMethodOfThisClass
        public ArrayList<Object[]> getAllDataFromDatabase(String[] tables,int dateModel,Calendar start,Calendar end,int days,String[] tags,int filerCondition)
        {
            Object[] sql1=makeRawDateQueryString(dateModel,start,end,days);
            String tagSql=makeRawTagsQueryString(tags,filerCondition);
            if(tagSql.length()!=0) {
                if (dateModel == SqlUtil.DATE_MODEL_ALL_DATE)
                    sql1[0] += " Where ";
                else
                    sql1[0] += " And ";
                sql1[0]+=tagSql;
            }
            String[] args= (String[]) sql1[1];
            if(args==null)
                sql1[1]=tags;
            else if(tags!=null && tags.length>0)
            {
                String[] newArgs=new String[args.length+tags.length];

                for(int i=0;i<args.length;i++)
                    newArgs[i]=args[i];
                for(int i=0;i<tags.length;i++)
                    newArgs[args.length+i]=tags[i];
                sql1[1]=newArgs;
            }
            ArrayList<Object[]> res=new ArrayList<>();

            for(String table:tables)
            {
                final String sql = String.format((String)sql1[0], table, table,table);
                Util.logi("SQL="+sql);
                Cursor c=mDb.rawQuery(sql, (String[]) sql1[1]);
                Util.logi("getByGetter all,cursor count="+c.getCount());
                ContentValues[] contentValues = SqlUtil.cursorToContentValues(c);
                for(ContentValues value:contentValues)
                {
                    res.add(new Object[]{table,value});
                }
            }
            return res;
        }


        /**
         *
         * @param dateModel
         * @param start
         * @param end
         * @param days
         * @return sql formatter& sql args, sql formatter needs 1 table argument
         */
        @HelperMethods
        public Object[] makeRawDateQueryString(int dateModel,Calendar start,Calendar end,int days)
        {
            String sqlRaw=null;
            String[] sqlRawArgs=null;
            Util.logi("model="+dateModel);
            if(dateModel== SqlUtil.DATE_MODEL_ALL_DATE)
            {
                sqlRaw="Select * From %s";
            }else if (dateModel == SqlUtil.DATE_MODEL_JUST_DAYS_TO_TODAY || dateModel == SqlUtil.DATE_MODEL_JUST_DAYS_FROM_START || dateModel== SqlUtil.DATE_MODEL_FROM_START_TO_END) {
                if (days <= 0)
                    throw new UnsupportedOperationException("days cannot be less than 1.");
                if (dateModel == SqlUtil.DATE_MODEL_JUST_DAYS_TO_TODAY) {
                    end = CalendarUtil.getNextDayBegin();
                    start = CalendarUtil.cloneCalendar(end);
                    start.add(Calendar.DAY_OF_MONTH, -days);
                }else if(dateModel== SqlUtil.DATE_MODEL_JUST_DAYS_FROM_START){
                    end=CalendarUtil.cloneCalendar(start);
                    CalendarUtil.timeToDayBegin(end).add(Calendar.DAY_OF_MONTH,days);
                }
                sqlRaw="Select * From %s Where "+SqlUtil.COL_CREATED_TIME+">=? And "+SqlUtil.COL_CREATED_TIME+"< ? ";
                sqlRawArgs=new String[]{""+start.getTimeInMillis(),""+end.getTimeInMillis()};
            }else if(dateModel== SqlUtil.DATE_MODEL_FROM_START){
                sqlRaw="Select * From %s Where "+SqlUtil.COL_CREATED_TIME+">=? ";
                sqlRawArgs=new String[]{""+start.getTimeInMillis()};
            }else if(dateModel== SqlUtil.DATE_MODEL_TO_END) {
                sqlRaw="Select * From %s Where "+SqlUtil.COL_CREATED_TIME+"<? ";
                sqlRawArgs=new String[]{""+end.getTimeInMillis()};
            }else{
                throw new UnsupportedOperationException("Model:"+dateModel+" is not supported");
            }
            return new Object[]{sqlRaw,sqlRawArgs};
        }

        @HelperMethods
        public String makeRawQueryStringWithCreatedTimesTagsFormatter(int tagSize,int filterCondition)
        {
            String formatter="Select * From %%s Where %s >= ? And %s < ?";
            String[] conditions=new String[]{
                    "0 <",""+tagSize+" =","0 ="
            };
            if(tagSize>0) {
                formatter += " And %s " +
                        "(Select Count(*) From %s Where %s.%s= \"%%s\" And %s.%s= %%s.%s And %s.%s In (%s))";
                return String.format(formatter,SqlUtil.COL_CREATED_TIME,SqlUtil.COL_CREATED_TIME,
                        conditions[filterCondition],
                        SqliteHelper.TABLE_TAG,
                        SqliteHelper.TABLE_TAG,SqlUtil.COL_TABLE_NAME,
                        SqliteHelper.TABLE_TAG,SqlUtil.COL_INNER_ID,
                        SqlUtil.COL_ID,
                        SqliteHelper.TABLE_TAG,SqlUtil.COL_CONTENT,
                        Util.joinRepeat(",","?",tagSize)
                );
            }else{
                return String.format(formatter,SqlUtil.COL_CREATED_TIME,SqlUtil.COL_CREATED_TIME);
            }
        }


        /**
         *
         * @param tags
         * @param filterCondition
         * @return  a formatter needs 2 table arguments
         */
        @HelperMethods
        public String makeRawTagsQueryString(String[] tags,int filterCondition)
        {
            if(tags==null||tags.length==0)
                return "";
            int tagSize=tags.length;
            String[] conditions=new String[]{
                    "0 <",""+tagSize+" =","0 ="
            };
            String formatter = "%s " +
                    "(Select Count(*) From %s Where %s.%s= \"%%s\" And %s.%s= %%s.%s And %s.%s In (%s))";
            return String.format(formatter,
                    conditions[filterCondition],
                    SqliteHelper.TABLE_TAG,
                    SqliteHelper.TABLE_TAG,SqlUtil.COL_TABLE_NAME,
                    SqliteHelper.TABLE_TAG,SqlUtil.COL_INNER_ID,
                    SqlUtil.COL_ID,
                    SqliteHelper.TABLE_TAG,SqlUtil.COL_CONTENT,
                    Util.joinRepeat(",","?",tagSize)
            );

        }

        @MayConsumeTimePleaseUtilize("Data structure reforming")
        public ArrayList<Object[]> sortAndGroupAllData(ArrayList<Object[]> data)//Calendar,[Table,ContentValues]
        {
            if(data==null||data.size()==0)return new ArrayList<>();
            //sorted
            Collections.sort(data, new Comparator<Object[]>() {
                @Override
                public int compare(Object[] o1, Object[] o2) {
                    ContentValues v1=(ContentValues)o1[1];
                    ContentValues v2=(ContentValues)o2[1];
                    long t1=SqliteHelper.getGeneralRecordShownTime((String)o1[0],v1);
                    long t2=SqliteHelper.getGeneralRecordShownTime((String)o2[0],v2);
                    return (int) (t1-t2);
                }
            });

            //
            Calendar startCalendar= CalendarUtil.timeInMillisToCalendar(
                    SqliteHelper.getGeneralRecordShownTime((String)data.get(0)[0],(ContentValues)data.get(0)[1]));
            CalendarUtil.timeToDayBegin(startCalendar);

            ArrayList<Object[]> res=new ArrayList<>();
            double startTime=startCalendar.getTimeInMillis();//keep moving

            int i=0;
            res.add(new Object[]{
                    CalendarUtil.cloneCalendar(startCalendar),new ArrayList<>()
            });
            for(Object[] objects:data)
            {
                ContentValues thisValue= (ContentValues) objects[1];
                double thisCreatedTime=SqliteHelper.getGeneralRecordShownTime((String)data.get(0)[0],thisValue);
                if(thisCreatedTime - startTime >= CalendarUtil.ONE_DAY)
                {
                    do {
                        startTime += CalendarUtil.ONE_DAY;
                        startCalendar.add(Calendar.DAY_OF_MONTH, 1);
                    }while (thisCreatedTime - startTime >= CalendarUtil.ONE_DAY);
                    i++;
                    res.add(new Object[]{
                            CalendarUtil.cloneCalendar(startCalendar),new ArrayList<>()
                    });
                }
                ((ArrayList<Object[]>)res.get(i)[1]).add(objects);
            }
            return res;
        }
        public Cursor getDataWithTableNameAndId(String table,String externTable,int externId)
        {
            return mDb.query(table,null,SqlUtil.COL_TABLE_NAME+"=? and "+SqlUtil.COL_INNER_ID+"=?",new String[]{externTable,String.valueOf(externId)},null,null,null);
        }

    }
}
