package fulton.shaw.android.x.viewgetter.transferview;

import android.view.View;

import fulton.util.android.interfaces.typetransfers.ValueTypeTransfer;
import fulton.util.android.interfaces.typetransfers.ValueTypeTransferFactory;
import fulton.shaw.android.x.viewgetter.transferview.viewadapter.ViewDatabaseAdapter;

/**
 * Created by 13774 on 8/13/2017.
 */

public class ShareableViewValueTransfer<ViewType extends View,ValueType> {
    protected ValueTypeTransfer<Object,ValueType> mValueTypeTransfer;

    public ShareableViewValueTransfer(Class<ViewType> viewClass,Class<ValueType> valueClass)
    {
        this(ValueTypeTransferFactory.getTransfer(ViewDatabaseAdapter.getViewAdapterDefaultValueType(viewClass),valueClass));
    }
    public ShareableViewValueTransfer(ValueTypeTransfer<?,ValueType> transfer)
    {
        mValueTypeTransfer= (ValueTypeTransfer<Object, ValueType>) transfer;
    }
    /**
     *  view-->value
     * @return
     */
    public ValueType getValue(ViewType view)
    {
        return mValueTypeTransfer.transferPositive(ViewDatabaseAdapter.getViewValue(view));//because getViewValue never fails.so check will start at transfer
    }

    /**
     * value-->view
     * @param value
     */
    public void setValue(ViewType view,ValueType value)
    {
        ViewDatabaseAdapter.setViewValue(view,mValueTypeTransfer.transferNagetive(value));
    }
    public ValueTypeTransfer<Object, ValueType> getTypeTransfer() {
        return mValueTypeTransfer;
    }

    public void setTypeTransfer(ValueTypeTransfer<?, ValueType> valueTypeTransfer) {
        mValueTypeTransfer = (ValueTypeTransfer<Object, ValueType>) valueTypeTransfer;
    }


}
