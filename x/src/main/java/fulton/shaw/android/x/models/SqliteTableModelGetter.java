package fulton.shaw.android.x.models;

import java.util.ArrayList;
import java.util.HashMap;

import fulton.shaw.android.x.public_interfaces.SqliteHelper;
import fulton.util.android.interfaces.ViewInfo;
import fulton.util.android.utils.SqlUtil;

/**
 * Created by 13774 on 8/14/2017.
 */

public class SqliteTableModelGetter extends LazyGetter {
    protected static HashMap mStores;
    private static SqliteTableModelGetter sGetter;

    private SqliteTableModelGetter()
    {}

    public static ArrayList<ViewInfo> get(@SqliteHelper.TableName  String table)
    {
        if(sGetter==null)
            sGetter=new SqliteTableModelGetter();
        return sGetter.get(table,ArrayList.class);
    }
    @Override
    protected <E, V> V instanceGet(E e) {
        if(e instanceof String)
        {
            switch ((String)e)
            {
                case SqliteHelper.TABLE_DATE_RECORD:
                    return (V) new ArrayList<ViewInfo>(){{
                add(new ViewInfo(SqlUtil.COL_ID,Long.class));
                add(new ViewInfo(SqlUtil.COL_TARGETING_DATE,String.class));
                add(new ViewInfo(SqlUtil.COL_CREATED_TIME,String.class));
                add(new ViewInfo(SqlUtil.COL_SUMMARY,String.class));
                add(new ViewInfo(SqlUtil.COL_LOCATION,String.class));
                add(new ViewInfo(SqlUtil.COL_WEATHER,String.class));
                add(new ViewInfo(SqlUtil.COL_REMARK,String.class));
                add(new ViewInfo(SqlUtil.COL_LEVEL,Float.class));
            }};
                case SqliteHelper.TABLE_INCOME:
                    return (V) new ArrayList<ViewInfo>(){{
                        add(new ViewInfo(SqlUtil.COL_ID,Long.class));
                        add(new ViewInfo(SqlUtil.COL_INNER_ID,Long.class));
                        add(new ViewInfo(SqlUtil.COL_CREATED_TIME,String.class));
                        add(new ViewInfo(SqlUtil.COL_EVENT_TIME,String.class));
                        add(new ViewInfo(SqlUtil.COL_RECORD,Integer.class));
                        add(new ViewInfo(SqlUtil.COL_REASON,String.class));
                    }};
                case SqliteHelper.TABLE_GENERAL_RECORDS:
                    return (V) new ArrayList<ViewInfo>(){{
                        add(new ViewInfo(SqlUtil.COL_ID,Long.class));
                        add(new ViewInfo(SqlUtil.COL_CREATED_TIME,String.class));
                        add(new ViewInfo(SqlUtil.COL_WEATHER,String.class));
                        add(new ViewInfo(SqlUtil.COL_PLACE,String.class));
                        add(new ViewInfo(SqlUtil.COL_CAUSING,String.class));
                        add(new ViewInfo(SqlUtil.COL_TARGET,String.class));
                        add(new ViewInfo(SqlUtil.COL_BRIEF,String.class));
                        add(new ViewInfo(SqlUtil.COL_DETAIL,String.class));
                        add(new ViewInfo(SqlUtil.COL_MAIN_TAG,String.class));
                        add(new ViewInfo(SqlUtil.COL_SHOWN_DATE,String.class));
                        add(new ViewInfo(SqlUtil.COL_SHOWN_TYPE,Integer.class));
                    }};
                case SqliteHelper.TABLE_REVIEW:
                    return (V) new ArrayList<ViewInfo>(){{
                        add(new ViewInfo(SqlUtil.COL_ID,Long.class));
                        add(new ViewInfo(SqlUtil.COL_TABLE_NAME,String.class));
                        add(new ViewInfo(SqlUtil.COL_INNER_ID,Long.class));
                        add(new ViewInfo(SqlUtil.COL_CONTENT,String.class));
                        add(new ViewInfo(SqlUtil.COL_CREATED_TIME,String.class));
                    }};
                case SqliteHelper.TABLE_ALARM:
                    return (V) new ArrayList<ViewInfo>(){{
                        add(new ViewInfo(SqlUtil.COL_ID,Long.class));
                        add(new ViewInfo(SqlUtil.COL_TABLE_NAME,String.class));
                        add(new ViewInfo(SqlUtil.COL_INNER_ID,Long.class));
                        add(new ViewInfo(SqlUtil.COL_CAUSING,String.class));
                        add(new ViewInfo(SqlUtil.COL_REPEAT_TYPE,Integer.class));
                        add(new ViewInfo(SqlUtil.COL_START_TIME,Long.class));
                        add(new ViewInfo(SqlUtil.COL_END_TIME,Long.class));
                        add(new ViewInfo(SqlUtil.COL_CREATED_TIME,Long.class));
                    }};
                case SqliteHelper.TABLE_TAG:
                    return (V) new ArrayList<ViewInfo>(){{
                            add(new ViewInfo(SqlUtil.COL_ID, Long.class));
                            add(new ViewInfo(SqlUtil.COL_TABLE_NAME, String.class));
                            add(new ViewInfo(SqlUtil.COL_INNER_ID, Long.class));
                            add(new ViewInfo(SqlUtil.COL_CONTENT, String.class));
                        }};
                case SqliteHelper.TABLE_REFERENCE:
                    return (V) new ArrayList<ViewInfo>(){{
                        add(new ViewInfo(SqlUtil.COL_ID, Long.class));
                        add(new ViewInfo(SqlUtil.COL_TABLE_NAME, String.class));
                        add(new ViewInfo(SqlUtil.COL_INNER_ID, Long.class));
                        add(new ViewInfo(SqlUtil.COL_REF_TABLE_NAME, String.class));
                        add(new ViewInfo(SqlUtil.COL_REF_INNER_ID, Long.class));
                        add(new ViewInfo(SqlUtil.COL_RELATION, String.class));
                    }};
                default:
                    throw new UnsupportedOperationException("table "+e+" not supported");

            }
        }
        throw new UnsupportedOperationException(e.getClass()+" is not String");
    }

    @Override
    protected HashMap<Object, Object> getStores() {
        if(mStores==null)
        {
            mStores=new HashMap();
        }
        return mStores;
    }
}
