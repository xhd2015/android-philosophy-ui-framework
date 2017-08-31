package fulton.util.android.interfaces.iterators;

import java.util.Iterator;

import fulton.util.android.interfaces.tuples.Single;
import fulton.util.android.notations.TestNote;
import fulton.util.android.utils.DataUtils;

/**
 * Created by 13774 on 8/25/2017.
 */

@TestNote(TestNote.STATE.TESTED_SUCCESS_WEAK)
public class Zip<I extends Single> implements Iterable<I> {
    private Object[] mIters;

    public Zip(Object...iters) {
        mIters = iters;
    }

    @Override
    public Iterator<I> iterator() {
        Iterator[] iterators=new Iterator[mIters.length];
        for(int i=0;i<iterators.length;i++) {
            if(mIters[i] instanceof Iterable)
                iterators[i] =((Iterable) mIters[i]).iterator();
            else
                iterators[i]=new DataUtils.LinearIterator(mIters[i],Object.class);
        }
        return new ZipIterator<>(iterators);
    }

    public static <I extends Single> Zip<I> zip(Object...iters)
    {
        return new Zip<I>(iters);
    }
}
