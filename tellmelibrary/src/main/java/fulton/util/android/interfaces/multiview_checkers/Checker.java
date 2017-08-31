package fulton.util.android.interfaces.multiview_checkers;

import android.view.View;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.ListView;

import fulton.util.android.interfaces.tuples.Pair;
import fulton.util.android.interfaces.tuples.Single;

/**
 * Created by 13774 on 8/26/2017.
 */

public class Checker<I,C extends AbstractChecker<? super I>> {
    private I mTarget;
    private C mChecker;

    public Checker(I target, C checker) {
        mTarget = target;
        mChecker = checker;
    }

    public boolean check() {
        return mChecker.check(mTarget);//the super,if determined,is a sure type.
    }

    public static void done()
    {
        Checker<Pair<View,View>,MultiViewChecker<Single<View>>> c;
        Test<CursorAdapter,ListView> test;
    }
    public static boolean runCheckers(Checker[] checkers)
    {
        for(int i=0;i<checkers.length;i++)
            if(checkers[i]!=null  && !checkers[i].check())
                return false;
        return true;
    }


    public static class Test<T extends CursorAdapter,AV extends AdapterView<? super T>>
    {

    }
}
