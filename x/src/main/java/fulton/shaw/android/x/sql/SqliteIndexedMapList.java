package fulton.shaw.android.x.sql;

import android.content.ContentValues;
import android.database.Cursor;
import android.support.annotation.NonNull;

import java.util.ArrayList;

import fulton.shaw.android.x.public_interfaces.SqliteHelper;
import fulton.util.android.utils.SqlUtil;

/**
 * Created by 13774 on 8/15/2017.
 */

/**
 *  map into a list.
 * @param <K> must be one of primitive type
 * @param <V> must be one of primitive type
 */
public class SqliteIndexedMapList<K,V> {

    protected SqliteHelper mSqliteHelper;
    protected String mTable;
    protected String mIndexCol;
    protected String mContentCol;

    protected String mSelectAny;
    protected String mSelectAnyWhereIndexEquals;
    protected Class<K> mKeyClass;
    protected Class<V> mValueClass;
    protected StringValueGetter<K> mKeyStringGetter;
    protected StringValueGetter<V> mValueStringGetter;

    public interface StringValueGetter<ValueType>{
        String getString(@NonNull ValueType v);
    }
    public static class DefaultStringValueGetter<ValueType> implements StringValueGetter<ValueType>{

        @Override
        public String getString(@NonNull ValueType v) {
            return v.toString();
        }
    }

    public SqliteIndexedMapList(String table, String indexCol, String contentCol,Class<K> keyClass,Class<V> valueClass,
                                StringValueGetter<K> keyStringGetter,
                                StringValueGetter<V> valueStringGetter
                                )
    {
        mTable=table;
        mIndexCol=indexCol;
        mContentCol=contentCol;
        mKeyClass=keyClass;
        mValueClass=valueClass;
        mKeyStringGetter=keyStringGetter;
        mValueStringGetter=valueStringGetter;



        mSelectAny="Select * From "+mTable;
        mSelectAnyWhereIndexEquals=mSelectAny+" Where "+indexCol+" = ?";
    }
    protected SqliteIndexedMapList(String table, String indexCol, String contentCol,Class<K> keyClass,Class<V> valueClass)
    {
        this(table, indexCol,contentCol,keyClass,valueClass,new DefaultStringValueGetter<K>(),new DefaultStringValueGetter<V>());
    }
    public ArrayList<V> get(K key)
    {
        checkKeyThrowError(key);
        Cursor cursor=mSqliteHelper.rawQuery("Select "+mContentCol+" From "+mTable+" Where "+mIndexCol+"=?",new String[]{mKeyStringGetter.getString(key)});
        ArrayList<V> res=new ArrayList<>(cursor.getCount());
        while (cursor.moveToNext())
            res.add(SqlUtil.getCursorValue(cursor,0,mValueClass));
        cursor.close();
        return res;
    }
    public void set(K key,ArrayList<V> list)
    {
        remove(key);
        for(V value:list)
        {
            add(key,value);
        }
    }
    public V get(K key,int j)
    {
        checkKeyThrowError(key);
        String sKey=mKeyStringGetter.getString(key);
        Cursor cursor=mSqliteHelper.rawQuery(mSelectAnyWhereIndexEquals,new String[]{sKey});
        if(cursor.getCount()<=j)
            throw new ArrayIndexOutOfBoundsException(" size="+cursor.getCount()+",index="+j);
        cursor.moveToPosition(j);
        V res=SqlUtil.getCursorValue(cursor,mContentCol,mValueClass);

        cursor.close();
        return res;
    }
    public ArrayList<ArrayList<V>> getAllDataset()
    {
        Cursor cursor=mSqliteHelper.rawQuery("Select "+mContentCol+" From "+mTable+" Order By "+mIndexCol,null);//Group By auto distinct
        ArrayList<ArrayList<V>> res=new ArrayList<>();
        long lastInnerId=-1;
        ArrayList<V> curList=null;
        while (cursor.moveToNext())
        {
            long thisInnerId=cursor.getLong(cursor.getColumnIndex(SqlUtil.COL_INNER_ID));
            if(lastInnerId!=thisInnerId)
            {
                curList=new ArrayList<>();
                res.add(curList);
                lastInnerId=thisInnerId;
            }
            curList.add(SqlUtil.getCursorValue(cursor,0,mValueClass));
        }
        cursor.close();
        return res;
    }

    /**
     *
     * @return _id, key,value
     */
    public ArrayList<ArrayList<Object[]>> getAllDatasetFullInfo()
    {
        Cursor cursor=mSqliteHelper.rawQuery("Select "+SqlUtil.COL_ID+","+mIndexCol+","+mContentCol+" From "+mTable+" Order By "+mIndexCol,null);//Group By auto distinct
        ArrayList<ArrayList<Object[]>> res=new ArrayList<>();
        long lastInnerId=-1;
        ArrayList<Object[]> curList=null;
        while (cursor.moveToNext())
        {
            long thisInnerId=cursor.getLong(cursor.getColumnIndex(SqlUtil.COL_INNER_ID));
            if(lastInnerId!=thisInnerId)
            {
                curList=new ArrayList<>();
                res.add(curList);
                lastInnerId=thisInnerId;
            }

            curList.add(new Object[]{
                    SqlUtil.getCursorValue(cursor,0,Long.class),
                    SqlUtil.getCursorValue(cursor,1,mKeyClass),
                    SqlUtil.getCursorValue(cursor,0,mValueClass)
            });
        }
        cursor.close();
        return res;
    }

    public boolean set(K key,int j,V value)
    {
        checkKeyThrowError(key);
        String sKey=mKeyStringGetter.getString(key);
        Cursor cursor=mSqliteHelper.rawQuery(mSelectAnyWhereIndexEquals,new String[]{sKey});
        if(cursor.getCount()<=j)
            throw new ArrayIndexOutOfBoundsException(" size="+cursor.getCount()+",index="+j);
        cursor.moveToPosition(j);
        long id=SqlUtil.getCursorId(cursor);
        cursor.close();
        return mSqliteHelper.updateItemInTable(mTable,id,mContentCol,value==null?null:mValueStringGetter.getString(value));
    }

    public void add(K key,V value)
    {
        checkKeyThrowError(key);
        if(value==null)
            mSqliteHelper.execSQL("Insert Into "+mTable+"("+mIndexCol+") Values(?)",new String[]{mKeyStringGetter.getString(key)});
        else
            mSqliteHelper.execSQL("Insert Into "+mTable+"("+mIndexCol+","+mContentCol+") Values(?,?)",new String[]{mKeyStringGetter.getString(key),
                    mValueStringGetter.getString(value)});
    }

    protected void checkKeyThrowError(K key)
    {
        if(key==null)
            throw new NullPointerException("Key should not be null");
    }

    public void remove(K key)
    {
        checkKeyThrowError(key);
        mSqliteHelper.deleteItemInTable(mTable,mIndexCol,mKeyStringGetter.getString(key));
    }
    public boolean remove(K key,int j)
    {
        checkKeyThrowError(key);
        String sKey=mKeyStringGetter.getString(key);
        long id=getId(sKey,j);
        return mSqliteHelper.deleteItemInTable(mTable,id);
    }

    public @interface EnsureChecked {}

    @EnsureChecked
    protected long getId(String key,int j)
    {
        Cursor cursor=mSqliteHelper.rawQuery(mSelectAnyWhereIndexEquals,new String[]{key});
        if(cursor.getCount()<=j)
            throw new ArrayIndexOutOfBoundsException(" size="+cursor.getCount()+",index="+j);
        cursor.moveToPosition(j);
        long id=SqlUtil.getCursorId(cursor);
        cursor.close();
        return id;
    }
    public boolean contains(K key)
    {
        return size(key)>0;
    }


    public int size()
    {
        Cursor cursor=mSqliteHelper.rawQuery("Select Count(*) From "+mTable+" Group By "+mIndexCol,null);
        cursor.moveToFirst();
        int size=cursor.getCount();
        cursor.close();
        return size;
    }
    public int size(K key)
    {
        checkKeyThrowError(key);
        Cursor cursor=mSqliteHelper.rawQuery("Select Count(*) From "+mTable+" Where "+mIndexCol+"=?",new String[]{mKeyStringGetter.getString(key)});
        cursor.moveToFirst();
        int valSize=cursor.getInt(0);
        cursor.close();
        return valSize;
    }

}
