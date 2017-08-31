package fulton.shaw.android.tellme.experiment.event;

import fulton.util.android.event.GenericNotifier;

/**
 * Created by 13774 on 7/20/2017.
 */

public abstract class NotifiableRunnable extends GenericNotifier implements Runnable {
    public static final int EVENT_DONE=0;
    public static final int EVENT_COUNT=1;
    public NotifiableRunnable()
    {
        super(EVENT_COUNT);
    }
    public NotifiableRunnable(int eventCount)
    {
        super(eventCount);
    }


}
