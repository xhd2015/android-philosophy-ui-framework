package fulton.util.android.interfaces.valueproviders;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import fulton.util.android.interfaces.ValueGetter;
import fulton.util.android.interfaces.comparators.ListBasedComparator;
import fulton.util.android.interfaces.linear_dataholder.LinearDataHolder;
import fulton.util.android.notations.HowToUse;
import fulton.util.android.notations.SingleInstance;
import fulton.util.android.utils.DataUtils;

/**
 * Created by 13774 on 8/22/2017.
 */

/**
 *
 * @param <L> must has the format: <i,i+1>---><KeyType,ValueType>
 * @param <KeyType>
 */
@SingleInstance
public class ValueProviderOfLinearStructure<L,KeyType> extends ValueProvider<KeyType,KeyType[]> {

    private KeyType[] mKeys;
    private LinearDataHolder<L,Object> mHolder;
    private ListBasedComparator<KeyType[],KeyType> mComparator;

    /**
     * @param keys
     * @param comparator  if it is null,the default comparator will be used.see {@link ListBasedComparator#getSimplestComparator()}
     */
    public ValueProviderOfLinearStructure(
                     @DataUtils.LinearStructure L holder,
                    @NonNull KeyType[] keys,
                                                 @Nullable ListBasedComparator<KeyType[],KeyType> comparator
                                        ) {
        if(keys==null||keys.length==0)
            throw new NullPointerException("Keys cannot be null not empty.");
        mHolder=new LinearDataHolder<>(holder);
        mKeys=keys;
        if(comparator==null)
            mComparator=ListBasedComparator.getSimplestComparator();
        else
            mComparator=comparator;
    }



    private int findIndex(KeyType k)
    {
        return DataUtils.findIndex(mKeys,(Class<KeyType[]>)mKeys.getClass(),k,0,1,mComparator);
    }

    @Override
    public <S> S getValue(KeyType k, Class<S> valueClass) {
        int index=findIndex(k);
        return (S) mHolder.get(index);
    }

    @Override
    public <S> S getOrDefaultValue(KeyType k, @ValueGetter.ValueGetterType Object defaultValue, Class<S> valueClass) {
        int index=findIndex(k);
        if(index!=-1)
            return (S)mHolder.get(index);
        else
            return (S)ValueGetter.getValueFromObjectOrGetter((KeyType)k,defaultValue);
    }

    @Override
    public <S> S getOrPutDefaultValue(KeyType k, @ValueGetter.ValueGetterType Object defaultValue, Class<S> valueClass) {
        throw new ShareableValueProvider.UnsupportedModifying("Cannot put value into fixed-key-list,all the present keys have their default value");
    }

    @Override
    public void putValue( KeyType k, Object value) {
        int index=findIndex(k);
        mHolder.set(index,value);
    }

    @Override
    public boolean contains( KeyType k) {
        int index=findIndex(k);
        return index!=-1;
    }

    @Override
    public boolean remove(KeyType k) {
        throw new ShareableValueProvider.UnsupportedModifying("Cannot remove key from key-fixed list");
    }

    @Override
    public KeyType[] getKeys() {
        return mKeys;
    }
}
