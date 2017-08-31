package fulton.shaw.android.x.viewgetter.transferview;

import android.support.annotation.NonNull;
import android.view.View;

import fulton.util.android.interfaces.typetransfers.ValueTypeTransfer;

/**
 * Created by 13774 on 8/9/2017.
 */

/**
 *  long<-->calendar
 *  int<-->String
 *  boolean<-->int
 *  boolean
 *
 *   allowes
 *
 *   取消subType，因为完全可以通过继承来实现
 *
 * E-->  view's data type
 * V-->  database data type
 *
 * this is also a factory
 */
public class FixedViewValueTransfer<ViewType extends View,ValueType> extends ShareableViewValueTransfer<ViewType,ValueType> {

    protected ViewType mView;
    /**
     * @param view
     * @param valueTypeTransfer
     */
    public FixedViewValueTransfer(ViewType view, @NonNull ValueTypeTransfer<?, ValueType> valueTypeTransfer) {
        super(valueTypeTransfer);
        mView = view;
    }


    /**
     *  use default transfer
     * @param view
     */
    public FixedViewValueTransfer(ViewType view, Class<ValueType> valueClass)
    {
        super((Class<ViewType>) view.getClass(),valueClass);
        mView=view;
    }

    public ValueType getValue() {
        return super.getValue(mView);
    }

    public void setValue(ValueType value) {
        super.setValue(mView, value);
    }
    public ViewType getView()
    {
        return mView;
    }
}
