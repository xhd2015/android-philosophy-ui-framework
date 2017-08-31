package fulton.util.android.aware;

import android.database.sqlite.SQLiteDatabase;

/**
 * Created by 13774 on 7/19/2017.
 */

public interface ContextSQLiteAware {
    SQLiteDatabase getContextDB();
}
