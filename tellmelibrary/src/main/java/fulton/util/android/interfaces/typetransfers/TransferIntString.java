package fulton.util.android.interfaces.typetransfers;

/**
 * Created by 13774 on 8/9/2017.
 */

public class TransferIntString implements ValueTypeTransfer<Integer,String> {
    @Override
    public String transferPositive(Integer integer) {
        return integer==null?null:String.valueOf(integer);
    }

    @Override
    public Integer transferNagetive(String s) {
        return s==null?null:Integer.valueOf(s);
    }
}
