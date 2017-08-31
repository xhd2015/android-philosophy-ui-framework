package fulton.util.android.interfaces.typetransfers;

import java.util.Calendar;
import java.util.HashMap;

/**
 * Created by 13774 on 8/10/2017.
 */

public class ValueTypeTransferFactory {
    /**
     * lazy mode
     */
    public static HashMap<Class,HashMap<Class,ValueTypeTransfer>> MAP_CLASS;

    public static <E,V> ValueTypeTransfer<E,V> getTransfer(Class<E> eClass, Class<V> vClass)
    {
        if(MAP_CLASS==null)
        {
            MAP_CLASS=new HashMap<Class, HashMap<Class, ValueTypeTransfer>>();
         }
        HashMap<Class,ValueTypeTransfer> targetMap=MAP_CLASS.get(eClass);
        if(targetMap==null)
        {
            targetMap=new HashMap<>();
            MAP_CLASS.put(eClass,targetMap);
        }
        ValueTypeTransfer<E,V> transfer=targetMap.get(vClass);
        if(transfer==null)
        {
            transfer=getInstanceOfTransfer(eClass,vClass);
            if(transfer!=null)
                targetMap.put(vClass,transfer);
        }
        return transfer;
    }

    protected static <E,V> ValueTypeTransfer getInstanceOfTransfer(Class<E> eClass,Class<V> vClass)
    {
        if(eClass.equals(vClass))
            return new TransferSameValue();

        if(String.class.equals(eClass))
        {
            if(Integer.class.equals(vClass))
            {
                return new TransferReversedValueType(new TransferIntString());//using any existing transfer
            }
        }
        else if(Boolean.class.equals(eClass))
        {
            if(String.class.equals(vClass))
            {
                return new TransferBooleanString();
            }
        }else if(Calendar.class.equals(eClass)){
            if(Long.class.equals(vClass))
            {
                return new TransferCalendarToLong();
            }else if(String.class.equals(vClass)){
                return new TransferCalendarToString();
            }
        }else if(Integer.class.equals(eClass)){
            if(Boolean.class.equals(vClass))
            {
                return new TransferIntToBoolean();
            }else if(String.class.equals(vClass)){
                return new TransferIntString();
            }else if(Long.class.equals(vClass)){
                return new TransferIntToLong();
            }
        }else if(Long.class.equals(eClass)){
            if(Calendar.class.equals(vClass)){
                return new TransferLongToCalendar();
            }else if(Integer.class.equals(vClass)){
                return new TransferReversedValueType(new TransferIntToLong());
            }
        }
        throw new UnsupportedClassVersionError("<"+eClass.getName()+","+vClass.getName()+"> not supported");
    }
}
