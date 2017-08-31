package fulton.shaw.android.x.views.viewhelpers.singleview_functions;

import android.content.ContentValues;

import java.util.ArrayList;

import fulton.shaw.android.x.public_interfaces.SqliteHelper;
import fulton.shaw.android.x.viewgetter.database_auto.ShareableSingleViewAutoCollector;
import fulton.shaw.android.x.viewgetter.database_auto.SingleViewAutoCollector;
import fulton.util.android.utils.SqlUtil;
import fulton.util.android.utils.Util;

/**
 * Created by 13774 on 8/22/2017.
 */

public class FunctionUpdateToDatabase extends DatabaseModelViewFunction {
    private String mTable;
    private long  mId;
    private SqliteHelper mSqliteHelper;
    private Runnable mErrorHandler;
    private Runnable mSuccessHandler;
    private Runnable mInputCheckFailedHandler;
    private ArrayList mClear;
    private String[] mAllowedKeys;

    public FunctionUpdateToDatabase(String table, long id, SqliteHelper sqliteHelper, Runnable errorHandler, Runnable successHandler, Runnable inputCheckFailedHandler,
                                    ArrayList clear, String[] allowedKeys) {
        mTable = table;
        mId = id;
        mSqliteHelper = sqliteHelper;
        mErrorHandler = errorHandler;
        mSuccessHandler = successHandler;
        mInputCheckFailedHandler = inputCheckFailedHandler;
        mClear = clear;
        mAllowedKeys = allowedKeys;
    }


    @Override
    public void apply(SingleViewAutoCollector collector) {
        try {
            ContentValues values = collector.collectValue(ContentValues.class);
            Util.logi("original values:   "+values);
            if(mAllowedKeys!=null)
                SqlUtil.removeNonExistKeysForContentValues(values,mAllowedKeys);
            Util.logi("after removing:   "+values);
            if(!mSqliteHelper.updateItemInTable(mTable,mId,values))
            {
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
