package fulton.util.android.public_interfaces;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by 13774 on 7/28/2017.
 */

public abstract class SqliteHelper extends SQLiteOpenHelper {
    public SqliteHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }
}
