package fulton.shaw.android.x.fragments.filter_conditions;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.ArrayList;

import fulton.util.android.interfaces.IndexedGetter;
import fulton.util.android.interfaces.tuples.Pair;
import fulton.util.android.utils.DataUtils;
import fulton.util.android.utils.Util;

/**
 * Created by 13774 on 8/23/2017.
 */

public abstract class FilterConditionBase {
    private String mCachedSql;
    private String[] mCachedArgs;

    /**
     * the condition part(cluase of where)
     * @return something like '1=1' or 'date>?'.
     *   if it returns null, then this means no filter condition.it can be ignored.
     *   when this method returns null, {@link #getSqlConditionArgs}'result is ignored.it can be null.
     */
    public @Nullable String getSqlConditionString() {
        return mCachedSql;
    }

    /**
     *  the returned values will be combine by {@link fulton.util.android.utils.DataUtils#combineStringArrays(String[]...)}
     * @return return the args.
     */
    @NonNull
    public String[] getSqlConditionArgs() {
        return mCachedArgs;
    }



    protected void setCachedSql(String cachedSql) {
        mCachedSql = cachedSql;
    }

    protected void setCachedArgs(String[] cachedArgs) {
        mCachedArgs = cachedArgs;
    }

    /**
     *  do operations here, then getByGetter result from the other two methods.
     */
    public abstract void updateCachedResult();

    /**
     * null to be all
     * @param tableOrSubQuery  if it is a table ,it cannot contain ' '; if it is a subquery,it must contain ' ';
     * @param cols
     * @param orderBy
     * @return
     */
    private static String constructSql(@NonNull String tableOrSubQuery, @Nullable String subTableAs, @Nullable String[] cols, String orderBy, @NonNull FilterConditionBase condition)
    {
        String colString=getColsString(cols);
        String subAs="";
        if(subTableAs!=null)
            subAs=" As "+subTableAs;
        if(condition.getSqlConditionString()==null)
        {
          return getSelectIfTable(tableOrSubQuery,cols, orderBy);
        }else{
            return "Select "+colString+" From \n("+tableOrSubQuery+")"+subAs+
                    "\n Where "+condition.getSqlConditionString() +
                    getString(orderBy,""," Order By "+orderBy);
        }
    }

    public static Pair<String,String[]> constructSql(@NonNull String tableOrSubQuery, @Nullable String subTableAs, @Nullable String[] cols, String orderBy, @NonNull final ArrayList<FilterConditionBase> condition)
    {
        final String[] nullArray=new String[0];
        String[] args=DataUtils.combineStringArrays(new IndexedGetter<String[]>() {

            @Override
            public String[] getValue(Integer integer) {
                String[] res=condition.get(integer).getSqlConditionArgs();
                return res==null?nullArray:res;
            }

            @Override
            public int size() {
                return condition.size();
            }
        });
        String sqlCondition=tableOrSubQuery;
        if(condition.size()==0)
            sqlCondition=getSelectIfTable(tableOrSubQuery,cols, orderBy);
        else
            sqlCondition=constructSql(sqlCondition,subTableAs,cols, orderBy, condition.get(0));
        for(int i=1;i<condition.size();i++){
            sqlCondition=constructSql(sqlCondition,null,null, null, condition.get(i));
        }
        return new Pair<>(sqlCondition,args);
    }

    public static String getSelectIfTable(@NonNull String tableOrSubQuery, String[] cols, String orderBy)
    {
        return "Select "+getColsString(cols)+" From ("+tableOrSubQuery+")"+getString(orderBy,""," Order By "+orderBy);
    }

    public static String  getColsString(String[] col)
    {
        if(col==null)
            return "*";
        else
            return Util.join(",",col);
    }

    public static String getString(Object o,String onNull,String onNonNull)
    {
        return o==null?onNull:onNonNull;
    }

}
