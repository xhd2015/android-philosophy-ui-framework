package fulton.shaw.android.x.activities;

import android.content.ContentValues;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

import fulton.shaw.android.x.public_interfaces.SqliteHelper;
import fulton.shaw.android.x.sql.AdapterFieldsInfo;
import fulton.util.android.utils.SqlUtil;
import fulton.util.android.utils.Util;

public class AddPlanActivity extends AddForDatabaseActivity {

    @Override
    protected Object[][] getDatabaseViewAdapterUnderlyingData() {
        return new Object[][]{
                //hidden default
                {SqliteHelper.TABLE_PLAN,SqlUtil.COL_CREATED_TIME,AdapterFieldsInfo.SHOW_TYPE_NOT_SHOWN,null,AdapterFieldsInfo.PROCESS_TYPE_HIDDEN_TIME_AUTO_CURRENT,null,null,null},
                {SqliteHelper.TABLE_ISSUE,SqlUtil.COL_STATUS,AdapterFieldsInfo.SHOW_TYPE_NOT_SHOWN,null,AdapterFieldsInfo.PROCESS_TYPE_HIDDEN_USE_DEFAULT_VALUE,AdapterFieldsInfo.VALUE_TYPE_STRING,null,SqlUtil.STATUS_NOT_COMPLISHED},
                {SqliteHelper.TABLE_ISSUE,SqlUtil.COL_TABLE_NAME,AdapterFieldsInfo.SHOW_TYPE_NOT_SHOWN,null,AdapterFieldsInfo.PROCESS_TYPE_HIDDEN_USE_DEFAULT_VALUE,AdapterFieldsInfo.VALUE_TYPE_STRING,null,SqliteHelper.TABLE_PLAN},
                {SqliteHelper.TABLE_PLAN,SqlUtil.COL_STATUS,AdapterFieldsInfo.SHOW_TYPE_NOT_SHOWN,null,AdapterFieldsInfo.PROCESS_TYPE_HIDDEN_USE_DEFAULT_VALUE,AdapterFieldsInfo.VALUE_TYPE_STRING,null,SqlUtil.STATUS_NOT_COMPLISHED},

                {SqliteHelper.TABLE_PLAN,SqlUtil.COL_NAME,null,"名称",null,null,null,null},
                {SqliteHelper.TABLE_PLAN,SqlUtil.COL_PLACE,null,"地点",null,null,null,null},
                {SqliteHelper.TABLE_PLAN,SqlUtil.COL_END_TIME,null,"结束时间",AdapterFieldsInfo.PROCESS_TYPE_DATE_TIME_DIALOGS_TIME,null,null,null},

                //multi-content
                {SqliteHelper.TABLE_ISSUE,SqlUtil.COL_CONTENT,AdapterFieldsInfo.SHOW_TYPE_DEFAULT_MULTI_CONTENT,"事务",AdapterFieldsInfo.PROCESS_TYPE_MULTI_EDITTEXT,null,null,null}
        };
    }

    @Override
    public void onClickPositive(View v) {
        final HashMap<String, ArrayList<ContentValues>> data = mAdapter.collectAsContentValues();
        long id=mDb.insert(SqliteHelper.TABLE_PLAN,null,data.get(SqliteHelper.TABLE_PLAN).get(0));
        if(id!=-1)
        {
            Util.logi("issue count="+data.get(SqliteHelper.TABLE_ISSUE).size());
            for(ContentValues value:data.get(SqliteHelper.TABLE_ISSUE))
            {
                value.put(SqlUtil.COL_INNER_ID,id);
                long subId=mDb.insert(SqliteHelper.TABLE_ISSUE,null,value);
                Util.logi("subId="+subId);
            }
        }else{
            Util.logi("insertBasedOnKeys failed");
            Toast.makeText(this,"insertBasedOnKeys into "+SqliteHelper.TABLE_PLAN+" failed",Toast.LENGTH_LONG);
        }
    }

}
