package fulton.util.android.interfaces.multiview_checkers;

import android.view.View;

import fulton.util.android.interfaces.tuples.Single;

/**
 * Created by 13774 on 8/25/2017.
 */

public abstract class ViewCheckerOfSingleView<V extends View> extends MultiViewChecker<Single<V>>  {
    @Override
    public boolean check(Single<V> vSingle) {
        return check(vSingle.first);
    }

    public abstract boolean check(V view);
}
