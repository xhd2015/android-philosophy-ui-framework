package fulton.util.android.interfaces.valueproviders;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import fulton.util.android.interfaces.ValueGetter;
import fulton.util.android.interfaces.comparators.ListBasedComparator;
import fulton.util.android.interfaces.multiview_valuegetter.ViewValueGetterOfFixed;
import fulton.util.android.notations.HowToUse;
import fulton.util.android.utils.DataUtils;

/**
 * Created by 13774 on 8/25/2017.
 */

public class ValueProviderOfViews extends
        ValueProviderOfLinearStructure<ViewValueGetterOfFixed[],String> {
    /**
     * @param getters
     * @param keys
     * @param comparator if it is null,the default comparator will be used.see {@link ListBasedComparator#getSimplestComparator()}
     */
    public ValueProviderOfViews(@DataUtils.LinearStructure ViewValueGetterOfFixed[] getters, @NonNull String[] keys, @Nullable ListBasedComparator<String[], String> comparator) {
        super(getters, keys, comparator);
    }

    @Override
    public <S> S getValue(String k, Class<S> valueClass) {
        return (S) super.getValue(k, ViewValueGetterOfFixed.class).getValue();
    }

    @Override
    public <S> S getOrDefaultValue(String k, @ValueGetter.ValueGetterType Object defaultValue, Class<S> valueClass) {
        ViewValueGetterOfFixed getter=super.getOrDefaultValue(k, null, ViewValueGetterOfFixed.class);
        if(getter==null)
            return (S) ValueGetter.getValueFromObjectOrGetter(k,defaultValue);
        else
            return (S) getter.getValue();
    }

    @Override
    public <S> S getOrPutDefaultValue(String k, @ValueGetter.ValueGetterType Object defaultValue, Class<S> valueClass) {
        return super.getOrPutDefaultValue(k, defaultValue, valueClass);
    }

    @Override
    public void putValue(String k, Object value) {
        ViewValueGetterOfFixed getter=super.getValue(k,ViewValueGetterOfFixed.class);
        if(getter!=null)
            getter.setValue(value);
        else
            throw new UnsupportedOperationException("key "+k+" does not exists");
    }

    @Override
    public boolean remove(String k) {
        throw new UnsupportedOperationException("views does not support removing");
    }

    @Override
    public String[] getKeys() {
        return super.getKeys();
    }
}
