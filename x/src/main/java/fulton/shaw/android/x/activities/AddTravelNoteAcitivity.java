package fulton.shaw.android.x.activities;

import fulton.shaw.android.x.public_interfaces.SqliteHelper;
import fulton.shaw.android.x.sql.AdapterFieldsInfo;
import fulton.util.android.utils.SqlUtil;

public class AddTravelNoteAcitivity extends AddForDatabaseActivity {
    @Override
    protected Object[][] getDatabaseViewAdapterUnderlyingData() {
        return new Object[][]{
                {SqliteHelper.TABLE_TRAVEL_NOTE,SqlUtil.COL_CREATED_TIME,AdapterFieldsInfo.SHOW_TYPE_NOT_SHOWN,null,AdapterFieldsInfo.PROCESS_TYPE_HIDDEN_TIME_AUTO_CURRENT,null,null,null},
                {SqliteHelper.TABLE_TRAVEL_NOTE,SqlUtil.COL_START_TIME,null,"开始时间",AdapterFieldsInfo.PROCESS_TYPE_DATE_TIME_DIALOGS_TIME,null,null,null},
                {SqliteHelper.TABLE_TRAVEL_NOTE,SqlUtil.COL_SRC,null,"出发地",null,null,null,null},
                {SqliteHelper.TABLE_TRAVEL_NOTE,SqlUtil.COL_DST,null,"目的地",null,null,null,null},
                {SqliteHelper.TABLE_TRAVEL_NOTE,SqlUtil.COL_PREDICT_COST_TIME,null,"预计用时",null,null,null,null},
                {SqliteHelper.TABLE_TRAVEL_NOTE,SqlUtil.COL_PREDICT_COST_MONEY,null,"预计用费",null,null,null,null},
                {SqliteHelper.TABLE_TRAVEL_NOTE,SqlUtil.COL_PREDICT_METHOD,null,"预计出行方式",null,null,null,null},
                {SqliteHelper.TABLE_TRAVEL_NOTE,SqlUtil.COL_REAL_COST_TIME,null,"实际用时",null,null,null,null},
                {SqliteHelper.TABLE_TRAVEL_NOTE,SqlUtil.COL_REAL_COST_MONEY,null,"实际用费",null,null,null,null},
                {SqliteHelper.TABLE_TRAVEL_NOTE,SqlUtil.COL_REAL_METHOD,null,"实际出行方式",null,null,null,null},
                {SqliteHelper.TABLE_TRAVEL_NOTE,SqlUtil.COL_BENIFIT_OR_GOOD_PLAN,null,"是否有用",AdapterFieldsInfo.PROCESS_TYPE_EDITTEXT,AdapterFieldsInfo.VALUE_TYPE_INT,null,null},
        };
    }
}
