package fulton.util.android.interfaces.comparators;

import fulton.util.android.notations.LazyGetter;
import fulton.util.android.utils.DataUtils;

/**
 * Created by 13774 on 8/22/2017.
 */

public abstract class ListBasedComparator<L,E> extends Comparator<L,E> {

    private static ListBasedComparator sSimplestComparator=null;

    public abstract int getCurrentIndex();
    public abstract void setCurrentIndex(int index);
    @Override
    public abstract boolean equalsWith(L l, E e);
    @Override
    public int computeDiff(L l, E e) throws CannotComputeDifference {
        return super.computeDiff(l,e);
    }

    /**
     *  this comparator simply returns a comparator that compares current element with the given element.
     *  this is the most case that will be needed by common usage.
     * @param <L>
     * @param <E>
     * @return
     */
    @LazyGetter
    public static <L,E> ListBasedComparator<L,E> getSimplestComparator() {
        if(sSimplestComparator==null)
            sSimplestComparator=new ListBasedComparator<L,E>() {
                private int mCurIndex;
                @Override
                public boolean equalsWith(L l, E e) {
                    return Comparator.getEqualsComparator().equalsWith(
                            DataUtils.getLinearValue(l,(Class<L>)l.getClass(),getCurrentIndex(),Object.class),
                            e
                            );
                }

                @Override
                public int getCurrentIndex() {
                    return mCurIndex;
                }

                @Override
                public void setCurrentIndex(int index) {
                    mCurIndex=index;
                }
            };
        return sSimplestComparator;
    }

}
