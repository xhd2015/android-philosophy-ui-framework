package fulton.shaw.android.x.models;

import java.util.HashMap;

/**
 * Created by 13774 on 8/14/2017.
 */

/**
 * does not hold null value
 * no null key
 */
public abstract class LazyGetter {
    public interface InstanceLoader<K, V> {
        V getValue(K k);
    }

    public LazyGetter() {
    }


    public <E,V> V get(E key)
    {
        if(key==null)throw new NullPointerException("key is null");
        HashMap stores=getStores();
        V value = (V) stores.get(key);
        if (value == null) {
            value=instanceGet(key);
            stores.put(key,value);
        }
        return value;
    }
    public <E, V> V get(E key, Class<V> vClass) {
        return get(key);
    }

    protected abstract <E,V> V instanceGet(E e);
    protected abstract HashMap<Object, Object> getStores();
}
