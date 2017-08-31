package fulton.shaw.android.tellme.experiment.fragments;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import fulton.shaw.android.tellme.R;
import fulton.shaw.android.tellme.experiment.DateDetailActivity;
import fulton.util.android.utils.SqlUtil;
import fulton.util.android.event.GenericListener;
import fulton.shaw.android.tellmelibrary.sql.SqliteHelper;
import fulton.util.android.utils.Util;
import fulton.util.android.aware.DateAware;

/**
 * Created by 13774 on 7/17/2017.
 */

/**
 * watch input texts change.But it did not know.
 * It just knows that it needs three arguments
 *
 * Fragment需要被重新实例化，这可能与。。。，最好使用
 */
public class DateIssueFragment extends Fragment {

    private ListView mIssueList;
    private IssueListAdapter mListAdapter;
    private DateDetailActivity mActivity;
    private Handler mFastUpdater;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v=inflater.inflate(R.layout.date_issue_fragment_layout,container,false);


        mIssueList= (ListView) v.findViewById(R.id.issueList);

        mActivity = (DateDetailActivity) getArguments().getSerializable(SharedArgument.ARG_ACTIVITY);
        mFastUpdater=new Handler(mActivity.getMainLooper());
        mListAdapter=new IssueListAdapter(this);


        new Thread(new Runnable() {
            @Override
            public void run() {
                mListAdapter.updateDataSet(mActivity.getYear(),mActivity.getMonth(),mActivity.getDate());
                Util.logi("item count:"+mListAdapter.getCount());

                mFastUpdater.postAtFrontOfQueue(new Runnable() {
                    @Override
                    public void run() {
                        mIssueList.setAdapter(mListAdapter);
                    }
                });
            }
        }).start();

//        Util.logi("Fragment's activity==null?"+(mActivity==null));

        mActivity.getProxyNotifier().setOnNotifyListener(-1, DateDetailActivity.EVENT_ADD_ISSUE, new GenericListener() {
            @Override
            public void applyListen(int listenerId, int eventType, final Object... args) {//result
                new Thread(new Runnable() {
                    @Override
                    public void run() {
//                        ContentValues result= (ContentValues) args[0];
//
//                        final int index=mListAdapter.insertData(result);
////                        final View v=mListAdapter.getViewFromData(index);
                        mListAdapter.updateDataSet();
                        mActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mListAdapter.notifyDataSetChanged();
                            }
                        });

                    }
                }).start();
            }
        });

        mActivity.getProxyNotifier().setOnNotifyListener(-1, mActivity.EVENT_DATE_CHANGED, new GenericListener() {
            @Override
            public void applyListen(int listenerId, int eventType, final Object... args) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        mListAdapter.updateDataSet((int)args[0],(int)args[1],(int)args[2]);
                        mActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mListAdapter.notifyDataSetChanged();
                            }
                        });
                    }
                }).start();
            }
        });

        return v;


    }


    public static class IssueListAdapter extends BaseAdapter implements DateAware {
        private SQLiteDatabase mdb;
        private DateIssueFragment mFragment;

        private int mYear,mMonth,mDate;
        private ArrayList<Object[]> mData;//_id,hour,minute,type,issue  order by <hour,minute>

        public static final int DATA_CHANGE_INSERT_ONE=0;
        public static final int DATA_CHANGE_ALL=1;

        private int mDataChangeType =DATA_CHANGE_ALL;
        private Object[] mDataChangeArgs;

        /**
         * after init,must call setDate(...),otherwise,this call will not be coordinated
         * @param fragment
         */
        public IssueListAdapter(DateIssueFragment fragment)
        {
            mFragment=fragment;
            mdb=mFragment.mActivity.getDb();
            mData=new ArrayList<>();
        }
        public void updateDataSet()
        {
            String[] projection={SqlUtil.COL_HOUR, SqlUtil.COL_MINUTE, SqlUtil.COL_ID, SqlUtil.COL_TYPE, SqlUtil.COL_ISSUE};
            Cursor cursor=mdb.query(
                    SqliteHelper.TABLE_ISSUE_LIST,
                    projection,
                    SqlUtil.COL_YEAR+"=? and "+ SqlUtil.COL_MONTH+"=? and "+ SqlUtil.COL_DATE+"=?",
                    new String[]{String.valueOf(mYear),String.valueOf(mMonth),String.valueOf(mDate)},
                    null,
                    null,
                    SqlUtil.COL_HOUR + " ASC,"+ SqlUtil.COL_MINUTE+" ASC"
            );

            mData.clear();//_id,hour,minute,type,issue  order by <hour,minute>
            while (cursor.moveToNext())
            {
                mData.add(new Object[]{
                        cursor.getInt(cursor.getColumnIndex(SqlUtil.COL_ID)),
                        cursor.getInt(cursor.getColumnIndex(SqlUtil.COL_HOUR)),
                        cursor.getInt(cursor.getColumnIndex(SqlUtil.COL_MINUTE)),
                        cursor.getString(cursor.getColumnIndex(SqlUtil.COL_TYPE)),
                        cursor.getString(cursor.getColumnIndex(SqlUtil.COL_ISSUE))
                });
            }
            cursor.close();
        }

        /**
         * better do this in background
         * @param year
         * @param month
         * @param date
         */
        public void updateDataSet(int year,int month,int date)
        {
            mYear=year;
            mMonth=month;
            mDate=date;

            updateDataSet();

        }

        /**
         *
         * @param oneValue
         * @return the position of inserted value
         */
        public int insertData(ContentValues oneValue)
        {
            int hourOfValue=oneValue.getAsInteger(SqlUtil.COL_HOUR);
            int minuteOfValue=oneValue.getAsInteger(SqlUtil.COL_MINUTE);
            int i=0;
            for(;i<mData.size();i++)
            {
                int hour= (int) mData.get(i)[1];
                int minute = (int)mData.get(i)[2];
                if(hourOfValue < hour || (hourOfValue==hour && minuteOfValue <minute))
                    break;
            }
            mData.add(i,new Object[]{
                    oneValue.getAsInteger(SqlUtil.COL_ID),
                    hourOfValue,
                    minuteOfValue,
                    oneValue.getAsString(SqlUtil.COL_TYPE),
                    oneValue.getAsString(SqlUtil.COL_ISSUE)
            });
            return i;
        }

        public void setDataChangeType(int type,Object...args)
        {
            mDataChangeType =type;
            mDataChangeArgs = args;
        }
        @Override
        public int getCount() { //select count(*) from issue where
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
        public View getView(int position, View convertView, ViewGroup parent) {
            if(convertView==null)
            {
                convertView=mFragment.mActivity.getLayoutInflater().inflate(R.layout.date_issue_fragment_list_subitem,parent,false);
            }
            TextView timeView= (TextView) convertView.findViewById(R.id.time);
            TextView issueView=(TextView)convertView.findViewById(R.id.issue);

            timeView.setText(""+(int)mData.get(position)[1] + ":" + (int)mData.get(position)[2]);
            issueView.setText((String)mData.get(position)[4]);

            return convertView;
        }

        @Override
        public void setYear(int year) {
            mYear=year;
        }

        @Override
        public void setMonth(int month) {
            mMonth=month;
        }

        @Override
        public void setDate(int date) {
            mDate=date;
        }

        @Override
        public int getYear() {
            return mYear;
        }

        @Override
        public int getMonth() {
            return mMonth;
        }

        @Override
        public int getDate() {
            return mDate;
        }

//        public View getViewFromData(int i)
//        {
//            View v=mFragment.mActivity.getLayoutInflater().inflate(R.layout.date_issue_fragment_list_subitem,null,false);
//            TextView timeView= (TextView) v.findViewById(R.id.time);
//            TextView issueView=(TextView)v.findViewById(R.id.issue);
//
//            timeView.setText(""+(int)mData.getByGetter(i)[1] + ":" + (int)mData.getByGetter(i)[2]);
//            issueView.setText((String)mData.getByGetter(i)[4]);
//
//            return v;
//        }
    }
}
