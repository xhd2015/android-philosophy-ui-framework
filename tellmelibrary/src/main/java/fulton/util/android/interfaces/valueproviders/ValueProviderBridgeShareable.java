package fulton.util.android.interfaces.valueproviders;

import fulton.util.android.interfaces.ValueGetter;

/**
 * Created by 13774 on 8/23/2017.
 */

public class ValueProviderBridgeShareable<E,KeyType,KeySetType ,PShared extends ShareableValueProvider<E,KeyType,KeySetType>>
        extends ValueProvider<KeyType,KeySetType>

{
    private PShared mShared;
    private E mHolder;

    public ValueProviderBridgeShareable(E holder, PShared shared)
    {
        mHolder=holder;
        mShared=shared;
    }

    /**
     * based on the holder's type,choose one.
     * @param holder
     */
    public ValueProviderBridgeShareable(E holder)
    {
        mHolder=holder;
        mShared=ShareableValueProvider.getInstanceBasedHolderTypePrefer(holder.getClass());
    }


    public <S> S getValue(KeyType k, Class<S> valueClass) {
        return mShared.getValue(mHolder,k,valueClass);
    }

    public <S> S getOrDefaultValue(KeyType k, @ValueGetter.ValueGetterType Object defaultValue, Class<S> valueClass) {
        return mShared.getOrDefaultValue(mHolder,k,defaultValue,valueClass);
    }

    public <S> S getOrPutDefaultValue(KeyType k, @ValueGetter.ValueGetterType Object defaultValue, Class<S> valueClass) {
        return mShared.getOrPutDefaultValue(mHolder,k,defaultValue,valueClass);
    }

    public void putValue(KeyType k, Object value) {
        mShared.putValue(mHolder,k,value);
    }

    public boolean remove(KeyType k) {
        return mShared.remove(mHolder,k);
    }

    public KeySetType getKeys() {
        return mShared.getKeys(mHolder);
    }

    @Override
    public boolean contains(KeyType k) {
        return mShared.containsKey(mHolder,k);
    }

    public ShareableValueProvider<E, KeyType, KeySetType> getShareableValueProvider() {
        return mShared;
    }

    public E getHolder() {
        return mHolder;
    }

    public void setSharedValueProvider(PShared shared) {
        mShared = shared;
    }
    public void setHolder(E holder) {
        mHolder = holder;
    }
}
