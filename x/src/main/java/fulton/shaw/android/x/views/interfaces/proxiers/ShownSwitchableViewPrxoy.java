package fulton.shaw.android.x.views.interfaces.proxiers;

import android.support.annotation.NonNull;
import android.view.View;

import fulton.shaw.android.x.views.interfaces.ShownSwitchableView;
import fulton.util.android.utils.ViewUtil;

/**
 * Created by 13774 on 8/17/2017.
 */

public class ShownSwitchableViewPrxoy<V extends View> implements ShownSwitchableView {

    private V mView;
    private boolean mShown;

    private LinkedListenerAwareProxy<ShownSwitchableView,OnSwitchListener> mListenerProxy;

    public ShownSwitchableViewPrxoy(V view,boolean shown,OnSwitchListener listener) {
        mView = view;
        mShown=shown;
        ViewUtil.setShown(mView,shown);
        mListenerProxy=new LinkedListenerAwareProxy<>();
        if(listener!=null)
            setOnSwitchListener(listener);
    }
    public ShownSwitchableViewPrxoy(V view)
    {
        mView=view;
        mShown=true;
    }


    @Override
    public void switchShown() {
        mShown=!mShown;
        ViewUtil.setShown(mView,mShown);
        if(mListenerProxy.getOnEventLinkedListener()!=null)
            mListenerProxy.getOnEventLinkedListener().doFunction(this);
    }

    @Override
    public void setShown(boolean shown) {
        if(mShown!=shown)
        {
            switchShown();
        }
    }
    @Override
    public V getDecroView() {
        return mView;
    }


    @Override
    public boolean isShown() {
        return mShown;
    }

    @Override
    public void setOnSwitchListener(@NonNull ShownSwitchableView.OnSwitchListener listener) {
        mListenerProxy.seOnEventLinkedListener(listener);
    }

    @Override
    public OnSwitchListener getOnSwitchListener() {
        return mListenerProxy.getOnEventLinkedListener();
    }

}
