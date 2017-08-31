package fulton.shaw.android.x.activities;

import android.text.InputType;
import android.widget.EditText;
import android.widget.TextView;

import fulton.shaw.android.x.adapters.SequenceViewFieldInputterAdapter;
import fulton.shaw.android.x.public_interfaces.SqliteHelper;
import fulton.shaw.android.x.sql.AdapterFieldsInfo;
import fulton.util.android.utils.SqlUtil;

/**
 * Created by 13774 on 8/3/2017.
 */

@Deprecated
public class AddGeneralRecordTagSaySomethingActivity extends AddForDatabaseActivity {
    @Override
    protected Object[][] getDatabaseViewAdapterUnderlyingData() {
        return new Object[][]{
                //hidden default
                {SqliteHelper.TABLE_GENERAL_RECORDS, SqlUtil.COL_CREATED_TIME, AdapterFieldsInfo.SHOW_TYPE_NOT_SHOWN, null, AdapterFieldsInfo.PROCESS_TYPE_HIDDEN_TIME_AUTO_CURRENT, null, null, null},
                {SqliteHelper.TABLE_GENERAL_RECORDS, SqlUtil.COL_MAIN_TAG, AdapterFieldsInfo.SHOW_TYPE_NOT_SHOWN, null, AdapterFieldsInfo.PROCESS_TYPE_HIDDEN_USE_DEFAULT_VALUE, AdapterFieldsInfo.VALUE_TYPE_STRING, null, SqlUtil.TAG_SAY_SOMETHING},
                {SqliteHelper.TABLE_GENERAL_RECORDS, SqlUtil.COL_DETAIL, AdapterFieldsInfo.SHOW_TYPE_DEFAULT_DESCRIPTIVE_EDITTEXT, "说点什么", null, null, null, null},
        };
    }

    @Override
    protected void doAdditionalSettingsAfterInit() {
        mAdapter.setActionAfterEachGetView(new SequenceViewFieldInputterAdapter.ActionAfterEachGetView() {
            @Override
            public void performAction(AdapterFieldsInfo info) {
                if(info.dbKey.equals(SqlUtil.COL_DETAIL))
                {
                    info.getTitleViewAsTextView().setText("");
                    info.getContentViewAsEditText().setHint("说点什么吧...");
                    info.generatedContentView.requestFocus();
                    info.getContentViewAsEditText().setMinLines(3);
                    info.getContentViewAsEditText().setMaxLines(20);//helps
                    info.getContentViewAsEditText().requestFocus();
                }
            }
        });
    }
}

