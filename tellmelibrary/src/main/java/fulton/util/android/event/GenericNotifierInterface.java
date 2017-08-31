package fulton.util.android.event;

/**
 * Created by 13774 on 7/18/2017.
 */

/**
 * listnerId is used for listner to identify the source notification.
 */
public interface GenericNotifierInterface {
    void setOnNotifyListener(int listenerId, int typeIndex, GenericListener listener);
    int getEventCount();
    void notifyListeners(int typeIndex,Object...args);
    void notifyListener(int listenerId, int typeIndex, GenericListener listener, Object... args);//in this case,listener does not need to call setOnNotifyListerner
}
