package fulton.shaw.android.x.activities;

import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import fulton.shaw.android.x.R;
import fulton.shaw.android.x.adapters.SequenceViewFieldInputterAdapter;
import fulton.shaw.android.x.public_interfaces.ActivityUtil;
import fulton.shaw.android.x.public_interfaces.AppConfig;
import fulton.shaw.android.x.public_interfaces.SqliteHelper;
import fulton.shaw.android.x.sql.AdapterFieldsInfo;
import fulton.util.android.utils.Util;
import fulton.util.android.aware.ActivityHasPositiveNagetiveButtons;
import fulton.util.android.aware.ContextSQLiteAware;
import fulton.util.android.notations.BeCareful;
import fulton.util.android.notations.ChainMethods;
import fulton.util.android.notations.CoreMethodOfThisClass;
import fulton.util.android.notations.OverrideToCustomBehaviour;

/**
 *  process the open arguments: one for all
 *
 *
 *  condition:
 *          add for multi-table
 *          多表联合添加，
 *          显示部分
 *          有的具有多项内容，可以没有，也可以有。不定项内容其控件是可以增减的。增减控件是通过与控件同组addOne,deleteOne的两个可点击控件完成的。
 *
 *          多表的每一个控件，必须标明： 表名称，列名称。
 *
 *
 *          处理数据
 *          1.首先是收集所有的数据
 *          2.确定哪些数据添加到哪些表，添加完成之后剩余的数据进行修改。
 *          3.不同的表收集到不同的ContentValues(返回一个HashMap，以表名作为键，以多个ContentValues作为值)
 *          4.如果同一个表的ContentValues收集到重复的键，将已经存在的所有该表的ContentValues复制一份，然后覆盖新键。
 *
 *    activity打开参数：（假想）
 *       支持模式：  添加模式
 *                      使用默认值填充控件
 *                   更新模式（仅能对一个表的一行进行修改，因此需要指定表，id）
 *                      view模式同添加模式
 *                      使用给定值填充控件
 *                      positive 按钮名称改成 更新
 *                      流程：startActivity-->选择出一行cursor-->若无，则显示无数据
 *                                                                若多余一行，则显示数据过多，不能进行修改
 *                                                                正常-->将cursor转换成contentValues,同时以默认值填入控件中
 *
 *                      提交更新: 点击提交按钮-->构造条件是id=?-->update即可，若失败则显示失败信息，成功也显示信息-->返回。
 *                   查看模式
 *                      需要指定关联的表
 *                      取消positive按钮，nagetive按钮改成返回
 *       支持在这三种模式之间进行切换。
 */
public abstract class AddForDatabaseActivity extends AppCompatActivity implements ActivityHasPositiveNagetiveButtons,ContextSQLiteAware{

    public static final int REQUEST_CODE_ADD_ONCE=0;
    public static final int REQUEST_CODE_ADD_MANY=1;

    public static final String OPEN_ARG_REQUEST_METHOD="requestMethod";

    protected SQLiteDatabase mDb;
    protected AdapterView<SequenceViewFieldInputterAdapter> mAdapterView;
    protected SequenceViewFieldInputterAdapter mAdapter;
    protected int mRequestMethod;

    protected SharedPreferences mActivityPref;
    protected SharedPreferences.Editor mActivityPrefEditor;
    protected boolean mExitAfterAdd;
    protected boolean mViewDetailAfterExit;

    protected String mLastTable;
    protected int    mLastId;

    @Override
    @CoreMethodOfThisClass
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent openIntent=getIntent();
        if(openIntent!=null)
        {
            mRequestMethod=openIntent.getIntExtra(OPEN_ARG_REQUEST_METHOD,REQUEST_CODE_ADD_MANY);
        }else{
            mRequestMethod=REQUEST_CODE_ADD_MANY;
        }
        mActivityPref=getPreferences(MODE_PRIVATE);
        mActivityPrefEditor = mActivityPref.edit();
        mExitAfterAdd = mActivityPref.getBoolean(AppConfig.CONFIG_KEY_EXIT_AFTER_ADD,false);
        mViewDetailAfterExit=mActivityPref.getBoolean(AppConfig.CONFIG_KEY_VIEW_DETAIL_AFTER_EXIT,false);

        setContentView(getContentViewId());
        mDb = SqliteHelper.getDatabase(this);

        mAdapter=getDatabaseViewAdapter();
        if(mAdapter==null)
        {
            mAdapter= (SequenceViewFieldInputterAdapter) new SequenceViewFieldInputterAdapter(this).addAll(AdapterFieldsInfo.initFields(getDatabaseViewAdapterUnderlyingData()));
        }
        mAdapterView= (AdapterView<SequenceViewFieldInputterAdapter>) findViewById(getAdapterViewId());
        mAdapterView.setAdapter(mAdapter);

        doAdditionalSettingsAfterInit();
    }

    @OverrideToCustomBehaviour
    protected int getContentViewId()
    {
        return R.layout.activity_add_for_database;
    }

    @OverrideToCustomBehaviour
    protected int getAdapterViewId()
    {
        return R.id.fieldInputter;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add_for_database,menu);
        menu.findItem(R.id.exitAfterAdd).setChecked(mExitAfterAdd);
        menu.findItem(R.id.viewDetailAfterExit).setChecked(mViewDetailAfterExit);
        return true;
    }

    @Override
    protected void onDestroy() {
        //save p.
        mActivityPrefEditor.putBoolean(AppConfig.CONFIG_KEY_EXIT_AFTER_ADD,mExitAfterAdd);
        mActivityPrefEditor.putBoolean(AppConfig.CONFIG_KEY_VIEW_DETAIL_AFTER_EXIT,mViewDetailAfterExit);
        mActivityPrefEditor.commit();
        super.onDestroy();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.clear:
                mAdapter.clear();
                return true;
            case R.id.exitAfterAdd:
                mExitAfterAdd=!mExitAfterAdd;
                item.setChecked(mExitAfterAdd);
                return true;
            case R.id.viewDetailAfterExit:
                mViewDetailAfterExit=!mViewDetailAfterExit;
                item.setChecked(mViewDetailAfterExit);
                return true;
        }
        return false;
    }


    @ChainMethods("adapter-1")
    @OverrideToCustomBehaviour
    protected SequenceViewFieldInputterAdapter getDatabaseViewAdapter()
    {
        return null;
    }

    @ChainMethods("adapter-2")
    /**
     * if this method returns null,then getDatabaseViewAdapter is requested.
     *
     *
     * @return
     */
    @BeCareful("hidden default values must be placed ahead of multi-content")
    @OverrideToCustomBehaviour
    protected Object[][] getDatabaseViewAdapterUnderlyingData()
    {
        return null;
    }

    @OverrideToCustomBehaviour
    protected void doAdditionalSettingsAfterInit(){

    }

    @Override
    @OverrideToCustomBehaviour
    public void onClickPositive(View v) {
//        long id=mDb.insertBasedOnKeys(getTable(),null,mAdapter.collectAsContentValues());
        HashMap<String,ArrayList<ContentValues>> res=mAdapter.collectAsContentValues();
        if(res.size()==1)
        {
            for(Map.Entry<String,ArrayList<ContentValues>> entry:res.entrySet())
            {
                Util.logi("insertBasedOnKeys into single table:"+entry.getKey());
                for(ContentValues values:entry.getValue())
                {
                    long id=mDb.insert(entry.getKey(),null,values);
                    if(id==-1)
                    {
                        Util.logi("insertBasedOnKeys into single table failed.");
                        Toast.makeText(this,"Cannot Insert Data",Toast.LENGTH_LONG);
                    }else{
                        mLastTable=entry.getKey();
                        mLastId= (int) id;
                    }
                    setResult(RESULT_OK);
                }
            }
        }else{
            Util.logi("insertBasedOnKeys into multiple tables.Not implemented");
            //insertBasedOnKeys into multiple tables
        }
        if(mExitAfterAdd)
        {
            if(mViewDetailAfterExit && mLastTable!=null)
            {
                ActivityUtil.startViewDetailActivity(AddForDatabaseActivity.this,mLastTable,mLastId);
            }
            this.finish();
        }
    }

    @Override
    @OverrideToCustomBehaviour
    public void onClickNagetive(View v) {
        if(mRequestMethod==REQUEST_CODE_ADD_ONCE)
        {
            setResult(RESULT_CANCELED);
        }
        finish();
    }



    @Override
    public SQLiteDatabase getContextDB() {
        return mDb;
    }
}
