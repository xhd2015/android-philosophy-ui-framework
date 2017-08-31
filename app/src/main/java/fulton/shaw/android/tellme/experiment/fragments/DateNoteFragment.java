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

/**
 * Created by 13774 on 7/17/2017.
 */

public class DateNoteFragment extends Fragment {

    private ListView mNoteList;
    private DateDetailActivity mActivity;
    private Handler mFastUpdater;
    private NoteListAdapter mListAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
       View v=inflater.inflate(R.layout.date_note_fragment_layout,container,false);


        mActivity= (DateDetailActivity) getArguments().getSerializable(SharedArgument.ARG_ACTIVITY);
        mNoteList = (ListView) v.findViewById(R.id.noteList);
        mListAdapter = new NoteListAdapter(this);

        mFastUpdater=new Handler(mActivity.getMainLooper());

        mActivity.getProxyNotifier().setOnNotifyListener(-1, mActivity.EVENT_ADD_NOTE, new GenericListener() {
            @Override
            public void applyListen(int listenerId, int eventType, Object... args) {

            }
        });
        mActivity.getProxyNotifier().setOnNotifyListener(-1, mActivity.EVENT_DATE_CHANGED, new GenericListener() {
            @Override
            public void applyListen(int listenerId, int eventType, final Object... args) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        mListAdapter.setDate((int)args[0],(int)args[1],(int)args[2]);
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


        new Thread(new Runnable() {
            @Override
            public void run() {
                mListAdapter.setDate(mActivity.getYear(),mActivity.getMonth(),mActivity.getDate());
                Util.logi("item count:"+mListAdapter.getCount());

                mFastUpdater.postAtFrontOfQueue(new Runnable() {
                    @Override
                    public void run() {
                        mNoteList.setAdapter(mListAdapter);
                    }
                });
            }
        }).start();


        mActivity.getProxyNotifier().setOnNotifyListener(-1, DateDetailActivity.EVENT_ADD_NOTE, new GenericListener() {
            @Override
            public void applyListen(int listenerId, int eventType, final Object... args) {//result
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        ContentValues result= (ContentValues) args[0];

                        final int index=mListAdapter.insertData(result);
//                        final View v=mListAdapter.getViewFromData(index);
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

    public static class NoteListAdapter extends BaseAdapter
    {

        private SQLiteDatabase mdb;
        private DateNoteFragment mFragment;
        private ArrayList<Object[]> mData;//hour,note

        public NoteListAdapter(DateNoteFragment fragment)
        {
            mFragment=fragment;
            mdb=mFragment.mActivity.getDb();
            mData=new ArrayList<>();
        }


        public void setDate(int year,int month,int date)
        {
            String[] projection={SqlUtil.COL_HOUR, SqlUtil.COL_NOTE};
            Cursor cursor=mdb.query(
                    SqliteHelper.TABLE_NOTE,
                    projection,
                    SqlUtil.COL_YEAR+"=? and "+ SqlUtil.COL_MONTH+"=? and "+ SqlUtil.COL_DATE+"=?",
                    new String[]{String.valueOf(year),String.valueOf(month),String.valueOf(date)},
                    null,
                    null,
                    SqlUtil.COL_HOUR + " ASC"
            );

            mData.clear();//_id,hour,minute,type,issue  order by <hour,minute>
            while (cursor.moveToNext())
            {
                mData.add(new Object[]{
                        cursor.getInt(cursor.getColumnIndex(SqlUtil.COL_HOUR)),
                        cursor.getString(cursor.getColumnIndex(SqlUtil.COL_NOTE)),
                });
            }
            cursor.close();
        }
        /**
         *
         * @param oneValue
         * @return the position of inserted value
         */
        public int insertData(ContentValues oneValue)
        {
            int hourOfValue=oneValue.getAsInteger(SqlUtil.COL_HOUR);
            int i=0;
            for(;i<mData.size();i++)
            {
                int hour= (int) mData.get(i)[0];
                if(hourOfValue <= hour )
                    break;
            }
            mData.add(i,new Object[]{
                    hourOfValue,
                    oneValue.getAsString(SqlUtil.COL_NOTE)
            });
            return i;
        }



        @Override
        public int getCount() {
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
                convertView=mFragment.mActivity.getLayoutInflater().inflate(R.layout.date_note_fragment_list_subitem,parent,false);
            }
            TextView noteView=(TextView)convertView.findViewById(R.id.note);

            noteView.setText((String)mData.get(position)[1]);

            return convertView;
        }
    }
}
