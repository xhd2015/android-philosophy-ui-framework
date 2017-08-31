package fulton.shaw.android.tellme.experiment;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

import fulton.util.android.utils.Util;

public class MyContentProvider extends ContentProvider {
    public static final Uri CONTENT_URI = Uri.parse("content://fulton.shaw.android.tellme.MyContentProvider/students");
    private  SQLiteDatabase mDb;

    private static class DatabaseHelper extends SQLiteOpenHelper {
        public DatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
            super(context, name, factory, version);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE students(" +
                    "_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "name TEXT NOT NULL," +
                    "grade TEXT NOT NULL);");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP IF EXISTS students;");
            onCreate(db);
        }
    }

    public MyContentProvider() {

    }

    @Override
    public boolean onCreate() {
        // TODO: Implement this to initialize your content provider on startup.
        Context context = getContext();
        DatabaseHelper dbHelper=new DatabaseHelper(context,"MyDatabase",null,1);
        mDb = dbHelper.getWritableDatabase();
        return mDb!=null;//reture if successful
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // Implement this to handle requests to delete one or more rows.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public String getType(Uri uri) {
        // TODO: Implement this to handle requests for the MIME type of the data
        // at the given URI.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        // TODO: Implement this to handle requests to insert a new row.
        long rowID = mDb.insert("students","",values);
        Util.logi("ID is "+rowID);
        if(rowID > 0)
        {

            Uri _uri=ContentUris.withAppendedId(CONTENT_URI,rowID);
            getContext().getContentResolver().notifyChange(_uri,null);//通知数据改变
            return _uri;
        }
        throw new SQLException("Data not inserted");
    }


    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        // TODO: Implement this to handle query requests from clients.
        SQLiteQueryBuilder qb=new SQLiteQueryBuilder();
        qb.setTables("students");

//        Util.logi(uri.toString());//content://fulton.shaw.android.tellme.MyContentProvider
//        Util.logi(Util.join(";",projection));//cloumn name given
//        Util.logi(selection==null?"":selection);
//        Util.logi(Util.join(";",selectionArgs));
//        Util.logi(sortOrder==null?"":sortOrder);


        Cursor c=qb.query(mDb,projection,selection,selectionArgs,null,null,sortOrder);
        c.setNotificationUri(getContext().getContentResolver(),uri);
        return c;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        // TODO: Implement this to handle requests to update one or more rows.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
