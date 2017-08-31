package fulton.util.android.interfaces.multiview_checkers;

import fulton.util.android.interfaces.multiview_valuegetter.MultiViewValueGetter;
import fulton.util.android.interfaces.tuples.Single;

/**
 * Created by 13774 on 8/25/2017.
 */

public abstract class MultiViewChecker<I extends Single> extends AbstractChecker<I> {
    public abstract boolean check(I i) ;
}
