package fulton.util.android.utils;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import fulton.util.android.dialog.PopupDialog;
import fulton.util.android.interfaces.IndexedGetter;
import fulton.util.android.interfaces.ValueGetter;
import fulton.util.android.interfaces.comparators.Comparator;
import fulton.util.android.interfaces.comparators.ListBasedComparator;
import fulton.util.android.notations.IsAFactory;
import fulton.util.android.notations.PythonStyle;
import fulton.util.android.notations.for_as.ClassDef;


/**
 * Created by 13774 on 8/19/2017.
 */

public class DataUtils {

    public static class LinearIterator<L,K> implements Iterator<K> {
        @DataUtils.LinearStructure
        private L mHolder;
        private int mIndex;
        private Class<K> mValueClz;

        public LinearIterator(L holder, Class<K> valueClz) {
            mHolder = holder;
            mValueClz = valueClz;
            mIndex=0;
        }

        @Override
        public boolean hasNext() {
            return mIndex < DataUtils.getSize(mHolder,(Class<L>) mHolder.getClass());
        }

        @Override
        public K next() {
            return DataUtils.getLinearValue(mHolder,(Class<L>) mHolder.getClass(),mIndex++,mValueClz);
        }
    }

    @Retention(RetentionPolicy.SOURCE)
    @ClassDef({
            List.class,
            Object[].class,
            int[].class,
            long[].class,
            short[].class,
            double[].class,
            float[].class,
            char[].class,
            boolean[].class
    })
    public  @interface LinearStructure{}

    /**
     * means Object[] is not null.
     */
    public @interface StrictArray{}

    /**
     *
     * @param list
     * @param clz  for the annotation
     * @param i
     * @param e
     * @param <L>
     * @param <E>
     */
    public  static <L,E> void setLinearValue(L list, @LinearStructure Class<L> clz, int i, E e)
    {
        if(List.class.isAssignableFrom(clz))
        {
            List ll= (List) list;
            ll.set(i,e);
        }else if(Object[].class.isAssignableFrom(clz)){
            Object[] ol= (Object[]) list;
            ol[i]=e;
        }else if(int[].class.isAssignableFrom(clz)){
            int[] ol= (int[]) list;
            ol[i]=(Integer)e;
        }else if(long[].class.isAssignableFrom(clz)){
            long[] ol= (long[]) list;
            ol[i]=(Long)e;
        }else if(short[].class.isAssignableFrom(clz)){
                short[] ol= (short[]) list;
            ol[i]=(Short)e;
        }else if(double[].class.isAssignableFrom(clz)){
                double[] ol= (double[]) list;
            ol[i]=(Double)e;
        }else if(float[].class.isAssignableFrom(clz)){
                float[] ol= (float[]) list;
            ol[i]=(Float)e;
        }else if(char[].class.isAssignableFrom(clz)){
            char[] ol= (char[]) list;
            ol[i]=(char)(Character)e;
        }else if(boolean[].class.isAssignableFrom(clz)){
            boolean[] ol= (boolean[]) list;
            ol[i]=(boolean)(Boolean)e;
        }
    }

    public static  <L,E> E getLinearValue(L list,@LinearStructure Class<L> clz,int i,Class<E> eclz)
    {
        if(List.class.isAssignableFrom(clz))
        {
            List ll= (List) list;
            return (E) ll.get(i);
        }else if(Object[].class.isAssignableFrom(clz)){
            Object[] ol= (Object[]) list;
            return (E) ol[i];
        }else if(int[].class==clz){
            int[] ol= (int[]) list;
            return (E)(Integer) ol[i];
        }else if(long[].class.isAssignableFrom(clz)){
            long[] ol= (long[]) list;
            return (E)(Long) ol[i];
        }else if(short[].class.isAssignableFrom(clz)){
            short[] ol= (short[]) list;
            return (E)(Short) ol[i];
        }else if(double[].class.isAssignableFrom(clz)){
            double[] ol= (double[]) list;
            return (E)(Double) ol[i];
        }else if(float[].class.isAssignableFrom(clz)){
            float[] ol= (float[]) list;
            return (E)(Float) ol[i];
        }else if(char[].class.isAssignableFrom(clz)){
            char[] ol= (char[]) list;
            return (E)(Character) ol[i];
        }else if(boolean[].class.isAssignableFrom(clz)){
            boolean[] ol= (boolean[]) list;
            return (E)(Boolean) ol[i];
        }else{
            throw new UnsupportedClassVersionError(clz.getName()+" is not supported");
        }
    }


    public static <L> int getSize(L list,@LinearStructure Class<L> clz)
    {
        if(List.class.isAssignableFrom(clz))
        {
            List ll= (List) list;
            return ll.size();
        }else if(Object[].class.isAssignableFrom(clz)){
            Object[] ol= (Object[]) list;
            return ol.length;
        }else if(int[].class.isAssignableFrom(clz)){
            int[] ol= (int[]) list;
            return ol.length;
        }else if(long[].class.isAssignableFrom(clz)){
            long[] ol= (long[]) list;
            return ol.length;
        }else if(short[].class.isAssignableFrom(clz)){
            short[] ol= (short[]) list;
            return ol.length;
        }else if(double[].class.isAssignableFrom(clz)){
            double[] ol= (double[]) list;
            return ol.length;
        }else if(float[].class.isAssignableFrom(clz)){
            float[] ol= (float[]) list;
            return ol.length;
        }else if(char[].class.isAssignableFrom(clz)){
            char[] ol= (char[]) list;
            return ol.length;
        }else if(boolean[].class.isAssignableFrom(clz)){
            boolean[] ol= (boolean[]) list;
            return ol.length;
        }else{
            throw new UnsupportedClassVersionError(clz.getName()+" is not supported");
        }
    }

    public static <L,E> L newList(@LinearStructure Class<L> clz,int size,E defaultValue)
    {
        if(List.class.isAssignableFrom(clz))
        {
            try {
                final Constructor<L> constructor = clz.getConstructor(int.class);
                List list= (List) constructor.newInstance(size);
                while (list.size()<size)
                    list.add(defaultValue);
                return (L) list;

            } catch (NoSuchMethodException e) {
                throw new UnsupportedOperationException("No Init method for "+clz);
            } catch (IllegalAccessException|InstantiationException|InvocationTargetException e) {
                throw new Error("Other unknown error");
            }
        }else if(Object[].class.isAssignableFrom(clz)){
            Object[] ol= new Object[size];
            if(defaultValue!=null)
                for(int i=0;i<size;i++)ol[i]=defaultValue;
            return (L) ol;
        }else if(int[].class.isAssignableFrom(clz)){
            int[] ol= new int[size];
            if(defaultValue!=null)
                for(int i=0;i<size;i++)ol[i]=(Integer)defaultValue;
            return (L) ol;
        }else if(long[].class.isAssignableFrom(clz)){
            long[] ol= new long[size];
            if(defaultValue!=null)
                for(int i=0;i<size;i++)ol[i]=(Long)defaultValue;
            return (L) ol;
        }else if(short[].class.isAssignableFrom(clz)){
            short[] ol= new short[size];
            if(defaultValue!=null)
                for(int i=0;i<size;i++)ol[i]=(Short)defaultValue;
            return (L) ol;
        }else if(double[].class.isAssignableFrom(clz)){
            double[] ol= new double[size];
            if(defaultValue!=null)
                for(int i=0;i<size;i++)ol[i]=(Double)defaultValue;
            return (L) ol;
        }else if(float[].class.isAssignableFrom(clz)){
            float[] ol= new float[size];
            if(defaultValue!=null)
                for(int i=0;i<size;i++)ol[i]=(Float)defaultValue;
            return (L) ol;
        }else if(char[].class.isAssignableFrom(clz)){
            char[] ol= new char[size];
            if(defaultValue!=null)
                for(int i=0;i<size;i++)ol[i]=(Character)defaultValue;
            return (L) ol;
        }else if(boolean[].class.isAssignableFrom(clz)){
            boolean[] ol= new boolean[size];
            if(defaultValue!=null)
                for(int i=0;i<size;i++)ol[i]=(Boolean)defaultValue;
            return (L) ol;
        }else{
            throw new UnsupportedClassVersionError(clz.getName()+" is not supported");
        }
    }

    /**
     *  find the first which value appears.if not found,then return -1.
     * @param list
     * @param clz
     * @param value
     * @param <L>
     * @param <E>
     * @return
     */
    public static <L,E> int findIndex(L list, @LinearStructure Class<L> clz, E value, int start, int step,ListBasedComparator<L,E> listValueComparator)
    {
        int size=getSize(list,clz);
        for(int i=start;i<size;i+=step)
        {
            listValueComparator.setCurrentIndex(i);
            if(listValueComparator.equalsWith(list,value))
            {
                return i;
            }
        }
        return -1;
    }

    public static <L,E> int findIndex(L list, @LinearStructure Class<L> clz, E value,ListBasedComparator<L,E> listValueComparator)
    {
        return findIndex(list,clz,value,0,1,listValueComparator);
    }
    public static <L,E> int findIndex(L list, @LinearStructure Class<L> clz, E value)
    {
        return findIndex(list,clz,value,ListBasedComparator.<L, E>getSimplestComparator());
    }







    /**
     *
     * @param map
     * @param key
     * @param getter   if it is instance of ValueGetter,
     * @param <K>
     * @param <V>
     * @return
     */
    public static <K,V,GetterType> V getOrPutDefaultValueMap(Map<K,V> map, K key, GetterType getter)
    {
        V value=map.get(key);
        if(value==null)
        {
            if(getter==null)
                throw new NullPointerException("getter or value cannot be null");
            if(getter instanceof ValueGetter)
                value=((ValueGetter<K,V>)getter).getValue(key);
            else
                value=(V)getter;
            map.put(key,value);
        }
        return value;
    }

    /**
     *   null is not considered as a good String[].
     *   @see StrictArray
     * @param args
     * @return
     */
    public static String[] combineStringArrays(String[]...args)throws NullPointerException
    {
        return combineStringArrays(IndexedGetter.getArrayGetter(args));
    }

    public static String[] combineStringArrays(IndexedGetter<String[]> provider)
    {
        int size=provider.size();
        int length=0;
        for(int i=0;i<size;i++)
            length+=provider.getValue(i).length;
        String[] x=new String[length];
        int index=0;
        for(int i=0;i<size;i++){
            for(String s:provider.getValue(i))
            {
                x[index++]=s;
            }
        }
        return x;
    }

}
