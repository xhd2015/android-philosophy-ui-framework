package fulton.util.android.interfaces.valueproviders;

import java.util.Iterator;

import fulton.util.android.interfaces.ValueGetter;
import fulton.util.android.interfaces.tuples.Pair;
import fulton.util.android.interfaces.tuples.Triple;
import fulton.util.android.interfaces.typetransfers.ValueTypeTransfer;
import fulton.util.android.utils.DataUtils;

/**
 * Created by 13774 on 8/27/2017.
 */


public abstract class ValueProvider<KeyType,KeySetType> implements Iterable<KeyType> {


    
    public abstract <S> S getValue(KeyType k, Class<S> valueClass) ;

    
    public abstract <S> S getOrDefaultValue(KeyType k, @ValueGetter.ValueGetterType Object defaultValue, Class<S> valueClass) ;
    
    public abstract <S> S getOrPutDefaultValue(KeyType k, @ValueGetter.ValueGetterType Object defaultValue, Class<S> valueClass) ;


    public abstract void putValue(KeyType k, Object value);


    public abstract boolean remove(KeyType k) ;
    
    public abstract KeySetType getKeys() ;
    public abstract boolean contains(KeyType k);
    
    public String toString()
    {
        StringBuilder sb=new StringBuilder("{");
        for(KeyType key:this)
        {
            sb.append(String.valueOf(key));
            sb.append(":");
            sb.append(String.valueOf(getValue(key,Object.class)));
            sb.append(", ");
        }
        sb.append("}");
        return sb.toString();
    }

    public ValueProvider<KeyType,?> copyDupKeysPositive(ValueProvider<KeyType,?> target, Pair<KeyType,ValueTypeTransfer>[] preserve)
    {
        return copyValuePositive(this,target,preserve);
    }

    /**
     * returns self.
     * @param target
     * @param preserve
     * @return
     */
    public ValueProvider<KeyType,?> copyDupKeysNagetive(ValueProvider<KeyType,?> target, Pair<KeyType,ValueTypeTransfer>[] preserve)
    {
        return copyValueNagetive(this,target,preserve);
    }
    public <TargetKeyType> ValueProvider<TargetKeyType,?> copyDiffKeysPositive(ValueProvider<TargetKeyType,?> target, Triple<KeyType,TargetKeyType,ValueTypeTransfer>[] preserve)
    {
        return copyValueDiffKeysPositive(this,target,preserve);
    }

    /**
     * return self.
     * @param target
     * @param preserve
     * @param <TargetKeyType>
     * @return
     */
    public <TargetKeyType> ValueProvider<KeyType,?> copyDiffKeysNagetive(ValueProvider<TargetKeyType,?> target, Triple<KeyType,TargetKeyType,ValueTypeTransfer>[] preserve)
    {
        return copyValueDiffKeysNagetive(this,target,preserve);
    }

    
    public Iterator<KeyType> iterator()
    {
        KeySetType keys=getKeys();
        if(keys instanceof Object[])
        {
            return (Iterator<KeyType>) new DataUtils.LinearIterator<Object[],Object>((Object[])keys,Object.class);
        }else if(keys instanceof Iterable){
            return ((Iterable)keys).iterator();
        }else{
            throw new UnsupportedOperationException("key set type for "+keys.getClass()+" is not supported");
        }
    }


    //===============used for the same key.
    /**
     * keys, the transfer.
     *
     *  when null,no transferation is performed.
     * @param p1
     * @param p2
     */
    public static <KeyType> ValueProvider<KeyType,?> copyValuePositive(ValueProvider<KeyType,?> p1, ValueProvider<KeyType,?> p2,
                                                                       Pair<KeyType,ValueTypeTransfer>[] preserve)
    {
        for(Pair<KeyType, ValueTypeTransfer> o:preserve)
        {
            Object value=p1.getValue(o.first,Object.class);
            if(value!=null)
            {
                if(o.second!=null)
                    p2.putValue(o.first,o.second.transferPositive(value));
                else
                    p2.putValue(o.first,value);
            }
        }
        return p2;
    }

    /**
     * @param p1
     * @param p2
     */
    public static <KeyType> ValueProvider<KeyType,?> copyValueNagetive(ValueProvider<KeyType,?> p1, ValueProvider<KeyType,?> p2, Pair<KeyType,ValueTypeTransfer>[] preserve)
    {
        for(Pair<KeyType, ValueTypeTransfer> o:preserve)
        {
            Object val=p2.getValue(o.first,Object.class);
            if(val!=null) {
                if (o.second != null)
                    p1.putValue(o.first, o.second.transferNagetive(val));
                else
                    p1.putValue(o.first,val);
            }
        }
        return p1;
    }

    /**
     *
     * @param p1
     * @param p2
     * @param preserve
     * @param <KeyType1>
     * @param <KeyType2>
     * @return
     */
    public static <KeyType1,KeyType2> ValueProvider<KeyType2,?> copyValueDiffKeysPositive(
            ValueProvider<KeyType1,?> p1, ValueProvider<KeyType2,?> p2, Triple<KeyType1,KeyType2,ValueTypeTransfer>[] preserve)
    {
        for(Triple<KeyType1, KeyType2, ValueTypeTransfer> o:preserve)
        {
            Object val=p1.getValue(o.first,Object.class);
            if(val!=null) {
                if (o.third != null)
                    p2.putValue(o.second, o.third.transferPositive(val));
                else
                    p2.putValue(o.second, val);
            }
        }
        return p2;
    }

    public static <KeyType1,KeyType2> ValueProvider<KeyType1,?> copyValueDiffKeysNagetive(
            ValueProvider<KeyType1,?> p1, ValueProvider<KeyType2,?> p2, Triple<KeyType1,KeyType2,ValueTypeTransfer>[] preserve)
    {
        for(Triple<KeyType1, KeyType2, ValueTypeTransfer> o:preserve)
        {
            Object val=p2.getValue(o.second,Object.class);
            if(val!=null) {
                if (o.third != null)
                    p1.putValue(o.first, o.third.transferNagetive(val));
                else
                    p1.putValue(o.first,val);
            }
        }
        return p1;
    }




}
