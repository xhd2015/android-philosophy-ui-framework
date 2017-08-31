package fulton.util.android.interfaces.typetransfers;

/**
 * Created by 13774 on 8/13/2017.
 */

public class TransferIntToLong implements ValueTypeTransfer<Integer,Long> {
    @Override
    public Long transferPositive(Integer integer) {
        return integer!=null?(long)integer:null;
    }

    @Override
    public Integer transferNagetive(Long aLong) {
        return aLong!=null?(int)(long)aLong:null;
    }
}
