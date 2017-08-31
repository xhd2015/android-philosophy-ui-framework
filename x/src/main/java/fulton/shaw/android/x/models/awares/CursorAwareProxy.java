package fulton.shaw.android.x.models.awares;

import android.database.Cursor;
import android.support.annotation.Nullable;

import java.util.Calendar;

import fulton.util.android.utils.CalendarUtil;
import fulton.util.android.utils.SqlUtil;
import fulton.util.android.notations.HelperStaticMethods;

/**
 * Created by 13774 on 8/8/2017.
 */

public class CursorAwareProxy implements ContentAware,ExternReferenceAware,RowIdAware,CreatedTimeAware{
    private Cursor mCursor;

    public CursorAwareProxy(Cursor cursor)
    {
        mCursor=cursor;
    }

    @Override
    public @Nullable Integer getId() {
        return getAsInteger(mCursor,SqlUtil.COL_ID);
    }

    @Override
    public String getContent() {
        return mCursor.getString(mCursor.getColumnIndex(SqlUtil.COL_CONTENT));
    }

    @Override
    public String getTableName() {
        return mCursor.getString(mCursor.getColumnIndex(SqlUtil.COL_TABLE_NAME));
    }

    @Override
    public Integer getInnerId() {
        return getAsInteger(mCursor,SqlUtil.COL_INNER_ID);
    }

    @HelperStaticMethods
    public static Integer getAsInteger(Cursor cursor,String key)
    {
        int index=cursor.getColumnIndex(key);
        if(index < 0 || cursor.isNull(index))
            return null;
        else
            return cursor.getInt(index);
    }

    @HelperStaticMethods
    public static Double getAsDouble(Cursor cursor,String key)
    {
        int index=cursor.getColumnIndex(key);
        if(index < 0 || cursor.isNull(index))
            return null;
        else
            return cursor.getDouble(index);
    }

    @Override
    public Calendar getCreatedTime() {
        Double t=getAsDouble(mCursor,SqlUtil.COL_CREATED_TIME);
        return t==null?null:CalendarUtil.timeInMillisToCalendar((long)(double)t);
    }
}
