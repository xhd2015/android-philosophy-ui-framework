package fulton.shaw.android.x.public_interfaces;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.CancellationSignal;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringDef;

import java.util.ArrayList;
import java.util.Calendar;

import fulton.util.android.interfaces.comparators.ListBasedComparator;
import fulton.util.android.notations.TestNote;
import fulton.util.android.utils.CalendarUtil;
import fulton.util.android.utils.SqlUtil;
import fulton.util.android.utils.Util;
import fulton.util.android.notations.MayConsumeTimePleaseUtilize;
import fulton.util.android.public_interfaces.BaseSqliteHelper;

/**
 * Created by 13774 on 7/28/2017.
 */


public class SqliteHelper extends SQLiteOpenHelper implements BaseSqliteHelper {

    private SQLiteDatabase mDb;
    public static final String TABLE_TEST="tableTest";

    //===Model Tables
    public static final String TABLE_TRAVEL_NOTE="tableTravelNote";
    public static final String TABLE_THING="tableThing";
    public static final String TABLE_CONDITIONAL_PRICE="tableConditionalPrice";
    public static final String TABLE_PLAN="tablePlan";
    public static final String TABLE_GENERAL_RECORDS="tableGeneralRecords";
    public static final String TABLE_DATE_RECORD="tableDateRecord";
    public static final String TABLE_PERSON_RECORD="tablePersonRecord";

    //===Controlling Tables
    public static final String TABLE_REVIEW="tableReview";
    public static final String TABLE_ALARM="tableAlarm";
    public static final String TABLE_ISSUE="tableIssue";
    public static final String TABLE_TAG="tableTag";
    public static final String TABLE_CREATED_TIME_RECORD="tableCreatedTimeRecord";
    public static final String TABLE_INCOME="tableIncome";
    public static final String TABLE_TAG_SET="tableTagSet";
    public static final String TABLE_REFERENCE="tableReference";

    @StringDef({
            TABLE_TEST,
            TABLE_TRAVEL_NOTE,
            TABLE_THING,
            TABLE_CONDITIONAL_PRICE,TABLE_PLAN,TABLE_GENERAL_RECORDS,TABLE_DATE_RECORD,TABLE_PERSON_RECORD,
            TABLE_REVIEW,TABLE_ALARM,TABLE_ISSUE,TABLE_TAG,TABLE_CREATED_TIME_RECORD,TABLE_INCOME,TABLE_TAG_SET,
            TABLE_REFERENCE
    })
    public @interface TableName{}

    //====alarm repeat type
    public static final int REPEAT_TYPE_ONCE=0;
    public static final int REPEAT_TYPE_BY_DAY=1;
    public static final int REPEAT_TYPE_BY_WEEK=2;
    public static final int REPEAT_TYPE_BY_MONTH=3;
    public static final int REPEAT_TYPE_BY_YEAR=4;

    //定义关联表：即a表关联b表
    //关联方式1:innerId
    //关联方式2:innerId,tableName
    @MayConsumeTimePleaseUtilize("Static init")
    public static final ArrayList<String> TABLES=new ArrayList<String>(){{
        add(TABLE_TEST);
        add(TABLE_TRAVEL_NOTE);
        add(TABLE_THING);
        add(TABLE_CONDITIONAL_PRICE);
        add(TABLE_PLAN);
        add(TABLE_GENERAL_RECORDS);
        add(TABLE_REVIEW);
        add(TABLE_ALARM);
        add(TABLE_ISSUE);
        add(TABLE_TAG);
        add(TABLE_CREATED_TIME_RECORD);
        add(TABLE_DATE_RECORD);
        add(TABLE_PERSON_RECORD);
        add(TABLE_INCOME);
        add(TABLE_TAG_SET);
        add(TABLE_REFERENCE);
    }};
    //==each item has the format: model table, [relating tables,type
    //      [0 only innerId, 1 plus tablename]]
    //the first two elements is shared tables.
    public static final Object[] RELATED_TABLES=new Object[]{
            new Object[]{ //all shared
                    TABLE_ALARM,1,
                    TABLE_REVIEW,1,
                    TABLE_TAG,1,
                    TABLE_ISSUE,1,
                    TABLE_REFERENCE,1
            },
            TABLE_TRAVEL_NOTE,new Object[]{},
            TABLE_GENERAL_RECORDS,new Object[]{},
            TABLE_PLAN,new Object[]{},
            TABLE_THING,new Object[]{TABLE_CONDITIONAL_PRICE,0},
            TABLE_DATE_RECORD,new Object[]{TABLE_INCOME,0},
            TABLE_PERSON_RECORD,new Object[]{}
//            TABLE,new Object[]{},
//            TABLE,new Object[]{},
//            TABLE,new Object[]{},
//            TABLE,new Object[]{},

    };

    public static final ArrayList<String> CREATE_TABLES=new ArrayList<String>(){{
        add(String.format(SqlUtil.FORMATTER_CREATE_TABLE_WITH_ID,TABLE_TEST,
                    SqlUtil.COL_CREATED_TIME+SqlUtil.SQL_DOUBLE+SqlUtil.SQL_SEG+
                    SqlUtil.COL_CONTENT+SqlUtil.SQL_CONTENT_STRING+SqlUtil.SQL_NOT_NULL)
        );
        add(String.format(SqlUtil.FORMATTER_CREATE_TABLE_WITH_ID, TABLE_TRAVEL_NOTE,
                SqlUtil.COL_CREATED_TIME+SqlUtil.SQL_DOUBLE+SqlUtil.SQL_SEG+
                SqlUtil.COL_START_TIME+SqlUtil.SQL_DOUBLE+SqlUtil.SQL_SEG+
                        SqlUtil.COL_SRC+SqlUtil.SQL_SHORT_STRING+SqlUtil.SQL_SEG+
                        SqlUtil.COL_DST+SqlUtil.SQL_SHORT_STRING+SqlUtil.SQL_NOT_NULL+","+
                        SqlUtil.COL_PREDICT_COST_TIME+SqlUtil.SQL_SHORT_STRING+","+
                        SqlUtil.COL_PREDICT_COST_MONEY+SqlUtil.SQL_SHORT_STRING+","+
                        SqlUtil.COL_PREDICT_METHOD+SqlUtil.SQL_SHORT_STRING+","+
                        SqlUtil.COL_BENIFIT_OR_GOOD_PLAN+SqlUtil.SQL_INT+","+ //1-5
                        SqlUtil.COL_REAL_COST_TIME+SqlUtil.SQL_SHORT_STRING+","+
                        SqlUtil.COL_REAL_COST_MONEY+SqlUtil.SQL_SHORT_STRING+","+
                        SqlUtil.COL_REAL_METHOD+SqlUtil.SQL_SHORT_STRING
        ));
        add(String.format(SqlUtil.FORMATTER_CREATE_TABLE_WITH_ID,TABLE_THING,
                SqlUtil.COL_NAME+SqlUtil.SQL_SHORT_STRING+","+
                        SqlUtil.COL_DESCRIPTION+SqlUtil.SQL_DESCRIPTIVE_STRING
                ));
        add(String.format(SqlUtil.FORMATTER_CREATE_TABLE_WITH_ID,TABLE_CONDITIONAL_PRICE,
                SqlUtil.COL_INNER_ID+SqlUtil.SQL_INT+","+
                SqlUtil.COL_CONDITION+SqlUtil.SQL_DESCRIPTIVE_STRING+","+
                SqlUtil.COL_PRICE+SqlUtil.SQL_SHORT_STRING
        ));
        add(String.format(SqlUtil.FORMATTER_CREATE_TABLE_WITH_ID,TABLE_PLAN,
                SqlUtil.COL_CREATED_TIME+SqlUtil.SQL_DOUBLE+SqlUtil.SQL_NOT_NULL+","+
                        SqlUtil.COL_PLACE+SqlUtil.SQL_SHORT_STRING+","+
                        SqlUtil.COL_END_TIME+SqlUtil.SQL_DOUBLE+","+
                        SqlUtil.COL_NAME+SqlUtil.SQL_SHORT_STRING+","+
                        SqlUtil.COL_STATUS+SqlUtil.SQL_SHORT_STRING

        ));
        add(String.format(SqlUtil.FORMATTER_CREATE_TABLE_WITH_ID,TABLE_GENERAL_RECORDS,
            SqlUtil.COL_CREATED_TIME+SqlUtil.SQL_DATETIME_NOT_NULL+SqlUtil.SQL_DEFAULT_CURRENT_DATETIME+","+
                    SqlUtil.COL_WEATHER+SqlUtil.SQL_SHORT_STRING+","+
                    SqlUtil.COL_PLACE+SqlUtil.SQL_SHORT_STRING+","+
                    SqlUtil.COL_CAUSING+SqlUtil.SQL_CONTENT_STRING+","+
                    SqlUtil.COL_TARGET+SqlUtil.SQL_SHORT_STRING+","+
                    SqlUtil.COL_BRIEF+SqlUtil.SQL_DESCRIPTIVE_STRING+","+
                    SqlUtil.COL_MAIN_TAG+SqlUtil.SQL_SHORT_STRING+","+
                    SqlUtil.COL_DETAIL+SqlUtil.SQL_CONTENT_STRING+","+
                    SqlUtil.COL_SHOWN_DATE+SqlUtil.SQL_DATE+SqlUtil.SQL_DEFAULT_NULL+","+
                    SqlUtil.COL_SHOWN_TYPE+SqlUtil.SQL_INT+SqlUtil.SQL_DEFAULT_0
        ));
        add(String.format(SqlUtil.FORMATTER_CREATE_TABLE_WITH_ID,TABLE_REVIEW,
                SqlUtil.COL_TABLE_NAME+SqlUtil.SQL_SHORT_STRING+","+
                        SqlUtil.COL_INNER_ID+SqlUtil.SQL_INT+","+
                        SqlUtil.COL_CONTENT+SqlUtil.SQL_CONTENT_STRING+","+
                        SqlUtil.COL_CREATED_TIME+SqlUtil.SQL_DATETIME_NOT_NULL+SqlUtil.SQL_DEFAULT_CURRENT_DATETIME
        ));
        add(String.format(SqlUtil.FORMATTER_CREATE_TABLE_WITH_ID,TABLE_ALARM,
                SqlUtil.COL_TABLE_NAME+SqlUtil.SQL_SHORT_STRING+","+
                        SqlUtil.COL_INNER_ID+SqlUtil.SQL_INT+","+
                        SqlUtil.COL_CREATED_TIME+SqlUtil.SQL_LONG+","+
                        SqlUtil.COL_CAUSING+SqlUtil.SQL_CONTENT_STRING+","+
                        SqlUtil.COL_REPEAT_TYPE+SqlUtil.SQL_INT+","+
                        SqlUtil.COL_START_TIME+SqlUtil.SQL_LONG+","+
                        SqlUtil.COL_END_TIME+SqlUtil.SQL_LONG
        ));
        add(String.format(SqlUtil.FORMATTER_CREATE_TABLE_WITH_ID,TABLE_ISSUE,
                SqlUtil.COL_TABLE_NAME+SqlUtil.SQL_SHORT_STRING+","+
                        SqlUtil.COL_INNER_ID+SqlUtil.SQL_INT+","+
                        SqlUtil.COL_CONTENT+SqlUtil.SQL_CONTENT_STRING+","+
                        SqlUtil.COL_STATUS+SqlUtil.SQL_SHORT_STRING
        ));
        add(String.format(SqlUtil.FORMATTER_CREATE_TABLE_WITH_ID,TABLE_TAG,
                SqlUtil.COL_TABLE_NAME+SqlUtil.SQL_SHORT_STRING+","+
                        SqlUtil.COL_INNER_ID+SqlUtil.SQL_INT+","+
                        SqlUtil.COL_CONTENT+SqlUtil.SQL_TAG_STRING+","+
                        "Unique("+SqlUtil.COL_TABLE_NAME+","+SqlUtil.COL_INNER_ID+","+SqlUtil.COL_CONTENT+") On Conflict Ignore" //to getByGetter the id
        ));
        add(String.format(SqlUtil.FORMATTER_CREATE_TABLE_WITH_ID,TABLE_CREATED_TIME_RECORD,
                SqlUtil.COL_TABLE_NAME+SqlUtil.SQL_SHORT_STRING+","+
                        SqlUtil.COL_INNER_ID+SqlUtil.SQL_INT+","+
                        SqlUtil.COL_CREATED_TIME+SqlUtil.SQL_LONG+","+
                        "Unique("+SqlUtil.COL_TABLE_NAME+","+SqlUtil.COL_INNER_ID+") On Conflict Ignore"
        ));
        add(String.format(SqlUtil.FORMATTER_CREATE_TABLE_WITH_ID,TABLE_DATE_RECORD,
                SqlUtil.COL_TARGETING_DATE+SqlUtil.SQL_DATE+SqlUtil.SQL_NOT_NULL+","+
                        SqlUtil.COL_CREATED_TIME+SqlUtil.SQL_DATETIME+SqlUtil.SQL_NOT_NULL+","+
                        SqlUtil.COL_SUMMARY+SqlUtil.SQL_SUMMARY_STRING+","+
                        SqlUtil.COL_LOCATION+SqlUtil.SQL_LOCATION_STRING+","+
                        SqlUtil.COL_WEATHER+SqlUtil.SQL_WEATHER_STRING+","+
                        SqlUtil.COL_LEVEL+SqlUtil.SQL_FLOAT+SqlUtil.SQL_DEFAULT+"0,"+
                        "Unique("+SqlUtil.COL_TARGETING_DATE+") On Conflict Ignore"
        ));
        add(String.format(SqlUtil.FORMATTER_CREATE_TABLE_WITH_ID,TABLE_PERSON_RECORD,
                SqlUtil.COL_TABLE_NAME+SqlUtil.SQL_SHORT_STRING+","+
                        SqlUtil.COL_INNER_ID+SqlUtil.SQL_INT+","+
                        SqlUtil.COL_CREATED_TIME+SqlUtil.SQL_LONG+","+
                        "Unique("+SqlUtil.COL_TABLE_NAME+","+SqlUtil.COL_INNER_ID+") On Conflict Ignore"
        ));
        add(String.format(SqlUtil.FORMATTER_CREATE_TABLE_WITH_ID,TABLE_INCOME,
                        SqlUtil.COL_INNER_ID+SqlUtil.SQL_LONG+SqlUtil.SQL_NOT_NULL+","+
                        SqlUtil.COL_CREATED_TIME+SqlUtil.SQL_DATETIME_NOT_NULL+SqlUtil.SQL_DEFAULT_CURRENT_TIMESTAMP +","+
                                SqlUtil.COL_EVENT_TIME+SqlUtil.SQL_TIME+","+
                                SqlUtil.COL_RECORD+SqlUtil.SQL_INT+SqlUtil.SQL_NOT_NULL+","+//以人民币：元为统一单位。
                                SqlUtil.COL_REASON+SqlUtil.SQL_REASON_STRING
        ));
        add(String.format(SqlUtil.FORMATTER_CREATE_TABLE_WITH_ID,TABLE_TAG_SET,
                SqlUtil.COL_INNER_ID+SqlUtil.SQL_LONG+SqlUtil.SQL_NOT_NULL+","+
                        SqlUtil.COL_CONTENT+SqlUtil.SQL_TAG_STRING+SqlUtil.SQL_NOT_NULL+
                            SqlUtil.SQL_CHECK_NOT_EMPTY_START+SqlUtil.COL_CONTENT+SqlUtil.SQL_CHECK_NOT_EMPTY_END+","+
                        SqlUtil.COL_FILTER_CONDITION+SqlUtil.SQL_INT+SqlUtil.SQL_NOT_NULL+SqlUtil.SQL_DEFAULT+ SqlUtil.FILTER_ALL
        ));
        add(String.format(SqlUtil.FORMATTER_CREATE_TABLE_WITH_ID,TABLE_REFERENCE,
                SqlUtil.COL_TABLE_NAME+SqlUtil.SQL_SHORT_STRING+SqlUtil.SQL_NOT_NULL+","+
                SqlUtil.COL_INNER_ID+SqlUtil.SQL_LONG+SqlUtil.SQL_NOT_NULL+","+
                        SqlUtil.COL_REF_TABLE_NAME+SqlUtil.SQL_SHORT_STRING+SqlUtil.SQL_NOT_NULL+","+
                        SqlUtil.COL_REF_INNER_ID+SqlUtil.SQL_LONG+SqlUtil.SQL_NOT_NULL+","+
                        SqlUtil.COL_CREATED_TIME+SqlUtil.SQL_DATETIME_NOT_NULL+SqlUtil.SQL_DEFAULT_CURRENT_TIMESTAMP +","+
                        SqlUtil.COL_RELATION +SqlUtil.SQL_REFERENCE_STRING
        ));


    }};



    protected static String DBNAME="miscellanousDB";
    protected static int DBVERSION=
//            1
//            2  //added reference table
//            3 //add 2 columns in general record. in the next version,I'll possibly discard unused tables.
//            4 // change create_time double --> datetime not null default current_timestamp.
//            5 // remove datemodel from tagset for google, xiao,review table null create_time.
//            6 //change all default_current_timestamp to local current_time_stamp using date or datetime.  tableGeneralRecord & tableReview
            7// removes all mainTags other than "blog", "saysomething".
            ;
    protected void upgradeToNext(int nextVersion)
    {
        if(false){} //ignore this
        else if(nextVersion==7){
            mActionDelayed.add(new Runnable() {
                @Override
                public void run() {
                    Cursor allOtherTagsIds=rawQuery("Select _id From "+SqliteHelper.TABLE_GENERAL_RECORDS+" Where "+SqlUtil.COL_MAIN_TAG+" not in (?,?)",
                            new String[]{SqlUtil.TAG_BLOG,SqlUtil.TAG_SAY_SOMETHING});
                    while (allOtherTagsIds.moveToNext())
                    {
                        purgeModel(TABLE_GENERAL_RECORDS,SqlUtil.getCursorId(allOtherTagsIds));
                    }
                    allOtherTagsIds.close();
                }
            });
        }
        else if(nextVersion==4)
        {
            mActionDelayed.add(new Runnable() {
                @Override
                public void run() {
                    refactorTable(SqliteHelper.TABLE_GENERAL_RECORDS, new String[]{
                            SqlUtil.COL_ID,
                            "datetime("+SqlUtil.COL_CREATED_TIME+"/1000,'unixepoch','localtime')",
                            SqlUtil.COL_WEATHER,
                            SqlUtil.COL_PLACE,
                            SqlUtil.COL_CAUSING,
                            SqlUtil.COL_TARGET,
                            SqlUtil.COL_BRIEF,
                            SqlUtil.COL_MAIN_TAG,
                            SqlUtil.COL_DETAIL,
                            SqlUtil.COL_SHOWN_DATE,
                            SqlUtil.COL_SHOWN_TYPE,
                    },null);
                }
            });
        }else if(nextVersion==5){
            mActionDelayed.add(new Runnable() {
                @Override
                public void run() {
                    refactorTable(TABLE_TAG_SET,
                            new String[]{SqlUtil.COL_ID,SqlUtil.COL_INNER_ID,SqlUtil.COL_CONTENT},
                            new String[]{SqlUtil.COL_ID,SqlUtil.COL_INNER_ID,SqlUtil.COL_CONTENT});
                }
            });
        }else if(nextVersion==6){
            mActionDelayed.add(new Runnable() {
                @Override
                public void run() {
//                    refactorTable(TABLE_GENERAL_RECORDS,null,null);
                    dropTable(TABLE_REVIEW);
                    renameTable(TABLE_REVIEW+"_temp",TABLE_REVIEW);
                    refactorTable(TABLE_REVIEW,new String[]{
                            SqlUtil.COL_ID,SqlUtil.COL_TABLE_NAME,SqlUtil.COL_INNER_ID,SqlUtil.COL_CONTENT,
                            "ifnull("+SqlUtil.COL_CREATED_TIME+","+SqlUtil.SQL_DATETIME_NOW_LOCALTIME+")"
                    },null);
                }
            });
        }
    }

    private ArrayList<Runnable> mActionDelayed=new ArrayList<>();

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, final int newVersion) {
        Util.logi("onUpgrade:"+oldVersion+" --> "+newVersion);
        if(oldVersion==1)
        {
            if(newVersion==2)
            {
                mActionDelayed.add(new Runnable() {
                    @Override
                    public void run() {
                        recreate(TABLE_REFERENCE);
                    }
                });
            }
        }else if(oldVersion==2){
            if(newVersion==3)
            {
                mActionDelayed.add(new Runnable() {
                    @Override
                    public void run() {
                        execSQL("Alter Table "+TABLE_GENERAL_RECORDS+" Add Column "+
                                SqlUtil.COL_SHOWN_DATE+SqlUtil.SQL_DATE+SqlUtil.SQL_DEFAULT_NULL);
                        execSQL("Alter Table "+TABLE_GENERAL_RECORDS+" Add Column "+
                                SqlUtil.COL_SHOWN_TYPE+SqlUtil.SQL_INT+SqlUtil.SQL_DEFAULT_0);
                    }
                });
            }
        }else if(oldVersion>=3){
            while (oldVersion<newVersion)
                upgradeToNext(++oldVersion);
        }
    }

    protected void downradeToPrevious(int previous)
    {
        if(previous==5)
        {
//            mActionDelayed.add(new Runnable() {
//                @Override
//                public void run() {
//                    renameTable(TABLE_GENERAL_RECORDS+"_temp",TABLE_GENERAL_RECORDS);
//                }
//            });
            mActionDelayed.add(new Runnable() {
                @Override
                public void run() {
                    Cursor cursor=selectAll(TABLE_REVIEW);//nothing
                    Util.logi("count:"+cursor.getCount());
                    Cursor cursor1=selectAll(TABLE_REVIEW+"_temp");//everything;
                    Util.logi("count2:"+cursor1.getCount());
//                    renameTable(TABLE_REVIEW+"_temp",TABLE_REVIEW);
//                    dropTable(TABLE_REVIEW+"_temp");
                }
            });
        }
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        Util.logi("Downgrade:"+oldVersion+"--->"+newVersion);
        while (oldVersion>newVersion)
            downradeToPrevious(--oldVersion);
    }

    public SqliteHelper(Context context) {
        super(context,DBNAME,null, DBVERSION);
        getContextDB();//ensure set
        for(Runnable r:mActionDelayed)
            r.run();
    }

    /**
     *
     * @param table
     * @param newCols cannot be empty
     * @param oldCols null means everything, but cannot be empty
     */
    @TestNote(TestNote.STATE.NOT_STARTED)
    public void refactorTable(String table,@Nullable  String[] oldCols,@Nullable  String[] newCols)
    {
        String tempTable=table+"_temp";
        renameTable(table,tempTable);
        recreate(table);
        String newColString="";
        if(newCols!=null)
            newColString="("+Util.join(",",newCols)+")";
        String oldColString="*";
        if(oldCols!=null)
            oldColString=Util.join(",",oldCols);
        mDb.execSQL("Insert Into "+table+newColString+" Select "+oldColString+" From "+tempTable);
        this.dropTable(tempTable);
    }

    public void copyTableWithCreation(String fromTable, String toTable, @Nullable  String[] fromCols, @Nullable  String[] toCols)
    {
        String tempTable=fromTable+"_temp";
        renameTable(fromTable,tempTable);
        recreate(fromTable);
        renameTable(fromTable,toTable);
        renameTable(tempTable,fromTable);
        copyTableWithExistence(fromTable,toTable,fromCols,toCols);

    }
    public void copyTableWithExistence(String fromTable, String toTable, @Nullable String[] fromCols,@Nullable String[] toCols)
    {
        String fromColString="*";
        if(fromCols!=null)
            fromColString=Util.join(",",fromCols);

        String toColString="";
        if(toCols!=null)
            toColString="("+Util.join(",",toCols)+")";
        mDb.execSQL("Insert Into "+toTable+toColString+" Select "+fromColString+" From "+fromTable);

    }

    public void renameTable(String table,String newName)
    {
        mDb.execSQL("Alter Table "+table+" Rename To "+newName);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        for(String sql:getCreateTables())
        {
            Util.logi("SQL="+sql);
            db.execSQL(sql);
        }
    }


    public static void recreate(BaseSqliteHelper helper,SQLiteDatabase db,String table)
    {
        int i=0;
        for(;i<helper.getTables().size();i++)
            if(helper.getTables().get(i).equals(table))break;
        if(i<helper.getTables().size())
        {
            db.execSQL(SqlUtil.SQL_DROP_TABLE_IF_EXISTS +table);
            db.execSQL(helper.getCreateTables().get(i));
        }
    }
    public static void dropTable(SQLiteDatabase db,String table)
    {
        db.execSQL(SqlUtil.SQL_DROP_TABLE_IF_EXISTS+table);
    }


    public ArrayList<String> getTables()
    {
        return TABLES;
    }

    @Override
    public ArrayList<String> getCreateTables() {
        return CREATE_TABLES;
    }

    @Override
    public void recreate(String table) {
        recreate(this,getContextDB(),table);
    }

    @Override
    public void recreateAllTables() {
        for(String table:getTables())
        {
            recreate(table);
        }
    }

    @Override
    public void dropTable(String table) {
        mDb.execSQL(SqlUtil.SQL_DROP_TABLE_IF_EXISTS+table);
    }

    @Override
    public void dropAllTables() {
        for(String table:getTables())
        {
            dropTable(table);
        }
    }

    public static SQLiteDatabase getDatabase(Context context)
    {
        return new SqliteHelper(context).getWritableDatabase();
    }

    @Override
    public SQLiteDatabase getContextDB() {
        if(mDb==null)
            mDb=this.getWritableDatabase();
        return mDb;
    }

    //========================general purge

    public static class CannotDeleteRefTable extends Error{
        public CannotDeleteRefTable(String tableMain,String tableRef) {
            super("TableMain:"+tableMain+",TableRef:"+tableRef);
        }
    }
    /**
     *  this will delete all references owned by one model.
     *  If an error happens during deleting a reference, false is returned to indicate unsuccessful operation.
     * @param modelTable
     * @param modelId
     */
    @TestNote
    public boolean purgeModel(String modelTable,long modelId)
    {
        int modelIndex=Util.searchArray(RELATED_TABLES, modelTable, 1, 2, ListBasedComparator.<Object[], String>getSimplestComparator());
        if(modelIndex==-1)
            throw new UnsupportedOperationException("Model:"+modelTable+" does not exist");
        Object[] sharedModels= (Object[]) RELATED_TABLES[0];
        Object[] specModels=(Object[])RELATED_TABLES[modelIndex+1];
        for(int i=0;i<sharedModels.length+specModels.length;i+=2)
        {
            Integer mode=null;
            String opTable=null;
            if(i<sharedModels.length) {
                mode= (Integer) sharedModels[i + 1];
                opTable = (String) sharedModels[i];
            }else{
                mode=(Integer)specModels[i-sharedModels.length+1];
                opTable=(String)specModels[i-sharedModels.length];
            }
            if(mode==0)
            {
                deleteRefItemInTable(opTable,modelId);
            }else{
                deleteRefItemInTable(opTable,modelTable,modelId);//no matter how many changes.
            }
        }
        return deleteItemInTable(modelTable,modelId);
    }

    //===========useful methods
    /**
     *
     * @param table
     * @param innderId
     * @param time
     * @return
     */

    @Deprecated/*
            because created time is not a multi-row attribute.
       */
    public boolean insertCreatedTime(String table, int innderId,@Nullable Long time)
    {
        if(innderId==-1)return false;
        ContentValues value=new ContentValues();
        value.put(SqlUtil.COL_TABLE_NAME,table);
        value.put(SqlUtil.COL_INNER_ID,innderId);
        if(time==null)
            time=System.currentTimeMillis();
        value.put(SqlUtil.COL_CREATED_TIME,time);
        return mDb.insert(TABLE_CREATED_TIME_RECORD,null,value)!=-1;
    }

    public boolean deleteRefItemInTable(String table,String tableName,long innerId)
    {
        mDb.execSQL("Delete From "+table+" Where "+SqlUtil.COL_TABLE_NAME+" = \""+tableName+"\" and "+SqlUtil.COL_INNER_ID+"="+innerId);
        int changes=getChanges();
        return changes>0;
    }
    public boolean deleteRefItemInTable(String table,long innerId)
    {
        mDb.execSQL("Delete From "+table+" Where "+SqlUtil.COL_INNER_ID+"="+innerId);
        int changes=getChanges();
        return changes>0;
    }
    public boolean deleteItemInTable(String table,long id)
    {
        mDb.execSQL("Delete From "+table+" Where "+SqlUtil.COL_ID+" = "+id);
        int changes=getChanges();
        return changes>0;
    }
    public int deleteItemInTable(String table,String col,String value)
    {
        mDb.execSQL("Delete From "+table+" Where "+col+"=?",new String[]{value});
        return getChanges();
    }

    public boolean deleteItemInTable(String table, Cursor cursor)
    {
        return deleteItemInTable(table,  SqlUtil.getCursorId(cursor));
    }

    public boolean updateItemInTable(String table,long id,ContentValues values)
    {
        return mDb.update(table,values,SqlUtil.COL_ID+"=?",new String[]{""+id})>0;
    }

    /**
     * single column
     * @param newValue  can be null,that means set the value into null.
     */
    public boolean updateItemInTable(String table,long id,String col,String newValue)
    {
        if(newValue==null)
            mDb.execSQL("Update "+table+" Set "+col+"=NULL Where "+SqlUtil.COL_ID+" = "+id,null);
        else
            mDb.execSQL("Update "+table+" Set "+col+"=? Where "+SqlUtil.COL_ID+" = "+id,new String[]{newValue});
        int changes=getChanges();
        return changes>0;
    }

    @Deprecated
    public Long getCreatedTime(String table,long innerId)
    {
        final Cursor cursor = mDb.rawQuery("Select " + SqlUtil.COL_CREATED_TIME + " " + " From " +
                SqliteHelper.TABLE_CREATED_TIME_RECORD + " Where " + SqlUtil.COL_TABLE_NAME + "=? And " + SqlUtil.COL_INNER_ID + "=?", new String[]{
                table, String.valueOf(innerId)
        });
        Long data=null;
        if(cursor.moveToFirst() && !cursor.isNull(0))
            data=cursor.getLong(0);
        cursor.close();
        return data;
    }


    //===for table tagSet
    public ArrayList<ArrayList<ContentValues>> getAllTagSet()
    {
        //被distinct 了
        Cursor cursor=mDb.rawQuery("Select * From "+TABLE_TAG_SET+" Order By "+SqlUtil.COL_INNER_ID,null);//Group By auto distinct
        ArrayList<ArrayList<ContentValues>> res=new ArrayList<>();
        long lastInnerId=-1;
        ArrayList<ContentValues> curList=null;
        while (cursor.moveToNext())
        {
            long thisInnerId=cursor.getLong(cursor.getColumnIndex(SqlUtil.COL_INNER_ID));
            if(lastInnerId!=thisInnerId)
            {
//                if(curList!=null)
//                    Util.logi("getByGetter a size:"+curList.size());
                curList=new ArrayList<>();
                res.add(curList);
                lastInnerId=thisInnerId;
            }
            ContentValues contentValues=new ContentValues();
            contentValues.put(SqlUtil.COL_INNER_ID,thisInnerId);
            contentValues.put(SqlUtil.COL_CONTENT,cursor.getString(cursor.getColumnIndex(SqlUtil.COL_CONTENT)));
            contentValues.put(SqlUtil.COL_ID,cursor.getLong(cursor.getColumnIndex(SqlUtil.COL_ID)));
            contentValues.put(SqlUtil.COL_FILTER_CONDITION,cursor.getLong(cursor.getColumnIndex(SqlUtil.COL_FILTER_CONDITION)));
            curList.add(contentValues);
        }
        cursor.close();
        return res;
    }

    public void addTagSet(ArrayList<String> tags, int filterCondition)
    {
        if(tags.size()==0)return;
        Cursor maxInnerId=mDb.rawQuery("Select Max("+SqlUtil.COL_INNER_ID+") From "+TABLE_TAG_SET,null);
        maxInnerId.moveToFirst();
        long maxInnerIdValue=0;
        if(!maxInnerId.isNull(0))
        {
            maxInnerIdValue=maxInnerId.getLong(0)+1;
        }
        maxInnerId.close();
//        Util.logi("max InnerId is "+maxInnerIdValue);
        addTagSet(maxInnerIdValue,tags, filterCondition);
    }
    public void addTagSet(long innerId, ArrayList<String> tags, int filterCondition)
    {
        if(tags.size()==0)return;
        String sql="Insert Into "+TABLE_TAG_SET+"("+SqlUtil.COL_INNER_ID+","+SqlUtil.COL_CONTENT+","+
                SqlUtil.COL_FILTER_CONDITION+
                ") " +
                "Values("+innerId+",?,?)";
//        Util.logi("sql="+sql);
        String[] args=new String[]{null,""+filterCondition};
        for(int i=0;i<tags.size();i++)
        {
            args[0]=tags.get(i);
            mDb.execSQL(sql,args);
//            Util.logi("changes:"+getChanges());
        }

    }


    public void updateTagSet(long innerId, ArrayList<String> newTags, int filterCondition)
    {
        deleteTagSet(innerId);
        if(newTags.size()==0)
            return;
        addTagSet(innerId,newTags, filterCondition);
    }
    public void deleteTagSet(long innerId)
    {
        String sInnerId=""+innerId;
        mDb.execSQL("Delete From "+TABLE_TAG_SET+" Where "+SqlUtil.COL_INNER_ID+"=?",new String[]{sInnerId});
    }

    //==============for table reference
    public String getBriefString(String table,long innerId)
    {
        Cursor cursor=getCursorById(table,innerId);
        if(!cursor.moveToFirst())
        {
            cursor.close();
            return null;
        }
        String brief=null;
        if(SqliteHelper.TABLE_GENERAL_RECORDS.equals(table))
        {
            String mainTag=SqlUtil.getCursorValue(cursor,SqlUtil.COL_MAIN_TAG,String.class);
            if(SqlUtil.TAG_SAY_SOMETHING.equals(mainTag))
            {
                brief=Util.getSubString(SqlUtil.getCursorValue(cursor,SqlUtil.COL_DETAIL,String.class),0,30);
            }else if(SqlUtil.TAG_BLOG.equals(mainTag)){
                brief=SqlUtil.getCursorValue(cursor,SqlUtil.COL_BRIEF,String.class)+"\n"+Util.getSubString(SqlUtil.getCursorValue(cursor,SqlUtil.COL_DETAIL,String.class),0,30);
            }
        }
        cursor.close();
        return brief;
    }

    //========public static methods
    public static long getGeneralRecordShownTime(String table,ContentValues values)
    {
        if(TABLE_GENERAL_RECORDS.equals(table))
        {
            String showDate=values.getAsString(SqlUtil.COL_SHOWN_DATE);
            if(showDate!=null) {
//                Util.logi("showDate="+showDate);
                Calendar calendar=CalendarUtil.getDateOfSqlString(showDate);
//                Util.logi(calendar);
                return calendar.getTimeInMillis();
            }
            else
                return (long)(double)values.getAsDouble(SqlUtil.COL_CREATED_TIME);
        }else{
            return (long)(double)values.getAsDouble(SqlUtil.COL_CREATED_TIME);
        }
    }


    //==========for tableDateRecord
    /**
     *
     * @param date
     * @return the cursor that can be reused to requery the data.
     *
     */
    public Cursor ensureDateRecordExists(Calendar date)
    {
        long sec=date.getTimeInMillis()/1000;
        String defLoc=getNearestDateLocation(sec);
        if(defLoc==null)
            defLoc="null";
        execSQL("Insert Into "+TABLE_DATE_RECORD+"("+
                SqlUtil.COL_TARGETING_DATE+","+
                SqlUtil.COL_CREATED_TIME+","+
                SqlUtil.COL_LOCATION+
                ") Values(" +
                        "date("+(sec)+",'unixepoch','localtime')," +
                        "datetime('now','localtime')," +
                        defLoc+
                        ")");
        Cursor cursor=rawQuery("Select * From "+TABLE_DATE_RECORD+" Where "+SqlUtil.COL_TARGETING_DATE+" = date("+(sec)+",'unixepoch','localtime')",null);
        return cursor;
    }



    /**
     *     地点默认值从上一个最近的日期继承
     * @param date
     * @return null if no nearest date
     */
    public String getNearestDateLocation(Calendar date)
    {
        return getNearestDateLocation(date.getTimeInMillis()/1000);
    }
    public String getNearestDateLocation(long sec)
    {
        Cursor cursor=rawQuery("Select "+SqlUtil.COL_LOCATION+" From "+TABLE_DATE_RECORD+" Where "+SqlUtil.COL_TARGETING_DATE+" < date("+sec+",'unixepoch','localtime') " +
                "Order By "+SqlUtil.COL_TARGETING_DATE+" Desc " +
                "Limit 1",null);
        String res=null;
        if(cursor.moveToFirst())
        {
            res=cursor.getString(0);
        }
        cursor.close();
        return res;
    }
    public long getLastInsertedId()
    {
        Cursor cursor=rawQuery("Select last_insert_rowid()",null);
        long id=-1;
        if(cursor.moveToFirst() && !cursor.isNull(0))
        {
            id=cursor.getLong(0);
        }
        cursor.close();
        return id;
    }
    public int getChanges()
    {
        Cursor cursor=mDb.rawQuery("Select changes()",null);
        cursor.moveToFirst();
        int num=cursor.getInt(0);
        cursor.close();
        return num;
    }

    public Cursor getAllTags()
    {
        return mDb.rawQuery("Select Distinct "+SqlUtil.COL_ID+","+SqlUtil.COL_CONTENT+" From "+SqliteHelper.TABLE_TAG+" " +
                "Group By "+SqlUtil.COL_CONTENT,null);
    }

    public Cursor selectAll(String table)
    {
        return mDb.rawQuery("Select * From "+table,null);
    }

    public Cursor getCursorById(String table,long id)
    {
        return getCursorByIdCols(table,id,"*");
    }

    @Override
    public Cursor getCursorByIdCols(String table, long id, @NonNull String cols) {
        return mDb.rawQuery("Select "+cols+" From "+table+" Where "+SqlUtil.COL_ID+" = "+id,null);
    }

    @Override
    public Cursor getCursorById(String table, long id, String[] cols) {
        return getCursorByIdCols(table,id,SqlUtil.makeColumns(cols));
    }

    public Cursor getCursorByRefId(String table,String refTable,long innerId)
    {
        return mDb.rawQuery("Select * From "+table+" Where "+SqlUtil.COL_TABLE_NAME+"=\""+refTable+"\" and "+SqlUtil.COL_INNER_ID+"="+innerId,null);
    }
    public Cursor getCursorByRefId(String table,String refTable)
    {
        return mDb.rawQuery("Select * From "+table+" Where "+SqlUtil.COL_TABLE_NAME+"=\""+refTable+"\"",null);
    }

    //========delegate methods
    public Cursor query(boolean distinct, String table, String[] columns, String selection, String[] selectionArgs, String groupBy, String having, String orderBy, String limit) {
        return mDb.query(distinct, table, columns, selection, selectionArgs, groupBy, having, orderBy, limit);
    }

    public Cursor query(boolean distinct, String table, String[] columns, String selection, String[] selectionArgs, String groupBy, String having, String orderBy, String limit, CancellationSignal cancellationSignal) {
        return mDb.query(distinct, table, columns, selection, selectionArgs, groupBy, having, orderBy, limit, cancellationSignal);
    }

    public Cursor query(String table, String[] columns, String selection, String[] selectionArgs, String groupBy, String having, String orderBy) {
        return mDb.query(table, columns, selection, selectionArgs, groupBy, having, orderBy);
    }

    public Cursor query(String table, String[] columns, String selection, String[] selectionArgs, String groupBy, String having, String orderBy, String limit) {
        return mDb.query(table, columns, selection, selectionArgs, groupBy, having, orderBy, limit);
    }

    public Cursor rawQuery(String sql) {
        return mDb.rawQuery(sql, null);
    }
    public Cursor rawQuery(String sql, String[] selectionArgs) {
        return mDb.rawQuery(sql, selectionArgs);
    }

    public Cursor rawQuery(String sql, String[] selectionArgs, CancellationSignal cancellationSignal) {
        return mDb.rawQuery(sql, selectionArgs, cancellationSignal);
    }

    public long insert(@TableName String table, String nullColumnHack, ContentValues values) {
        return mDb.insert(table, nullColumnHack, values);
    }

    /**
     *  just for fast test.
     * @param table
     * @param values
     * @return
     */
    @Override
    public long insertBasedOnKeys(@TableName String table, Object...values)
    {
        ContentValues values1=new ContentValues();
        for(int i=0;i<values.length;i+=2)
            SqlUtil.putValue(values1,(String)values[i],values[i+1]);
//        Util.logi(values1);
        return insert(table,null,values1);
    }

    public long replace(@TableName String table, String nullColumnHack, ContentValues initialValues) {
        return mDb.replace(table, nullColumnHack, initialValues);
    }

    public int delete(@TableName String table, String whereClause, String[] whereArgs) {
        return mDb.delete(table, whereClause, whereArgs);
    }

    public int update(@TableName String table, ContentValues values, String whereClause, String[] whereArgs) {
        return mDb.update(table, values, whereClause, whereArgs);
    }

    public void execSQL(String sql) throws SQLException {
        mDb.execSQL(sql);
    }

    public void execSQL(String sql, Object[] bindArgs) throws SQLException {
        mDb.execSQL(sql, bindArgs);
    }

    public long insertWithOnConflict(String table, String nullColumnHack, ContentValues initialValues, int conflictAlgorithm) {
        return mDb.insertWithOnConflict(table, nullColumnHack, initialValues, conflictAlgorithm);
    }
}
