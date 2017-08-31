package fulton.shaw.android.x.activities;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.database.Cursor;
import android.os.SystemClock;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import fulton.shaw.android.x.R;
import fulton.shaw.android.x.async_thread.AcitivityAsyncThreadManager;
import fulton.shaw.android.x.broadcast.StartAlarmServiceBroadcast;
import fulton.shaw.android.x.fragments.filter_conditions.FilterConditionBase;
import fulton.shaw.android.x.fragments.filter_conditions.FilterConditionByContent;
import fulton.shaw.android.x.fragments.filter_conditions.FilterConditionByShownDate;
import fulton.shaw.android.x.fragments.filter_conditions.FilterConditionByTag;
import fulton.shaw.android.x.models.SqliteTableModelGetter;
import fulton.shaw.android.x.public_interfaces.SqliteHelper;
import fulton.shaw.android.x.services.TestStopPendingAlarmService;
import fulton.shaw.android.x.viewgetter.database_auto.AdapterViewAutoCompletor;
import fulton.shaw.android.x.viewgetter.database_auto.ShareableSingleViewAutoCollector;
import fulton.shaw.android.x.viewgetter.database_auto.SingleViewAutoCompletor;
import fulton.shaw.android.x.viewgetter.database_auto.ViewGenerator;
import fulton.shaw.android.x.viewgetter.database_auto.ViewGetter;
import fulton.util.android.interfaces.multiview_checkers.Checker;
import fulton.util.android.interfaces.standard_functions.ConditionHandler;
import fulton.util.android.interfaces.standard_functions.FunctionOfAddingItem;
import fulton.util.android.interfaces.tuples.Pair;
import fulton.util.android.interfaces.ViewInfo;
import fulton.shaw.android.x.viewgetter.database_auto.checkers.CheckerGetPositiveInteger;
import fulton.shaw.android.x.viewgetter.transferview.FixedSwitchablePairedViews;
import fulton.shaw.android.x.viewgetter.transferview.ShareablePairedViewsValueTransfer;
import fulton.util.android.interfaces.tuples.Quad;
import fulton.util.android.interfaces.tuples.Single;
import fulton.util.android.interfaces.tuples.Triple;
import fulton.util.android.interfaces.typetransfers.TransferCalendarToSqliteTime;
import fulton.util.android.interfaces.typetransfers.TransferStringToPositiveInteger;
import fulton.util.android.interfaces.typetransfers.ValueTypeTransfer;
import fulton.shaw.android.x.views.TimePickerDialogWithTextView;
import fulton.shaw.android.x.views.viewhelpers.AdapterViewWrapper;
import fulton.shaw.android.x.views.viewhelpers.DatabaseAdapterView;
import fulton.shaw.android.x.views.viewhelpers.functions.FunctionCommitAdderViewToDatabase;
import fulton.shaw.android.x.views.viewhelpers.functions.FunctionDeleteItemFunction;
import fulton.shaw.android.x.views.viewhelpers.functions.FunctionHideAdderView;
import fulton.shaw.android.x.views.viewhelpers.functions.FunctionShowAdderViewForAdd;
import fulton.shaw.android.x.views.viewhelpers.functions.FunctionShowAdderViewForUpdate;
import fulton.util.android.interfaces.valueproviders.ValueProviderOfCachedCursor;
import fulton.util.android.interfaces.valueproviders.ValueProvider;
import fulton.util.android.interfaces.valueproviders.ValueProviderOfLinearStructure;
import fulton.util.android.utils.CalendarUtil;
import fulton.util.android.utils.SqlUtil;
import fulton.util.android.utils.Util;

public class TestActivity extends AppCompatActivity {
    public Object mShareVariable;
    public SqliteHelper mSqliteHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_transfer_value);

        mSqliteHelper=new SqliteHelper(this);

        doTest();
    }

    public void doTest()
    {
        testSearchContent();
    }
    public void testSearchContent()
    {
        FilterConditionByContent filter=new FilterConditionByContent(FilterConditionByContent.SEARCH_SEPERATE,
                new String[]{"detail","brief"},
                new String[]{"ar%kl","我%来了"}
                );
        filter.updateCachedResult();
        Util.logi(filter.getSqlConditionString());
        Util.logi(filter.getSqlConditionArgs());
    }
    public void testStandardFunction()
    {
        ConditionHandler<FunctionOfAddingItem>[] handlers=new ConditionHandler[4];
        int i=0;
        for(final String s:new String[]{"external error","internal error","succeed","failed"})
        {
            handlers[i++]=new ConditionHandler<FunctionOfAddingItem>() {
                @Override
                public void handle() {
                    Util.logi("condition:"+s);
                }
            };
        }
        mSqliteHelper.dropTable("t1");
        mSqliteHelper.execSQL("Create table t1(id long,reason varchar(100))");
        String[] keys=new String[]{"id","reason"};
        Object[] values=new Object[]{1L,"what"};
        ValueProvider<String,String[]> myProvider=new ValueProviderOfLinearStructure<>(values,keys,null);
        FunctionOfAddingItem addingItem=new FunctionOfAddingItem(mSqliteHelper,handlers[0],handlers[1],handlers[2],handlers[3]);
        addingItem.setInput(new Quad<Single<String>, ValueProvider, Checker[], Triple<String, String, ValueTypeTransfer>[]>(
            new Single<String>("t1"),myProvider,null,new Triple[]{ new Triple("id","id",null),new Triple("reason","reason",null)}
        ));
        addingItem.apply();
        Util.logi("id="+addingItem.getOutput());
        mSqliteHelper.dropTable("t1");
    }
    public void testCacheCursor()
    {
        ValueProviderOfCachedCursor provider=new ValueProviderOfCachedCursor(SqliteHelper.TABLE_GENERAL_RECORDS, new String[]{
                SqlUtil.COL_DETAIL,
                SqlUtil.COL_MAIN_TAG,
                SqlUtil.COL_CREATED_TIME
        });
        provider.putValue(SqlUtil.COL_MAIN_TAG,SqlUtil.TAG_SAY_SOMETHING);
        provider.putValue(SqlUtil.COL_DETAIL,"Hello");
        provider.commit(mSqliteHelper);
        Util.logi(provider);
    }
    //==test refactor
    //===
    public void testConditionSearch()
    {
        FilterConditionByTag conditionByTag=new FilterConditionByTag(
                SqlUtil.FILTER_ANY,SqliteHelper.TABLE_GENERAL_RECORDS,new String[]{"DONE","DOING"});
        conditionByTag.updateCachedResult();
        Util.logi(conditionByTag.getSqlConditionString());
        Util.logi(conditionByTag.getSqlConditionArgs());

        FilterConditionByShownDate conditionByShownDate=new FilterConditionByShownDate("groupDate",
                SqlUtil.DATE_MODEL_TO_END,null,CalendarUtil.getNextDayBegin(),-1
                );
        conditionByShownDate.updateCachedResult();
        Util.logi(conditionByShownDate.getSqlConditionString());
        Util.logi(conditionByShownDate.getSqlConditionArgs());


        ArrayList<FilterConditionBase> conditions=new ArrayList<>();
        conditions.add(conditionByTag);
        conditions.add(conditionByShownDate);
        Pair<String,String[]> x=FilterConditionBase.constructSql(SqliteHelper.TABLE_GENERAL_RECORDS,null,
                new String[]{
                        SqlUtil.COL_ID,
                        "ifnull("+SqlUtil.COL_SHOWN_DATE+",date("+SqlUtil.COL_CREATED_TIME+")) as groupDate",
                        SqlUtil.COL_CREATED_TIME,
                        SqlUtil.COL_BRIEF,
                        SqlUtil.COL_DETAIL,
                },
                null, conditions);
        Util.logi(x.first);
        Util.logi(x.second);

//        mSqliteHelper.execSQL("insertBasedOnKeys into "+SqliteHelper.TABLE_TAG+"("+SqlUtil.COL_TABLE_NAME+","+
//            SqlUtil.COL_INNER_ID+","+SqlUtil.COL_CONTENT+") Select \""+SqliteHelper.TABLE_GENERAL_RECORDS+"\","+
//                SqliteHelper.TABLE_GENERAL_RECORDS+"."+SqlUtil.COL_ID+","+
//                "\"DONE\" From "+SqliteHelper.TABLE_GENERAL_RECORDS+" Limit 1"
//        );
//        Util.logi("changes:"+mSqliteHelper.getChanges());

        Util.logi(mSqliteHelper.rawQuery(x.first,x.second));
    }

    public void testRefactorTable()
    {
        Util.logi(mSqliteHelper.selectAll(SqliteHelper.TABLE_GENERAL_RECORDS));
//        ActivityUtil.abortApp();
        //create time:double --> datetime current timestamp


//        mSqliteHelper.dropTable("testGR");
//        mSqliteHelper.copyTableWithCreation(SqliteHelper.TABLE_GENERAL_RECORDS,"testGR",
//                new String[]{
//                        SqlUtil.COL_ID,
//                        "datetime("+SqlUtil.COL_CREATED_TIME+"/1000,'unixepoch','localtime')",
//                        SqlUtil.COL_WEATHER,
//                        SqlUtil.COL_PLACE,
//                        SqlUtil.COL_CAUSING,
//                        SqlUtil.COL_TARGET,
//                        SqlUtil.COL_BRIEF,
//                        SqlUtil.COL_MAIN_TAG,
//                        SqlUtil.COL_DETAIL,
//                        SqlUtil.COL_SHOWN_DATE,
//                        SqlUtil.COL_SHOWN_TYPE,
//                },
//                null
//                );
//        final  Cursor cursor1=mSqliteHelper.selectAll(SqliteHelper.TABLE_GENERAL_RECORDS);
//        final Cursor cursor = mSqliteHelper.selectAll("testGR");
//        Util.logi("current time:"+System.currentTimeMillis());
//        Util.logi(mSqliteHelper.rawQuery("Select datetime(1503455655,'unixepoch','localtime')"));
//        Util.logi(cursor1);
//        Util.logi(cursor);
    }

    private int mA=-1;
    private int mB=-1;
    private int mC=-1;
    public void testAsyncManager()
    {
        AcitivityAsyncThreadManager manager=new AcitivityAsyncThreadManager(this);


        manager.startThread("set:mA=-2", new Runnable() {
            @Override
            public void run() {
                mA=-2;
                Util.logi("set:mA=-2 end");
            }
        });
        manager.asyncWaitThread("set:mA=-2", "set:mB=mA-1", new Runnable() {
            @Override
            public void run() {
                mB=mA-1;
                Util.logi("set:mB=mA-1 end");
            }
        });
        manager.asyncWaitThread("set:mB=mA-1", "set:mC=2*mB", new Runnable() {
            @Override
            public void run() {
                mC=2*mB;
                Util.logi("set:mC=2*mB end");
            }
        });
        Util.logi("hello");
        manager.asyncWaitThread(new String[]{
                "set:mA=-2", "set:mB=mA-1","set:mC=2*mB"
        }, "showAll", new Runnable() {
            @Override
            public void run() {
                Util.logi("mA,mB,mC:"+mA+","+mB+","+mC);
            }
        });
        //onDestroy
        //manager.waitOnConditions(
    }

    public void testDatabaseAdapterView()
    {
        final Cursor todayModel=mSqliteHelper.ensureDateRecordExists(CalendarUtil.getTodayBegin());
        todayModel.moveToFirst();
        //time record reason
        Cursor cursor=mSqliteHelper.rawQuery("Select * From "+SqliteHelper.TABLE_INCOME,null);
        Util.logi(cursor);
        ListView list=(ListView)findViewById(R.id.listview);
        AdapterViewAutoCompletor<ListView> completor=new AdapterViewAutoCompletor<>(this,cursor,
                SqliteHelper.TABLE_INCOME,list,
                ViewGenerator.ViewGeneratorByInflateRes(getLayoutInflater(),R.layout.income_item),
                SqliteTableModelGetter.get(SqliteHelper.TABLE_INCOME),
                ViewGetter.getViewGettersBaseOnKey(SqliteTableModelGetter.get(SqliteHelper.TABLE_INCOME),
                        SqlUtil.COL_EVENT_TIME,R.id.timeTextView,TextView.class,
                        SqlUtil.COL_RECORD,R.id.recordTextView,TextView.class,
                        SqlUtil.COL_REASON,R.id.reasonTextView,TextView.class
                        )
                );
        View adderView=findViewById(R.id.adderViewContainer);
        ViewGroup twoButtonsOnAdder= (ViewGroup) adderView.findViewById(R.id.twoButtons);
        DatabaseAdapterView<ListView> dList=new DatabaseAdapterView<>(this,mSqliteHelper,list,
                completor,//completor
                adderView,
                ViewGetter.getViewGettersBaseOnKey(
                        SqliteTableModelGetter.get(SqliteHelper.TABLE_INCOME),
                    SqlUtil.COL_EVENT_TIME,R.id.eventTimeTextView, TimePickerDialogWithTextView.class,
                    SqlUtil.COL_RECORD,R.id.recordEditText,EditText.class,
                    SqlUtil.COL_REASON,R.id.reasonEditText,EditText.class
                )
                );
        //must have some preset values
        dList.setAdderTransfer(SqlUtil.COL_EVENT_TIME,new TransferCalendarToSqliteTime());
        final Spinner typeSpinner= (Spinner) adderView.findViewById(R.id.incomeTypeSpinner);
        dList.setAdderPresetValuesBasedOnKey(SqlUtil.COL_INNER_ID,SqlUtil.getCursorId(todayModel),
                SqlUtil.COL_REASON,"一般支出",
                SqlUtil.COL_RECORD,0 //you must give me the database type.
                );
        dList.addGetCheckersBasedOnKey(
                SqlUtil.COL_RECORD,new CheckerGetPositiveInteger()
        );
        dList.setCheckGetFailedActionBaseOnKey(SqlUtil.COL_RECORD, ShareableSingleViewAutoCollector.ACTION_ABORT);

        dList.setAdderTransfer(SqlUtil.COL_RECORD, new TransferStringToPositiveInteger(){
            @Override
            public Integer transferPositive(String s) {
                if(typeSpinner.getSelectedItemPosition()==0)
                    return super.transferPositive(s)*-1;
                else
                    return super.transferPositive(s);
            }

            @Override
            public String transferNagetive(Integer integer) {
                typeSpinner.setSelection(integer>=0?1:0);
                return super.transferNagetive(integer);
            }
        });

        dList.setErrorHandler(new DatabaseAdapterView.OnErrorConditionHandler<ListView>() {
            @Override
            public void onCollectAdderViewValueFailed(DatabaseAdapterView<ListView> adpertView) {
                Toast.makeText(TestActivity.this,"请检查数据是否填写正确",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFillAdderViewValueFailed(DatabaseAdapterView<ListView> adpertView) {

            }

            @Override
            public void onCommitDatabaseFailed(DatabaseAdapterView<ListView> adapterView) {
                Toast.makeText(TestActivity.this,"数据库操作有错误,请重试",Toast.LENGTH_SHORT).show();
            }
        });

        dList.setClickFunction(AdapterViewWrapper.FUNCTION_POP_UP_CONTEXT_MENU);
        dList.addFunctions(
                new FunctionDeleteItemFunction<ListView>(),
                new FunctionShowAdderViewForUpdate<ListView>(),
                new FunctionShowAdderViewForAdd<ListView>(),
                new FunctionCommitAdderViewToDatabase<ListView>(null),
                new FunctionHideAdderView<ListView>()
        );
        dList.addContextMenu("删除",0);
        dList.addContextMenu("修改",1);
        dList.setButtonFunction(twoButtonsOnAdder.findViewById(R.id.positiveButton),3);
        dList.setButtonFunction(twoButtonsOnAdder.findViewById(R.id.nagetiveButton),4);
        dList.setButtonFunction(findViewById(R.id.addButton),2);

    }

    private class MyFunctionProvider extends AdapterViewWrapper.FunctionProvider{

        @Override
        public void applyFunction(int i, int position) {
            Util.logi("function id="+i,"position ="+position);
            if(i==0)
            {
                //delete
            }else if(i==1){
                //modify
            }else if(i==2){
                //add

            }
        }

        @Override
        public int getCount() {
            return 3;
        }
    }

    public void testAdapterViewWrapper()
    {
        View twoButtons=getLayoutInflater().inflate(R.layout.two_buttons,null,false);
        AdapterViewWrapper<ListView> wrapper=new AdapterViewWrapper<>(this, (ListView) findViewById(R.id.listview));
        wrapper.addContextMenu("Modify",1);
        wrapper.addContextMenu("Delete",0);
        wrapper.setFunctionProvider(new MyFunctionProvider());
        wrapper.setButtonGroupContainerView((ViewGroup) twoButtons);
        wrapper.addButton(twoButtons.findViewById(R.id.positiveButton),1);
        wrapper.addButton(twoButtons.findViewById(R.id.nagetiveButton),0);
        wrapper.setClickFunction(AdapterViewWrapper.FUNCTION_SHOW_BUTTON_GROUP);//tested

        ArrayAdapter<String> adapter=new ArrayAdapter<String>(this,R.layout.income_item,R.id.reasonTextView,new ArrayList<String>(){{
            add("Goode");
            add("Nicee");
            add("Neturale");
        }});
        wrapper.getDecroView().setAdapter(adapter);

    }
    public void testSwitchableViews()
    {
        final FixedSwitchablePairedViews<EditText,TextView,String> transfer=new FixedSwitchablePairedViews<>(
                (EditText)findViewById(R.id.fixedEditText),(TextView)findViewById(R.id.fixedTextView),String.class, ShareablePairedViewsValueTransfer.VIEW_1
        );
        View.OnClickListener listener=new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                transfer.switchMode();
            }
        };
        transfer.getView1().setOnClickListener(listener);
        transfer.getView2().setOnClickListener(listener);
    }

    public void testDateRecordOperations()
    {
        mSqliteHelper.recreate(SqliteHelper.TABLE_DATE_RECORD);
//        long id=mSqliteHelper.ensureDateRecordExists(java.util.Calendar.getInstance());
//        Util.logi("id="+id);
//        Util.logi("repeat id="+mSqliteHelper.ensureDateRecordExists(java.util.Calendar.getInstance()));
    }

    /**
     * suggest: using simple datetime format
     */
    public void testDbDate()
    {
        mSqliteHelper.execSQL("Drop Table If Exists a");
        mSqliteHelper.execSQL("Create table a(a1 date,a2 datetime,a3 time)");
//        mSqliteHelper.execSQL("insertBasedOnKeys into a(a1) values(date('now','localtime'))");
        mSqliteHelper.execSQL("insertBasedOnKeys into a values(date('now','localtime')," +
                                                        "datetime('now','localtime')," +
                                                        "time('now','localtime'))");
        Cursor cursor=mSqliteHelper.rawQuery("select a1,a2,a3 from a",null);
        cursor.moveToFirst();


        Util.logi("types:"+cursor.getType(0)+","+cursor.getType(1)+","+cursor.getType(2));//全是String
        cursor.moveToPosition(-1);
        Util.logi(SqlUtil.dumpCursorValue(cursor));
        cursor.moveToFirst();
        Util.logi("long values:"+cursor.getLong(0)+","+cursor.getLong(1)+","+cursor.getLong(2));//获得的是首个整数
        Util.logi("int values:"+cursor.getInt(0)+","+cursor.getInt(1)+","+cursor.getInt(2));//获得的是首个整数,2017
        Util.logi("double values:"+cursor.getDouble(0)+","+cursor.getDouble(1)+","+cursor.getDouble(2));//获得的是首个整数，然后转换成小数
//        java.util.Calendar calendar=CalendarUtil.getDateOfString(cursor.getString(0));
//        Util.logi(calendar);

        mSqliteHelper.execSQL("Drop Table If Exists a");

    }

    public void testDbDateAndJavaDate()
    {
        mSqliteHelper.execSQL("Drop Table If Exists a");
        mSqliteHelper.execSQL("Create table a(a1 date,a2 datetime,a3 time)");
        mSqliteHelper.execSQL("Insert into a(a1) values(date('now','localtime'))");
        Cursor cursor=mSqliteHelper.rawQuery("Select date('now','localtime')=date(?,'unixepoch','localtime')",new String[]{String.valueOf(System.currentTimeMillis()/1000)});
        Util.logi(SqlUtil.dumpCursorValue(cursor));

        mSqliteHelper.execSQL("Drop Table If Exists a");
    }
    public void testSingleViewFiller()
    {
        View view=getLayoutInflater().inflate(R.layout.test_auto_view_filler,null);

        Cursor modelCursor=mSqliteHelper.rawQuery("Select * From tableTag",null);
        SingleViewAutoCompletor filler=new SingleViewAutoCompletor(
                modelCursor,
                "", view,
                new ArrayList<ViewInfo>(){{
                    add(new ViewInfo(SqlUtil.COL_ID,Integer.class));
                    add(new ViewInfo(SqlUtil.COL_CONTENT,String.class));
                    add(new ViewInfo(SqlUtil.COL_TABLE_NAME,String.class));//must
                }},
                new ArrayList<ViewGetter>(){{
                  add(ViewGetter.IdViewGetter(R.id.idTextView, null));
                    add(ViewGetter.IdViewGetter(R.id.content, null));
                    add(ViewGetter.IdViewGetter(R.id.tableName, null));
                }}
                );
        filler.setTransfer(2, new ValueTypeTransfer<Integer,String>(){

            @Override
            public String transferPositive(Integer integer) {
                return integer==1?SqliteHelper.TABLE_GENERAL_RECORDS:"";
            }

            @Override
            public Integer transferNagetive(String s) {
                switch (s)
                {
                    case SqliteHelper.TABLE_GENERAL_RECORDS:
                        return 1;
                    default:
                        return 0;
                }
            }
        });
        filler.refresh();
        new AlertDialog.Builder(this).setView(view).show();
    }


    public void testTwoViewFillerTogether()
    {

        //the cursors you want.
    }
    /**
     * 令所有的闹钟都在同一个表中，可得到不同的_id,用这个_id作为AlarmManager的requestCode
     *
     *   所有的闹钟都只是send一个broadcast。设置结束时间的方法是，在指定的结束时间发送一个结束的指令。
     *
     */
    //cancellable.
    public void testAlarm()
    {
        AlarmManager alarmManager= (AlarmManager) getSystemService(ALARM_SERVICE);
        Intent intent=new Intent(this,SeePriceActivity.class);
        PendingIntent alarmIntent=PendingIntent.getActivity(this,0,intent,PendingIntent.FLAG_UPDATE_CURRENT);
        long cur=System.currentTimeMillis();
        alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime()+10,
                CalendarUtil.ONE_SECOND*10,
                alarmIntent
                );
        //every 10 sec
        //start a service to cancel it after 2 times
        Intent intent2=new Intent(TestActivity.this, TestStopPendingAlarmService.class);
        PendingIntent pendingIntent=PendingIntent.getService(TestActivity.this,1,intent2,PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,SystemClock.elapsedRealtime()+CalendarUtil.ONE_SECOND*60*2,pendingIntent);
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    Thread.sleep(CalendarUtil.ONE_SECOND*10*3);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//
//            }
//        }).start();
    }
    public void testStartServiceSendBroadCast()
    {
        Intent intent=new Intent(this, StartAlarmServiceBroadcast.class);
        sendBroadcast(intent);
    }

    public void testValueTypeTransfer()
    {

    }

    public void testAdapterFactory()
    {

    }

    public void onClickTestButton(View v)
    {
        //只需要指定模板参数，就可实现任意转换。
        //不使用接口可行吗？
//        TransferView<TextView,String,EditText>  transferView= (TransferView) mShareVariable;
//        transferView.switchMode();


    }
}
