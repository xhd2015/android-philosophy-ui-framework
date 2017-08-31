package fulton.util.android.interfaces.valueproviders;

/**
 * Created by 13774 on 8/21/2017.
 */

import android.content.ContentValues;
import android.database.Cursor;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import fulton.util.android.interfaces.ValueGetter;
import fulton.util.android.notations.HelperStaticMethods;
import fulton.util.android.notations.IsAFactory;
import fulton.util.android.notations.for_as.ClassDef;
import fulton.util.android.utils.DataUtils;
import fulton.util.android.utils.Util;
import fulton.util.android.varman.StateVariableManager;


/**
 *
 * @param <E>
 * @param <KeyType>  之所以将Key提到类型参数上，是因为key即使范围变动，许多容器也不要求使用相应的方法。Object可以作为key。
 */
public abstract class ShareableValueProvider<E,KeyType,KeySetType>{

    public static class UnsupportedKeyType extends Error{
        public UnsupportedKeyType(){}
        public UnsupportedKeyType(Object key){
            super(key==null?"Null class type":key.getClass().getName());
        }
    }
    public static class UnsupportedModifying extends Error{
        public UnsupportedModifying(){}
        public UnsupportedModifying(String s){super(s);}
    }
    public static class UnsupportedMethod extends Error{
        public UnsupportedMethod(){}
        public UnsupportedMethod(String s){super(s);}
    }


    @ClassDef({
            StateVariableManager.class,
            ContentValues.class,
            Map.class,
            List.class,
            Cursor.class,
            Object[].class,
            int[].class,
            long[].class,
            short[].class,
            double[].class,
            float[].class,
            char[].class,
            boolean[].class,
            byte[].class
    })
    public @interface KeyedValueHolder{}

    @ClassDef({
            Object[].class, Set.class,int[].class,
            long[].class,
            short[].class,
            double[].class,
            float[].class,
            char[].class,
            boolean[].class,
            byte[].class
    })
    public @interface KeyedSetType{}

    @KeyedValueHolder
    private Class<E> mHolderType;

    protected ShareableValueProvider(@KeyedValueHolder Class<E> holderClass)
    {
        if(holderClass==null)
            throw new NullPointerException("holder class cannot be null at runtime");
        mHolderType=holderClass;
    }

    public Class<E> getHolderType()
    {
        return mHolderType;
    }

    public abstract  <S> S getValue(E holder,KeyType k,Class<S> valueClass);

    /**
     *
     * @param holder
     * @param k
     * @param defaultValue either value or valuegetter
     * @param valueClass  must match the defaultValue's type or its returned type as a getter
     * @param <S>
     * @return
     */
    public abstract <S> S getOrDefaultValue(E holder,KeyType k,@ValueGetter.ValueGetterType Object defaultValue,Class<S> valueClass);
    public abstract <S> S getOrPutDefaultValue(E holder,KeyType k,@ValueGetter.ValueGetterType Object defaultValue,Class<S> valueClass);
    public abstract void putValue(E holder,KeyType k,Object value);
    public abstract boolean containsKey(E holder,KeyType k);
    public abstract boolean remove(E holder, KeyType k);


//    /**
//     *
//     * @return
//     */
//    public ShareableValueProvider<E,KeyType,KeySetType> copy()
//    {
//        throw new UnsupportedMethod();
//    }
//    /**
//     *  this method copies from the valueProvider.
//     * @param valueProvider
//     * @return
//     */
//    public ShareableValueProvider<E,KeyType,KeySetType> newInstance(ShareableValueProvider<E,KeyType> valueProvider,Class<E> holder){
//        ShareableValueProvider<E,KeyType> provider=newInstance((Class<E>) holder.getClass());
//        KeyType[] keySetList=getKeys(null);//or set,or k[]
//        final Class aClass = KeyType[].class;
//
//    }
    public abstract  KeySetType getKeys(E holder);


    @HelperStaticMethods
    public static <K,S> void checkKeyTypeThrowsError(K k,Class<S> sClass)throws UnsupportedKeyType
    {
        if(k!=null&& !sClass.isAssignableFrom(k.getClass()))
        {
            throw new UnsupportedKeyType(k);
        }
    }

    @IsAFactory
    public static <ProviderType> ProviderType getInstanceBasedHolderTypePrefer(Class clz)
    {
        if (false)
        {
            return null;
        }else{
            throw new UnsupportedOperationException("Factory does not support holder type:"+clz);
        }
    }



}
