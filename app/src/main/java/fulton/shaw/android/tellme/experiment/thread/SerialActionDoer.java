package fulton.shaw.android.tellme.experiment.thread;

/**
 * Created by 13774 on 7/16/2017.
 */

/**
 * each action will not be completed in the order that will be betray their coming order, this is what Serial means.
 */
public abstract class SerialActionDoer extends LooperThread {

    public abstract void putAction(Runnable next);
}
