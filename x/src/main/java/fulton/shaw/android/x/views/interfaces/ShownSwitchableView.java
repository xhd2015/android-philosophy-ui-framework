package fulton.shaw.android.x.views.interfaces;

import android.view.View;

/**
 * Created by 13774 on 8/17/2017.
 */

public interface ShownSwitchableView<V extends View> {
    public abstract class OnSwitchListener extends LinkedListener<ShownSwitchableView>{

        @Override
        protected void sublistenerFunction(ShownSwitchableView arg) {
            if(arg.isShown())
                onShown(arg);
            else
                onHide(arg);
        }

        public abstract void onShown(ShownSwitchableView view);
        public abstract void onHide(ShownSwitchableView view);
    }

    V getDecroView();
    void switchShown();
    void setShown(boolean shown);
    boolean isShown();
    void setOnSwitchListener(OnSwitchListener listener);
    OnSwitchListener getOnSwitchListener();

}
