package fulton.util.android.interfaces.linear_dataholder;

import java.util.Iterator;

import fulton.util.android.utils.DataUtils;

/**
 * Created by 13774 on 8/30/2017.
 */

public class LinearArrayIterator<E> implements Iterator<E>{
    @DataUtils.LinearStructure
    private E[] mHolder;
    private int mIndex;

    public LinearArrayIterator(E[] holder) {
        mHolder = holder;
        mIndex=0;
    }

    @Override
    public boolean hasNext() {
        return mIndex < mHolder.length;
    }

    @Override
    public E next() {
        return mHolder[mIndex++];
    }
}
