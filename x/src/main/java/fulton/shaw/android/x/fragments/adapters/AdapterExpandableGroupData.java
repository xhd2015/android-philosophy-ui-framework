package fulton.shaw.android.x.fragments.adapters;

import android.database.Cursor;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;

import java.util.ArrayList;
import java.util.Calendar;

import fulton.util.android.interfaces.tuples.Pair;
import fulton.util.android.notations.ForDebug;
import fulton.util.android.notations.HelperStaticMethods;
import fulton.util.android.utils.CalendarUtil;
import fulton.util.android.utils.SqlUtil;
import fulton.util.android.utils.Util;

/**
 * Created by 13774 on 8/23/2017.
 */

public abstract class AdapterExpandableGroupData extends BaseExpandableListAdapter {
    private Cursor mCursor;
    private ArrayList<Pair<Calendar,ArrayList<Integer>>> mPosMap;
    private String mGroupDateCol;

    public AdapterExpandableGroupData(Cursor cursor,String groupDateCol) {
        mCursor = cursor;
        mGroupDateCol=groupDateCol;
    }

    @Override
    public int getGroupCount() {
        return mPosMap.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return mPosMap.get(groupPosition).second.size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return mPosMap.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return mPosMap.get(groupPosition).second.get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return mPosMap.get(groupPosition).first.getTimeInMillis()/ CalendarUtil.ONE_DAY;//they have no id actually.
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        int curPos=mPosMap.get(groupPosition).second.get(childPosition);
        mCursor.moveToPosition(curPos);
        return mCursor.getLong(mCursor.getColumnIndex(SqlUtil.COL_ID));
    }

    @Override
    public boolean hasStableIds() {//child is,father is yes also.
        return true;
    }

    public abstract View getDateView(Calendar date, boolean isExpanded, View convertView, ViewGroup parent, int groupPosition);
    public abstract View getItemView(Cursor cursor, boolean isLastChild, View convertView, ViewGroup parent, int groupPos, int childPos);

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        return getDateView(mPosMap.get(groupPosition).first,isExpanded,convertView,parent, groupPosition);
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        mCursor.moveToPosition(mPosMap.get(groupPosition).second.get(childPosition));
        return getItemView(mCursor,isLastChild,convertView,parent, groupPosition,childPosition);
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    public Cursor getCursor() {
        return mCursor;
    }
    public Cursor getCursor(int group,int child){
        mCursor.moveToPosition(mPosMap.get(group).second.get(child));
        return mCursor;
    }
    @ForDebug
    public ArrayList getPosMap()
    {
        return mPosMap;
    }

    public void setCursor(Cursor cursor) {
        mCursor = cursor;
    }

    public void prepareData()
    {
        mPosMap=getCursorGroupByDateInformation(mCursor,mGroupDateCol);
    }

    @Override
    public void notifyDataSetChanged() {
        prepareData();
        super.notifyDataSetChanged();
    }

    /**
     *  assuming that all of them is coming fomr one table.
     *   and that it is ordered by the date.
     *   we just do group operation.
     *
     *
     *   ArrayList<Pair<Date,ArrayList>>
     *
     *       the first must be its date.
     * @param cursor
     */
    @HelperStaticMethods
    public static ArrayList<Pair<Calendar,ArrayList<Integer>>> getCursorGroupByDateInformation(Cursor cursor,String colGroupDate)
    {
        cursor.moveToPosition(-1);
        Util.logi(cursor);
        ArrayList<Pair<Calendar,ArrayList<Integer>>> posMap=new ArrayList<>();

//        Util.logi(cursor);
        int colIndex=cursor.getColumnIndex(colGroupDate);
//        Util.logi("groupDate col,index=:"+colGroupDate+","+colIndex);
        Calendar lastDate=null;
        ArrayList<Integer> curMap=null;
        while (cursor.moveToNext())
        {
            Calendar curDate=CalendarUtil.getDateOfSqlString(cursor.getString(colIndex));
            if(lastDate==null || CalendarUtil.computeDiffAsDays(lastDate,curDate)!=0)//found a new group
            {
                curMap=new ArrayList<>();
                posMap.add(new Pair<Calendar, ArrayList<Integer>>(curDate,curMap));
                lastDate=curDate;
            }
            curMap.add(cursor.getPosition());
        }
        //count =cursor.getCount() + posMap.size();

        return posMap;
    }


}
