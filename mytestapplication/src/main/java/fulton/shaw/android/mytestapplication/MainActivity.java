package fulton.shaw.android.mytestapplication;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.databinding.DataBindingUtil;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.HashSet;

import fulton.shaw.android.mytestapplication.databinding.TestDataBindingLayoutBinding;
import fulton.shaw.android.mytestapplication.models.User;
import fulton.shaw.android.tellmelibrary.sql.SqliteHelper;
import fulton.util.android.interfaces.iterators.Zip;
import fulton.util.android.interfaces.multiview_checkers.Checker;
import fulton.util.android.interfaces.multiview_checkers.ViewCheckerOfSingleView;
import fulton.util.android.interfaces.multiview_valuegetter.ViewValueGetterOfFixed;
import fulton.util.android.interfaces.multiview_valuegetter.ViewValueGetterOfSpinner;
import fulton.util.android.interfaces.multiview_valuegetter.ViewValueGetterOfTextView;
import fulton.util.android.interfaces.multiview_valuegetter.ViewValueGetterWithCheckBox;
import fulton.util.android.interfaces.tuples.Pair;
import fulton.util.android.interfaces.tuples.Single;
import fulton.util.android.interfaces.tuples.Triple;
import fulton.util.android.interfaces.valueproviders.ValueProvider;
import fulton.util.android.interfaces.valueproviders.ValueProviderBridgeShareable;
import fulton.util.android.interfaces.valueproviders.ValueProviderOfViews;
import fulton.util.android.utils.SqlUtil;
import fulton.util.android.utils.Util;
import fulton.util.android.aware.UiThreadAware;

public class MainActivity extends AppCompatActivity implements UiThreadAware{

    private Thread mUiThread;
    private ViewGroup mCustom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mUiThread=Thread.currentThread();
        setContentView(R.layout.activity_main);
        mCustom= (ViewGroup) findViewById(R.id.customView);
//        TestDataBindingLayoutBinding binding= DataBindingUtil.inflate(getLayoutInflater(),R.layout.test_data_binding_layout,null,false);
//        TestDataBindingLayoutBinding binding=DataBindingUtil.setContentView(this,R.layout.test_data_binding_layout);
//        final User user=new User("X","男");
//        binding.setUser(user);

    }

    protected void setCustomView(@LayoutRes int res)
    {
        View view=getLayoutInflater().inflate(res,mCustom,true);
    }
    protected View findCustomView(@IdRes int res)
    {
        return mCustom.findViewById(res);
    }

    public void onClickTestButton(View v)
    {
//        testDb(v);
//        testTimepickerDialog(v);
//        testDatabinding();
//        testProviders();
        testZip();
    }

    public void testZip()
    {
        String[] s=new String[]{"A","B"};
        Integer[] h=new Integer[]{1,2};
        for(Pair<String, Integer> a:new Zip<Pair<String,Integer>>(s,h))
        {
            Util.logi("pair of :"+a.first+","+a.second);
        }
        Triple<String,Integer,Integer>[] paired=Single.zip(new String[]{"X","Y"},new Integer[]{1,2,3,4},new Integer[]{10});
        for(Triple<String, Integer,Integer> i:paired)
        {
            Util.logi("pair of :"+i.first+","+i.second+","+i.third);
        }
    }

    public void testProviders()
    {
        setCustomView(R.layout.test_providers);
        String[] keys=new String[]{
                "name","option","note"
        };
        Single<View>[] views=new Single[]{
                new Single<EditText>((EditText) findViewById(R.id.nameEditText)),
                new Single<Spinner>((Spinner) findViewById(R.id.optionsSpinner)),
                new Single<TextView>((TextView)findViewById(R.id.noteEditText))
        };
        CheckBox checkBox=(CheckBox) findViewById(R.id.addNoteCheckBox);

        ViewValueGetterOfFixed[] getters=new ViewValueGetterOfFixed[]{
                new ViewValueGetterOfFixed(views[0],ViewValueGetterOfTextView.getInstance()),
                new ViewValueGetterOfFixed(views[1], ViewValueGetterOfSpinner.getInstance()),
                new ViewValueGetterOfFixed(views[2], new ViewValueGetterWithCheckBox(checkBox,ViewValueGetterOfTextView.getInstance()))
        };
        final Checker[] checkers=new Checker[]{
                  new Checker(views[0], new ViewCheckerOfSingleView<TextView>() {
                      @Override
                      public boolean check(TextView view) {
                          return view.length()>0 && view.length()<=10;
                      }
                  }),
                null,
                null
        };

        // mutable keys
        //  fixed keys

        final ValueProviderOfViews provider=new ValueProviderOfViews(getters,keys,null);

        findViewById(R.id.positiveButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!Checker.runCheckers(checkers))
                {
                    Toast.makeText(MainActivity.this,"输入有误，请重新检查（姓名不能为空，不能超过10个字符)",Toast.LENGTH_SHORT).show();
                    return;
                }

                Util.logi(provider);

                final ValueProvider valueProvider = ValueProvider.copyValuePositive(provider,
                        new ValueProviderBridgeShareable(new ContentValues()),
                        new Pair[]{
                                new Pair("name", null),
                                new Pair("option", null),
                                new Pair("note", null)
                        }
                );
                Util.logi("copied:");
                Util.logi(valueProvider);
            }
        });
    }


    public void testDatabinding()
    {
        TestDataBindingLayoutBinding binding= DataBindingUtil.inflate(getLayoutInflater(),R.layout.test_data_binding_layout,null,false);
        User user=new User("X","男");
        binding.setUser(user);
    }

    public void testDatepickerDialog()
    {
        DatePickerDialog dialog=new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

            }
        },11,11,11);
    }

    public void testTimepickerDialog()
    {
        TimePickerDialog dialog=new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                Util.logi("hour,min="+hourOfDay+","+minute);
            }
        },23,12,true);
        dialog.show();
    }

    public void testDb(View v)
    {
        SQLiteDatabase db=new SqliteHelper(this).getWritableDatabase();
        SqliteHelper.recreate(db,SqliteHelper.TABLE_TEST);

        ContentValues values=new ContentValues();
        values.put(SqlUtil.COL_ID,3);
        values.put(SqlUtil.COL_CONTENT,"hello");
        long id=db.insert(SqliteHelper.TABLE_TEST,null,values);
        Util.logi("id="+id);

        id=db.insert(SqliteHelper.TABLE_TEST,null,values);
        Util.logi("id="+id);

        id=db.insertWithOnConflict(SqliteHelper.TABLE_TEST,null,values,SQLiteDatabase.CONFLICT_IGNORE);
        Util.logi("id="+id);

        values.put(SqlUtil.COL_ID,4);


        id=db.insertWithOnConflict(SqliteHelper.TABLE_TEST,null,values,SQLiteDatabase.CONFLICT_IGNORE);
        Util.logi("id="+id);


        //========
        Util.logi(SqlUtil.getTableColumns(db,SqliteHelper.TABLE_CITY));
        Util.logi(SqlUtil.contentValuesToWhereClause(values));
        Util.logi(SqlUtil.contentValuesToWhereClause(new ContentValues()));
        Util.logi(values);
        Cursor c=db.query(SqliteHelper.TABLE_TEST,null,null,null,null,null,null);
        Util.logi(SqlUtil.cursorToContentValues(c));
        ContentValues temp=SqlUtil.cursorToContentValues(c)[0];
        temp.put("content",(String)null);
        Object[] sels=SqlUtil.contentValuesToWhereClause(temp);
        Util.logi(sels);

        c=db.query(SqliteHelper.TABLE_TEST,null,(String)sels[0],(String[])sels[1],null,null,null);
        Util.logi(SqlUtil.cursorToContentValues(c));

        ContentValues temp2=new ContentValues();
        temp2.put("content","hello");
        Util.logi(SqlUtil.getRowCountInTable(db,SqliteHelper.TABLE_TEST,temp2));


        ContentValues temp3=new ContentValues();
        temp3.put("content","fuck");
        temp3.put("_id",100);
        Util.logi("res="+SqlUtil.insertOrUpdateIfThereIsOnlyOneOrZeroRow(db,SqliteHelper.TABLE_TEST,temp3,new HashSet<String>(){{add("_id");}}));//inserted
        Util.logi("res="+SqlUtil.insertOrUpdateIfThereIsOnlyOneOrZeroRow(db,SqliteHelper.TABLE_TEST,temp3,new HashSet<String>(){{add("_id");}}));//updated
        ContentValues temp4=new ContentValues();
        temp4.put("content","hello");

        Util.logi("res="+SqlUtil.insertOrUpdateIfThereIsOnlyOneOrZeroRow(db,SqliteHelper.TABLE_TEST,temp4,new HashSet<String>(){{add("content");}}));//nothing affected

    }

    @Override
    public Thread getUiThread() {
        return mUiThread;
    }
}
