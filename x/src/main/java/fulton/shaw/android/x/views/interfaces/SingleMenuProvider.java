package fulton.shaw.android.x.views.interfaces;

/**
 * Created by 13774 on 8/17/2017.
 */

/**
 *  不要直接使用属性，而是使用相应的方法。属性不是多态的，但是方法是。使用private具有更好的性质。
 *  建议完全舍弃protected field的用法。
 */
public abstract class SingleMenuProvider{
    public abstract void onRequestMenuShow();
    public abstract void onRequestMenuHide();
    /**
     *
     * @return how many functions that can be requested on menu type i.
     */
    public abstract int getActionsNumber();

    public abstract boolean isShown();
}
