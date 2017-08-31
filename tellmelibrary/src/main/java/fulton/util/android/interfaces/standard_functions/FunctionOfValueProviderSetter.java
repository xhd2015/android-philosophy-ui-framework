package fulton.util.android.interfaces.standard_functions;

/**
 * Created by 13774 on 8/25/2017.
 */

import fulton.util.android.interfaces.iterators.Zip;
import fulton.util.android.interfaces.tuples.Pair;
import fulton.util.android.interfaces.tuples.Triple;
import fulton.util.android.interfaces.valueproviders.ValueProviderBridgeShareable;

/**
 * needs
 *    valueprovider,
 *    keys,
 *    values
 */
public class FunctionOfValueProviderSetter extends
        StandardFunction<Triple<ValueProviderBridgeShareable,String[],Object[]>,Void> {
    public FunctionOfValueProviderSetter(ConditionHandler onSucceed, ConditionHandler onInteralError, ConditionHandler onExternalError, ConditionHandler onFailed) {
        super(onSucceed, onInteralError, onExternalError, onFailed);
    }

    @Override
    public void apply() {
        ValueProviderBridgeShareable provider=getInput().first;
        Object[] values=getInput().third;//how to zip them?
        try {
            for(Pair<String,Object> p:new Zip<Pair<String,Object>>(getInput().second,getInput().third))
                    provider.putValue(p.first,p.second);
        }catch (Exception|Error e){
            runOnCondtionHandler(this,getOnExternalError());
            return;
        }
        runOnCondtionHandler(this,getOnSucceed());
    }
}
