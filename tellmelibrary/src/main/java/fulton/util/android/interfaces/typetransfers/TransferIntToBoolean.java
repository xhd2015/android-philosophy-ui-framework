package fulton.util.android.interfaces.typetransfers;

/**
 * Created by 13774 on 8/9/2017.
 */

public class TransferIntToBoolean implements ValueTypeTransfer<Integer,Boolean> {
    @Override
    public Boolean transferPositive(Integer integer) {
        return integer==null?null:
                (integer==0?false:true);
    }

    @Override
    public Integer transferNagetive(Boolean aBoolean) {
        return aBoolean==null?
                null:
                aBoolean?1:0;
    }
}
