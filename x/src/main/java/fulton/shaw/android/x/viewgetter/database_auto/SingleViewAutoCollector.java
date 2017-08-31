package fulton.shaw.android.x.viewgetter.database_auto;

import android.view.View;

import java.util.ArrayList;

import fulton.util.android.interfaces.ViewInfo;
import fulton.util.android.interfaces.valueproviders.ValueProvider;

/**
 * Created by 13774 on 8/14/2017.
 */

public class SingleViewAutoCollector extends ShareableSingleViewAutoCollector{

    //===user given
    protected View mRootView;



    public SingleViewAutoCollector(View rootView, ArrayList<ViewInfo> infoList, ArrayList<ViewGetter> viewGetters)
    {
        super(infoList,viewGetters);
        mRootView=rootView;
    }
    public SingleViewAutoCollector(View rootView, ArrayList<ViewInfo> infoList)
    {
        super(infoList);
        mRootView=rootView;
    }



    public <E> void fillValue(int i,E value)
    {
        super.fillValue(mRootView,i,value);
    }

    public <E> void fillValueBasedOnKey(Object... args) {
        super.fillValueBasedOnKey(mRootView, args);
    }

    public void fillValue(Object[] values)
    {
        for(int i=0;i<values.length;i++)
            if(checkProcessNeeded(i))
                fillValue(i,values[i]);
    }

    /**
     *   you cannot collect null value,then you cannot set null value.
     * @param list
     */
    public void fillValue(ArrayList list)
    {
        for(int i=0;i<list.size();i++)
            if(checkProcessNeeded(i))
                fillValue(i,list.get(i));
    }


    /**
     *
     * @param eClass
     * @param <E>
     * @return  the type you wanted
     */
    public <E> E collectValue(Class<E> eClass) {
        return super.collectValue(mRootView, eClass);
    }

    public void fillPresetValue() {
        super.fillPresetValue(mRootView);
    }

    public <V extends View> V findViewByKey(String key, Class<V> vClz) {
        return super.findViewByKey(mRootView, key, vClz);
    }
    public View getRootView() {
        return mRootView;
    }

    public void fillValue(ValueProvider<String,?> provider) {
        super.fillValue(mRootView, provider);
    }
}
