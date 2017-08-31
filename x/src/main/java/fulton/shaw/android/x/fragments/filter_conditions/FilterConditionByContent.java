package fulton.shaw.android.x.fragments.filter_conditions;

import fulton.util.android.utils.Util;

/**
 *   you must provide _,% yourself when you select mode {@link #SEARCH_SEPERATE}.And no special character is expected.
 */
public class FilterConditionByContent extends FilterConditionBase{
    public static final int SEARCH_ALL=0;
    public static final int SEARCH_SEPERATE=1;

    private int mSearchMode;
    private String[] mSearchCols;
    private String[] mSearchArgs;//the % is provided by you, and no special character is expected.

    public FilterConditionByContent(int searchMode, String[] searchCols, String[] searchArgs) {
        mSearchMode = searchMode;
        mSearchCols = searchCols;
        mSearchArgs = searchArgs;
    }

    public int getSearchMode() {
        return mSearchMode;
    }

    public void setSearchMode(int searchMode) {
        mSearchMode = searchMode;
    }

    public String[] getSearchCols() {
        return mSearchCols;
    }

    public void setSearchCols(String[] searchCols) {
        mSearchCols = searchCols;
    }

    public String[] getSearchArgs() {
        return mSearchArgs;
    }

    public void setSearchArgs(String[] searchArgs) {
        mSearchArgs = searchArgs;
    }

    @Override
    public void updateCachedResult() {
        if(mSearchCols==null||mSearchCols.length==0)
        {
            setCachedSql(null);
            setCachedArgs(null);
        }else{
            if(mSearchMode==SEARCH_ALL)
            {
                StringBuilder sb=new StringBuilder();
                for(int i=0;i<mSearchCols.length;i++)
                {
                    sb.append(mSearchCols[i]+" = ? ");
                    if(i<=mSearchCols.length-2)
                        sb.append(" and ");
                }
                setCachedSql(sb.toString());
                setCachedArgs(mSearchArgs);
            }else if(mSearchMode==SEARCH_SEPERATE){
                // like
                StringBuilder sb=new StringBuilder();
                for(int i=0;i<mSearchCols.length;i++)
                {
                    sb.append(mSearchCols[i]).append(" like \"").append(mSearchArgs[i]).append("\"");
                    if(i<=mSearchCols.length-2)
                        sb.append(" and ");
                }
                setCachedSql(sb.toString());
                setCachedArgs(null);
            }
        }
    }
}
