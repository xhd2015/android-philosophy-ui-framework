package fulton.shaw.android.x.activities;

import fulton.shaw.android.x.public_interfaces.SqliteHelper;
import fulton.shaw.android.x.sql.AdapterFieldsInfo;
import fulton.util.android.utils.SqlUtil;

/**
 * Created by 13774 on 8/3/2017.
 */

public class AddGeneralRecordTagDiaryActivity extends AddForDatabaseActivity {

    @Override
    protected Object[][] getDatabaseViewAdapterUnderlyingData() {
        return new Object[][]{
                //hidden default
                {SqliteHelper.TABLE_GENERAL_RECORDS, SqlUtil.COL_CREATED_TIME, AdapterFieldsInfo.SHOW_TYPE_NOT_SHOWN, null, AdapterFieldsInfo.PROCESS_TYPE_HIDDEN_TIME_AUTO_CURRENT, null, null, null},
                {SqliteHelper.TABLE_GENERAL_RECORDS, SqlUtil.COL_MAIN_TAG, AdapterFieldsInfo.SHOW_TYPE_NOT_SHOWN, null, AdapterFieldsInfo.PROCESS_TYPE_HIDDEN_USE_DEFAULT_VALUE, AdapterFieldsInfo.VALUE_TYPE_STRING, null, SqlUtil.TAG_DIARY},
                {SqliteHelper.TABLE_GENERAL_RECORDS, SqlUtil.COL_WEATHER, null, "天气", null, null, null, null},
                {SqliteHelper.TABLE_GENERAL_RECORDS, SqlUtil.COL_PLACE, null, "地点", null, null, null, null},
                {SqliteHelper.TABLE_GENERAL_RECORDS, SqlUtil.COL_BRIEF, null, "标题", null, null, null, null},
                {SqliteHelper.TABLE_GENERAL_RECORDS, SqlUtil.COL_DETAIL, AdapterFieldsInfo.SHOW_TYPE_DEFAULT_DIARY_EDITTEXT, "内容", null, null, null, null}
        };
    }
}
