package fulton.util.android.interfaces.typetransfers;

/**
 * Created by 13774 on 8/9/2017.
 */


public class TransferBooleanString implements ValueTypeTransfer<Boolean,String> {
    @Override
    public String transferPositive(Boolean aBoolean) {
        return aBoolean==null?null:String.valueOf(aBoolean);
    }

    @Override
    public Boolean transferNagetive(String s) {
        return s==null?null:Boolean.valueOf(s);
    }
}
