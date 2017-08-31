package fulton.util.android.interfaces.standard_functions;

import fulton.util.android.interfaces.tuples.Pair;
import fulton.util.android.public_interfaces.BaseSqliteHelper;

/**
 * Created by 13774 on 8/25/2017.
 */

public class FunctionOfDeleteItem extends FunctionOfDatabase<Pair<String,Long>,Boolean> {


    public FunctionOfDeleteItem(BaseSqliteHelper sqliteHelper, ConditionHandler<FunctionOfDeleteItem> onExternalError, ConditionHandler<FunctionOfDeleteItem> onInteralError, ConditionHandler<FunctionOfDeleteItem> onSucceed, ConditionHandler<FunctionOfDeleteItem> onFailed) {
        super(sqliteHelper, onExternalError, onInteralError, onSucceed, onFailed);
    }

    @Override
    public void apply(BaseSqliteHelper sqliteHelper) {
        try {
            boolean res=sqliteHelper.deleteItemInTable(getInput().first,getInput().second);
            if(res)
            {
                runOnCondtionHandler(this,(ConditionHandler)getOnSucceed());
            }else{
               runOnCondtionHandler(this,(ConditionHandler)getOnFailed());
            }
        }catch (Exception|Error e){
            runOnCondtionHandler(this,(ConditionHandler)getOnInteralError());
        }
    }
}
