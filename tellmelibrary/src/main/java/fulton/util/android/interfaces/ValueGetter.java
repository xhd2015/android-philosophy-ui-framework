package fulton.util.android.interfaces;

import android.support.annotation.NonNull;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Calendar;

import fulton.util.android.notations.SingleInstance;

/**
 * Created by 13774 on 8/19/2017.
 */

public abstract class ValueGetter<E,V>{
    /**
     *  this is an annotation on types that should be obtained value throught {@link #getValueFromObjectOrGetter(Object, Object)}
     */
    @Retention(RetentionPolicy.SOURCE)
    public @interface ValueGetterType{}

    public abstract V getValue(E e);
    public Class<V> getValueType(){return null;}

    /**
     * @see ValueGetterType
     * @param k
     * @param o
     * @param <K>
     * @return
     */
    public static <K> Object getValueFromObjectOrGetter(K k,@ValueGetterType Object o)
    {
        if(o instanceof ValueGetter)
        {
            return ((ValueGetter<K,Object>)o).getValue(k);
        }else{
            return  o;
        }
    }

    /**
     *  if o is null, clz mustn't be null.
     *  if o is normal value,clz can be null,but it better not be null.it is used to cast the value.
     *  if o is value getter,clz can be null.its type will be retreived from value getter.But also,it better not be null,it is used to cast the value.
     *          UPDATE: for sake of programming,clz is always recommended return nonnull.
     * @param k
     * @param o
     * @param clz
     * @param <K>
     * @return
     */
    @NonNull
    public static <K> Class getTypeFromObjectOrGetter(K k, @ValueGetterType Object o, Class clz)
    {
        Class res=null;
        if(o==null)
        {
            res=clz;
        }else if(o instanceof ValueGetter){
            res=((ValueGetter<K,Object>)o).getValueType();
        }else{
             res=o.getClass();
        }
        if(res==null)
            throw new NullPointerException("param clz cannot be null when param o is null");
        return res;
    }

    @SingleInstance
    public static class ValueGetterOfCalendarNow extends ValueGetter<Object,Calendar>{
        private static ValueGetterOfCalendarNow sIntance=null;

        public static ValueGetterOfCalendarNow getIntance() {
            if(sIntance==null)
                sIntance=new ValueGetterOfCalendarNow();
            return sIntance;
        }

        private ValueGetterOfCalendarNow() {
        }

        @Override
        public Calendar getValue(Object o) {
            return Calendar.getInstance();
        }

        @Override
        public Class<Calendar> getValueType() {
            return Calendar.class;
        }
    }
}
