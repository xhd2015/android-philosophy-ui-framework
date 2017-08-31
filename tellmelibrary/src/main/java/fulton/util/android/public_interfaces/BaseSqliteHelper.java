package fulton.util.android.public_interfaces;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.ArrayList;

import fulton.util.android.aware.ContextSQLiteAware;
import fulton.util.android.aware.ContextSQLiteSaver;

/**
 * Created by 13774 on 7/30/2017.
 */


public interface BaseSqliteHelper extends ContextSQLiteAware{

    ArrayList<String> getTables();
    ArrayList<String> getCreateTables();
    void recreate(String table);
    void recreateAllTables();
    void dropTable(String table);
    void dropAllTables();

    void refactorTable(String table,@Nullable  String[] oldCols,@Nullable  String[] newCols);
    void copyTableWithCreation(String fromTable, String toTable, @Nullable String[] fromCols, @Nullable  String[] toCols);
    void renameTable(String table,String newName);
    boolean deleteRefItemInTable(String table,String tableName,long innerId);
    boolean deleteItemInTable(String table,long id);
    int deleteItemInTable(String table,String col,String value);
    boolean deleteItemInTable(String table, Cursor cursor);
    boolean updateItemInTable(String table,long id,ContentValues values);
    boolean updateItemInTable(String table,long id,String col,String newValue);
    long getLastInsertedId();
    int getChanges();
    Cursor selectAll(String table);
    Cursor getCursorById(String table,long id);
    Cursor getCursorByIdCols(String table,long id,@NonNull String cols);
    Cursor getCursorById(String table,long id,String[] cols);
    Cursor getCursorByRefId(String table,String refTable,long innerId);
    Cursor getCursorByRefId(String table,String refTable);
    Cursor rawQuery(String sql);
    Cursor rawQuery(String sql, String[] selectionArgs);
    long insert(String table, String nullColumnHack, ContentValues values);
    long insertBasedOnKeys(String table,Object...values);
    void execSQL(String sql) throws SQLException;
    void execSQL(String sql, Object[] bindArgs) throws SQLException;


}
