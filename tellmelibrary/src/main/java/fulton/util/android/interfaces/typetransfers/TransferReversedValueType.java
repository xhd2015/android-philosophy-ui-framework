package fulton.util.android.interfaces.typetransfers;

/**
 * Created by 13774 on 8/10/2017.
 */

public class TransferReversedValueType<E,V> implements ValueTypeTransfer<V,E> {
    protected ValueTypeTransfer<E,V> mOriginalTransfer;
    public TransferReversedValueType(ValueTypeTransfer<E,V> originalTransfer)
    {
        mOriginalTransfer=originalTransfer;
    }
    @Override
    public E transferPositive(V v) {
        return mOriginalTransfer.transferNagetive(v);
    }

    @Override
    public V transferNagetive(E e) {
        return mOriginalTransfer.transferPositive(e);
    }
}
