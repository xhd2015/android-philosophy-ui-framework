package fulton.shaw.android.x.fragments.filter_conditions;

import fulton.util.android.utils.SqlUtil;
import fulton.util.android.utils.Util;

/**
 * Created by 13774 on 8/23/2017.
 */

public class FilterConditionByShownType extends FilterConditionBase{
    private String mShownCol;
    private int  mShownType;

    public FilterConditionByShownType(String shownCol, int shownType) {
        mShownCol = shownCol;
        mShownType = shownType;
    }

    @Override
    public void updateCachedResult() {
        if(mShownType!=SqlUtil.SHOWN_TYPE_ALL)
        {
            setCachedSql(mShownCol+"= ?");
            setCachedArgs(new String[]{""+mShownType});
        }else{
            setCachedSql(null);
            setCachedArgs(null);
        }
    }

    public String getShownCol() {
        return mShownCol;
    }

    public void setShownCol(String shownCol) {
        mShownCol = shownCol;
    }

    public int getShownType() {
        return mShownType;
    }

    /**
     *  if it is set to -1,means no filtering.which is what we want.
     * @param shownType
     */
    public void setShownType(int shownType) {
        mShownType = shownType;
    }
}
