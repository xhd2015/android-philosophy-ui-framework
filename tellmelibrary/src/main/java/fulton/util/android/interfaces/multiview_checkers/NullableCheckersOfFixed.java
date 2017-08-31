package fulton.util.android.interfaces.multiview_checkers;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by 13774 on 8/26/2017.
 */

/**
 *  nullable,always returns true.
 */
public class NullableCheckersOfFixed implements Iterable<Checker>{
    private ArrayList<Checker> mCheckers;

    public NullableCheckersOfFixed() {
        mCheckers = new ArrayList<>();
    }

    public Checker getChecker(int index) {
        return mCheckers.get(index);
    }

    public boolean addChecker(Checker checker) {
        return mCheckers.add(checker);
    }

    public Checker removeChecker(int index) {
        return mCheckers.remove(index);
    }

    public Iterator<Checker> iterator() {
        return mCheckers.iterator();
    }

    public boolean check()
    {
        for(Checker c:this)
        {
            if(c!=null && !c.check())
                return false;
        }
        return true;
    }
}
