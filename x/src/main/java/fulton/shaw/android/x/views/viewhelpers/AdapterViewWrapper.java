package fulton.shaw.android.x.views.viewhelpers;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import java.util.ArrayList;

import fulton.shaw.android.x.views.interfaces.ButtonGroupMenuProvider;
import fulton.shaw.android.x.views.interfaces.ContextMenuButtonGroupMenuProvider;
import fulton.shaw.android.x.views.interfaces.ContextMenuProvider;
import fulton.shaw.android.x.views.interfaces.proxiers.ShownSwitchableViewPrxoy;
import fulton.util.android.interfaces.comparators.Comparator;
import fulton.util.android.utils.Util;

/**
 * Created by 13774 on 8/17/2017.
 */

/**
 * 显示菜单的功能由此类完成，而定义各个菜单正是其职责所在。菜单的具体类型，有菜单provider接口提供。
 *
 * user define:
 *          functions.
 *          click map to which function, or popup context menu,or show button group
 *          long click map to popup context menu
 *
 *          context menu-->match function
 *          button group -> match function
 *
 *          function type: position.
 *
 * @param <V>
 */
public class AdapterViewWrapper<V extends AdapterView> extends ShownSwitchableViewPrxoy<V>{
    public static abstract class FunctionProvider{
        public abstract void applyFunction(int i,int position);//i>=0
        public abstract int getCount();
    }

    /**
     *  this is built-in functions
     */
    public static final int FUNCTION_POP_UP_CONTEXT_MENU=-1;
    public static final int FUNCTION_SHOW_BUTTON_GROUP=-2;

    private Activity mActivity;
    private ContextMenuButtonGroupMenuProvider mMenuProvider;
    private FunctionProvider mFuncProvider;
    private ArrayList<int[]> mContextMenuMapFunction;
    private ArrayList<int[]> mButtonGroupMapFunction;
    private int[] mClickMapFunctions;

    private MenuItem.OnMenuItemClickListener mContextMenuItemClickListener;
    private View.OnClickListener mButtonGroupItemClickListener;


    private int mArgContextMenuOpViewIndex;
    private View mArgContextMenuOpView;

    private int mArgButtonGroupOpViewIndex;
    private View mArgButtonGroupOpView;

    private int mArgOpViewIndex;
    private View mArgOpView;


    public Activity getActivity() {
        return mActivity;
    }

    public AdapterViewWrapper(Activity activity, V adapterView)
    {
        super(adapterView);
        mActivity=activity;
        mMenuProvider=new ContextMenuButtonGroupMenuProvider();
        mContextMenuMapFunction=new ArrayList<>();
        mButtonGroupMapFunction=new ArrayList<>();

        mActivity.registerForContextMenu(getDecroView());

        mContextMenuItemClickListener=new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int pos=item.getItemId();
                int[] funId=mContextMenuMapFunction.get(pos);//id can never be less than 0
                setArgOpViewIndex(mArgContextMenuOpViewIndex);
                setArgOpView(mArgContextMenuOpView);
                doFuncs(funId);
                return true;
            }
        };

        mButtonGroupItemClickListener=new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int pos= Util.searchArrayList(mMenuProvider.getButtonGroupMenuProvider().getButtonList(),v,Comparator.getViewComparator());
                int[] funId=mButtonGroupMapFunction.get(pos);
                setArgOpViewIndex(mArgButtonGroupOpViewIndex);
                setArgOpView(mArgButtonGroupOpView);
                doFuncs(funId);
            }
        };


        getAdapterView().setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
            @Override
            public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
                AdapterView.AdapterContextMenuInfo menuInfo1= (AdapterView.AdapterContextMenuInfo) menuInfo;
                setArgContextMenuOpViewIndex(menuInfo1.position);
                setArgContextMenuOpView(menuInfo1.targetView);
                getContextMeuProvider().prepareShowArguments(menu,mContextMenuItemClickListener);
                getContextMeuProvider().onRequestMenuShow();
            }
        });
        getAdapterView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                for(int i:mClickMapFunctions)
                {
                    if(i ==FUNCTION_SHOW_BUTTON_GROUP) {
                        setArgButtonGroupOpViewIndex(position);
                        setArgButtonGroupOpView(view);
                    }else  if(i ==FUNCTION_POP_UP_CONTEXT_MENU){
                        setArgContextMenuOpViewIndex(position);
                        setArgContextMenuOpView(view);
                    }else{
                        setArgOpViewIndex(position);
                        setArgOpView(view);
                    }
                    doFunc(i);
                }
            }
        });
    }

    public View getArgOpView() {
        return mArgContextMenuOpView;
    }

    public void setArgContextMenuOpView(View argContextMenuOpView) {
        mArgContextMenuOpView = argContextMenuOpView;
    }

    protected void doFunc(int i)
    {
        if(i>=0)
        {
            mFuncProvider.applyFunction(i,mArgOpViewIndex);
        }else if(i==FUNCTION_POP_UP_CONTEXT_MENU){
            mActivity.openContextMenu(mArgContextMenuOpView);//this may returns null.
        }else if(i==FUNCTION_SHOW_BUTTON_GROUP){
            getButtonGroupMenuProvider().setArgAppendIndex(getArgButtonGroupOpViewIndex());
            getButtonGroupMenuProvider().setArgAppendView((ViewGroup) getDecroView().getChildAt(getArgButtonGroupOpViewIndex()));
            getButtonGroupMenuProvider().onRequestMenuShow();
        }
    }
    protected void doFuncs(int[] list)
    {
        for(int i:list)
            doFunc(i);
    }



    public void setButtonGroupContainerView(ViewGroup v)
    {
        getButtonGroupMenuProvider().setConainterView(v);
    }
    public void addButton(View v,int...funcId)
    {
        v.setOnClickListener(mButtonGroupItemClickListener);
        mMenuProvider.getButtonGroupMenuProvider().addButton(v);
        mButtonGroupMapFunction.add(funcId);
    }
    public void addContextMenu(String s,int...funcId)
    {
        mMenuProvider.getContextMeuProvider().addContextMenu(s);
        mContextMenuMapFunction.add(funcId);
    }
    public void setFunctionProvider(FunctionProvider provider)
    {
        mFuncProvider=provider;
    }
    public void setContextMenuProvider(ContextMenuProvider provider) {
        mMenuProvider.setContextMenuProvider(provider);
    }

    public void setButtonGroupMenuProvider(ButtonGroupMenuProvider provider) {
        mMenuProvider.setButtonGroupMenuProvider(provider);
    }

    public ContextMenuProvider getContextMeuProvider() {
        return mMenuProvider.getContextMeuProvider();
    }

    public ButtonGroupMenuProvider getButtonGroupMenuProvider() {
        return mMenuProvider.getButtonGroupMenuProvider();
    }

    public void setItemHeight()
    {

    }
    public void setMinItems()
    {

    }
    public void setMaxItems()
    {

    }

    public void setClickFunction(int...i) {
        mClickMapFunctions = i;
    }
    public int getArgOpViewIndex() {
        return mArgOpViewIndex;
    }

    public void setArgOpViewIndex(int argOpViewIndex) {
        mArgOpViewIndex = argOpViewIndex;
    }



    public int getArgContextMenuOpViewIndex() {
        return mArgContextMenuOpViewIndex;
    }

    public void setArgContextMenuOpViewIndex(int argContextMenuOpViewIndex) {
        mArgContextMenuOpViewIndex = argContextMenuOpViewIndex;
    }

    public int getArgButtonGroupOpViewIndex() {
        return mArgButtonGroupOpViewIndex;
    }

    public void setArgButtonGroupOpViewIndex(int argButtonGroupOpViewIndex) {
        mArgButtonGroupOpViewIndex = argButtonGroupOpViewIndex;
    }

    public View getArgContextMenuOpView() {
        return mArgContextMenuOpView;
    }

    public View getArgButtonGroupOpView() {
        return mArgButtonGroupOpView;
    }

    public void setArgButtonGroupOpView(View argButtonGroupOpView) {
        mArgButtonGroupOpView = argButtonGroupOpView;
    }

    public void setArgOpView(View argOpView) {
        mArgOpView = argOpView;
    }


    public V getAdapterView()
    {
        return getDecroView();
    }

}
