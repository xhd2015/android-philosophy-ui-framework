package fulton.shaw.android.x.views.interfaces;

/**
 * Created by 13774 on 8/17/2017.
 */

import java.util.ArrayList;

/**
 * 菜单，本质上就是可视化的view加上功能actions。
 */
public class MultipleTypeMenuProvider {
    private ArrayList<SingleMenuProvider> mMenuList;



    public MultipleTypeMenuProvider()
    {
        mMenuList=new ArrayList<>();
    }

    protected ArrayList<SingleMenuProvider> getMenuList()
    {
        return mMenuList;
    }
    public void addMenuProvider(SingleMenuProvider provider)
    {
        mMenuList.add(provider);
    }

    public void onRequestMenuShow(int i)
    {
        mMenuList.get(i).onRequestMenuShow();
    }
    public void onRequestMenuHide(int i)
    {
        mMenuList.get(i).onRequestMenuHide();
    }

    /**
     *
     * @return how many functions that can be requested on menu type i.
     */
    public int getActionsNumber(int i)
    {
        return mMenuList.get(i).getActionsNumber();
    }

    /**
     *
     * @return how many menu types you do provide
     */
    public int getMenuTypes()
    {
        return mMenuList.size();
    }

}
