package fulton.util.android.interfaces.typetransfers;

/**
 * Created by 13774 on 8/17/2017.
 */

public class TransferStringToPositiveInteger implements ValueTypeTransfer<String,Integer> {
    @Override
    public Integer transferPositive(String s){
        return Math.abs(Integer.valueOf(s));
    }

    @Override
    public String transferNagetive(Integer integer) {
        return integer.toString();
    }
}
