package fulton.util.android.interfaces.standard_functions;

import android.content.ContentValues;

import fulton.util.android.interfaces.tuples.Pair;
import fulton.util.android.public_interfaces.BaseSqliteHelper;

/**
 * Created by 13774 on 8/25/2017.
 */

public class FunctionOfUpdatingOrAddingItem extends FunctionOfOperatingItemBase<Pair<String,Long>,Pair<Long,Boolean>> {
    public FunctionOfUpdatingOrAddingItem(BaseSqliteHelper sqliteHelper, ConditionHandler onExternalError,
                                          ConditionHandler onInteralError,
                                          ConditionHandler onSucceed,
                                          ConditionHandler onFailed) {
        super(sqliteHelper, onExternalError, onInteralError, onSucceed, onFailed);
    }

    @Override
    protected boolean doFuncAndSetResult(BaseSqliteHelper sqliteHelper, ContentValues values) {
        if(getInput().first.second>=0)
        {
            boolean res=sqliteHelper.updateItemInTable(getInput().first.first,getInput().first.second,values);
            setOutput(new Pair<Long, Boolean>(getInput().first.second,res));
            return res;
        }else{
            long id=sqliteHelper.insert(getInput().first.first,null,values);
            setOutput(new Pair<Long, Boolean>(id,id>=0));
            return id>=0;
        }
    }
}
