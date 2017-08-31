package fulton.shaw.android.x.views.interfaces;

import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

/**
 * Created by 13774 on 8/17/2017.
 */

public class ContextMenuButtonGroupMenuProvider extends MultipleTypeMenuProvider {
    private static final int CONTEXT_MENU_INDEX=0;
    private static final int BUTTON_GROUP_MENU_INDEX=1;

    public ContextMenuButtonGroupMenuProvider(String[] titles, ViewGroup buttonConainter, ArrayList<View> buttonList)
    {
        super();
        addMenuProvider(new ContextMenuProvider(titles));
        addMenuProvider(new ButtonGroupMenuProvider(buttonConainter,buttonList));
    }
    public ContextMenuButtonGroupMenuProvider()
    {
        super();
        addMenuProvider(new ContextMenuProvider());
        addMenuProvider(new ButtonGroupMenuProvider());
    }

    public void setContextMenuProvider(ContextMenuProvider provider)
    {
        getMenuList().set(CONTEXT_MENU_INDEX,provider);
    }
    public void setButtonGroupMenuProvider(ButtonGroupMenuProvider provider)
    {
        getMenuList().set(BUTTON_GROUP_MENU_INDEX,provider);
    }


    public ContextMenuProvider getContextMeuProvider()
    {
        return (ContextMenuProvider) getMenuList().get(CONTEXT_MENU_INDEX);
    }
    public ButtonGroupMenuProvider getButtonGroupMenuProvider()
    {
        return (ButtonGroupMenuProvider) getMenuList().get(BUTTON_GROUP_MENU_INDEX);
    }

}
