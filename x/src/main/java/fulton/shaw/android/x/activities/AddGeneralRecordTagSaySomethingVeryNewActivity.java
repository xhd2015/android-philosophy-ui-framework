package fulton.shaw.android.x.activities;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;

import fulton.shaw.android.x.R;
import fulton.shaw.android.x.models.SqliteTableModelGetter;
import fulton.shaw.android.x.public_interfaces.AppConfig;
import fulton.shaw.android.x.public_interfaces.SqliteHelper;
import fulton.util.android.interfaces.valueproviders.ValueProviderOfImmutableCursor;
import fulton.util.android.varman.StateVariableManager;
import fulton.shaw.android.x.viewgetter.database_auto.ShareableSingleViewAutoCollector;
import fulton.shaw.android.x.viewgetter.database_auto.SingleViewAutoCollector;
import fulton.shaw.android.x.viewgetter.database_auto.ViewGetter;
import fulton.shaw.android.x.viewgetter.database_auto.checkers.CheckerGetBase;
import fulton.shaw.android.x.viewgetter.database_auto.checkers.CheckerGetNotEmpty;
import fulton.util.android.interfaces.typetransfers.TransferCalendarToSqliteDate;
import fulton.shaw.android.x.views.DatePickerDialogWithTextView;
import fulton.shaw.android.x.views.TextSwitchableTextView;
import fulton.shaw.android.x.views.viewhelpers.singleview_functions.DatabaseModelViewFunction;
import fulton.shaw.android.x.views.viewhelpers.singleview_functions.FunctionCommitDatabase;
import fulton.shaw.android.x.views.viewhelpers.singleview_functions.FunctionExitActivity;
import fulton.util.android.interfaces.ViewInfo;
import fulton.util.android.thread.PackedObject;
import fulton.util.android.utils.SqlUtil;
import fulton.util.android.utils.Util;
import fulton.util.android.utils.ViewUtil;

/**
 * 流程：
 *      进入-->设置presetValues-->输入
 *              -->取消然后退出(保存输入)
 *              -->提交
 *                     -->成功 (清除，退出)
 *                     -->错误-->弹出对话框，是否退出（保存当前输入状态)
 *
 *
 *       presetValues 创建时间，保存的的变量。
 */

/**
 *  @Accept id  as argument.
 */
public class AddGeneralRecordTagSaySomethingVeryNewActivity extends AppCompatActivity {



    private SingleViewAutoCollector mCollector;
    private StateVariableManager mVarMan;
    private SqliteHelper mSqliteHelper;

    private long mId;


    private String[] mUpdateableCols=new String[]{ SqlUtil.COL_DETAIL,SqlUtil.COL_SHOWN_TYPE,SqlUtil.COL_SHOWN_DATE};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_general_record_tag_saysomething);
        mVarMan=new StateVariableManager(getPreferences(MODE_PRIVATE));
        mVarMan.load();
        mSqliteHelper=new SqliteHelper(this);

        mId=getIntent().getLongExtra(AppConfig.ARG_ID,-1L);
        Util.logi("argId="+mId);

        final ArrayList<ViewInfo> infos= SqliteTableModelGetter.get(SqliteHelper.TABLE_GENERAL_RECORDS);

        mCollector=new SingleViewAutoCollector(findViewById(R.id.rootView),
                infos,
                ViewGetter.getViewGettersBaseOnKey(infos,
                        SqlUtil.COL_DETAIL,R.id.contentEditText,EditText.class,
                        SqlUtil.COL_SHOWN_TYPE,R.id.recordTypeSpinner,Spinner.class,
                        SqlUtil.COL_SHOWN_DATE,R.id.shownDateTextView, DatePickerDialogWithTextView.class
                        )
                );//当value由多个view影响时，怎么解决？自定义新的transfer,或者checker
        final CheckBox checkBox= (CheckBox) findViewById(R.id.setShownDateCheckBox);
        mCollector.setTransfersBasedOnKey(SqlUtil.COL_SHOWN_DATE,new TransferCalendarToSqliteDate());
        mCollector.setPresetValuesBasedOnKey(
                SqlUtil.COL_MAIN_TAG,SqlUtil.TAG_SAY_SOMETHING
        );
        mCollector.addGetCheckersBasedOnKey(SqlUtil.COL_BRIEF, new CheckerGetNotEmpty(),
                SqlUtil.COL_SHOWN_DATE,new CheckerGetBase(){
                    @Override
                    public boolean checkDatePicker(DatePickerDialogWithTextView view) {
                        if(!checkBox.isChecked())
                            return false;
                        else
                            return true;
                    }
                }
                );
        mCollector.setCheckGetFailedActionBasedOnKey(
                SqlUtil.COL_BRIEF,ShareableSingleViewAutoCollector.ACTION_ABORT,
                SqlUtil.COL_SHOWN_DATE,ShareableSingleViewAutoCollector.ACTION_DO_NOT_SET
                );
        ArrayList clearValue=ShareableSingleViewAutoCollector.genPresetValuesBasedOnKey(infos,
                SqlUtil.COL_DETAIL,""
        );
        Cursor cursor=null;
        if(mId==-1)
        {
            mCollector.fillPresetValue();
        }else{
            cursor=mSqliteHelper.getCursorById(SqliteHelper.TABLE_GENERAL_RECORDS,mId);
            if(!cursor.moveToFirst())
            {
                Toast.makeText(this,"数据有误,无法查询到要更新的数据，请重试",Toast.LENGTH_SHORT).show();
                this.setResult(RESULT_CANCELED);
                this.finish();
            }
            Util.logi("cursor:");
            Util.logi(cursor);
            ValueProviderOfImmutableCursor  provider=new ValueProviderOfImmutableCursor(cursor);
            mCollector.fillValue(provider);
            cursor.close();
        }
        final PackedObject<Boolean> added=new PackedObject<>(false);
        FunctionCommitDatabase addToDatabaseAndExit=new FunctionCommitDatabase(SqliteHelper.TABLE_GENERAL_RECORDS,mId,
                mSqliteHelper,
                new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(AddGeneralRecordTagSaySomethingVeryNewActivity.this, "不能添加或更新，尝试退出重试", Toast.LENGTH_SHORT).show();
                    }
                },
                new Runnable() {
                    @Override
                    public void run() {
                        if (mId != -1) {
                            Cursor finalCursor=mSqliteHelper.getCursorById(SqliteHelper.TABLE_GENERAL_RECORDS,mId);
                            finalCursor.moveToFirst();
                            Util.logi("after update:");
                            Util.logi(finalCursor);
                        }
                        added.e = true;
                        setResult(RESULT_OK);
                    }
                },
                new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(AddGeneralRecordTagSaySomethingVeryNewActivity.this,"请检查输入是否有误(内容不能为空)",Toast.LENGTH_SHORT).show();
                    }
                },
                clearValue,
                mUpdateableCols
        ){
            @Override
            public void apply(SingleViewAutoCollector collector) {
                super.apply(collector);
                if(added.e)
                    finish();
            }
        };

        DatabaseModelViewFunction.setButtonFunction(findViewById(R.id.positiveButton),mCollector,addToDatabaseAndExit);
        DatabaseModelViewFunction.setButtonFunction(findViewById(R.id.nagetiveButton),mCollector,new FunctionExitActivity(this));



        //====set additional switch functions
        final TextSwitchableTextView switcher= (TextSwitchableTextView) findViewById(R.id.switchTextView);
        final View additional=findViewById(R.id.additionalSettings);
        switcher.setOnSwitchListener(new TextSwitchableTextView.OnSwitchTitleListener() {
            @Override
            public void onSwitchTitle(TextSwitchableTextView view) {
                ViewUtil.setShown(additional,view.getTitleSelection()==1);
            }
        });
        ViewUtil.setShown(additional,switcher.getTitleSelection()==1);

        //===doCorrections
    }

    @Override
    protected void onDestroy() {

        //===all saved.
        mVarMan.save();

        super.onDestroy();
    }
}
