package fulton.util.android.interfaces.valueproviders;

import java.util.Set;

import fulton.util.android.interfaces.ValueGetter;
import fulton.util.android.varman.StateVariableManager;

/**
 * Created by 13774 on 8/28/2017.
 */

public class ValueProviderOfStateVariableManager extends ValueProvider<String,Set<String>> {
    private StateVariableManager holder;

    public ValueProviderOfStateVariableManager(StateVariableManager holder) {
        this.holder = holder;
    }

    @Override
    public <S> S getValue( String k, Class<S> valueClass) {
        return holder.get(k,valueClass);
    }

    @Override
    public <S> S getOrDefaultValue(String k, @ValueGetter.ValueGetterType Object defaultValue, Class<S> valueClass) {
        if(holder.containsKey(k))
        {
            return holder.get(k,valueClass);
        }else{
            return (S) ValueGetter.getValueFromObjectOrGetter(k,defaultValue);
        }
    }

    @Override
    public <S> S getOrPutDefaultValue(String k, @ValueGetter.ValueGetterType Object defaultValue, Class<S> valueClass) {
        return holder.get(k,defaultValue,valueClass);
    }

    @Override
    public void putValue(String k,Object value) {
        holder.set(k,value);
    }

    @Override
    public boolean remove(String k) {
        holder.remove(k);
        return true;
    }

    @Override
    public Set<String> getKeys() {
        return holder.keySet();
    }

    @Override
    public boolean contains(String k)
    {
        return holder.containsKey(k);
    }
}
