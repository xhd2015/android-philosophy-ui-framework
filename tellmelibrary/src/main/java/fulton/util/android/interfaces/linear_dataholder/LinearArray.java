package fulton.util.android.interfaces.linear_dataholder;

import android.support.annotation.NonNull;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import fulton.util.android.interfaces.comparators.ListBasedComparator;
import fulton.util.android.interfaces.valueproviders.ShareableValueProvider;
import fulton.util.android.utils.Util;

/**
 * Created by 13774 on 8/28/2017.
 */

/**
 *  L is primitive type of array or Object[]
 *
 *  however,we don't suggest using primitve array
 * @param <E>
 */
public class LinearArray<E> implements List<E>{

    private E[] mHolder;

    public LinearArray(E[] holder) {
        mHolder = holder;
    }

    @Override
    public int size() {
        return mHolder.length;
    }

    @Override
    public boolean isEmpty() {
        return mHolder.length==0;
    }

    @Override
    public boolean contains(Object o) {
        return Util.searchArray(mHolder, o, 0, 1, ListBasedComparator.<E[],Object>getSimplestComparator())!=-1;
    }

    @NonNull
    @Override
    public Iterator<E> iterator() {
        return new LinearArrayIterator<>(mHolder);
    }

    @NonNull
    @Override
    public Object[] toArray() {
        Object[] res=new Object[mHolder.length];
        for(int i=0;i<mHolder.length;i++)
            res[i]=mHolder[i];
        return res;
    }

    @NonNull
    @Override
    public <T> T[] toArray(@NonNull T[] a) {
        return null;
    }

    @Override
    public boolean add(E e) {
        throw new ShareableValueProvider.UnsupportedModifying("array does not support adding");
    }

    @Override
    public boolean remove(Object o) {
        throw new ShareableValueProvider.UnsupportedModifying("array does not support removing");
    }

    @Override
    public boolean containsAll(@NonNull Collection<?> c) {
        throw new ShareableValueProvider.UnsupportedModifying("array does not support removing");
    }

    @Override
    public boolean addAll(@NonNull Collection<? extends E> c) {
        throw new ShareableValueProvider.UnsupportedModifying("array does not support adding");
    }

    @Override
    public boolean addAll(int index, @NonNull Collection<? extends E> c) {
        throw new ShareableValueProvider.UnsupportedModifying("array does not support adding");
    }

    @Override
    public boolean removeAll(@NonNull Collection<?> c) {
        throw new ShareableValueProvider.UnsupportedModifying("array does not support removing");
    }

    @Override
    public boolean retainAll(@NonNull Collection<?> c) {

        throw new ShareableValueProvider.UnsupportedModifying("array does not support removing");
    }

    @Override
    public void clear() {
        throw new ShareableValueProvider.UnsupportedModifying("array does not support clearing");
    }

    @Override
    public E get(int index) {
        return mHolder[index];
    }

    @Override
    public E set(int index, E element) {
        return mHolder[index]=element;
    }

    @Override
    public void add(int index, E element) {
        throw new ShareableValueProvider.UnsupportedModifying("array does not support adding");
    }

    @Override
    public E remove(int index) {
        throw new ShareableValueProvider.UnsupportedModifying("array does not support removing");
    }

    @Override
    public int indexOf(Object o) {
        return Util.searchArray(mHolder, o, 0, 1, ListBasedComparator.<E[],Object>getSimplestComparator());
    }

    @Override
    public int lastIndexOf(Object o) {
        throw new UnsupportedOperationException("no support");
    }

    @Override
    public ListIterator<E> listIterator() {
        throw new UnsupportedOperationException();
    }

    @NonNull
    @Override
    public ListIterator<E> listIterator(int index) {
        throw new UnsupportedOperationException();
    }

    @NonNull
    @Override
    public List<E> subList(int fromIndex, int toIndex) {
        throw new UnsupportedOperationException();
    }
}
