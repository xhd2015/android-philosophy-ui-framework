package fulton.util.android.interfaces.valueproviders;

import android.content.ContentValues;

import java.util.Set;

import fulton.util.android.interfaces.ValueGetter;
import fulton.util.android.notations.SingleInstance;
import fulton.util.android.utils.SqlUtil;

/**
 * Created by 13774 on 8/21/2017.
 */

@SingleInstance
public class ValueProviderOfContentValues extends ValueProvider<String,Set<String>> {
    private ContentValues holder;

    public ValueProviderOfContentValues(ContentValues holder) {
        this.holder = holder;
    }

    @Override
    public <S> S getValue(String k, Class<S> valueClass) {
        return (S)holder.get(k);
    }

    @Override
    public <S> S getOrDefaultValue(String key, @ValueGetter.ValueGetterType Object defaultValue, Class<S> valueClass) {
        if(holder.containsKey(key))
        {
            return (S)holder.get(key);
        }else{
            return  (S) ValueGetter.getValueFromObjectOrGetter(key,defaultValue);
        }
    }

    @Override
    public <S> S getOrPutDefaultValue( String key, @ValueGetter.ValueGetterType Object defaultValue, Class<S> valueClass)
    {
        if(holder.containsKey(key))
        {
            return (S)holder.get(key);
        }else{
            S value=(S) ValueGetter.getValueFromObjectOrGetter(key,defaultValue);
            SqlUtil.putValue(holder,key,value);
            return value;
        }
    }

    @Override
    public void putValue( String key,Object value) {
        SqlUtil.putValue(holder,key,ValueGetter.getValueFromObjectOrGetter(key,value));
    }


    @Override
    public boolean contains(String key) {
        return holder.containsKey(key);
    }

    @Override
    public boolean remove(String key) {
        holder.remove(key);
        return true;
    }

    @Override
    public Set<String> getKeys() {
        return holder.keySet();
    }

}
