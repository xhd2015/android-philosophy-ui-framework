package fulton.util.android.interfaces.typetransfers;

/**
 * Created by 13774 on 8/9/2017.
 */
/**
 *
 * @param <E>  original value
 * @param <V>  target value
 */
public interface ValueTypeTransfer<E,V> {
//        class CannotFinishTranferingException extends Exception{} //can always be hide
        class UnsupportedTransferation extends Error{}
        V transferPositive(E e) ;//throws CannotFinishTranferingException;//E->V
        E transferNagetive(V v) ;//throws CannotFinishTranferingException;//V->E
}
