package fulton.util.android.interfaces.multiview_checkers;

import fulton.util.android.notations.MayConsumeTimePleaseUtilize;
import fulton.util.android.notations.SingleInstanceShareable;

/**
 * Created by 13774 on 8/26/2017.
 */

@SingleInstanceShareable
public abstract class AbstractChecker<I> {
    public abstract boolean check(I i) ;
}
