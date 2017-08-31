package fulton.shaw.android.x.fragments.filter_conditions;

import fulton.shaw.android.x.public_interfaces.SqliteHelper;
import fulton.util.android.utils.SqlUtil;
import fulton.util.android.utils.Util;

/**
 * Created by 13774 on 8/23/2017.
 */

public class FilterConditionByTag extends FilterConditionBase{
    private int mFilterCondition;
    private String mTable;
    private String[] mTags;

    public FilterConditionByTag(int filterCondition, String table, String[] tags) {
        mFilterCondition = filterCondition;
        mTable = table;
        mTags = tags;
    }

    public String getTable() {
        return mTable;
    }

    public void setTable(String table) {
        mTable = table;
    }

    public String[] getTags() {
        return mTags;
    }

    public void setTags(String[] tags) {
        mTags = tags;
    }

    @Override
    public void updateCachedResult() {
        if(mTags==null||mTags.length==0 ||mFilterCondition==SqlUtil.FILTER_NO_APPLY)
        {
            setCachedSql(null);
            setCachedArgs(null);
        }else{
            String mainSql="( Select Count(*) From "+ SqliteHelper.TABLE_TAG+" Where "+SqliteHelper.TABLE_TAG+"."+
                    SqlUtil.COL_TABLE_NAME+"=\""+mTable+"\" and "+
                    SqliteHelper.TABLE_TAG+"."+SqlUtil.COL_INNER_ID+"="+mTable+"."+SqlUtil.COL_ID+" and "+
                    SqliteHelper.TABLE_TAG+"."+SqlUtil.COL_CONTENT+" in ("+ Util.joinRepeat(",","?",mTags.length)+"))";
            setCachedArgs(mTags);
            if(mFilterCondition==SqlUtil.FILTER_ALL)
                mainSql=mTags.length+"="+mainSql;
            else if(mFilterCondition==SqlUtil.FILTER_ANY)
                mainSql="0 < "+mainSql;
            else if(mFilterCondition==SqlUtil.FILTER_NOT)
                mainSql="0 = "+mainSql;
            setCachedSql(mainSql);
        }
    }

    public int getFilterCondition() {
        return mFilterCondition;
    }

    public void setFilterCondition(int filterCondition) {
        mFilterCondition = filterCondition;
    }
}
