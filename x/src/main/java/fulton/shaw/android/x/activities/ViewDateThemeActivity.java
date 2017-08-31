package fulton.shaw.android.x.activities;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;

import fulton.shaw.android.x.R;
import fulton.shaw.android.x.models.SqliteTableModelGetter;
import fulton.shaw.android.x.public_interfaces.SqliteHelper;
import fulton.shaw.android.x.viewgetter.database_auto.ModelViewAutoCompletor;
import fulton.shaw.android.x.viewgetter.database_auto.SingleViewAutoCollector;
import fulton.shaw.android.x.viewgetter.database_auto.ViewGenerator;
import fulton.shaw.android.x.viewgetter.database_auto.ViewGetter;
import fulton.util.android.interfaces.ViewInfo;
import fulton.shaw.android.x.viewgetter.transferview.FixedViewValueTransfer;
import fulton.util.android.interfaces.typetransfers.TransferCalendarToSqliteTime;
import fulton.util.android.interfaces.typetransfers.ValueTypeTransfer;
import fulton.shaw.android.x.views.TimePickerDialogWithTextView;
import fulton.util.android.utils.SqlUtil;
import fulton.util.android.utils.Util;

/**
 *  will support date later
 */
public class ViewDateThemeActivity extends AppCompatActivity {

    protected SqliteHelper mSqliteHelper;
    protected Cursor mCursor;
    protected Calendar mDate;


    protected ModelViewAutoCompletor mModelViewAutoCompletor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //====helper fields
        mSqliteHelper=new SqliteHelper(this);
        mDate=Calendar.getInstance();

        setContentView(R.layout.activity_view_date_theme);



        final ArrayList<ViewInfo> incomeInfo=SqliteTableModelGetter.get(SqliteHelper.TABLE_INCOME);
        final ArrayList<ViewInfo> dateRecordInfo=SqliteTableModelGetter.get(SqliteHelper.TABLE_DATE_RECORD);

//        mSqliteHelper.recreate(SqliteHelper.TABLE_INCOME);

        mCursor=mSqliteHelper.ensureDateRecordExists(mDate);
        mCursor.moveToFirst();
        ContentValues contentValues=new ContentValues();

        contentValues.put(SqlUtil.COL_INNER_ID,SqlUtil.getCursorId(mCursor));
        contentValues.put(SqlUtil.COL_RECORD,"30元");
        contentValues.put(SqlUtil.COL_REASON,"正常支出");
        contentValues.put(SqlUtil.COL_EVENT_TIME,"11:30:00");

        mSqliteHelper.insertWithOnConflict(SqliteHelper.TABLE_INCOME,null,contentValues, SQLiteDatabase.CONFLICT_IGNORE);
        final Cursor controlCursor=mSqliteHelper.rawQuery("Select * From "+SqliteHelper.TABLE_INCOME+" Where "+SqlUtil.COL_INNER_ID+"="+
                SqlUtil.getCursorValue(mCursor,SqlUtil.COL_ID,Integer.class),null);
            Util.logi(SqlUtil.dumpCursorValue(controlCursor));
        mModelViewAutoCompletor =new ModelViewAutoCompletor(
                mCursor, SqliteHelper.TABLE_DATE_RECORD, findViewById(R.id.rootView),
                dateRecordInfo,
                new ArrayList<ViewGetter>(){{
                    add(null);
                    add(ViewGetter.IdViewGetter(R.id.dateTextView, TextView.class));
                    add(null);//if it is null, not needed
                    add(ViewGetter.IdViewGetter(R.id.summaryTextVIew, TextView.class));
                    add(ViewGetter.IdViewGetter(R.id.locationTextView, TextView.class));
                    add(ViewGetter.IdViewGetter(R.id.weatherTextView, TextView.class));
                    add(ViewGetter.IdViewGetter(R.id.remarkTextView, TextView.class));
                    add(ViewGetter.IdViewGetter(R.id.levelRatingBar, RatingBar.class));
                }},
                new ArrayList<ModelViewAutoCompletor.ControlFillerInfo>(){{
                    add(new ModelViewAutoCompletor.ControlFillerInfo(ViewDateThemeActivity.this, controlCursor,
                            SqliteHelper.TABLE_INCOME, (AdapterView)findViewById(R.id.incomeList),
                            ViewGenerator.ViewGeneratorByInflateRes(getLayoutInflater(),R.layout.income_item),
                            incomeInfo,
                            new ArrayList<ViewGetter>(){{
                                add(null);
                                add(null);
                                add(null);
                                add(ViewGetter.IdViewGetter(R.id.timeTextView, TextView.class));
                                add(ViewGetter.IdViewGetter(R.id.recordTextView, TextView.class));
                                add(ViewGetter.IdViewGetter(R.id.reasonTextView, TextView.class));
                            }}
                    ));
                }}
        );
        //an adder view
        final View adderView=getLayoutInflater().inflate(R.layout.income_item_adder,null,false);
        final FixedViewValueTransfer<Spinner,Integer> spinnerTransfer=new FixedViewValueTransfer<Spinner, Integer>(
                (Spinner) adderView.findViewById(R.id.incomeTypeSpinner), new ValueTypeTransfer<Integer, Integer>() {
            @Override
            public Integer transferPositive(Integer integer) {
                return integer==0?-1:1;
            }

            @Override
            public Integer transferNagetive(Integer integer) {
                return integer==-1?0:1;
            }
        });
        //一定要填充所有给定的info信息吗？
        //答案是不一定.info信息可能是共用的。而mask信息可以自定义。
        findViewById(R.id.addIncomeButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /**
                 * 不一定是同一批Info，因为有的Info还加进来了。
                 */
                final SingleViewAutoCollector collector = mModelViewAutoCompletor.getAdapterViewFillers().get(0).getAdditionalCollector( adderView, new ArrayList<ViewGetter>() {{
                    add(null);
                    add(null);
                    add(null);
                    add(ViewGetter.IdViewGetter(R.id.eventTimeTextView, TimePickerDialogWithTextView.class));
                    add(ViewGetter.IdViewGetter(R.id.recordEditText, EditText.class));
                    add(ViewGetter.IdViewGetter(R.id.reasonEditText, EditText.class));
                }});
                collector.setTransfer(3,new TransferCalendarToSqliteTime());
                collector.setTransfer(4, new ValueTypeTransfer<String,Integer>() {
                    @Override
                    public Integer transferPositive(String s) {
                        int value=Integer.valueOf(s);
                        return spinnerTransfer.getValue()*value;
                    }

                    @Override
                    public String transferNagetive(Integer integer) {
                        return String.valueOf(Math.abs(integer));
                    }
                });
                final ArrayList arrayList = (ArrayList) mModelViewAutoCompletor.getAdapterViewFillers().get(0).collectValue(0, ArrayList.class, collector.getViewGetters());
                collector.fillValue(arrayList);
                if((int)arrayList.get(4)<0)
                    spinnerTransfer.getView().setSelection(0);
                else
                    spinnerTransfer.getView().setSelection(1);

                new AlertDialog.Builder(ViewDateThemeActivity.this).setView(adderView).show();

            }
        });

        mModelViewAutoCompletor.refreshModel();
        Util.logi(mModelViewAutoCompletor.collectModelValue(ContentValues.class));


//        final PackedObject<Integer> attachPos=new PackedObject<>(null);
//        final View pnButton=getLayoutInflater().inflate(R.layout.two_buttons,null,false);
//        final AttachableViewHelper pnHelper=new AttachableViewHelper(pnButton);
//        pnButton.findViewById(R.id.nagetiveButton).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if(pnHelper.isAttached())
//                    pnHelper.detach();
//            }
//        });
//        pnButton.findViewById(R.id.positiveButton).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if(pnHelper.isAttached())
//                {
//                    pnHelper.detach();
//                    if(attachPos.e!=null)
//                    {
//                        mModelViewAutoCompletor.deleteControlItem(0,attachPos.e,mSqliteHelper);
//                    }
//                }
//
//            }
//        });
//
//        mModelViewAutoCompletor.getAdapterView(0).setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                pnHelper.attach((ViewGroup) view);
//                attachPos.e=position;
//            }
//        });
//


    }
}
