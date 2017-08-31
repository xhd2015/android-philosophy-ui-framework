package fulton.shaw.android.x.activities;

import fulton.shaw.android.x.public_interfaces.SqliteHelper;
import fulton.shaw.android.x.sql.AdapterFieldsInfo;
import fulton.util.android.utils.SqlUtil;

/**
 * Created by 13774 on 8/3/2017.
 */

public class AddGeneralRecordActivity extends AddForDatabaseActivity {

    @Override
    protected Object[][] getDatabaseViewAdapterUnderlyingData() {
        return new Object[][]{
                //hidden default
                {SqliteHelper.TABLE_PLAN, SqlUtil.COL_CREATED_TIME, AdapterFieldsInfo.SHOW_TYPE_NOT_SHOWN,null,AdapterFieldsInfo.PROCESS_TYPE_HIDDEN_TIME_AUTO_CURRENT,null,null,null},
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
}
