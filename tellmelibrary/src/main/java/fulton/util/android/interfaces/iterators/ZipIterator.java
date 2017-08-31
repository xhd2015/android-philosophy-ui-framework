package fulton.util.android.interfaces.iterators;

import java.util.Iterator;

import fulton.util.android.interfaces.tuples.Single;
import fulton.util.android.utils.Util;

/**
 * Created by 13774 on 8/25/2017.
 */

public class ZipIterator<I extends Single> implements Iterator<I> {
    private Iterator[] mIters;

    public ZipIterator(Iterator...iters) {
        if(iters.length==0)
            throw new NullPointerException("iterators cannot be empty or null");
        if(iters.length>Single.getMaxSizeSupported())
            throw new UnsupportedOperationException("iterators cannot be more than "+Single.getMaxSizeSupported());
        mIters = iters;
    }

    @Override
    public boolean hasNext() {
        for(int i=0;i<mIters.length;i++)
            if(mIters[i].hasNext())
                return true;
        return false;
    }

    @Override
    public I next() {
        I s=Single.getNewInstance(mIters.length);
        for(int i=0;i<mIters.length;i++)
            if(mIters[i].hasNext())
                Single.set(s,i,mIters[i].next());
        return s;
    }
}
