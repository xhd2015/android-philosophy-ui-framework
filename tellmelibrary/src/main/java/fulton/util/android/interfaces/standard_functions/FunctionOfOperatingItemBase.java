package fulton.util.android.interfaces.standard_functions;

import android.content.ContentValues;

import fulton.util.android.interfaces.multiview_checkers.Checker;
import fulton.util.android.interfaces.tuples.Quad;
import fulton.util.android.interfaces.tuples.Single;
import fulton.util.android.interfaces.tuples.Triple;
import fulton.util.android.interfaces.typetransfers.ValueTypeTransfer;
import fulton.util.android.interfaces.valueproviders.ValueProviderOfContentValues;
import fulton.util.android.interfaces.valueproviders.ValueProvider;
import fulton.util.android.public_interfaces.BaseSqliteHelper;

/**
 * Created by 13774 on 8/25/2017.
 */

/**
 *
 * @param <I> table & others
 * @param <E> result
 *
 *           ValueProviderOfFixed  -- how the value is provided
 *           checkers               -- how the values  are checked
 *           Triple<String,String,ValeuTypeTransfer>  -- to a content values, key & value transferss
 */
public abstract class FunctionOfOperatingItemBase<I extends Single<String>,E> extends
        FunctionOfDatabase<Quad<I,ValueProvider,Checker[],Triple<String,String,ValueTypeTransfer>[]>,E>
{
    public static final int ERROR_NO_ERROR=-1;
    public static final int ERROR_DURING_CHECK_FAILED =0;
    public static final int ERROR_DURING_TRANSFER_ERROR =1;
    public int errno;

    public FunctionOfOperatingItemBase(BaseSqliteHelper sqliteHelper,
                                       ConditionHandler<? extends StandardFunction<?,?>> onExternalError,
                                       ConditionHandler<? extends StandardFunction<?,?>> onInteralError,
                                       ConditionHandler<? extends StandardFunction<?,?>> onSucceed,
                                       ConditionHandler<? extends StandardFunction<?,?>> onFailed) {
        super(sqliteHelper, onExternalError, onInteralError, onSucceed, onFailed);
    }

    @Override
    public void apply(BaseSqliteHelper sqliteHelper) {
        errno=ERROR_NO_ERROR;//clear error first.

        ValueProvider provider=getInput().second;
        ContentValues values=new ContentValues();
        ValueProvider conReceiver=new ValueProviderOfContentValues(values);

        if(getInput().third!=null)//do some checking
        {
            if(Checker.runCheckers(getInput().third))
            {
                errno= ERROR_DURING_CHECK_FAILED;
                runOnCondtionHandler(this,getOnExternalError());
                return;
            }
        }


        try {
            ValueProvider.copyValueDiffKeysPositive(provider,conReceiver,getInput().fourth);
        }catch (Exception|Error e){
            errno=ERROR_DURING_TRANSFER_ERROR;
            runOnCondtionHandler(this,(ConditionHandler)getOnExternalError());
            return;
        }



        try {
            if(!doFuncAndSetResult(sqliteHelper,values))
            {
                runOnCondtionHandler(this,(ConditionHandler)getOnFailed());
                return;
            }else{
                runOnCondtionHandler(this,(ConditionHandler)getOnSucceed());
                return;
            }
        }catch (Exception | Error e){
            runOnCondtionHandler(this,(ConditionHandler)getOnInteralError());
            return;
        }

    }

    protected abstract boolean doFuncAndSetResult(BaseSqliteHelper sqliteHelper,ContentValues values);

}
