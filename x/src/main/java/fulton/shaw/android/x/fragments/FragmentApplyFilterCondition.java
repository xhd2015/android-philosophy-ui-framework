package fulton.shaw.android.x.fragments;

/**
 * Created by 13774 on 8/22/2017.
 */

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;

import fulton.shaw.android.x.R;
import fulton.shaw.android.x.activities.ViewAsListUsingPages;
import fulton.shaw.android.x.fragments.adapters.AdapterExpandableGroupData;
import fulton.shaw.android.x.public_interfaces.ActivityUtil;
import fulton.shaw.android.x.public_interfaces.SqliteHelper;
import fulton.util.android.interfaces.tuples.Triple;
import fulton.util.android.interfaces.valueproviders.ValueProviderOfImmutableCursor;
import fulton.util.android.utils.StringFormatters;
import fulton.shaw.android.x.public_interfaces.ViewFactory;
import fulton.util.android.varman.StateVariableManager;
import fulton.util.android.interfaces.tuples.Pair;
import fulton.util.android.notations.MayConsumeTimePleaseUtilize;
import fulton.util.android.utils.CalendarUtil;
import fulton.util.android.utils.SqlUtil;
import fulton.util.android.utils.Util;

/**
 *  this should be the base.
 *  I mean,a full filtering features supported.
 *  It's a good chance to discard the old one.It is not efficient enough.
 *  Using cursor ordered by their shownDate or createdTime.
 *   how to apply?
 *
 *    just the general_record table is used.
 *
 *     it is basically a list.
 *
 *      can be applied by chained queries.
 *
 *      supported conditions:
 *          showndate condition:
 *          tagcondition:
 *          isConstantCondition:
 *
 *     使用Expandable
 */
public abstract class FragmentApplyFilterCondition extends Fragment{
    private static final int TYPE_ORIGINAL_DELETED=0;
    private static final int TYPE_ORIGINAL_PRESENT=1;
    private static final int TYPE_NEWLY_ADDED=2;

    private int mArgFunction;
    private ArrayList<Triple<String,Long,Integer>> mSelectedData;//原来的是有内容的，其他的没有.  分类
                                                                // 原来的， 已删除
                                                                //原来的，未删除
                                                                //新增的, 新增的数据将会自动以sqlite的函数值

    private static final int REQUEST_ADD_SOMETHING=0;
    private static final int TAG_CONVERT_VIEW_MAINTAG= (2<<24);


    private final String mTable=SqliteHelper.TABLE_GENERAL_RECORDS;

    /**
     *  cursor only contains data from general_recrod
     */
    private AdapterExpandableGroupData mAdapter;
    private ExpandableListView mListView;
    private SqliteHelper mSqliteHelper;

    private StateVariableManager mVarMan;
    private View mRootView;

    private ValueProviderOfImmutableCursor mAdapterCursorValuer;



    private int findIndexOfSelected(String table,long id)
    {
        for(int i=0;i<mSelectedData.size();i++)
        {
            if(mSelectedData.get(i).first.equals(table) && mSelectedData.get(i).second==id)
                return i;
        }
        return -1;
    }
    /**
     *
     * @param selected define 2 colors, the original, and the red_litte
     */
    private void setSelected(View v,boolean selected)
    {
        if(selected)
            v.setBackgroundColor(getResources().getColor(R.color.red_little));
        else
            v.setBackgroundColor(getResources().getColor(R.color.transparent));
    }

    public void setResultForSelect()
    {
        String argTable=getArguments().getString(ViewAsListUsingPages.ARG_TABLE_NAME);
        long argId=getArguments().getLong(ViewAsListUsingPages.ARG_INNER_ID,-1);
        if(argId==-1)return;
        for(Triple<String, Long, Integer> data:mSelectedData)
        {
            if(data.third==TYPE_ORIGINAL_DELETED)
            {
                mSqliteHelper.execSQL("Delete From "+SqliteHelper.TABLE_REFERENCE+" Where "+SqlUtil.COL_TABLE_NAME+"=? and " +
                                SqlUtil.COL_INNER_ID+"=? and "+SqlUtil.COL_REF_TABLE_NAME+"=? and "+
                                SqlUtil.COL_REF_INNER_ID+"=?",
                        new String[]{argTable,""+argId,data.first,""+data.second});
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        Util.logi(getClass()+":onCreateView");

        mSqliteHelper=new SqliteHelper(getActivity());
       mArgFunction=getArguments().getInt(ViewAsListUsingPages.ARG_FUNCTION, ViewAsListUsingPages.FUNCTION_NORMAL);
        if(mArgFunction== ViewAsListUsingPages.FUNCTION_SELECT) { //for selection
            String table = getArguments().getString(ViewAsListUsingPages.ARG_TABLE_NAME, "");
            long id = getArguments().getLong(ViewAsListUsingPages.ARG_INNER_ID, -1);
            mSelectedData = new ArrayList<>();
            Cursor cursor = mSqliteHelper.getCursorByRefId(SqliteHelper.TABLE_REFERENCE, table, id);
            while (cursor.moveToNext()) {
                mSelectedData.add(new Triple<String, Long, Integer>(
                        SqlUtil.getCursorValue(cursor, SqlUtil.COL_REF_TABLE_NAME, String.class),
                        SqlUtil.getCursorValue(cursor, SqlUtil.COL_REF_INNER_ID, Long.class),
                        TYPE_ORIGINAL_PRESENT
                ));
            }
            cursor.close();
        }

        mVarMan= getLoadedVarManagaer();

        mRootView=inflater.inflate(getContentRes(),container,false);

        mListView=getShownList(mRootView);

        final Pair<String, String[]> sql = getSql();
        Util.logi("sql="+sql.first);
        Util.logi("args=");
        Util.logi(sql.second);
        mAdapterCursorValuer=new ValueProviderOfImmutableCursor(mSqliteHelper.rawQuery(sql.first,sql.second));
        mAdapter=new AdapterExpandableGroupData(mAdapterCursorValuer.getHolder(),getGroupDateCol()) {
            @Override
            public View getDateView(Calendar date, boolean isExpanded, View convertView, ViewGroup parent, int groupPosition) {
                if(convertView==null) {
                    convertView = inflater.inflate(R.layout.show_list_date, parent, false);
                }
                TextView dateTextView= (TextView)convertView.findViewById(R.id.dateTextView);
                String dateText= StringFormatters.formatDateWithCalendar(date);
                int weekDay=date.get(Calendar.DAY_OF_WEEK);
                dateText+="  "+getResources().getStringArray(R.array.dayOfWeek)[weekDay-1];
                if(weekDay==Calendar.MONDAY)
                {
                    dateText+="  第"+
                            date.get(Calendar.DAY_OF_YEAR)+"天  第"+date.get(Calendar.WEEK_OF_YEAR)+"周"
                    ;
                }
                /**
                 *  @see MayConsumeTimePleaseUtilize
                 *   because everytime it will generate the today.
                 */
                if(CalendarUtil.isDateToDay(date))
                {
                    dateText+="(今天)";
                }
                dateTextView.setText(dateText);
                return convertView;
            }

            @Override
            public View getItemView(Cursor cursor, boolean isLastChild, View convertView, ViewGroup parent, int groupPos, int childPos) {
                String mainTag=cursor.getString(cursor.getColumnIndex(SqlUtil.COL_MAIN_TAG));
                //based on mainTag,give me the view.
                if(convertView==null || !mainTag.equals(convertView.getTag()))//impossible to reuse
                {
                    if(convertView!=null)
                        convertView.setTag(TAG_CONVERT_VIEW_MAINTAG,null);//clear it.
                    convertView= ViewFactory.newView(mainTag,inflater,parent);
                    convertView.setTag(TAG_CONVERT_VIEW_MAINTAG,mainTag);
                }
                ViewFactory.fillView(mainTag,convertView,mAdapterCursorValuer);
                if(mArgFunction==ViewAsListUsingPages.FUNCTION_SELECT) {
                    long id = SqlUtil.getCursorId(cursor);
                    int index = findIndexOfSelected(mTable, id);
                    if (index != -1)
                        setSelected(convertView, true);
                }
                return convertView;
            }
        };
        mAdapter.prepareData();
        mListView.setAdapter(mAdapter);
        for(int i=0;i<mAdapter.getGroupCount();i++)
            mListView.expandGroup(i);

        if(mArgFunction== ViewAsListUsingPages.FUNCTION_NORMAL) {
            mListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
                @Override
                public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                    ActivityUtil.startViewDetailActivity(getActivity(), mTable, id);
                    return true;
                }
            });
        }else if(mArgFunction== ViewAsListUsingPages.FUNCTION_SELECT){
            mListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
                @Override
                public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long innerId) {
                    String table=SqliteHelper.TABLE_GENERAL_RECORDS;
                    int index=findIndexOfSelected(table,innerId);
                    String argTable=getArguments().getString(ViewAsListUsingPages.ARG_TABLE_NAME);
                    long argId=getArguments().getLong(ViewAsListUsingPages.ARG_INNER_ID,-1);
                    if(argId==-1)
                    {
                        Toast.makeText(getActivity(),"Id参数不能为-1,请退出重试",Toast.LENGTH_SHORT).show();
                        return true;
                    }
                    if(index==-1) {
                        long insertId = mSqliteHelper.insertBasedOnKeys(SqliteHelper.TABLE_REFERENCE,
                                SqlUtil.COL_TABLE_NAME, argTable,
                                SqlUtil.COL_INNER_ID, argId,
                                SqlUtil.COL_REF_TABLE_NAME, table,
                                SqlUtil.COL_REF_INNER_ID, innerId,
                                SqlUtil.COL_RELATION,mSqliteHelper.getBriefString(table,innerId)
                        );
                        if (insertId == -1) {
                            Toast.makeText(getActivity(), "不能插入数据库，请退出重试或更新版本", Toast.LENGTH_SHORT).show();
                        } else {
                            mSelectedData.add(new Triple<String, Long, Integer>(table, innerId, TYPE_NEWLY_ADDED));
                            setSelected(v, true);
                        }
                    }else if(mSelectedData.get(index).third==TYPE_NEWLY_ADDED){
                        mSqliteHelper.execSQL("Delete From "+SqliteHelper.TABLE_REFERENCE+" Where "+SqlUtil.COL_TABLE_NAME+"=? and " +
                                        SqlUtil.COL_INNER_ID+"=? and "+SqlUtil.COL_REF_TABLE_NAME+"=? and "+
                                        SqlUtil.COL_REF_INNER_ID+"=?",
                                new String[]{argTable,""+argId,table,""+innerId}
                        );
                        do {
                            mSelectedData.remove(index);
                        }while((index=findIndexOfSelected(table,innerId))!=-1  && mSelectedData.get(index).third==TYPE_NEWLY_ADDED);
                        setSelected(v,false);
                    }else{
                        mSelectedData.get(index).third=1-mSelectedData.get(index).third;
                        setSelected(v,mSelectedData.get(index).third==TYPE_ORIGINAL_PRESENT?true:false);
                    }
                    return true;
                }
            });
        }
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Util.sleepIgnoreInterruption(10);
                int groupIndex=mAdapter.getGroupCount()-1;
                if(groupIndex>=0)
                {
                    mListView.setSelectedGroup(groupIndex);
                    int childIndex=mAdapter.getChildrenCount(groupIndex)-1;
                    if(childIndex>=0)
                    {
                        mListView.setSelectedChild(groupIndex,childIndex,false);
                    }
                }
            }
        });

        registerForContextMenu(mListView);
        mListView.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
            @Override
            public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
                menu.add(Menu.NONE,0,Menu.NONE,"删除");
                menu.add(Menu.NONE,1,Menu.NONE,"修改");
                menu.add(Menu.NONE,2,Menu.NONE,"查看详细");
                final MenuItem.OnMenuItemClickListener listener = new MenuItem.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        if (item.getItemId() == 0) {
                            ExpandableListView.ExpandableListContextMenuInfo info = (ExpandableListView.ExpandableListContextMenuInfo) item.getMenuInfo();
                            if (mSqliteHelper.deleteItemInTable(mTable, info.id)) {
                                ViewAsListUsingPages activity = (ViewAsListUsingPages) getActivity();
                                activity.updateViews();
                            }
                            return true;
                        } else if (item.getItemId() == 1) {
                            ExpandableListView.ExpandableListContextMenuInfo info = (ExpandableListView.ExpandableListContextMenuInfo) item.getMenuInfo();
                            int group = mListView.getPackedPositionGroup(info.packedPosition);
                            int child = mListView.getPackedPositionChild(info.packedPosition);
                            mAdapter.prepareData();//the first's method is called.
                            getActivity().startActivityForResult(ActivityUtil.getAddActivity(getActivity(), SqliteHelper.TABLE_GENERAL_RECORDS,
                                    SqlUtil.getCursorValue(mAdapter.getCursor(group, child), SqlUtil.COL_MAIN_TAG, String.class),
                                    info.id
                            ), REQUEST_ADD_SOMETHING);
                            return true;
                        } else if (item.getItemId() == 2) {
                            ExpandableListView.ExpandableListContextMenuInfo info = (ExpandableListView.ExpandableListContextMenuInfo) item.getMenuInfo();
                            ActivityUtil.startViewDetailActivity(getActivity(), mTable, info.id);
                            return true;
                        } else {
                            return false;
                        }
                    }
                };
                for(int i=0;i<3;i++)
                    menu.getItem(i).setOnMenuItemClickListener(listener);
            }
        });

        return mRootView;
    }

    protected abstract Pair<String,String[]> getSql();

    public void refreshDataSet()
    {
        final Pair<String, String[]> sql = getSql();
        Cursor previous=mAdapterCursorValuer.getHolder();
        mAdapterCursorValuer.setHolder(mSqliteHelper.rawQuery(sql.first,sql.second));
        mAdapter.setCursor(mAdapterCursorValuer.getHolder());
        mAdapter.notifyDataSetChanged();
        if(previous!=null)
            previous.close();
    }

    protected abstract @LayoutRes int getContentRes();
    protected abstract ExpandableListView getShownList(View rootView);

    protected abstract String getGroupDateCol();

    protected abstract String getPrefFileName();

    @Deprecated
    public boolean onBugContextItemSelected(MenuItem item) {//this is called from the first fragment,wired aha?
       return false;
    }
    @Override
    public void onDetach() {
        mVarMan.save();
        if(mArgFunction== ViewAsListUsingPages.FUNCTION_SELECT)
            setResultForSelect();
        Util.logi(getClass()+":onDetach");
        super.onDetach();
    }

    public StateVariableManager getLoadedVarManagaer() {
        if(mVarMan==null) {
            mVarMan = new StateVariableManager(getActivity().getSharedPreferences(getPrefFileName(), Context.MODE_PRIVATE));
            mVarMan.load();
        }
        return mVarMan;
    }

    public View getRootView()
    {
        return mRootView;
    }

    public SqliteHelper getSqliteHelper() {
        return mSqliteHelper;
    }
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////   The End      /////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ///========================those are not considered part of the code.
    //============the real end.
    @Override
    public void onAttach(Activity activity) {
        Util.logi(getClass()+":onAttach(activity)");
        super.onAttach(activity);
    }

    @Override
    public void onAttach(Context context) {
        Util.logi(getClass()+":onAttach(context)");
        super.onAttach(context);
    }

    @Override
    public void onPause() {
        Util.logi(getClass()+":onPause");
        super.onPause();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

        Util.logi(getClass()+":onViewCreated");
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        Util.logi(getClass()+":onActivityResult");
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onStart() {
        Util.logi(getClass()+":onStart");
        super.onStart();
    }


    @Override
    public void onDestroyView() {

        Util.logi(getClass()+":onDestroyView");
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        Util.logi(getClass()+":onDestroy");
        super.onDestroy();
    }

}
