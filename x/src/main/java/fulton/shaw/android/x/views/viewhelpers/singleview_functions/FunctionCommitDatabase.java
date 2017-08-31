package fulton.shaw.android.x.views.viewhelpers.singleview_functions;

import java.util.ArrayList;

import fulton.shaw.android.x.public_interfaces.SqliteHelper;
import fulton.shaw.android.x.viewgetter.database_auto.SingleViewAutoCollector;

/**
 * Created by 13774 on 8/22/2017.
 */

public class FunctionCommitDatabase extends DatabaseModelViewFunction {

    private String mTable;
    private long  mId;
    private SqliteHelper mSqliteHelper;
    private Runnable mErrorHandler;
    private Runnable mAddSuccessHanadler;
    private Runnable mInputCheckFailedHandler;
    private ArrayList mClear;
    private String[] mAllowedKeys;

    private FunctionAddToDatabase mAdder;
    private FunctionUpdateToDatabase mUpdater;

    public FunctionCommitDatabase(String table, long id, SqliteHelper sqliteHelper, Runnable errorHandler, Runnable addSuccessHanadler,Runnable inputCheckFailedHandler, ArrayList clear, String[] allowedKeys) {
        mTable = table;
        mId = id;
        mSqliteHelper = sqliteHelper;
        mErrorHandler = errorHandler;
        mAddSuccessHanadler = addSuccessHanadler;
        mInputCheckFailedHandler=inputCheckFailedHandler;
        mClear = clear;
        mAllowedKeys = allowedKeys;
    }

    @Override
    public void apply(SingleViewAutoCollector collector) {
        if(mId>=0)
            getUpdater().apply(collector);
        else
            getAdder().apply(collector);
    }

    private FunctionAddToDatabase getAdder() {
        if(mAdder ==null)
            mAdder =new FunctionAddToDatabase(mTable,mSqliteHelper,mErrorHandler,mAddSuccessHanadler,mInputCheckFailedHandler,mClear);
        return mAdder;
    }

    private FunctionUpdateToDatabase getUpdater() {
        if(mUpdater ==null)
            mUpdater =new FunctionUpdateToDatabase(mTable,mId,mSqliteHelper,mErrorHandler,mAddSuccessHanadler,mInputCheckFailedHandler,mClear,mAllowedKeys);
        return mUpdater;
    }

    public long getId() {
        return mId;
    }

    public void setId(long id) {
        mId = id;
    }
}
