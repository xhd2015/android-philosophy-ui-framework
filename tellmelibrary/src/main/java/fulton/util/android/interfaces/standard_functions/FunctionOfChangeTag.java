package fulton.util.android.interfaces.standard_functions;

/**
 * Created by 13774 on 8/30/2017.
 */

import fulton.util.android.public_interfaces.BaseSqliteHelper;

/**
 *   define a group of tags.
 *    tagArr[i]= {}.
 *     this
 *   and defines
 */
public abstract class FunctionOfChangeTag extends FunctionOfDatabase {
    public FunctionOfChangeTag(BaseSqliteHelper sqliteHelper, ConditionHandler onExternalError, ConditionHandler onInteralError, ConditionHandler onSucceed, ConditionHandler onFailed) {
        super(sqliteHelper, onExternalError, onInteralError, onSucceed, onFailed);
    }
}
