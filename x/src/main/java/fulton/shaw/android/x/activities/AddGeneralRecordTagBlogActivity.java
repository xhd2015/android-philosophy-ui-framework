package fulton.shaw.android.x.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;

import fulton.shaw.android.x.R;
import fulton.shaw.android.x.models.SqliteTableModelGetter;
import fulton.shaw.android.x.public_interfaces.SqliteHelper;
import fulton.util.android.varman.StateVariableManager;
import fulton.shaw.android.x.viewgetter.database_auto.ShareableSingleViewAutoCollector;
import fulton.shaw.android.x.viewgetter.database_auto.SingleViewAutoCollector;
import fulton.shaw.android.x.viewgetter.database_auto.ViewGetter;
import fulton.shaw.android.x.viewgetter.database_auto.checkers.CheckerGetBase;
import fulton.shaw.android.x.views.viewhelpers.singleview_functions.DatabaseModelViewFunction;
import fulton.shaw.android.x.views.viewhelpers.singleview_functions.FunctionAddToDatabase;
import fulton.shaw.android.x.views.viewhelpers.singleview_functions.FunctionExitActivity;
import fulton.util.android.interfaces.ViewInfo;
import fulton.shaw.android.x.views.TextSwitchableTextView;
import fulton.util.android.interfaces.ValueGetter;
import fulton.util.android.interfaces.comparators.Comparator;
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
public class AddGeneralRecordTagBlogActivity extends AppCompatActivity {

    public static final String VAR_SAVED_EDIT_POS="savedEditPos";

    private SingleViewAutoCollector mCollector;
    private StateVariableManager mVarMan;
    private SqliteHelper mSqliteHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_general_record_tag_blog);
        mVarMan=new StateVariableManager(getPreferences(MODE_PRIVATE));
        mVarMan.load();
        mVarMan.setDefaultValue(SqlUtil.COL_BRIEF,"");
        mVarMan.setDefaultValue(SqlUtil.COL_SHOWN_TYPE,SqlUtil.SHOWN_TYPE_NORMAL);
        mVarMan.setDefaultValue(SqlUtil.COL_DETAIL,"");
        mVarMan.setDefaultValue(VAR_SAVED_EDIT_POS,0);
        mSqliteHelper=new SqliteHelper(this);



        final ArrayList<ViewInfo> infos= SqliteTableModelGetter.get(SqliteHelper.TABLE_GENERAL_RECORDS);
        ValueGetter<String,Object> getter=new ValueGetter<String, Object>() {
            @Override
            public Object getValue(String s) {
                int index = Util.searchArrayList(infos, s, Comparator.getViewInfoKeyComparator());
                return mVarMan.get(s, infos.get(index).typeClass);
            }
        };


        mCollector=new SingleViewAutoCollector(findViewById(R.id.rootView),
                infos,
                ViewGetter.getViewGettersBaseOnKey(infos,
                        SqlUtil.COL_BRIEF,R.id.titleEditText, EditText.class,
                        SqlUtil.COL_DETAIL,R.id.contentEditText,EditText.class,
                        SqlUtil.COL_SHOWN_TYPE,R.id.recordTypeSpinner,Spinner.class
                        )
                );
        mCollector.setPresetValuesBasedOnKey(
                SqlUtil.COL_BRIEF,getter,
                SqlUtil.COL_DETAIL,getter,
                SqlUtil.COL_SHOWN_TYPE,getter,
                SqlUtil.COL_MAIN_TAG,SqlUtil.TAG_BLOG
        );
        mCollector.addGetCheckersBasedOnKey(SqlUtil.COL_BRIEF, new CheckerGetBase() {
            @Override
            public boolean checkEditText(EditText view) {
                return view.getText().length()+mCollector.findViewByKey(SqlUtil.COL_DETAIL,EditText.class).getText().length()>0;
            }
        });
        mCollector.setCheckGetFailedActionBasedOnKey(SqlUtil.COL_BRIEF,ShareableSingleViewAutoCollector.ACTION_ABORT);
        mCollector.fillPresetValue();
        ArrayList clearValue=ShareableSingleViewAutoCollector.genPresetValuesBasedOnKey(infos,
                SqlUtil.COL_BRIEF,"",
                SqlUtil.COL_DETAIL,""
                );
        final PackedObject<Boolean> added=new PackedObject<>(false);
        FunctionAddToDatabase addToDatabaseAndExit=new FunctionAddToDatabase(SqliteHelper.TABLE_GENERAL_RECORDS,
                mSqliteHelper,
                new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(AddGeneralRecordTagBlogActivity.this, "不能添加，尝试退出重试", Toast.LENGTH_SHORT).show();
                    }
                },
                new Runnable() {
                    @Override
                    public void run() {
                        added.e = true;
                        setResult(RESULT_OK);
                    }
                },
                new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(AddGeneralRecordTagBlogActivity.this,"请检查输入是否有误(标题和内容不能同时为空)",Toast.LENGTH_SHORT).show();
                    }
                },
                clearValue
        ){
            @Override
            public void apply(SingleViewAutoCollector collector) {
                super.apply(collector);
                if(added.e) {

                    finish();
                }
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
        mCollector.findViewByKey(SqlUtil.COL_DETAIL,EditText.class).setSelection(mVarMan.get(VAR_SAVED_EDIT_POS,Integer.class));
    }

    @Override
    protected void onDestroy() {
        mVarMan.set(SqlUtil.COL_BRIEF,mCollector.findViewByKey(SqlUtil.COL_BRIEF,EditText.class).getText().toString());
        mVarMan.set(SqlUtil.COL_SHOWN_TYPE,mCollector.findViewByKey(SqlUtil.COL_SHOWN_TYPE,Spinner.class).getSelectedItemPosition());
        mVarMan.set(SqlUtil.COL_DETAIL,mCollector.findViewByKey(SqlUtil.COL_DETAIL,EditText.class).getText().toString());
        mVarMan.set(VAR_SAVED_EDIT_POS,mCollector.findViewByKey(SqlUtil.COL_DETAIL,EditText.class).getSelectionStart());

        //===all saved.
        mVarMan.save();

        super.onDestroy();
    }
}
