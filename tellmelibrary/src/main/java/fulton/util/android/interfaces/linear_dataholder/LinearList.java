package fulton.util.android.interfaces.linear_dataholder;

import android.support.annotation.NonNull;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 * Created by 13774 on 8/28/2017.
 */

public class LinearList<E> implements List<E>{
    private List<E> mHolder;

    public LinearList(List<E> holder) {
        mHolder = holder;
    }

    @Override
    public int size() {
        return mHolder.size();
    }

    @Override
    public boolean isEmpty() {
        return mHolder.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return mHolder.contains(o);
    }

    @NonNull
    @Override
    public Iterator<E> iterator() {
        return mHolder.iterator();
    }

    @NonNull
    @Override
    public Object[] toArray() {
        return mHolder.toArray();
    }

    @NonNull
    @Override
    public <T> T[] toArray(@NonNull T[] a) {
        return mHolder.toArray(a);
    }

    @Override
    public boolean add(E e) {
        return mHolder.add(e);
    }

    @Override
    public boolean remove(Object o) {
        return mHolder.remove(o);
    }

    @Override
    public boolean containsAll(@NonNull Collection<?> c) {
        return mHolder.containsAll(c);
    }

    @Override
    public boolean addAll(@NonNull Collection<? extends E> c) {
        return mHolder.addAll(c);
    }

    @Override
    public boolean addAll(int index, @NonNull Collection<? extends E> c) {
        return mHolder.addAll(index, c);
    }

    @Override
    public boolean removeAll(@NonNull Collection<?> c) {
        return mHolder.removeAll(c);
    }

    @Override
    public boolean retainAll(@NonNull Collection<?> c) {
        return mHolder.retainAll(c);
    }

    @Override
    public void clear() {
        mHolder.clear();
    }

    @Override
    public boolean equals(Object o) {
        return mHolder.equals(o);
    }

    @Override
    public int hashCode() {
        return mHolder.hashCode();
    }

    @Override
    public E get(int index) {
        return mHolder.get(index);
    }

    @Override
    public E set(int index, E element) {
        return mHolder.set(index, element);
    }

    @Override
    public void add(int index, E element) {
        mHolder.add(index, element);
    }

    @Override
    public E remove(int index) {
        return mHolder.remove(index);
    }

    @Override
    public int indexOf(Object o) {
        return mHolder.indexOf(o);
    }

    @Override
    public int lastIndexOf(Object o) {
        return mHolder.lastIndexOf(o);
    }

    @Override
    public ListIterator<E> listIterator() {
        return mHolder.listIterator();
    }

    @NonNull
    @Override
    public ListIterator<E> listIterator(int index) {
        return mHolder.listIterator(index);
    }

    @NonNull
    @Override
    public List<E> subList(int fromIndex, int toIndex) {
        return mHolder.subList(fromIndex, toIndex);
    }

}
