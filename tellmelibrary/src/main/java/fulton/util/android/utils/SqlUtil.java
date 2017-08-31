package fulton.util.android.utils;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import fulton.util.android.interfaces.comparators.ListBasedComparator;

/**
 * Created by 13774 on 7/24/2017.
 */

/**
 * 为了达成最后我能 用一步完成所有事情的宏愿，我写下了这些代码
 *
 * contains all COL_XX
 */
public class SqlUtil {

    public static final String COL_ID="_id";
    public static final String COL_CREATED_TIME="createTime";
    public static final String COL_PLACE="place";
    public static final String COL_CAUSING="causing";
    public static final String COL_TARGET="target";
    public static final String COL_BRIEF="brief";
    public static final String COL_DETAIL="detail";
    public static final String COL_SHOWN_DATE="shownDate";
    public static final String COL_SHOWN_TYPE="shownType";//TYPE_NORMAL,TYPE_CONSTANT.
    public static final String COL_THING="thing";
    public static final String COL_START_TIME="startTime";
    public static final String COL_END_TIME="endTime";
    public static final String COL_SRC="src";
    public static final String COL_DST="dst";
    public static final String COL_PREDICT_COST_TIME="predictCostTime";
    public static final String COL_PREDICT_COST_MONEY="predictCostMoney";
    public static final String COL_PREDICT_METHOD="predictMethod";
    public static final String COL_BENIFIT_OR_GOOD_PLAN="benifitOrGoodPlan";;
    public static final String COL_REAL_COST_TIME="realCostTime";
    public static final String COL_REAL_COST_MONEY="realCostMoney";
    public static final String COL_TABLE_NAME="tableName";
    public static final String COL_INNER_ID="innerId";
    public static final String COL_REF_TABLE_NAME="refTableName";
    public static final String COL_REF_INNER_ID="refInnerId";
    public static final String COL_CONTENT="content";
    public static final String COL_REPEAT_TYPE="repeatType";
    public static final String COL_STATUS="status";
    public static final String COL_REAL_METHOD="realMethod";
    public static final String COL_CONDITION="condition";
    public static final String COL_PRICE="price";
    public static final String COL_DESCRIPTION="description";
    public static final String COL_MAIN_TAG="mainTag";


    public static final String COL_LAST_MODIFIED_TIME="lastModifiedTime";
    public static final String COL_NAME="name";
    public static final String COL_YEAR="year";
    public static final String COL_MONTH="month";
    public static final String COL_DATE="date";
    public static final String COL_HOUR="hour";
    public static final String COL_MINUTE="minute";
    public static final String COL_ISSUE="issue";
    public static final String COL_TYPE="type";
    public static final String COL_NOTE="note";
    public static final String COL_FESTNAME="festname";
    public static final String COL_WEATHER_STATE="weatherState";
    public static final String COL_PROVINCE_ID="province_id";
    public static final String COL_CITY_ID="cityId";
    public static final String COL_QUERY_ID="queryId";
    public static final String COL_CUR_TEMPERATURE ="curTemperature";//last update's temperature
    public static final String COL_MAX_TEMPERATURE ="maxTemperature";
    public static final String COL_MIN_TEMPERATURE ="minTemperature";
    public static final String COL_LAST_UPDATE_HOUR="lastUpdateHour";
    public static final String COL_LAST_UPDATE_MINUTE="lastUpdateMinute";
    public static final String COL_WIND_SCALE="windScale";
    public static final String COL_WIND_DIRECTION="windDirection";
    public static final String COL_WEATHER_TEXT ="weatherText";
    public static final String COL_SUGGESTION_CAR_WASHING="suggestionCarWashing";
    public static final String COL_SUGGESTION_DRESSING="suggestionDressing";
    public static final String COL_SUGGESTION_FLU="suggestionFlu";
    public static final String COL_SUGGESTION_SPORT="suggestionSport";
    public static final String COL_SUGGESTION_TRAVEL="suggestionTravel";
    public static final String COL_SUGGESTION_UV="suggestionUV";//紫外线
    public static final String SELECT_COL_COUNT_ALL="count(*)";
    public static final String COL_SRC_PROV_ID="srcProvId";
    public static final String COL_SRC_CITY_ID="srcCityId";
    public static final String COL_DST_PROV_ID="dstProvId";
    public static final String COL_DST_CITY_ID="dstCityId";
    public static final String COL_TARGETING_DATE="targetingDate";
    public static final String COL_SUMMARY="summary";
    public static final String COL_LOCATION="location";
    public static final String COL_WEATHER="weather";
    public static final String COL_REMARK="remark";
    public static final String COL_LEVEL="level";
    public static final String COL_EVENT_TIME="eventTime";
    public static final String COL_RECORD="record";
    public static final String COL_REASON="reason";
    public static final String COL_RELATION ="relation";
    public static final String COL_DATE_MODEL="dateModel";
    public static final String COL_FILTER_CONDITION="filterCondition";
    public static final String COL_THE_END="theEnd";

    public static final String SQL_DROP_TABLE_IF_EXISTS ="Drop Table If Exists ";
    public static final String SQL_INTEGER_PRIMARY_KEY_AUTOINCREMENT =" Integer Primary Key Autoincrement ";
    public static final String SQL_CREATE_TABLE="Create Table ";
    public static final String SQL_ID_INTEGER_PRIMARY_KEY_AUTOINCREMENT=COL_ID+SQL_INTEGER_PRIMARY_KEY_AUTOINCREMENT;
    public static final String SQL_SHORT_STRING=" Character(16) ";
    public static final String SQL_TAG_STRING=" Character(64) ";
    public static final String SQL_NOT_NULL=" Not Null ";
    public static final String SQL_CHECK_NOT_EMPTY_START=" Check(";
    public static final String SQL_CHECK_NOT_EMPTY_END="!='') ";
    public static final String SQL_DATE=" Date ";
    public static final String SQL_TIME=" Time ";
    public static final String SQL_SUMMARY_STRING=" Varchar(1024) ";
    public static final String SQL_LOCATION_STRING=" Varchar(64) ";
    public static final String SQL_WEATHER_STRING=" Varchar(32) ";
    public static final String SQL_RECORD_STRING=" Varchar(64) ";
    public static final String SQL_REFERENCE_STRING=" Varchar(64) ";
    public static final String SQL_REASON_STRING=" Varchar(512) ";
    public static final String SQL_REMARK_STRING=" Varchar(64) ";
    public static final String SQL_DATETIME=" Datetime ";
    public static final String SQL_DATETIME_NOT_NULL=" Datetime Not Null ";
    public static final String SQL_DESCRIPTIVE_STRING=" Character(128) ";
    public static final String SQL_INTRODUCTIVE_STRING=" Character(256) ";
    public static final String SQL_CONTENT_STRING=" Character(512) ";
    public static final String SQL_LONG=" Long ";
    public static final String SQL_DOUBLE=" Double ";
    public static final String SQL_FLOAT=" Float ";
    public static final String SQL_INT=" Integer ";
    public static final String SQL_SEG=" , ";
    public static final String SQL_START_CLOSE=" ( ";
    public static final String SQL_END_CLOSE=" ) ";
    public static final String SQL_THE_END=" ; ";
    public static final String SQL_DEFAULT=" Default ";
    public static final String SQL_NULL=" Null ";
    public static final String SQL_DEFAULT_NULL=" Default Null ";
    public static final String SQL_DEFAULT_0=" Default 0 ";
    public static final String SQL_DEFAULT_CURRENT_TIMESTAMP =" Default CURRENT_TIMESTAMP ";
    public static final String SQL_DEFAULT_CURRENT_DATE=" Default (Date(CURRENT_TIMESTAMP,'localtime')) ";
    public static final String SQL_DEFAULT_CURRENT_DATETIME=" Default (Datetime(CURRENT_TIMESTAMP,'localtime')) ";
    public static final String SQL_DEFAULT_CURRENT_TIME=" Default (Time(CURRENT_TIMESTAMP,'localtime')) ";
    public static final String SQL_DATETIME_NOW_LOCALTIME =" Datetime('now','localtime') ";

    public static final String FORMATTER_CREATE_TABLE_GENERAL="Create Table %s(%s);";
    public static final String FORMATTER_CREATE_TABLE_WITH_ID="Create Table %s("+SQL_ID_INTEGER_PRIMARY_KEY_AUTOINCREMENT+",%s);";

    public static final String TAG_PROBLEM="problem";
    public static final String TAG_DIARY="diary";
    public static final String TAG_COMMON_SENSE="commonSense";
    public static final String TAG_THOUGHT="thought";
    public static final String TAG_SAY_SOMETHING="saySomething";
    public static final String TAG_BLOG="blog";

    public static final int SHOWN_TYPE_ALL=-1;//this cannot be placed into database;
    public static final int SHOWN_TYPE_NORMAL=0;
    public static final int SHOWN_TYPE_CONSTANT=1;

    public static final String STATUS_NOT_COMPLISHED="notComplished";
    public static final String STATUS_COMPLISHED="complished";

    //====Tag Filter Condition
    public static final int FILTER_NO_APPLY=-1;//means do not apply
    public static final int FILTER_ANY=0;
    public static final int FILTER_ALL=1;
    public static final int FILTER_NOT=2;

    //===Date Filter COndition
    public static final int DATE_MODEL_ALL_DATE=0;
    public static final int DATE_MODEL_JUST_DAYS_TO_TODAY=1;
    public static final int DATE_MODEL_FROM_START_TO_END=2;
    public static final int DATE_MODEL_JUST_DAYS_FROM_START=3;
    public static final int DATE_MODEL_FROM_START=4;
    public static final int DATE_MODEL_TO_END=5;
    public static final int DATE_MODEL_THIS_WEEK_TO_TODAY=6;
    @IntDef({DATE_MODEL_ALL_DATE,DATE_MODEL_JUST_DAYS_TO_TODAY,DATE_MODEL_FROM_START_TO_END,
            DATE_MODEL_JUST_DAYS_FROM_START,
            DATE_MODEL_FROM_START,
            DATE_MODEL_TO_END,DATE_MODEL_THIS_WEEK_TO_TODAY})
    public @interface DateModelType{}

    /**
     *
     * @param c
     * @return  null value is not present in the content
     */
    public static ContentValues[] cursorToContentValues(Cursor c)
    {
        ContentValues[] value=new ContentValues[c==null?0:c.getCount()];
        if(c!=null)
        {
            int pos=c.getPosition();
            int j=0;
            while (c.moveToNext()) {
                value[j]=new ContentValues();
                for (int i = 0; i < c.getColumnCount(); i++) {
                    switch (c.getType(i))//cannot be null,because this must exist
                    {
                        case Cursor.FIELD_TYPE_STRING:
                            value[j].put(c.getColumnName(i), c.getString(i));
                            break;
                        case Cursor.FIELD_TYPE_INTEGER:
                            value[j].put(c.getColumnName(i), c.getInt(i));
                            break;
                        case Cursor.FIELD_TYPE_FLOAT:
                            value[j].put(c.getColumnName(i), c.getFloat(i));
                            break;
                        case Cursor.FIELD_TYPE_BLOB:
                            value[j].put(c.getColumnName(i), c.getBlob(i));
                            break;
                    }
                }
                j++;
            }
            c.moveToPosition(pos);
        }
        return value;
    }

    public static String dumpCursorValue(Cursor cursor)
    {
        return Arrays.deepToString(cursorToContentValues(cursor));
    }

    public static String[] getTableColumns(SQLiteDatabase db,String table)
    {
        Cursor c=db.rawQuery("Select * From "+table+" Where 0;",null);
        String[] res=c.getColumnNames();
        c.close();
        return res;
    }
    /**
     *  if key does not exist or is null,return null.
     * @param cursor
     * @param key
     * @param valueClass
     * @param <E>
     * @return
     */
    public static <E> E getCursorValue(Cursor cursor,String key,@NonNull Class<E> valueClass)
    {
        int index=cursor.getColumnIndex(key);
        if(valueClass==Object.class)
            return getCursorValueByInnerType(cursor,index,valueClass);
        else
            return getCursorValue(cursor,index,valueClass);
    }

    public static String makeColumns(String[] string)
    {
        if(string==null)
            return "*";
        else
            return Util.join(",",string);
    }

    public static <E> E getCursorValue(Cursor cursor,int index,Class<E> valueClass)
    {
        if(index!=-1 && !cursor.isNull(index)) {
            if (String.class.isAssignableFrom(valueClass)) {
                return (E) cursor.getString(index);
            } else if (Integer.class.isAssignableFrom(valueClass)) {
                return (E) (Integer) cursor.getInt(index);
            } else if (Long.class.isAssignableFrom(valueClass)) {
                return (E) (Long) cursor.getLong(index);
            } else if (Short.class.isAssignableFrom(valueClass)) {
                return (E) (Short) cursor.getShort(index);
            } else if (Float.class.isAssignableFrom(valueClass)) {
                return (E) (Float) cursor.getFloat(index);
            } else if (Double.class.isAssignableFrom(valueClass)) {
                return (E) (Double) cursor.getDouble(index);
            } else if (byte[].class.isAssignableFrom(valueClass)) {
                return (E) cursor.getBlob(index);
            }else{
                throw new UnsupportedClassVersionError(valueClass.getName()+" not supported");
            }
        }
        return null;
    }

    public static <E> E getCursorValueByInnerType(Cursor cursor,int index,Class<E> valueClass)
    {
        if(index!=-1 && !cursor.isNull(index))
        {
            switch (cursor.getType(index))
            {
                case Cursor.FIELD_TYPE_STRING:
                    return (E) cursor.getString(index);
                case Cursor.FIELD_TYPE_INTEGER:
                    if(valueClass==Long.class)
                        return (E)(Long)cursor.getLong(index);
                    else if(valueClass==Short.class)
                        return (E)(Short)cursor.getShort(index);
                    else
                        return (E)(Integer)cursor.getInt(index);
                case Cursor.FIELD_TYPE_FLOAT:
                    if(valueClass==Double.class)
                        return (E)(Double) cursor.getDouble(index);
                    else
                        return (E)(Float)cursor.getFloat(index);
                case Cursor.FIELD_TYPE_BLOB:
                    return (E)cursor.getBlob(index);
                case Cursor.FIELD_TYPE_NULL:
                    return null;
            }
        }
        return null;
    }

    public static long getCursorId(Cursor cursor)
    {
        return getCursorValue(cursor,SqlUtil.COL_ID,Long.class);
    }

    /**
     *
     *  if (String.class.isAssignableFrom(valueClass)) {

     } else if (Integer.class.isAssignableFrom(valueClass)) {

     } else if (Long.class.isAssignableFrom(valueClass)) {

     } else if (Short.class.isAssignableFrom(valueClass)) {

     } else if (Float.class.isAssignableFrom(valueClass)) {

     } else if (Double.class.isAssignableFrom(valueClass)) {

     } else if (byte[].class.isAssignableFrom(valueClass)) {

     }else{
     throw new UnsupportedClassVersionError(valueClass.getName()+" not supported");
     }
     *
     * @param values
     * @param key
     * @param v
     * @param <V>
     * @return
     */
    public static <V> ContentValues putValue(ContentValues values,String key,V v) {
        if (v != null)
        {
                Class valueClass=v.getClass();
                if (String.class==valueClass) {
                    values.put(key,(String) v);
                } else if (Integer.class==valueClass) {
                    values.put(key,(Integer) v);
                } else if (Long.class==valueClass) {
                    values.put(key,(Long) v);
                } else if (Short.class==valueClass) {
                    values.put(key,(Short)v);
                } else if (Float.class==valueClass) {
                    values.put(key,(Float) v);
                } else if (Double.class==valueClass) {
                    values.put(key,(Double) v);
                } else if (byte[].class==valueClass) {
                    values.put(key,(byte[]) v);
                } else if (Byte.class==valueClass) {
                    values.put(key,(Byte) v);
                } else if (Boolean.class==valueClass) {
                    values.put(key,(Boolean) v);
                }else{
                    throw new UnsupportedClassVersionError(valueClass.getName()+" not supported");
                }
        }else{
            values.putNull(key);
        }
        return values;
    }

    public static void removeNonExistKeysForContentValues(ContentValues values, @NonNull String[] keys)
    {
        Iterator<String> it=values.keySet().iterator();
        while(it.hasNext())
        {
            String key=it.next();
            if(Util.searchArray(keys,key,0,1,ListBasedComparator.<String[], String>getSimplestComparator())==-1)
                it.remove();
        }
    }

    public static void putAllValues(ContentValues values, HashMap<String,Object> presetValues)
    {
        for(Map.Entry<String,Object> presetValue:presetValues.entrySet())
        {
            SqlUtil.putValue(values,presetValue.getKey(),presetValue.getValue());
        }
    }
    public static Object[] contentValuesToWhereClause(ContentValues values,Set<String> keepKeys)
    {
        Set<String>  keySet=values.keySet();
        StringBuilder selection=new StringBuilder();//a=? and b=?
        ArrayList<String> arrSelArgs=new ArrayList<>();
        for(String key:keySet)
        {
            if(keepKeys!=null && !keepKeys.contains(key))continue;
            Object value=values.get(key);
            if(value!=null) {
                if (selection.length() > 0) {
                    selection.append(" and " + key + " = ?");//a=? and b=?

                } else {
                    selection.append(key + " = ?");//a=?
                }
                arrSelArgs.add(String.valueOf(value));
            }
        }

        String[] selecionArgs=new String[arrSelArgs.size()];
        arrSelArgs.toArray(selecionArgs);
        return new Object[]{selection.toString(),selecionArgs};
    }

    public static Object[] contentValuesToWhereClause(ContentValues values)
    {return contentValuesToWhereClause(values,null);}

    public static int getRowCountInTable(SQLiteDatabase db,String table,ContentValues values,Set<String> queryKeys)
    {
        return (int) getRowCountInTableBesidesAndWhereArgs(db,table,values,queryKeys)[0];
    }
    public static int getRowCountInTable(SQLiteDatabase db,String table,ContentValues values)
    {return getRowCountInTable(db,table,values,null);}


    public static Object[] getRowCountInTableBesidesAndWhereArgs(SQLiteDatabase db,String table,ContentValues values,Set<String> queryKeys)
    {
        Object[] wheres=contentValuesToWhereClause(values,queryKeys);
        Cursor c=db.query(table,new String[]{"count(*)"},(String)wheres[0],(String[])wheres[1],null,null,null);//cursor must move to first
        c.moveToFirst();
        int count=c.getInt(0);

        c.close();
        return new Object[]{count,wheres[0],wheres[1]};
    }

    /**
     *
     * insert or update, not insert or replace.It will do a true update if there is already such exactly one row.
     *
     * @param db
     * @param table
     * @param value
     * @param queryKeys keys in value that used to identify such row.
     * @return 0 inserted,
     *              1 updated,
     *              -1 failed,more than 1 row
     */
    public static int insertOrUpdateIfThereIsOnlyOneOrZeroRow(SQLiteDatabase db,String table,ContentValues value,Set<String> queryKeys)
    {
        Object[] data=getRowCountInTableBesidesAndWhereArgs(db,table,value,queryKeys);
        int count= (int) data[0];
        if(count==0)
        {
            db.insert(table,null,value);
            return 0;
        }else if(count==1){
            db.update(table,value,(String)data[1],(String[])data[2]);
            return 1;
        }else{
            return -1;
        }
    }
}
