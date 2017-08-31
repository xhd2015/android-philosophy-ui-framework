package fulton.shaw.android.x.sql;

import android.database.sqlite.SQLiteDatabase;

import fulton.shaw.android.x.models.awares.RowIdAware;

/**
 * Created by 13774 on 8/8/2017.
 */

public class QueryHandler {
    private SQLiteDatabase mDb;
    public QueryHandler()
    {

    }

    /**
     *
     * @param <E>
     * @return return type of models
     */
    public <E> E query(String table, RowIdAware aware)
    {
        if(table=="")
        {
            return null;
        }else{
            return (E)(Integer)0;
        }
    }
}
