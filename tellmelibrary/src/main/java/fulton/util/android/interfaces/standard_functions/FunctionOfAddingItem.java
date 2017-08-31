package fulton.util.android.interfaces.standard_functions;

import android.content.ContentValues;

import fulton.util.android.interfaces.tuples.Single;
import fulton.util.android.public_interfaces.BaseSqliteHelper;

/**
 * Created by 13774 on 8/25/2017.
 */

/**
 *  givens a checkers group,valueprovider
 *
 *   preset values,table
 *
 *    check
 *          throws checkerror
 *    transfer value
 *          throws transfer error
 *
 */
public class FunctionOfAddingItem extends FunctionOfOperatingItemBase<Single<String>,Long> {

    public FunctionOfAddingItem(BaseSqliteHelper sqliteHelper, ConditionHandler onExternalError, ConditionHandler onInteralError,
                                ConditionHandler onSucceed, ConditionHandler onFailed) {
        super(sqliteHelper, onExternalError, onInteralError, onSucceed, onFailed);
    }

    @Override
    protected boolean doFuncAndSetResult(BaseSqliteHelper sqliteHelper,ContentValues values) {
        long id=sqliteHelper.insert(getInput().first.first,null,values);
        setOutput(id);
        return id>=0;
    }
}



