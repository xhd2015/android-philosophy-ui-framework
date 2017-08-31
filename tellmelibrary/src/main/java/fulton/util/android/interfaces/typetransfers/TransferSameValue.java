package fulton.util.android.interfaces.typetransfers;

/**
 * Created by 13774 on 8/10/2017.
 */

public class TransferSameValue<E> implements ValueTypeTransfer<E,E> {
    @Override
    public E transferPositive(E e) {
        return e;
    }

    @Override
    public E transferNagetive(E e) {
        return e;
    }
}
