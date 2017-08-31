package fulton.shaw.android.tellme.experiment.fragments;

import android.app.Dialog;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

import fulton.shaw.android.tellme.R;
import fulton.shaw.android.tellme.experiment.DateDetailActivity;
import fulton.util.android.utils.SqlUtil;
import fulton.util.android.event.GenericListener;
import fulton.shaw.android.tellmelibrary.sql.SqliteHelper;
import fulton.util.android.aware.ActivityAware;
import fulton.util.android.aware.FragmentAware;

/**
 * 显示最近4天的出行计划，但可以更多
 */
public class DateTravelPlanFragment extends Fragment implements ActivityAware<DateDetailActivity> {

    private int mShowNearstDaysNum;
    private DateDetailActivity mActivity;
    private TravelPlanListAdapter mAdapter;
    private ContextMenu mDeleteModifyContextMenu;

    private ListView mPlanListView;

    public DateTravelPlanFragment() {
        // Required empty public constructor
        mShowNearstDaysNum=4;//default
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v=inflater.inflate(R.layout.date_travel_plan_fragment_layout,container,false);
        final Bundle arg=getArguments();
        mActivity= (DateDetailActivity) arg.getSerializable(SharedArgument.ARG_ACTIVITY);


        mAdapter = new TravelPlanListAdapter(this);
        mPlanListView = (ListView) v.findViewById(R.id.travelPlanList);
        mShowNearstDaysNum=arg.getInt(SharedArgument.ARG_DATE_NUM, mShowNearstDaysNum);
        new Thread(new Runnable() {
            @Override
            public void run() {
                mAdapter.setResultSet(mActivity.getYear(),mActivity.getMonth(),mActivity.getDate(),mShowNearstDaysNum);

                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mPlanListView.setAdapter(mAdapter);
                    }
                });

            }
        }).start();


        registerForContextMenu(mPlanListView);
        mPlanListView.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
            @Override
            public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
                mActivity.getMenuInflater().inflate(R.menu.context_delete_modify,menu);
            }
        });

        mActivity.getProxyNotifier().setOnNotifyListener(-1, DateDetailActivity.EVENT_DATE_CHANGED, new GenericListener() {
            @Override
            public void applyListen(int listenerId, int eventType, final Object... args) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        mAdapter.setResultSet((int)args[0],(int)args[1],(int)args[2],mShowNearstDaysNum);
                        mActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mAdapter.notifyDataSetChanged();
                            }
                        });
                    }
                }).start();

            }
        });
        mActivity.getProxyNotifier().setOnNotifyListener(-1, DateDetailActivity.EVENT_ADD_TRAVEL_PLAN, new GenericListener() {
            @Override
            public void applyListen(int listenerId, int eventType, final Object... args) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        ContentValues result= (ContentValues) args[0];
                        mAdapter.setResultSet(result.getAsInteger(SqlUtil.COL_YEAR),result.getAsInteger(SqlUtil.COL_MONTH),
                                result.getAsInteger(SqlUtil.COL_DATE),mShowNearstDaysNum );
                        mActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mAdapter.notifyDataSetChanged();
                            }
                        });
                    }
                }).start();
            }
        });

        return v;
    }


    public void setShownDays(int num)
    {
        mShowNearstDaysNum=num;
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public DateDetailActivity getContextActivity() {
        return mActivity;
    }


    public static class TravelPlanListAdapter extends BaseAdapter implements FragmentAware<DateTravelPlanFragment> {
        private ArrayList<Object[]> mData;//_id the first,srcProvId,dstProvId...
        private DateTravelPlanFragment mFragment;
        private SQLiteDatabase mdb;

        public TravelPlanListAdapter(DateTravelPlanFragment fragment)
        {
            mFragment=fragment;
            mdb=fragment.getContextActivity().getDb();
            mData=new ArrayList<>();
        }
        public ArrayList<Object[]> getData()
        {
            return mData;
        }

        //without notifying dataset changed
        public void setResultSet(int year,int month,int date,int numDays)
        {
            ArrayList<Object[]> newData=new ArrayList<>();
            Calendar calendar=new GregorianCalendar(year,month,date);
            String selection= SqlUtil.COL_YEAR+"=? and "+ SqlUtil.COL_MONTH+"=? and "+ SqlUtil.COL_DATE+"=?";
            String[] selectionArgs=new String[3];
            String[] projection=new String[]{SqlUtil.COL_ID, SqlUtil.COL_SRC_PROV_ID, SqlUtil.COL_SRC_CITY_ID, SqlUtil.COL_DST_PROV_ID, SqlUtil.COL_DST_CITY_ID};
//           Util.logi("in fragment,set result set");
            for(int i=0;i<numDays;i++)
            {
//                Util.logi("y,m,d="+calendar.getByGetter(Calendar.YEAR)+","+(calendar.getByGetter(Calendar.MONTH)+1)+","+calendar.getByGetter(Calendar.DAY_OF_MONTH));
                selectionArgs[0]=String.valueOf(calendar.get(Calendar.YEAR));
                selectionArgs[1]=String.valueOf(calendar.get(Calendar.MONTH));
                selectionArgs[2]=String.valueOf(calendar.get(Calendar.DAY_OF_MONTH));
                Cursor c=mdb.query(SqliteHelper.TABLE_TRAVEL_PLAN,projection,selection,selectionArgs,null,null,null);
//                Util.logi("count="+c.getCount());
                while (c.moveToNext())
                {
//                    Util.logi("id:"+c.getInt(c.getColumnIndex(SqliteHelper.COL_ID)));
                    newData.add(new Object[]{i,
                            c.getInt(c.getColumnIndex(SqlUtil.COL_ID)),
                            c.getInt(c.getColumnIndex(SqlUtil.COL_SRC_PROV_ID)),
                            c.getInt(c.getColumnIndex(SqlUtil.COL_SRC_CITY_ID)),
                            c.getInt(c.getColumnIndex(SqlUtil.COL_DST_PROV_ID)),
                            c.getInt(c.getColumnIndex(SqlUtil.COL_DST_CITY_ID))
                    });
                }
                c.close();
                calendar.add(Calendar.DAY_OF_MONTH,1);
            }

            mData.clear();
            mData = newData;
        }

        @Override
        public int getCount() { //init at here
            return mData.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent){
            if(convertView==null)
            {
                convertView=mFragment.getContextActivity().getLayoutInflater().inflate(R.layout.date_travel_plan_fragment_listitem,parent,false);
            }
            TextView srcProvView= (TextView) convertView.findViewById(R.id.srcProvView);
            TextView srcCityView= (TextView) convertView.findViewById(R.id.srcCityView);
            TextView dstProvView= (TextView) convertView.findViewById(R.id.dstProvView);
            TextView dstCityView= (TextView) convertView.findViewById(R.id.dstCityView);
            TextView hintNumDaysView= (TextView) convertView.findViewById(R.id.numDaysHintView);


            int afterDays= (int) mData.get(position)[0];
            if(afterDays==0)
            {
                Calendar calendar=Calendar.getInstance();
                if(mFragment.getContextActivity().getDate() - calendar.get(Calendar.DAY_OF_MONTH)==0 &&
                        mFragment.getContextActivity().getMonth() - calendar.get(Calendar.MONTH)==0 &&
                mFragment.getContextActivity().getYear()-calendar.get(Calendar.YEAR)==0                 )
                {
                    hintNumDaysView.setText(mFragment.getResources().getString(R.string.today));
                }else{
                    hintNumDaysView.setText("");
                }
            }else{
                hintNumDaysView.setText(""+afterDays+mFragment.getResources().getString(R.string.after_days));
            }
            String srcProvText=SqliteHelper.getProvinceIDToNameAsHashMap(mdb).get(mData.get(position)[2]);
            String srcCityText=SqliteHelper.getCityIDToNameAsHashMap(mdb).get(mData.get(position)[3]);
            if(srcProvText==null)
                srcProvText=mFragment.getResources().getString(R.string.undetermined);
            if(srcCityText==null)
                srcCityText=mFragment.getResources().getString(R.string.undetermined);
            srcProvView.setText(srcProvText);
            srcCityView.setText(srcCityText);
            dstProvView.setText(SqliteHelper.getProvinceIDToNameAsHashMap(mdb).get(mData.get(position)[4]));
            dstCityView.setText(SqliteHelper.getCityIDToNameAsHashMap(mdb).get(mData.get(position)[5]));

            return convertView;

        }

        @Override
        public DateTravelPlanFragment getContextFragment() {
            return mFragment;
        }


    }
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        boolean proceesed=true;
        switch (item.getItemId())
        {
            case R.id.deleteAction: {
                AdapterView.AdapterContextMenuInfo menuInfo = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
                int i = getContextActivity().getContextDB().delete(SqliteHelper.TABLE_TRAVEL_PLAN, SqlUtil.COL_ID + "=?",
                        new String[]{String.valueOf(mAdapter.getData().get((int) menuInfo.position)[1])});
                mAdapter.getData().remove(menuInfo.position);
                mAdapter.notifyDataSetChanged();
                break;
            }
            case R.id.modifyAction: {
                AdapterView.AdapterContextMenuInfo menuInfo = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
                int position=menuInfo.position;
                ViewGroup view = (ViewGroup) menuInfo.targetView;
                int[] indexes = new int[]{0, 0, -1, -1};
                TextView[] textViews = new TextView[]{
                        (TextView) view.findViewById(R.id.srcProvView),
                        (TextView) view.findViewById(R.id.srcCityView),
                        (TextView) view.findViewById(R.id.dstProvView),
                        (TextView) view.findViewById(R.id.dstCityView)
                };
                Integer srcProvId = -1, dstProvId = -1;
                for (int i = 0; i < SqliteHelper.getProvincesAsArrayList(mActivity.getContextDB()).size(); i++) {
                    if (indexes[0] == 0 && textViews[0].getText().toString().equals(SqliteHelper.getProvincesAsArrayList(mActivity.getContextDB()).get(i)[1])) {
                        indexes[0] = i + 1;
                        srcProvId = (int) SqliteHelper.getProvincesAsArrayList(mActivity.getContextDB()).get(i)[0];
                    }
                    if (indexes[2] == -1 && textViews[2].getText().toString().equals(SqliteHelper.getProvincesAsArrayList(mActivity.getContextDB()).get(i)[1])) {
                        indexes[2] = i;
                        dstProvId = (int) SqliteHelper.getProvincesAsArrayList(mActivity.getContextDB()).get(i)[0];
                    }
                }
                if (indexes[0] != 0) {
                    ArrayList<Object[]> srcCityList = SqliteHelper.getCitiesAsHashMap(mActivity.getContextDB()).get(srcProvId);
                    for (int i = 0; i < srcCityList.size(); i++) {
                        if (textViews[1].getText().toString().equals(srcCityList.get(i)[1])) {
                            indexes[1] = i;
                            break;
                        }
                    }
                }
                ArrayList<Object[]> dstCityList = SqliteHelper.getCitiesAsHashMap(mActivity.getContextDB()).get(dstProvId);
                for (int i = 0; i < dstCityList.size(); i++) {
                    if (textViews[3].getText().toString().equals(dstCityList.get(i)[1])) {
                        indexes[3] = i;
                        break;
                    }
                }

                ContentValues contentValues = new ContentValues();
//                Util.logi("position="+position);
//                Util.logi("id="+(int)mAdapter.getData().getByGetter(position)[1]);
                contentValues.put(SqlUtil.COL_ID, (int) mAdapter.getData().get(position)[1]);
                final Dialog dialog = DateDetailActivity.buildTravelPlanDialog(mActivity, mActivity, mActivity, mActivity.getDialogCityList(),
                        mActivity.getProxyNotifier(), mActivity.EVENT_ADD_TRAVEL_PLAN, getResources().getString(R.string.changeTravelPlanTitle),
                        getResources().getString(R.string.confirmChange), indexes[0], indexes[1], indexes[2], indexes[3], contentValues);
                dialog.show();
                break;
            }
            default:
                proceesed=false;
        }
        return proceesed;
    }

}
