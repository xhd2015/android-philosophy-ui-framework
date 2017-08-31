package fulton.util.android.interfaces.standard_functions;

import android.support.annotation.Nullable;

import fulton.util.android.interfaces.tuples.Single;
import fulton.util.android.public_interfaces.BaseSqliteHelper;

/**
 * Created by 13774 on 8/25/2017.
 */

/**
 *   external-error,  interal-error, success;
 * @param <I>
 * @param <O>
 */
public abstract class FunctionOfDatabase<I,O> extends StandardFunction<I,O> {
    private BaseSqliteHelper mSqliteHelper;

    public FunctionOfDatabase(BaseSqliteHelper sqliteHelper,
                              ConditionHandler<? extends StandardFunction<?,?>> onExternalError,
                              ConditionHandler<? extends StandardFunction<?,?>> onInteralError,
                              ConditionHandler<? extends StandardFunction<?,?>> onSucceed,
                              ConditionHandler<? extends StandardFunction<?,?>> onFailed) {
        super(onSucceed, onInteralError, onExternalError, onFailed);
        mSqliteHelper = sqliteHelper;
    }

    @Override
    public void apply() {
        apply(mSqliteHelper);
    }

    public abstract void apply(BaseSqliteHelper sqliteHelper);


    protected BaseSqliteHelper getSqliteHelper() {
        return mSqliteHelper;
    }


}
