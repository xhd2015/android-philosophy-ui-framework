package fulton.util.android.interfaces.multiview_valuegetter;

import android.view.View;

import fulton.util.android.interfaces.ValueGetter;
import fulton.util.android.interfaces.tuples.Single;

/**
 * Created by 13774 on 8/25/2017.
 */

/**
 *  it can also set value of multi views.
 * @param <I>
 * @param <E>
 */
public abstract class MultiViewValueGetter<I extends Single,E> extends ValueGetter<I,E> {

    public @interface ViewsCollection{};

    public abstract void setValue(I i,E value);

    @Override
    public  abstract Class<E> getValueType() ;
}
