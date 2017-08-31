package fulton.shaw.android.x.views.viewhelpers.singleview_functions;

import android.content.ContentValues;

import java.util.ArrayList;

import fulton.shaw.android.x.public_interfaces.SqliteHelper;
import fulton.shaw.android.x.viewgetter.database_auto.ShareableSingleViewAutoCollector;
import fulton.shaw.android.x.viewgetter.database_auto.SingleViewAutoCollector;
import fulton.util.android.utils.Util;

/**
 * Created by 13774 on 8/22/2017.
 */

public class FunctionAddToDatabase extends DatabaseModelViewFunction {
    private String mTable;
    private SqliteHelper mSqliteHelper;
    private Runnable mErrorHandler;
    private Runnable mSuccessHandler;
    private Runnable mInputCheckFailedHandler;
    private ArrayList mClear;

    /**
     *
     * @param table
     * @param sqliteHelper
     * @param errorHandler optional, maybe null.
     * @param clear
     */
    public FunctionAddToDatabase(String table, SqliteHelper sqliteHelper, Runnable errorHandler, Runnable successHandler,Runnable inputCheckFailedHandler,ArrayList clear) {
        mTable = table;
        mSqliteHelper = sqliteHelper;
        mErrorHandler = errorHandler;
        mSuccessHandler =successHandler;
        mInputCheckFailedHandler=inputCheckFailedHandler;
        mClear = clear;
    }

    @Override
    public void apply(SingleViewAutoCollector collector) {

        try {
            ContentValues values = collector.collectValue(ContentValues.class);
            long id = mSqliteHelper.insert(mTable, null, values);
            if (id == -1) {
                if (mErrorHandler != null)
                    mErrorHandler.run();
            } else {
                if (mClear != null)
                    collector.fillValue(mClear);
                if (mSuccessHandler != null)//id should be passed to run.
                    mSuccessHandler.run();
            }
        }catch (ShareableSingleViewAutoCollector.ActionAbort e){
                if(mInputCheckFailedHandler!=null)
                    mInputCheckFailedHandler.run();
        }
    }
}
