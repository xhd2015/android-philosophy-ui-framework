package fulton.util.android.interfaces;

/**
 * Created by 13774 on 8/23/2017.
 */

public abstract class IndexedGetter<V> extends ValueGetter<Integer,V> {


    @Override
    public V getValue(Integer integer) {
        return null;
    }

    public abstract int size();

    public static <E> IndexedGetter<E> getArrayGetter(final E[] array)
    {
        return new IndexedGetter<E>(){
            @Override
            public E getValue(Integer integer) {
                return array[integer];
            }

            @Override
            public int size() {
                return array.length;
            }
        };
    }
}
