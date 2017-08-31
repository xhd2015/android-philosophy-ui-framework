package fulton.shaw.android.x.activities;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Looper;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import fulton.shaw.android.x.R;
import fulton.shaw.android.x.activities.specfunctions.FunctionViewDetailThroughReference;
import fulton.shaw.android.x.async_thread.AcitivityAsyncThreadManager;
import fulton.shaw.android.x.async_thread.DelayedActionsManager;
import fulton.shaw.android.x.models.SqliteTableModelGetter;
import fulton.shaw.android.x.public_interfaces.ActivityUtil;
import fulton.shaw.android.x.public_interfaces.AppConfig;
import fulton.shaw.android.x.public_interfaces.SqliteHelper;
import fulton.util.android.utils.StringFormatters;
import fulton.shaw.android.x.broadcast.AlarmNotifyBroadcastReceiver;
import fulton.util.android.varman.StateVariableManager;
import fulton.shaw.android.x.viewgetter.database_auto.AdapterViewAutoCompletor;
import fulton.shaw.android.x.viewgetter.database_auto.ShareableSingleViewAutoCollector;
import fulton.shaw.android.x.viewgetter.database_auto.SingleViewAutoCollector;
import fulton.shaw.android.x.viewgetter.database_auto.SingleViewAutoCompletor;
import fulton.shaw.android.x.viewgetter.database_auto.ViewGenerator;
import fulton.shaw.android.x.viewgetter.database_auto.ViewGetter;
import fulton.util.android.interfaces.ViewInfo;
import fulton.shaw.android.x.viewgetter.database_auto.checkers.CheckerGetBase;
import fulton.shaw.android.x.viewgetter.database_auto.valuegetters.ValueGetterCurrentDateTimeLong;
import fulton.shaw.android.x.viewgetter.transferview.ShareableViewValueTransfer;
import fulton.util.android.interfaces.typetransfers.TransferCalendarToLong;
import fulton.util.android.interfaces.typetransfers.TransferStringToLongDateTime;
import fulton.util.android.interfaces.typetransfers.ValueTypeTransfer;
import fulton.shaw.android.x.views.DatePickerDialogWithTextView;
import fulton.shaw.android.x.views.DateTimePickerDialogWithTextView;
import fulton.shaw.android.x.views.viewhelpers.AdapterViewWrapper;
import fulton.shaw.android.x.views.viewhelpers.ControlInfoListViewHelper;
import fulton.shaw.android.x.views.viewhelpers.DatabaseAdapterView;
import fulton.shaw.android.x.views.viewhelpers.ExpandCollaspViewHelper;
import fulton.shaw.android.x.views.viewhelpers.PositiveNagetiveViewHelper;
import fulton.shaw.android.x.views.viewhelpers.functions.AdapterViewFunction;
import fulton.shaw.android.x.views.viewhelpers.functions.FunctionClearAdderView;
import fulton.shaw.android.x.views.viewhelpers.functions.FunctionCommitAdderViewToDatabase;
import fulton.shaw.android.x.views.viewhelpers.functions.FunctionDeleteItemFunction;
import fulton.shaw.android.x.views.viewhelpers.functions.FunctionHideAdderView;
import fulton.shaw.android.x.views.viewhelpers.functions.FunctionShowAdderViewForAdd;
import fulton.shaw.android.x.views.viewhelpers.functions.FunctionShowAdderViewForUpdate;
import fulton.shaw.android.x.views.viewhelpers.functions.FunctionUpdateAdderViewToDatabase;
import fulton.util.android.thread.PackedObject;
import fulton.util.android.utils.CalendarUtil;
import fulton.util.android.utils.SqlUtil;
import fulton.util.android.utils.Util;
import fulton.util.android.notations.HelperMethods;
import fulton.util.android.utils.ViewUtil;

/**
 * @openArguments tableName, id.
 *
 *  This internally supports show each model itself
 *                          show controlling information relating each model.Each controlling information should be shown should be listed.
 *
 *    Show Tag information.
 *    Controlling information requires: tableName, innerId.
 */
public class ViewSubitemDetailActivity extends AppCompatActivity{

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Util.logi("onActivity result,reqCode,resCode="+requestCode+","+resultCode);
        if(requestCode== REQUEST_SELECT_REFERENCE && resultCode==RESULT_OK)
        {
            mDelayedActions.setArgument("onAcitivityResultSelectReference","test");
            mDelayedActions.run("onAcitivityResultSelectReference");//you can call some thing like: Return Error
            return;
        }else if(requestCode == REQUEST_UPDATE_MODEL && resultCode==RESULT_OK){
            mModelCompletor.requery();
            mModelCompletor.refresh();
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public static final int REQUEST_SELECT_REFERENCE =0;
    public static final int REQUEST_UPDATE_MODEL=1;


    public static final String ARG_TABLE_NAME="tableName";
    public static final String ARG_INNER_ID="innerId";

    public static final String CONTROL_TAG="tag";
    public static final String CONTROL_ALARM="alarm";
    public static final String CONTROL_REVIEW="review";


    //======helper fields
    private SqliteHelper mSqliteHelper;
    protected SharedPreferences mActivityPref;
    protected SharedPreferences.Editor mActivityPrefEditor;
    protected StateVariableManager mVarMan;


    //=====view fields
    protected LinearLayout mModelContainer;
    protected LinearLayout mControlContainer;
    protected ControlInfoListViewHelper<ListView> mReviewListHelper;
    protected ControlInfoListViewHelper<ListView> mAlarmListHelper;
    protected ControlInfoListViewHelper<GridView> mTagsListHelper;

    //===menu item
    protected MenuItem[] mSetItemsMenu=new MenuItem[4];
    protected int[]     mSetItemsValue=new int[4];

    @Deprecated
    protected String[] mSetItemKeys=new String[]{
        AppConfig.CONFIG_KEY_MIN_SHOWN_ITEM_REVIEW,
            AppConfig.CONFIG_KEY_MAX_SHOWN_ITEM_REVIEW,
            AppConfig.CONFIG_KEY_MIN_SHOWN_ITEM_ALARM,
            AppConfig.CONFIG_KEY_MAX_SHOWN_ITEM_ALARM
    };


    public static final String[] VAR_EXPANDS=new String[]{"referenceExpand","reviewExpand","tagExpand","alarmExpand"};
    public static final String[][] VAR_HEIGHT_PARAM=new String[][]{
            new String[]{"referenceUnit","referenceMin","referenceMax"},
            new String[]{"reviewUnit","reviewMin","reviewMax"},
            new String[]{"tagUnit","tagMin","tagMax"},
            new String[]{"alarmUnit","alarmMin","alarmMax"},
    };
    public static final String VAR_LAST_CHOOSE_LIST="lastChooseList";


    //========arguments fields
    protected String[] mModelArgs;
    protected String[] mControlArgs;
    protected String mSqlModel,mSqlReview,mSqlAlarm,mSqlTag;
    protected static final String SQL_MODEL_FORMMATER="Select * From %s Where _id=?";
    protected static final String SQL_CONTROL_FORMMATER="Select * From %s Where "+SqlUtil.COL_TABLE_NAME+" = ? and "+SqlUtil.COL_INNER_ID+" = ?";

    //=======worker part
    protected Cursor mModelCursor;
    protected Cursor mReviewListCursor;
    protected Cursor mAlarmListCursor;
    protected Cursor mTagListCursor;
    protected ReviewListAdapter mReviewListAdapter;
    protected AlarmListAdapter mAlarmListAdapter;
    protected TagListAdapter mTagListAdapter;

    //========new structure
    private SingleViewAutoCompletor mModelCompletor;

    private AcitivityAsyncThreadManager mThreadManager;
    private DelayedActionsManager mDelayedActions;

    //====switch
    private AdapterView[]       mAdapterViews;
    private ExpandCollaspViewHelper[] mSwitchers;
    private View mModelView;

    //====dialog wrapper
    private ListHeightDialogWrapper mDialogWrapper;


    //===argument field
    private String  mTable;
    private long    mInnderId;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        mThreadManager= new AcitivityAsyncThreadManager(this);
        mDelayedActions=new DelayedActionsManager();

        mThreadManager.startThread("initData", new Runnable() {
            @Override
            public void run() {
                mVarMan=new StateVariableManager(getPreferences(MODE_PRIVATE));
                mVarMan.load();
            }
        });
        mSqliteHelper=new SqliteHelper(ViewSubitemDetailActivity.this);
        setContentView(R.layout.show_list_detail);
        initIntentArguments();

//        @TestNote(TestNote.STATE.TESTED_SUCCESS_WEAK)
        mThreadManager.startThread("initModelViews", new Runnable() {
            @Override
            public void run() {
                //==假设只有saysomething和blog这两种类型
                //===set up models
                mModelView=getLayoutInflater().inflate(R.layout.show_list_detail_model_say_something, (ViewGroup) findViewById(R.id.modelInfoContainer),true);

                mModelCompletor=new SingleViewAutoCompletor(
                        mSqliteHelper.getCursorById(mTable,mInnderId),mTable,
                        mModelView,
                        SqliteTableModelGetter.get(mTable)
                );
                if(!checkCursorIsOnlySaySomethingAndBlog(mModelCompletor.getCursor()))
                    throw new UnsupportedOperationException("Do not support other types");
                if(SqlUtil.TAG_BLOG.equals(SqlUtil.getCursorValue(mModelCompletor.getCursor(),SqlUtil.COL_MAIN_TAG,String.class)))
                {
                    mModelCompletor.setViewGettersBasedOnKey(
                            SqlUtil.COL_CREATED_TIME,R.id.createdTimeTextView,TextView.class,
                            SqlUtil.COL_DETAIL,R.id.detailTextView,TextView.class,
                            SqlUtil.COL_BRIEF,R.id.briefTextView,TextView.class
                    );
                    mModelCompletor.findViewByKey(SqlUtil.COL_BRIEF,TextView.class).setVisibility(View.VISIBLE);
                }else{
                    mModelCompletor.setViewGettersBasedOnKey(
                            SqlUtil.COL_CREATED_TIME,R.id.createdTimeTextView,TextView.class,
                            SqlUtil.COL_DETAIL,R.id.detailTextView,TextView.class
                    );
                }
                findViewById(R.id.modifyButton).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivityForResult(ActivityUtil.getAddActivity(ViewSubitemDetailActivity.this,
                                mTable,
                                SqlUtil.getCursorValue(mModelCompletor.getCursor(),SqlUtil.COL_MAIN_TAG,String .class),
                                SqlUtil.getCursorId(mModelCompletor.getCursor())
                                ),REQUEST_UPDATE_MODEL);
                    }
                });
                mModelCompletor.refresh();//fill value
                mDelayedActions.add("onDestroy", new Runnable() {
                    @Override
                    public void run() {
                        mModelCompletor.closeCursorIfNeeded();
                    }
                });
            }
        });


        final View controlContainer=findViewById(R.id.controlInfoContainer);
        final View reviewListContainer=controlContainer.findViewById(R.id.reviewListContainer);
        final View tagListContainer=controlContainer.findViewById(R.id.tagListContainer);
        final View alarmListContainer=controlContainer.findViewById(R.id.alarmListContainer);
        final View referenceListContainer=controlContainer.findViewById(R.id.referenceListContainer);
        final ListView reviewListView= (ListView) reviewListContainer.findViewById(R.id.contentAdapterView);
        mAdapterViews=new AdapterView[]{
                (ListView)referenceListContainer.findViewById(R.id.contentAdapterView),
                (ListView)reviewListContainer.findViewById(R.id.contentAdapterView),
                (GridView)tagListContainer.findViewById(R.id.contentAdapterView),
                (ListView)alarmListContainer.findViewById(R.id.contentAdapterView),
        };
        mThreadManager.asyncWaitThread("initData","initSwitchers", new Runnable() {
            @Override
            public void run() {
                mSwitchers=new ExpandCollaspViewHelper[]{
                    new ExpandCollaspViewHelper(referenceListContainer.findViewById(R.id.switchButton)),
                    new ExpandCollaspViewHelper(reviewListContainer.findViewById(R.id.switchButton)),
                    new ExpandCollaspViewHelper(tagListContainer.findViewById(R.id.switchButton)),
                    new ExpandCollaspViewHelper(alarmListContainer.findViewById(R.id.switchButton))
                };
                boolean[] expandDefaultValues=new boolean[]{true,true,true,false};
                for(int i=0;i<mSwitchers.length;i++)
                {
                    final int j=i;
                    mSwitchers[i].setListener(new ExpandCollaspViewHelper.OnExpandCollaspListener() {
                        @Override
                        public void onExpand(ExpandCollaspViewHelper helper) {
                            ViewUtil.setShown(mAdapterViews[j],true);
                        }

                        @Override
                        public void onCollasp(ExpandCollaspViewHelper helper) {
                            ViewUtil.setShown(mAdapterViews[j],false);
                        }
                    });
                    mSwitchers[i].setExpand(mVarMan.get(VAR_EXPANDS[i],expandDefaultValues[i]));
                }
            }
        });

        final PackedObject<DatabaseAdapterView.OnErrorConditionHandler> handler=new PackedObject<>();
        mThreadManager.startThread("initSharedVars", new Runnable() {
            @Override
            public void run() {
                handler.e=new DatabaseAdapterView.OnErrorConditionHandler() {
                    @Override
                    public void onCollectAdderViewValueFailed(DatabaseAdapterView adpertView) {
                        Toast.makeText(ViewSubitemDetailActivity.this,"请重新检查输入是否符合要求",Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFillAdderViewValueFailed(DatabaseAdapterView adpertView) {
                        Toast.makeText(ViewSubitemDetailActivity.this,"内部数据转换错误",Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCommitDatabaseFailed(DatabaseAdapterView adapterView) {
                        Toast.makeText(ViewSubitemDetailActivity.this,"数据库有错误",Toast.LENGTH_SHORT).show();
                    }
                };
            }
        });

        //==Tested

        mThreadManager.asyncWaitThread("initSharedVars","initReviewList", new Runnable() {
            @Override
            public void run() {
                ArrayList clearValues=ShareableSingleViewAutoCollector.genPresetValuesBasedOnKey(
                        SqliteTableModelGetter.get(SqliteHelper.TABLE_REVIEW),
                        new Object[]{ SqlUtil.COL_CONTENT,""});
                View adderView=reviewListContainer.findViewById(R.id.adderViewContainer);

                  sharedWrapperRunner(SqliteHelper.TABLE_REVIEW,
                          reviewListContainer,
                          reviewListView,
                            R.layout.show_list_detail_review_item,
                          new Object[]{ SqlUtil.COL_CREATED_TIME, R.id.createdTimeTextView,TextView.class,
                                SqlUtil.COL_CONTENT, R.id.content,TextView.class},
                          new Object[]{},
                          new Object[]{SqlUtil.COL_CONTENT, R.id.content, EditText.class},
                          new Object[]{},
                          new AdapterViewFunction[]{
                                new FunctionDeleteItemFunction(),
                                new FunctionCommitAdderViewToDatabase(clearValues,SqlUtil.COL_CONTENT),
                                new FunctionHideAdderView(clearValues),
                                new FunctionShowAdderViewForAdd(),//3
                                new FunctionShowAdderViewForUpdate(),//4
                                new FunctionClearAdderView(clearValues)//5
                        },
                          new Object[]{
                                  SqlUtil.COL_TABLE_NAME,mTable,
                                SqlUtil.COL_INNER_ID,mInnderId,
                          },
                          new int[]{AdapterViewWrapper.FUNCTION_POP_UP_CONTEXT_MENU},
                        new Object[]{"删除", new int[]{0},
                                "修改", new int[]{4}
                        },
                        new Object[]{
                                adderView.findViewById(R.id.positiveButton),new int[]{1},
                                adderView.findViewById(R.id.nagetiveButton),new int[]{2},
                                reviewListContainer.findViewById(R.id.addButton),new int[]{3}
                            },
                          handler.e
                  );
            }
        });
        //===Tested
        mThreadManager.asyncWaitThread("initSharedVars", "initTagList", new Runnable() {
            @Override
            public void run() {
                ArrayList clearValues=ShareableSingleViewAutoCollector.genPresetValuesBasedOnKey(
                        SqliteTableModelGetter.get(SqliteHelper.TABLE_TAG),
                        new Object[]{ SqlUtil.COL_CONTENT,""});
                View adderView=tagListContainer.findViewById(R.id.adderViewContainer);

                sharedWrapperRunner(SqliteHelper.TABLE_TAG,
                        tagListContainer,
                        (GridView)tagListContainer.findViewById(R.id.contentAdapterView),
                        R.layout.show_list_detail_tag_item,
                        new Object[]{
                                SqlUtil.COL_CONTENT, R.id.content,TextView.class},
                        new Object[]{},
                        new Object[]{SqlUtil.COL_CONTENT, R.id.content, EditText.class},
                        new Object[]{},
                        new AdapterViewFunction[]{
                                new FunctionDeleteItemFunction(),
                                new FunctionCommitAdderViewToDatabase(clearValues,SqlUtil.COL_CONTENT),
                                new FunctionHideAdderView(clearValues),
                                new FunctionShowAdderViewForAdd(),//3
                                new FunctionShowAdderViewForUpdate(),//4
                                new FunctionClearAdderView(clearValues)//5
                        },
                        new Object[]{
                                SqlUtil.COL_TABLE_NAME,mTable,
                                SqlUtil.COL_INNER_ID,mInnderId,
                        },
                        new int[]{DatabaseAdapterView.FUNCTION_POP_UP_CONTEXT_MENU},
                        new Object[]{"删除", new int[]{0},
                                "修改", new int[]{4}
                        },
                        new Object[]{
                                adderView.findViewById(R.id.positiveButton),new int[]{1},
                                adderView.findViewById(R.id.nagetiveButton),new int[]{2},
                                tagListContainer.findViewById(R.id.addButton),new int[]{3}
                        },
                        handler.e
                );
            }
        });

        //==Doing
        //==problem: this does not need an online-adder, the adder will be
        // invoked from others.
        // so adder is null.
        mThreadManager.asyncWaitThread("initSharedVars", "initReferenceList", new Runnable() {
            @Override
            public void run() {
                ArrayList clearValues=ShareableSingleViewAutoCollector.genPresetValuesBasedOnKey(
                        SqliteTableModelGetter.get(SqliteHelper.TABLE_REFERENCE),
                        new Object[]{ SqlUtil.COL_RELATION,""});
                View adderView=referenceListContainer.findViewById(R.id.adderViewContainer);
                sharedWrapperRunner(SqliteHelper.TABLE_REFERENCE,
                        referenceListContainer,
                        (ListView)referenceListContainer.findViewById(R.id.contentAdapterView),
                        R.layout.show_list_detail_reference_item,
                        new Object[]{
                                SqlUtil.COL_RELATION, R.id.briefTextView,TextView.class},
                        new Object[]{
                                SqlUtil.COL_RELATION,new ValueTypeTransfer<String,String>() {
                            @Override
                            public String transferPositive(String s) {
                                throw new UnsupportedTransferation();
                            }

                            @Override
                            public String transferNagetive(String s) {
                                if(s==null)
                                    return "链接";
                                else
                                    return s;
//                                LinearLayout tagsContainer= (LinearLayout) referenceListContainer.findViewById(R.id.tagsContainer);
//                                TextView timeTextView= (TextView) referenceListContainer.findViewById(R.id.datetimeTextView);
//                                Cursor cursor=mSqliteHelper.getCursorById()
                            }
                        }
                        },
                        new Object[]{
                                SqlUtil.COL_RELATION,R.id.briefEditText,EditText.class
                        },
                        new Object[]{},
                        new AdapterViewFunction[]{
                                new FunctionDeleteItemFunction(),//0
                                new AdapterViewFunction() {
                                    @Override
                                    public void apply(final DatabaseAdapterView wrapper, int position) {
                                        Intent intent= ActivityUtil.getSelectReferenceActivityIntent(ViewSubitemDetailActivity.this,mTable,mInnderId);
                                        mDelayedActions.add("onAcitivityResultSelectReference", new Runnable() {
                                            @Override
                                            public void run() {
                                                wrapper.requery();
                                                wrapper.refresh();
                                            }
                                        });
                                        startActivityForResult(intent, REQUEST_SELECT_REFERENCE);
                                    }
                                },//click to into that detail.  //`
                                new FunctionShowAdderViewForUpdate(),//2
                                new FunctionUpdateAdderViewToDatabase(clearValues,SqlUtil.COL_RELATION),//3
                                new FunctionHideAdderView(clearValues),//4
                                new FunctionViewDetailThroughReference(mTable,mInnderId),//5
                        },
                        new Object[]{
                                SqlUtil.COL_TABLE_NAME,mTable,
                                SqlUtil.COL_INNER_ID,mInnderId,
                        },
                        new int[]{5},
                        new Object[]{"删除", new int[]{0},
                                "修改", new int[]{2}
                        },
                        new Object[]{
                                adderView.findViewById(R.id.positiveButton),new int[]{3},
                                adderView.findViewById(R.id.nagetiveButton),new int[]{4},
                                referenceListContainer.findViewById(R.id.addButton),new int[]{1}
                        },
                        handler.e
                );
            }
        });

        //==Doing --> Done
        mThreadManager.asyncWaitThread("initSharedVars", "initAlarmList", new Runnable() {
            @Override
            public void run() {
                ArrayList clearValues=ShareableSingleViewAutoCollector.genPresetValuesBasedOnKey(
                        SqliteTableModelGetter.get(SqliteHelper.TABLE_ALARM),
                        new Object[]{ SqlUtil.COL_CAUSING,""});
                View adderView=alarmListContainer.findViewById(R.id.adderViewContainer);

                sharedWrapperRunner(SqliteHelper.TABLE_ALARM,
                        alarmListContainer,
                        (ListView)alarmListContainer.findViewById(R.id.contentAdapterView),
                        R.layout.show_list_detail_alarm_item,
                        new Object[]{
                                SqlUtil.COL_CAUSING, R.id.causingTextView,TextView.class,
                                SqlUtil.COL_REPEAT_TYPE,R.id.reasonTextView,TextView.class,
                                SqlUtil.COL_START_TIME,R.id.startTimeTextView,TextView.class,
                                SqlUtil.COL_END_TIME,R.id.endTimeTextView,TextView.class,
                                SqlUtil.COL_CREATED_TIME,R.id.createdTimeTextView, TextView.class
                        },
                        new Object[]{
                                SqlUtil.COL_START_TIME,new TransferStringToLongDateTime(),//update through cursor
                                SqlUtil.COL_END_TIME,new TransferStringToLongDateTime(){
                            @Override
                            public String transferNagetive(Long aLong) {
                                if(aLong==null)
                                    return "------ -- -- ";
                                else
                                    return super.transferNagetive(aLong);
                            }
                        },//update through cursor
                                SqlUtil.COL_CREATED_TIME,new TransferStringToLongDateTime(),//update through cursor
                        },
                        new Object[]{
                                SqlUtil.COL_REPEAT_TYPE,R.id.repeatTypeSpinner,Spinner.class,
                                SqlUtil.COL_START_TIME,R.id.startTimeTextView, DatePickerDialogWithTextView.class,
                                SqlUtil.COL_END_TIME,R.id.endTimeTextView, DatePickerDialogWithTextView.class,
                                SqlUtil.COL_CAUSING, R.id.causingEditText, EditText.class,
                        },
                        new Object[]{
                                SqlUtil.COL_START_TIME,new TransferCalendarToLong(),
                                SqlUtil.COL_END_TIME,new TransferCalendarToLong()
                        },
                        new AdapterViewFunction[]{
                                new FunctionDeleteItemFunction(),
                                new FunctionCommitAdderViewToDatabase(clearValues,
                                        SqlUtil.COL_CAUSING,
                                        SqlUtil.COL_START_TIME,
                                        SqlUtil.COL_END_TIME,
                                        SqlUtil.COL_REPEAT_TYPE
                                        ),//this need change
                                new FunctionHideAdderView(clearValues),
                                new FunctionShowAdderViewForAdd(),//3
                                new FunctionShowAdderViewForUpdate(),//4
                                new FunctionClearAdderView(clearValues)//5
                        },
                        new Object[]{
                                SqlUtil.COL_TABLE_NAME,mTable,
                                SqlUtil.COL_INNER_ID,mInnderId,
                                SqlUtil.COL_CREATED_TIME,new ValueGetterCurrentDateTimeLong(),
                        },
                        new int[]{DatabaseAdapterView.FUNCTION_POP_UP_CONTEXT_MENU},
                        new Object[]{"删除", new int[]{0},
                                "修改", new int[]{4}
                        },
                        new Object[]{
                                adderView.findViewById(R.id.positiveButton),new int[]{1},
                                adderView.findViewById(R.id.nagetiveButton),new int[]{2},
                                alarmListContainer.findViewById(R.id.addButton),new int[]{3}
                        },
                        handler.e
                );
            }
        });
        mThreadManager.startThread("delayedUiCorrections", new Runnable() {
            @Override
            public void run() {
                Util.sleepIgnoreInterruption(100);
            }
        });
        mThreadManager.asyncWaitThreadOnUiThread("delayedUiCorrections","doUiCorrections", new Runnable() {
            @Override
            public void run() {
                for(int listType:new int[]{0,1,3}) {
                    ViewUtil.setListViewHeightRange(
                            (ListView) mAdapterViews[listType],
                            mVarMan.get(VAR_HEIGHT_PARAM[listType][0],131),
                            mVarMan.get(VAR_HEIGHT_PARAM[listType][1],1),
                            mVarMan.get(VAR_HEIGHT_PARAM[listType][2],-1));
                }
                ViewUtil.setViewInScope(mModelView);
            }
        });
    }
    private <E extends AdapterView> void sharedWrapperRunner(String table,
                                                             View container,
                                                             E adapterView,
                                                             int listItem,
                                                             Object[] itemGettersOnKey,
                                                             Object[] itemTransfersOnKey,
                                                             Object[] adderGettersOnKey,
                                                             Object[] adderTransfersOnKey,
                                                             AdapterViewFunction[] functions,
                                                             Object[] presetValuesOnKey,
                                                             int[] clickFunctions,
                                                             Object[] contextMenuFunctions,
                                                             Object[] buttonFunctions,
                                                             DatabaseAdapterView.OnErrorConditionHandler handler
                                                             )
    {
        Looper.prepare();
        final Cursor reviewCursor = mSqliteHelper.getCursorByRefId(table,mTable, mInnderId);
        final View adderView=container.findViewById(R.id.adderViewContainer);
        final DatabaseAdapterView<E> wrapper = getWrapper(
                table,
                reviewCursor,
                adapterView,
                adderView,
                listItem,
                ViewGetter.getViewGettersBaseOnKey(
                        SqliteTableModelGetter.get(table),itemGettersOnKey),
                ShareableSingleViewAutoCollector.genTransfersBasedOnKey(
                        SqliteTableModelGetter.get(table),itemTransfersOnKey
                ),
                ViewGetter.getViewGettersBaseOnKey(
                        SqliteTableModelGetter.get(table),adderGettersOnKey),
                ShareableSingleViewAutoCollector.genTransfersBasedOnKey(
                        SqliteTableModelGetter.get(table),adderTransfersOnKey
                ),
                functions,
                presetValuesOnKey,
                handler,
                clickFunctions,
               contextMenuFunctions,
                buttonFunctions
        );
        final Looper looper =Looper.myLooper();
        mDelayedActions.add("onDestroy", new Runnable() {
            @Override
            public void run() {
                looper.quit();
                wrapper.closeCursorIfNeeded();
                Util.logi("Review Looper Exit");
            }
        });
        Looper.loop();
    }

    private boolean checkCursorIsOnlySaySomethingAndBlog(Cursor cursor)
    {
        Util.logi("count:"+cursor.getCount());
        cursor.moveToFirst();
        Util.logi("mTable:"+mTable);
        Util.logi("mainTag:"+SqlUtil.getCursorValue(cursor,SqlUtil.COL_MAIN_TAG,String.class));
        if(cursor.getCount()==1 && cursor.moveToFirst() && SqliteHelper.TABLE_GENERAL_RECORDS.equals(mTable) &&
                (SqlUtil.TAG_SAY_SOMETHING.equals(SqlUtil.getCursorValue(cursor,SqlUtil.COL_MAIN_TAG,String.class))||
                    SqlUtil.TAG_BLOG.equals(SqlUtil.getCursorValue(cursor,SqlUtil.COL_MAIN_TAG,String.class)))
                )
        {
            return true;
        }
        else
            return false;
    }


    private void initIntentArguments()
    {
        final Intent intent=getIntent();
        if(intent!=null) {
            mTable = intent.getStringExtra(ARG_TABLE_NAME);
            mInnderId = intent.getLongExtra(ARG_INNER_ID, -1);//data is what is given.we are manipulating at the same database
        }else{
            showErrorPage("没有指定参数，无法查看详细信息");
        }
    }

    private void showErrorPage(String message)
    {

    }

    @Deprecated
    protected void onCreateDeprecated(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //=====helper fields
        mSqliteHelper=new SqliteHelper(this);
        mActivityPref=getPreferences(MODE_PRIVATE);
        mActivityPrefEditor=mActivityPref.edit();
        for(int i=0;i<mSetItemsValue.length;i++)
            mSetItemsValue[i]=mActivityPref.getInt(mSetItemKeys[i],(i==0||i==2)?1:-1);
        mVarMan=new StateVariableManager(mActivityPref);
        mVarMan.load();
//        mVarMan.remove("alarmExpaned");
//        mVarMan.remove("reviewExpaned");

        //====init all fileds that can be obtained from content view
        setContentView(R.layout.show_list_detail);
        mModelContainer= (LinearLayout) findViewById(R.id.modelInfoContainer);
        mControlContainer= (LinearLayout) findViewById(R.id.controlInfoContainer);
        mReviewListHelper=new ControlInfoListViewHelper<>(mControlContainer.findViewById(R.id.reviewListContainer));
        mAlarmListHelper=new ControlInfoListViewHelper<>(mControlContainer.findViewById(R.id.alarmListContainer));
        mTagsListHelper=new ControlInfoListViewHelper<>(mControlContainer.findViewById(R.id.tagListContainer));


        View reviewContainer=mControlContainer.findViewById(R.id.reviewListContainer);
        View tagListContainer=mControlContainer.findViewById(R.id.tagListContainer);
        View alarmContainer=mControlContainer.findViewById(R.id.alarmListContainer);

        //===retrieve field from intent
        initIntentArguments();


        //=====start to work:retreive data from cursor
        mModelCursor = mSqliteHelper.rawQuery(mSqlModel,mModelArgs);
        mAlarmListCursor=mSqliteHelper.rawQuery(mSqlAlarm,mControlArgs);
        mReviewListCursor=mSqliteHelper.rawQuery(mSqlReview,mControlArgs);
        mTagListCursor=mSqliteHelper.rawQuery(mSqlTag,mControlArgs);

        //====fill content and set adapter
        fillModelContainer();
        mReviewListAdapter=new ReviewListAdapter(this,mReviewListCursor,false);
        mAlarmListAdapter=new AlarmListAdapter(this,mAlarmListCursor,false);
        mTagListAdapter=new TagListAdapter(this,mTagListCursor,false);
        mReviewListHelper.mContentList.setAdapter(mReviewListAdapter);
        mAlarmListHelper.mContentList.setAdapter(mAlarmListAdapter);
        mTagsListHelper.mContentList.setAdapter(mTagListAdapter);


        //====set listeners
        mReviewListHelper.mAdderViewHelper.setShown(false);
        mAlarmListHelper.mAdderViewHelper.setShown(false);
        mTagsListHelper.mAdderViewHelper.setShown(false);
        mReviewListHelper.setListener(new ControlInfoListViewHelper.OnEventControlInfoListener() {
            @Override
            public void onClickAdd(ControlInfoListViewHelper helper) {
                helper.mAdderViewHelper.setShown(true);
            }
        });
        mAlarmListHelper.setListener(mReviewListHelper.mListener);
        mTagsListHelper.setListener(mReviewListHelper.mListener);

        mReviewListHelper.mAdderViewHelper.setListener(new PositiveNagetiveViewHelper.OnClickPositiveNagetiveListener() {
            @Override
            public void onClickPositive(PositiveNagetiveViewHelper helper) {
                ContentValues contentValues=new ContentValues();
                contentValues.put(SqlUtil.COL_TABLE_NAME,mTable);
                contentValues.put(SqlUtil.COL_INNER_ID,mInnderId);
                EditText contentView= (EditText) helper.mCustomViewContainer.findViewById(R.id.content);
                contentValues.put(SqlUtil.COL_CONTENT, contentView.getText().toString());

                long id=mSqliteHelper.insert(SqliteHelper.TABLE_REVIEW,null,contentValues);
                //generate create time table,id,...
                if(id==-1)
                {
                    Toast.makeText(ViewSubitemDetailActivity.this,"Insert Tag failed",Toast.LENGTH_LONG).show();
                }else{
                    mSqliteHelper.insertCreatedTime(SqliteHelper.TABLE_REVIEW,(int)id,null);
                    mReviewListCursor.requery();
                    //notify changed
                    Util.logi("cursor count:"+mReviewListCursor.getCount());
                    mReviewListAdapter.notifyDataSetChanged();
                    updateListHeight(0);
                }
                contentView.setText("");
                helper.setShown(false);
            }

            @Override
            public void onClickNagetive(PositiveNagetiveViewHelper helper) {
                helper.setShown(false);
            }
        });
        mAlarmListHelper.mAdderViewHelper.setListener(new PositiveNagetiveViewHelper.OnClickPositiveNagetiveListener() {
            @Override
            public void onClickPositive(PositiveNagetiveViewHelper helper) {
                Spinner repeatSpinner= (Spinner) helper.findViewById(R.id.repeatTypeSpinner);
                final int repeatIndex=repeatSpinner.getSelectedItemPosition();
                if(repeatIndex==0)
                {
                    Toast.makeText(ViewSubitemDetailActivity.this, R.string.pleaseSelectRepeatType, Toast.LENGTH_SHORT).show();
                    return;
                }
                final EditText cuasing= (EditText) helper.findViewById(R.id.causingEditText);
                if(cuasing.getText().length()==0)
                {
                    Toast.makeText(ViewSubitemDetailActivity.this, R.string.pleaseInputCuasing, Toast.LENGTH_SHORT).show();
                    return;
                }
                DateTimePickerDialogWithTextView start= (DateTimePickerDialogWithTextView) helper.findViewById(R.id.startTimeTextView);
                DateTimePickerDialogWithTextView end= (DateTimePickerDialogWithTextView) helper.findViewById(R.id.endTimeTextView);


                ContentValues values=new ContentValues();
                values.put(SqlUtil.COL_TABLE_NAME,mTable);
                values.put(SqlUtil.COL_INNER_ID,mInnderId);


                final long startLong=start.getTime().getTimeInMillis(),endLong=end.getTime().getTimeInMillis();
                final String causingText=cuasing.getText().toString();
                values.put(SqlUtil.COL_START_TIME,startLong);
                values.put(SqlUtil.COL_REPEAT_TYPE,repeatIndex-1);
                values.put(SqlUtil.COL_CAUSING,causingText);

                if(repeatIndex==1)
                {
                    Toast.makeText(ViewSubitemDetailActivity.this, "重复类型为单次,结束时间将被忽略", Toast.LENGTH_SHORT).show();
                }else  if(start.getTime().compareTo(end.getTime())>0 )
                {
                    Toast.makeText(ViewSubitemDetailActivity.this, R.string.pleaseCorrectTime, Toast.LENGTH_SHORT).show();
                    return;
                }else{
                    values.put(SqlUtil.COL_END_TIME,endLong);
                }


                final long id=mSqliteHelper.insert(SqliteHelper.TABLE_ALARM,null,values);
                if(id==-1)
                {
                    Toast.makeText(ViewSubitemDetailActivity.this,"无法插入数据库",Toast.LENGTH_LONG).show();
                }else{
                    //set alarm
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
//                            Looper.prepare();
                            Util.logi("setting alarm");
//                            Toast.makeText(ViewSubitemDetailActivity.this,"正在设置闹钟",Toast.LENGTH_SHORT).show();
                            Intent notifyIntent=getAlarmBroadcastIntent(ViewSubitemDetailActivity.this,0);
                            notifyIntent.putExtra("TABLE",mTable);
                            notifyIntent.putExtra("INNERID",mInnderId);
                            notifyIntent.putExtra("CAUSING",causingText);

                            PendingIntent alarmIntent=PendingIntent.getBroadcast(ViewSubitemDetailActivity.this,
                                    (int) id,
                                    notifyIntent,PendingIntent.FLAG_UPDATE_CURRENT);
                            AlarmManager alarmManager= (AlarmManager) getSystemService(ALARM_SERVICE);
                            if(repeatIndex==1)//repeat once do not need to cancel
                            {
                                alarmManager.set(AlarmManager.RTC_WAKEUP,startLong,alarmIntent);
                            }else{
                                long interval=new long[]{
                                        CalendarUtil.ONE_DAY,
                                        CalendarUtil.ONE_DAY*7,
                                        CalendarUtil.ONE_DAY*30,
                                        CalendarUtil.ONE_DAY*365
                                }[repeatIndex-2];
                                alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP,startLong,interval,alarmIntent);
                                Intent cancelIntent=getAlarmBroadcastIntent(ViewSubitemDetailActivity.this,1);
                                cancelIntent.putExtra("REQUEST_CODE",(int)endLong);
                                alarmManager.set(AlarmManager.RTC_WAKEUP,endLong,
                                        PendingIntent.getBroadcast(ViewSubitemDetailActivity.this,
                                                (int)endLong,
                                                cancelIntent,
                                                PendingIntent.FLAG_UPDATE_CURRENT
                                        )
                                );
                            }
//                            Toast.makeText(ViewSubitemDetailActivity.this,"设置闹钟成功！",Toast.LENGTH_SHORT).show();
                            Util.logi("setting alarm end");
                        }
                    }).start();

                    mSqliteHelper.insertCreatedTime(SqliteHelper.TABLE_ALARM,(int)id,null);
                    mAlarmListCursor.requery();
//                    Util.logi("requery alarm count:"+mAlarmListCursor.getCount());
                    mAlarmListAdapter.notifyDataSetChanged();
                    repeatSpinner.setSelection(1);
                    cuasing.setText("");
                    updateListHeight(2);
                }


                helper.setShown(false);
            }

            @Override
            public void onClickNagetive(PositiveNagetiveViewHelper helper) {
                helper.setShown(false);
            }
        });
        mTagsListHelper.mAdderViewHelper.setListener(new PositiveNagetiveViewHelper.OnClickPositiveNagetiveListener() {
            @Override
            public void onClickPositive(PositiveNagetiveViewHelper helper) {
                ContentValues contentValues=new ContentValues();
                contentValues.put(SqlUtil.COL_TABLE_NAME,mTable);
                contentValues.put(SqlUtil.COL_INNER_ID,mInnderId);
                EditText contentView= (EditText) helper.mCustomViewContainer.findViewById(R.id.content);
                contentValues.put(SqlUtil.COL_CONTENT, contentView.getText().toString());

                long id=mSqliteHelper.insert(SqliteHelper.TABLE_TAG,null,contentValues);
                //generate create time table,id,...
                if(id==-1)
                {
                    Toast.makeText(ViewSubitemDetailActivity.this,"Insert review failed",Toast.LENGTH_LONG).show();
                }else{
                    mSqliteHelper.insertCreatedTime(SqliteHelper.TABLE_TAG,(int)id,null);
                    mTagListCursor.requery();
                    //notify changed
                    mTagListAdapter.notifyDataSetChanged();
                }
                contentView.setText("");
                helper.setShown(false);
            }

            @Override
            public void onClickNagetive(PositiveNagetiveViewHelper helper) {
                helper.setShown(false);
            }
        });

        //===set tag can be added,removed
        registerForContextMenu(mTagsListHelper.mContentList);
        mTagsListHelper.mContentList.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
            @Override
            public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
                final AdapterView.AdapterContextMenuInfo info= (AdapterView.AdapterContextMenuInfo) menuInfo;
                getMenuInflater().inflate(R.menu.context_menu_for_tags_of_model,menu);
                menu.findItem(R.id.deleteTagForModel).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        int oldPosition=mTagListCursor.getPosition();
                        mTagListCursor.moveToPosition(info.position);
                        mSqliteHelper.deleteItemInTable(SqliteHelper.TABLE_TAG,mTagListCursor);
                        mTagListCursor.moveToPosition(oldPosition);
                        mTagListCursor.requery();
                        mTagListAdapter.notifyDataSetChanged();
                        return true;
                    }
                });
            }
        });
        mTagsListHelper.mContentList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                openContextMenu(mTagsListHelper.mContentList);
                openContextMenu(view);
//                mTagListCursor.move(position);
            }
        });


        //=======correct the views position&size
        ViewUtil.setViewInScope(mModelContainer);
        ((Spinner)mAlarmListHelper.mAdderViewHelper.findViewById(R.id.repeatTypeSpinner)).setSelection(1);
        updateListHeight(0);
        updateListHeight(2);
        mReviewListHelper.mCollasperHelper.setExpand(mVarMan.get("reviewExpand",false));
        mAlarmListHelper.mCollasperHelper.setExpand(mVarMan.get("alarmExpand",true));


    }



    /**
     * refactored structure
     *
     * @param table
     * @param cursor
     * @param contentView
     * @param adderView
     * @param itemRes
     * @param itemViewGetters
     * @param adderViewGetter
     * @param functions
     * @param presetValuesOnKey
     * @param handler
     * @param itemClickFunc
     * @param contextMenuFuncMaps  has the format <String,int>
     * @param viewFuncMaps has the format <View,int>
     * @param <V>
     * @return
     */
    public <V extends AdapterView> DatabaseAdapterView<V> getWrapper(
                    @SqliteHelper.TableName String table,
                    Cursor cursor,
                    @NonNull V contentView,
                    @Nullable View adderView,
                    @LayoutRes int itemRes,
                    ArrayList<ViewGetter> itemViewGetters,
                    ArrayList<ShareableViewValueTransfer> itemTrasfers,
                    ArrayList<ViewGetter> adderViewGetter,
                    ArrayList<ShareableViewValueTransfer> adderTransfers,
                    AdapterViewFunction<V>[] functions,
                    Object[] presetValuesOnKey,
                    DatabaseAdapterView.OnErrorConditionHandler<V> handler,
                    int[] itemClickFunc,
                    Object[]  contextMenuFuncMaps,//has the format: <String,int[]>
                    Object[]  viewFuncMaps//has the format:<View,int[]>
                    )
    {
        AdapterViewAutoCompletor<V> completor=new AdapterViewAutoCompletor<>(this,cursor,
                table,contentView,
                ViewGenerator.ViewGeneratorByInflateRes(getLayoutInflater(),itemRes),
                SqliteTableModelGetter.get(table),
                itemViewGetters,
                itemTrasfers
        );

        DatabaseAdapterView<V> wrapper=new DatabaseAdapterView<>(this,mSqliteHelper,contentView,
                completor,//completor
                adderView,
                adderViewGetter
        );
        wrapper.setAdderPresetValuesBasedOnKey(presetValuesOnKey);
        wrapper.addFunctions(functions);
        wrapper.setErrorHandler(handler);
        wrapper.setClickFunction(itemClickFunc);
        wrapper.setAdderTransfers(adderTransfers);
        for(int i=0;i<contextMenuFuncMaps.length;i+=2)
            wrapper.addContextMenu((String)contextMenuFuncMaps[i], (int[]) contextMenuFuncMaps[i+1]);
        for(int i=0;i<viewFuncMaps.length;i+=2)
            wrapper.setButtonFunction((View)viewFuncMaps[i], (int[]) viewFuncMaps[i+1]);
        return wrapper;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.view_subitem,menu);
        return true;
    }

    private class ListHeightDialogWrapper{
        public AlertDialog mDialog;
        public ListHeightDialogWrapper()
        {
            View v=getLayoutInflater().inflate(R.layout.view_subitem_detail_set_list_range_height,null,false);
            ArrayList<ViewInfo> infos=new ArrayList<ViewInfo>(){{
                add(new ViewInfo("listType",Integer.class));
                add(new ViewInfo("min",Integer.class));
                add(new ViewInfo("max",Integer.class));
                add(new ViewInfo("unit",Integer.class));
            }};
            final SingleViewAutoCollector collector=new SingleViewAutoCollector(v,infos,
                    ViewGetter.getViewGettersBaseOnKey(
                            infos,
                    "unit",R.id.unitHeightEditText,EditText.class,
                    "min",R.id.minItemEditText,EditText.class,
                    "max",R.id.maxItemEditText,EditText.class,
                    "listType",R.id.listSpinner,Spinner.class
            ));
            collector.addGetCheckersBasedOnKey(
                    "listType", new CheckerGetBase(){
                        @Override
                        public boolean checkSpinner(Spinner view) {
                            return view.getSelectedItemPosition()!=2;
                        }
                    }
            );
            collector.setCheckGetFailedActionBasedOnKey("listType",ShareableSingleViewAutoCollector.ACTION_ABORT);
            int lastChoosed=mVarMan.get(VAR_LAST_CHOOSE_LIST,0);
            final ArrayList presetValue = collector.genPresetValuesBasedOnKey(infos,
                    "listType", lastChoosed,
                    "unit", mVarMan.get(VAR_HEIGHT_PARAM[lastChoosed][0], getResources().getDimensionPixelSize(R.dimen.detailReviewSize)),
                    "min", mVarMan.get(VAR_HEIGHT_PARAM[lastChoosed][1], 1),
                    "max", mVarMan.get(VAR_HEIGHT_PARAM[lastChoosed][2], -1)
            );
            collector.fillValue(presetValue);
            Util.cast(v.findViewById(R.id.listSpinner),Spinner.class).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    if(position==2)
                        Toast.makeText(ViewSubitemDetailActivity.this,"暂不支持设置标签",Toast.LENGTH_SHORT).show();
                    else{
                        collector.fillValueBasedOnKey(
                                "unit", mVarMan.get(VAR_HEIGHT_PARAM[position][0], getResources().getDimensionPixelSize(R.dimen.detailReviewSize)),
                                "min", mVarMan.get(VAR_HEIGHT_PARAM[position][1], 1),
                                "max", mVarMan.get(VAR_HEIGHT_PARAM[position][2], -1)
                        );
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

            mDialog=new AlertDialog.Builder(ViewSubitemDetailActivity.this).setView(v)
                    .setNegativeButton("取消",null)
                    .setPositiveButton("确定",null)
                    .create();
            mDialog.setOnShowListener(new DialogInterface.OnShowListener() {
                @Override
                public void onShow(DialogInterface dialog) {
                    mDialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            try{
                                ArrayList list=collector.collectValue(ArrayList.class);
                                int listType=(Integer)list.get(0);
                                mVarMan.set(VAR_LAST_CHOOSE_LIST,listType);
                                mVarMan.set(VAR_HEIGHT_PARAM[listType][0],list.get(3));
                                mVarMan.set(VAR_HEIGHT_PARAM[listType][1],list.get(1));
                                mVarMan.set(VAR_HEIGHT_PARAM[listType][2],list.get(2));
                                ViewUtil.setListViewHeightRange((ListView)mAdapterViews[listType],(Integer)list.get(3),(Integer)list.get(1),(Integer)list.get(2));
                                mDialog.hide();
                            }catch (ShareableSingleViewAutoCollector.ActionAbort e){
                                Toast.makeText(ViewSubitemDetailActivity.this,"输入有误（暂不支持设置 标签列表)",Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                    mDialog.getButton(DialogInterface.BUTTON_NEGATIVE).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mDialog.hide();
                        }
                    });
                }
            });
        }

        public AlertDialog getDialog()
        {
            return mDialog;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {

            case R.id.setListHeightRange:
                if(mDialogWrapper==null)
                    mDialogWrapper=new ListHeightDialogWrapper();
                mDialogWrapper.getDialog().show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void updateShownItemMenu(int i)
    {
        String formatter="设置%s显示%s的项数:%s";
        String finalString=String.format(formatter, i<=1?"评论":
                                                        i<=3?"提醒":"",
                                                    i%2==0?"最小":"最大",
                                                    ""+mSetItemsValue[i]
        );
        mSetItemsMenu[i].setTitle(finalString);
    }
    public void updateListHeight(int i)
    {
        int index=(i<=1?0:2);
        Adapter adapter=index==0?mReviewListAdapter:mAlarmListAdapter;
        AdapterView view=index==0?mReviewListHelper.mContentList:mAlarmListHelper.mContentList;
        int unitHeight=index==0?R.dimen.detailReviewSize:R.dimen.detailAlarmSize;

        int count=(mSetItemsValue[index+1]==-1||mSetItemsValue[index+1]>adapter.getCount())?adapter.getCount():mSetItemsValue[index+1];
        count=Math.max(count,mSetItemsValue[index]);
        view.getLayoutParams().height=count*getResources().getDimensionPixelSize(unitHeight);
        view.requestLayout();
    }

    /**
     *
     * @param context
     * @param type =0 send a notify
     *             =1  cancel an alarm
     * @return
     */
    public static Intent getAlarmBroadcastIntent(Context context,int type)
    {
        Intent intent=new Intent(context, AlarmNotifyBroadcastReceiver.class);
        intent.putExtra("TYPE",type);
        return intent;
    }


    @Override
    protected void onDestroy() {

        mDelayedActions.run("onDestroy");

        //====save state
        for(int i=0;i<mSwitchers.length;i++)
        {
            mVarMan.set(VAR_EXPANDS[i],mSwitchers[i].isExpand());
        }

        mVarMan.save();

//        for(int i=0;i<mSetItemsValue.length;i++)
//            mActivityPrefEditor.putInt(mSetItemKeys[i],mSetItemsValue[i]);
//        mActivityPrefEditor.commit();


        super.onDestroy();
    }

    protected void fillModelContainer()
    {
        if(mModelCursor.getCount()!=1)//有数据错误
        {
            TextView errorView=new TextView(this);
            errorView.setText(R.string.dataError);
            mModelContainer.addView(errorView);
            return;
        }
        mModelCursor.moveToFirst();
        if(SqliteHelper.TABLE_GENERAL_RECORDS.equals(mTable))
        {
            String mainTag=mModelCursor.getString(mModelCursor.getColumnIndex(SqlUtil.COL_MAIN_TAG));
            if(SqlUtil.TAG_SAY_SOMETHING.equals(mainTag))
            {
                View view=getLayoutInflater().inflate(R.layout.show_list_detail_model_say_something,mModelContainer,true);
                TextView time= (TextView) view.findViewById(R.id.createdTimeTextView);
                TextView content= (TextView) view.findViewById(R.id.detailTextView);

                time.setText(StringFormatters.formatDateTimeWithCalendar(
                        CalendarUtil.timeInMillisToCalendar(mModelCursor.getLong(mModelCursor.getColumnIndex(SqlUtil.COL_CREATED_TIME)))));
                content.setText(mModelCursor.getString(mModelCursor.getColumnIndex(SqlUtil.COL_DETAIL)));
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshContentView();
    }

    @HelperMethods
    protected void refreshContentView()
    {
        //all views here are created
        //getByGetter view here, fill information later


    }

    //======inner class area
    public class AlarmListAdapter extends CursorAdapter{

        public AlarmListAdapter(Context context, Cursor c, boolean autoRequery) {
            super(context, c, autoRequery);
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            return ViewSubitemDetailActivity.this.getLayoutInflater().inflate(R.layout.show_list_detail_alarm_item,parent,false);
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            TextView startTime= (TextView) view.findViewById(R.id.startTimeTextView);
            TextView endTime= (TextView) view.findViewById(R.id.endTimeTextView);
            TextView repeatType= (TextView) view.findViewById(0);
            TextView causing= (TextView) view.findViewById(R.id.causingTextView);
            TextView createdTime= (TextView) view.findViewById(R.id.createdTimeTextView);

            startTime.setText(
                    StringFormatters.formatDateTimeWithCalendar(
                            CalendarUtil.timeInMillisToCalendar(cursor.getLong(cursor.getColumnIndex(SqlUtil.COL_START_TIME)))));
            if(!cursor.isNull(cursor.getColumnIndex(SqlUtil.COL_END_TIME)))
            {
                endTime.setText(
                        StringFormatters.formatDateTimeWithCalendar(
                                CalendarUtil.timeInMillisToCalendar(
                                        cursor.getLong(cursor.getColumnIndex(SqlUtil.COL_END_TIME)))));
            }else{
                endTime.setText("---- -- --");
            }

            int repeatTypeIndex=cursor.getInt(cursor.getColumnIndex(SqlUtil.COL_REPEAT_TYPE));
            repeatType.setText(getResources().getStringArray(R.array.alarmRepeatTypes)[repeatTypeIndex+1]);

            causing.setText(cursor.getString(cursor.getColumnIndex(SqlUtil.COL_CAUSING)));

            long createdTimeData=mSqliteHelper.getCreatedTime(SqliteHelper.TABLE_ALARM,cursor.getInt(cursor.getColumnIndex(SqlUtil.COL_ID)));
            createdTime.setText(
                    StringFormatters.formatDateTimeWithCalendar(
                            CalendarUtil.timeInMillisToCalendar(createdTimeData)));
        }
    };

    public class ReviewListAdapter extends CursorAdapter{

        public ReviewListAdapter(Context context, Cursor c, boolean autoRequery) {
            super(context, c, autoRequery);
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            return ViewSubitemDetailActivity.this.getLayoutInflater().inflate(R.layout.show_list_detail_review_item,parent,false);
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            TextView createdTime= (TextView) view.findViewById(R.id.createdTimeTextView);
            TextView contentView= (TextView) view.findViewById(R.id.content);

            Long time=mSqliteHelper.getCreatedTime(SqliteHelper.TABLE_REVIEW,cursor.getInt(cursor.getColumnIndex(SqlUtil.COL_ID)));
            if(time!=null)
                createdTime.setText(StringFormatters.formatDateTimeWithCalendar(CalendarUtil.timeInMillisToCalendar(time)));
            else
                createdTime.setText(R.string.lostTime);
            contentView.setText(cursor.getString(cursor.getColumnIndex(SqlUtil.COL_CONTENT)));

        }

        @Override
        public void notifyDataSetChanged() {
            super.notifyDataSetChanged();
        }

    };

    public class TagListAdapter extends CursorAdapter{

        public TagListAdapter(Context context, Cursor c, boolean autoRequery) {
            super(context, c, autoRequery);
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            return getLayoutInflater().inflate(R.layout.show_list_detail_tag_item,parent,false);
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            TextView tagView= (TextView) view.findViewById(R.id.content);
            tagView.setText(cursor.getString(cursor.getColumnIndex(SqlUtil.COL_CONTENT)));
        }
    };
}
