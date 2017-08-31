package fulton.util.android.interfaces.valueproviders;

import android.content.ContentValues;
import android.database.Cursor;

import java.util.HashSet;
import java.util.Set;

import fulton.util.android.interfaces.ValueGetter;
import fulton.util.android.public_interfaces.BaseSqliteHelper;
import fulton.util.android.utils.SqlUtil;

/**
 * Created by 13774 on 8/27/2017.
 */

/**
 *  cursor is got table & id.
 *
 *   you can completely create a new,then Id is got automatically.
 */
public class ValueProviderOfCachedCursor extends ValueProvider<String,Set<String>> {
    private String mTable;
    private long mId;
    private Cursor mCursor;
    private ContentValues mCache;
    private String mCols;

    /**
     *  for adding purepose.
     * @param table
     * @param cols
     */
    public ValueProviderOfCachedCursor(String table, String[] cols)
    {
        mCache=new ContentValues();
        mTable=table;
        mId=-1;
        mCursor=null;
        mCols=SqlUtil.makeColumns(cols);
    }

    /**
     *  for updating purepose
     * @param helper
     * @param table
     * @param id
     * @param cols
     */
    public ValueProviderOfCachedCursor(BaseSqliteHelper helper, String table, long id, String[] cols)
    {
        mTable=table;
        mId=id;
        mCols=SqlUtil.makeColumns(cols);
        mCursor=helper.getCursorByIdCols(table,id,mCols);
        if(mCursor.getCount()!=1)
            throw new RuntimeException("The cursor format is not targeting type");
        mCursor.moveToFirst();
        mCache=new ContentValues();
    }


    public long getId()
    {
        return mId;
    }
    public String getTable()
    {
        return mTable;
    }

    public void commit(BaseSqliteHelper helper)
    {
        if( (mId>=0 && helper.updateItemInTable(mTable,mId,mCache)) ||
                (mId<0 && (mId=helper.insert(mTable,null,mCache))>=0)
                ) {
            if(mCursor!=null)
                mCursor.close();
            mCache.clear();
            mCursor = helper.getCursorByIdCols(mTable, mId,mCols);
            if(mCursor.getCount()!=1)
                throw new RuntimeException("Not Targeting format.Check if it is identified by _id");
            mCursor.moveToFirst();
        }else{
            throw new RuntimeException("Cannot update cache.");
        }
    }

    @Override
    public <S> S getValue(String k, Class<S> valueClass) {
        if(mCache.containsKey(k))
            return (S) mCache.get(k);
        else
            return mCursor==null?null:SqlUtil.getCursorValue(mCursor,k,valueClass);
    }

    @Override
    public <S> S getOrDefaultValue(String k, @ValueGetter.ValueGetterType Object defaultValue, Class<S> valueClass) {
        int index=0;
        if(mCache.containsKey(k))
            return (S)mCache.get(k);
        else if(mCursor!=null && !mCursor.isNull(index=mCursor.getColumnIndex(k)))
            return SqlUtil.getCursorValue(mCursor,index,valueClass);
        else
            return (S) ValueGetter.getValueFromObjectOrGetter(k,defaultValue);
    }

    @Override
    public <S> S getOrPutDefaultValue(String k, @ValueGetter.ValueGetterType Object defaultValue, Class<S> valueClass) {
        int index=0;
        if(mCache.containsKey(k))
            return (S)mCache.get(k);
        else if(mCursor!=null && !mCursor.isNull(index=mCursor.getColumnIndex(k)))
            return SqlUtil.getCursorValue(mCursor,index,valueClass);
        else{
            S value= (S) ValueGetter.getValueFromObjectOrGetter(k,defaultValue);
            SqlUtil.putValue(mCache,k,value);
            return value;
        }
    }

    @Override
    public void putValue(String k, Object value) {
        SqlUtil.putValue(mCache,k,value);
    }

    /**
     *    the context is different.
     *    removing it means that set it to default value? or set it to null?
     *
     *    we don't know.so for data put by user,we handle it.
     *    otherwise we return false.
     *    if user want to remove it,use putValue with null.
     * @param k
     * @return
     */
    @Override
    public boolean remove(String k) {
        if(mCache.containsKey(k)) {
            mCache.remove(k);
            return true;
        }else{
            throw new UnsupportedOperationException("the key requested cannot be removed from cursor directly." +
                    "If you want to set it to null or override its value,use putValue() instead.We cannot understand your true intend");
        }
    }

    @Override
    public Set<String> getKeys() {
        Set<String> newSet=new HashSet<>();
        newSet.addAll(mCache.keySet());
        if(mCursor!=null)
            for(String s:mCursor.getColumnNames())
                newSet.add(s);
        return newSet;
    }

    @Override
    public boolean contains(String k) {
        return mCache.containsKey(k) || (mCursor!=null && !mCursor.isNull(mCursor.getColumnIndex(k)));
    }
}
