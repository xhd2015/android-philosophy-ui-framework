package fulton.util.android.interfaces.linear_dataholder;

import android.support.annotation.NonNull;

import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import fulton.util.android.notations.IsAFactory;

/**
 * Created by 13774 on 8/28/2017.
 */

@IsAFactory
public class LinearDataHolder<L,E> implements List<E>{
    protected L mHolder;
    private List<E>  mProxyList;

    public LinearDataHolder(L holder) {
        mHolder = holder;
        if(holder instanceof List)
        {
            mProxyList=new LinearList<>((List<E>)holder);
        }else{
            mProxyList=new LinearArray<>((E[])holder);
        }
    }

    @Override
    public int size() {
        return mProxyList.size();
    }

    @Override
    public boolean isEmpty() {
        return mProxyList.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return mProxyList.contains(o);
    }

    @NonNull
    @Override
    public Iterator<E> iterator() {
        return mProxyList.iterator();
    }

    @NonNull
    @Override
    public Object[] toArray() {
        return mProxyList.toArray();
    }

    @NonNull
    @Override
    public <T> T[] toArray(@NonNull T[] a) {
        return mProxyList.toArray(a);
    }

    @Override
    public boolean add(E e) {
        return mProxyList.add(e);
    }

    @Override
    public boolean remove(Object o) {
        return mProxyList.remove(o);
    }

    @Override
    public boolean containsAll(@NonNull Collection<?> c) {
        return mProxyList.containsAll(c);
    }

    @Override
    public boolean addAll(@NonNull Collection<? extends E> c) {
        return mProxyList.addAll(c);
    }

    @Override
    public boolean addAll(int index, @NonNull Collection<? extends E> c) {
        return mProxyList.addAll(index, c);
    }

    @Override
    public boolean removeAll(@NonNull Collection<?> c) {
        return mProxyList.removeAll(c);
    }

    @Override
    public boolean retainAll(@NonNull Collection<?> c) {
        return mProxyList.retainAll(c);
    }

    @Override
    public void clear() {
        mProxyList.clear();
    }

    @Override
    public boolean equals(Object o) {
        return mProxyList.equals(o);
    }

    @Override
    public int hashCode() {
        return mProxyList.hashCode();
    }

    @Override
    public E get(int index) {
        return mProxyList.get(index);
    }

    @Override
    public E set(int index, E element) {
        return mProxyList.set(index, element);
    }

    @Override
    public void add(int index, E element) {
        mProxyList.add(index, element);
    }

    @Override
    public E remove(int index) {
        return mProxyList.remove(index);
    }

    @Override
    public int indexOf(Object o) {
        return mProxyList.indexOf(o);
    }

    @Override
    public int lastIndexOf(Object o) {
        return mProxyList.lastIndexOf(o);
    }

    @Override
    public ListIterator<E> listIterator() {
        return mProxyList.listIterator();
    }

    @NonNull
    @Override
    public ListIterator<E> listIterator(int index) {
        return mProxyList.listIterator(index);
    }

    @NonNull
    @Override
    public List<E> subList(int fromIndex, int toIndex) {
        return mProxyList.subList(fromIndex, toIndex);
    }

}
