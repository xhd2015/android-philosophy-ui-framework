package fulton.util.android.interfaces;

/**
 * Created by 13774 on 8/21/2017.
 */

/**
 *  you cannot call getClass from an int or long's instance
 *  but you call that on int[]'s instance.
 *
 *  this means the primitives are not actually Object.
 *
 *   so the primitives will not be actually passed as argument to obtain anything.
 */
public abstract class PrimitiveTypeProcessor {
    public void processInteger(){}
    public void processFloat(){}
    public void processint(){}
    public void processfloat(){}
    public void processboolean(){}
    public void processBoolean(){}
    public void processDouble(){}
    public void prcoessdouble(){}
    public void processintArray(){}
    public void processObjectArray(){
        Class a=int.class;

    }
    public abstract void processNull();
}
