package fulton.util.android.event;

/**
 * Created by 13774 on 7/18/2017.
 */

/**
 * 委托者
 * 委托一个Notifier进行通知,针对Java没有多继承的变通方法
 */
public interface GenericNotifierPrincipal {
    GenericNotifier getProxyNotifier();
}
