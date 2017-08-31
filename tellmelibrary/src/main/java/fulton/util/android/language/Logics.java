package fulton.util.android.language;

import fulton.util.android.notations.HowToUse;

/**
 * Created by 13774 on 7/29/2017.
 */

public class Logics {

    public Object or(Object...objects)
    {
        for(Object o:objects)
        {

        }
        return null;
    }

    public static <E> E valueOrDefault(E value,E defaultValue)
    {
        return value==null?defaultValue:value;
    }


    public static <K,E> E cast(K k,E e)
    {
        return (E) k;
    }

    public static <E> E cast(Object o)
    {
        return (E)o;
    }
}
