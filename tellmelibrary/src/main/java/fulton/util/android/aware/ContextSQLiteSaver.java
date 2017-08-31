package fulton.util.android.aware;

import android.database.sqlite.SQLiteDatabase;

/**
 * Created by 13774 on 8/2/2017.
 */

public interface ContextSQLiteSaver extends ContextSQLiteAware {
    void saveDb(SQLiteDatabase db);
}
