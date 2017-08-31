package fulton.util.android.event;

/**
 * Created by 13774 on 7/20/2017.
 */

/**
 * sometimes a class needs to be serializable to be sent as a Listener.But its member cannot be serialized.
 */
public interface GenericListenerPrincipal{
    GenericListener getProxyListener();
}
