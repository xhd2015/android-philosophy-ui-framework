package fulton.util.android.interfaces.typetransfers;

/**
 * Created by 13774 on 8/20/2017.
 */

public class TransferDoubleChained<V,M,T> implements ValueTypeTransfer<V,T> {
    private ValueTypeTransfer<V,M> mT1;
    private ValueTypeTransfer<M,T> mT2;

    public TransferDoubleChained(ValueTypeTransfer<V, M> t1, ValueTypeTransfer<M, T> t2) {
        mT1 = t1;
        mT2 = t2;
    }

    @Override
    public T transferPositive(V v) {
        return mT2.transferPositive(mT1.transferPositive(v));
    }

    @Override
    public V transferNagetive(T t) {
        return mT1.transferNagetive(mT2.transferNagetive(t));
    }
}
