package fulton.util.android.interfaces.standard_functions;

import android.content.ContentValues;

import fulton.util.android.interfaces.tuples.Pair;
import fulton.util.android.public_interfaces.BaseSqliteHelper;

/**
 * Created by 13774 on 8/25/2017.
 */

public class FunctionOfUpdatingItem extends FunctionOfOperatingItemBase<Pair<String,Long>,Boolean> {
    public FunctionOfUpdatingItem(BaseSqliteHelper sqliteHelper, ConditionHandler onExternalError,
                                  ConditionHandler onInteralError, ConditionHandler onSucceed,
                                  ConditionHandler onFailed) {
        super(sqliteHelper, onExternalError, onInteralError, onSucceed, onFailed);
    }

    @Override
    protected boolean doFuncAndSetResult(BaseSqliteHelper sqliteHelper, ContentValues values) {
        boolean res=sqliteHelper.updateItemInTable(getInput().first.first,getInput().first.second,values);
        setOutput(res);
        return res;
    }
}
