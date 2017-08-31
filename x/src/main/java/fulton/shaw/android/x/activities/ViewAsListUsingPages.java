package fulton.shaw.android.x.activities;

import android.app.Dialog;
import android.content.Intent;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;

import fulton.shaw.android.x.R;
import fulton.shaw.android.x.fragments.adapters.ViewAsListPagerAdapter;
import fulton.shaw.android.x.public_interfaces.FirstTimeAppRunning;
import fulton.util.android.utils.Util;

public class ViewAsListUsingPages extends AppCompatActivity {

    public static final int FUNCTION_NORMAL =0;
    public static final int FUNCTION_SELECT=1;
    public static final String ARG_FUNCTION="argFunction";
    public static final String ARG_TABLE_NAME="tableName";
    public static final String ARG_INNER_ID="innerId";


    private static final int REQUEST_ADD_SOMETHING=0;
    private static final int REQUEST_SHOW_DETTAIL=1;
    private static final int REQUEST_OPEN_UPDATE=2;

    private static Class[] MAPPED_ACTIVITY=new Class[]{
            AddPlanActivity.class,
            AddTravelNoteAcitivity.class,
            AddGeneralRecordTagDiaryActivity.class,
            AddGeneralRecordTagBlogActivity.class,
            AddGeneralRecordTagProblemActivity.class,

            null,
            null,
            AddThingsPriceActivity.class,
            null,
            null,

            null,
            AddGeneralRecordTagSaySomethingVeryNewActivity.class
    };

    private ViewPager mPager;
    private ViewAsListPagerAdapter mAdapter;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirstTimeAppRunning.runIfTheFirstTime(this);
        setContentView(R.layout.activity_view_as_list_using_pages);


        mPager= (ViewPager) findViewById(R.id.pager);
        mAdapter=new ViewAsListPagerAdapter(getSupportFragmentManager(),getIntent().getIntExtra(ARG_FUNCTION,FUNCTION_NORMAL),
                getIntent().getStringExtra(ARG_TABLE_NAME),getIntent().getLongExtra(ARG_INNER_ID,-1));
        mPager.setOffscreenPageLimit(mAdapter.getCount()-1);//keep all
        mPager.setAdapter(mAdapter);
    }

    public void onClickAddButton(View v)//create a dialog
    {
        final Dialog dialog=new Dialog(this);
        final GridView rootView= (GridView) getLayoutInflater().inflate(R.layout.view_as_list_dialog_layout,null);
        dialog.setContentView(rootView);
        rootView.setNumColumns(3);
        ArrayAdapter<String> adapter=new ArrayAdapter<String>(this,R.layout.select_types_subitem,R.id.textView,getResources().getStringArray(R.array.support_types));
        rootView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                dialog.dismiss();
                onClickDialogSelection(dialog,position);
            }
        });
        rootView.setAdapter(adapter);
        dialog.show();
    }

    public void onClickDialogSelection(Dialog dialog,int position)//we start a new activity
    {
        Intent intent=new Intent(this,MAPPED_ACTIVITY[position]);
        startActivityForResult(intent,REQUEST_ADD_SOMETHING);
    }

    /**
     *  data must be put to indicate: a new item of what type has beed added?
     *          a new constant?--> call adapters notify new constant.
     *
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode==REQUEST_ADD_SOMETHING)
        {
            if(resultCode==RESULT_OK)
            {
                updateViews();
            }
        }else{
            super.onActivityResult(requestCode,resultCode,data);
        }
    }

    public void updateViews()
    {
        Util.logi("updating views");
        mAdapter.notifyWhichShouldUpdate(new int[]{0,1,2});
    }

    @Override
    public void onBackPressed() {
        if(getIntent().getIntExtra(ARG_FUNCTION,FUNCTION_NORMAL)==FUNCTION_SELECT)
        {
            setResult(RESULT_OK);
            this.finish();
        }else{
            super.onBackPressed();
        }
    }
}
