package fulton.shaw.android.x.views.interfaces;

import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;

import fulton.util.android.notations.HowToUse;
import fulton.util.android.utils.Util;

/**
 * Created by 13774 on 8/17/2017.
 */

/**
 *  single menu provider
 */
public class ContextMenuProvider extends SingleMenuProvider{
    protected ArrayList<String> mTitles;

    //==args
    private ContextMenu argInflateMenu;
    private MenuItem.OnMenuItemClickListener argMenuItemClickListener;

    public MenuItem.OnMenuItemClickListener getArgMenuItemClickListener() {
        return argMenuItemClickListener;
    }

    public void setArgMenuItemClickListener(MenuItem.OnMenuItemClickListener argMenuItemClickListener) {
        this.argMenuItemClickListener = argMenuItemClickListener;
    }
//    public void setMenuFunction(int i,)



    public ContextMenuProvider(ArrayList<String> titles) {
        mTitles = titles;
    }
    public ContextMenuProvider()
    {
        this(new ArrayList<String>());
    }

    public ContextMenuProvider(String[] titles)
    {
        this(Util.arrayToList(titles));
    }
    public void prepareShowArguments(ContextMenu menu, MenuItem.OnMenuItemClickListener listener)
    {
        setArgInflateMenu(menu);
        setArgMenuItemClickListener(listener);
    }
    public void addContextMenu(String s)
    {
        mTitles.add(s);
    }

    /**
     * prior to call this, set argument:
     *             argInflateMenu
     *          argMenuItemListener
     */
    @Override
    public void onRequestMenuShow() {//need some extra args.
        for(int j=0;j<mTitles.size();j++) {
            argInflateMenu.add(Menu.NONE,j,Menu.NONE,mTitles.get(j));
            argInflateMenu.getItem(j).setOnMenuItemClickListener(argMenuItemClickListener);
        }
    }

    @Override
    public void onRequestMenuHide() {
        //never called,it will always cancelled automatically
    }

    @Override
    public int getActionsNumber() {
        return mTitles.size();
    }

    @Override
    public boolean isShown() {
        throw new RuntimeException("You cannot getByGetter the shown state of context menu");
    }


    @HowToUse
    public static void HowToUse()
    {
        final ContextMenuProvider provider=new ContextMenuProvider(new String[]{"d","d"});

    }
    public ContextMenu getArgInflateMenu() {
        return argInflateMenu;
    }

    public void setArgInflateMenu(ContextMenu argInflateMenu) {
        this.argInflateMenu = argInflateMenu;
    }
}
