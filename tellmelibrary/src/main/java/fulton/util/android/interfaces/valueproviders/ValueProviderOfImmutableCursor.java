package fulton.util.android.interfaces.valueproviders;

import android.database.Cursor;

import fulton.util.android.interfaces.ValueGetter;
import fulton.util.android.notations.SingleInstance;
import fulton.util.android.utils.SqlUtil;

/**
 * Created by 13774 on 8/21/2017.
 */

@SingleInstance
public class ValueProviderOfImmutableCursor extends ValueProvider<String,String[]> {

    private Cursor holder;

    public ValueProviderOfImmutableCursor(Cursor holder) {
        this.holder = holder;
    }

    @Override
    public <S> S getValue(String key, Class<S> valueClass) {
        return SqlUtil.getCursorValue(holder,key,valueClass);
    }

    @Override
    public <S> S getOrDefaultValue(String key, @ValueGetter.ValueGetterType Object defaultValue, Class<S> valueClass) {
        if (contains(key))
        {
            return (S)SqlUtil.getCursorValue(holder,
                    key,
                    ValueGetter.getTypeFromObjectOrGetter(key,defaultValue,valueClass));
        }else{
            return (S) ValueGetter.getValueFromObjectOrGetter(key,defaultValue);
        }
    }

    @Override
    public <S> S getOrPutDefaultValue( String k, @ValueGetter.ValueGetterType Object defaultValue, Class<S> valueClass) {
        throw new ShareableValueProvider.UnsupportedModifying("add value to cursor not supported,please use getOrDefaultValue instead.");
    }

    @Override
    public void putValue( String k, Object value) {
        throw new ShareableValueProvider.UnsupportedModifying("put value to cursor not supported.");
    }

    @Override
    public boolean contains(String key) {
        int index=holder.getColumnIndex(key);
        return index!=-1;
    }

    @Override
    public boolean remove(String k) {
        throw new ShareableValueProvider.UnsupportedModifying("remove key from cursor not supported");
    }

    @Override
    public String[] getKeys() {
        return holder.getColumnNames();
    }

    public Cursor getHolder() {
        return holder;
    }

    public void setHolder(Cursor holder) {
        this.holder = holder;
    }
}
