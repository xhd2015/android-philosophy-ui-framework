package fulton.util.android.interfaces.multiview_valuegetter;

import android.content.Context;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import java.util.ArrayList;

import fulton.shaw.android.tellmelibrary.R;
import fulton.util.android.interfaces.tuples.Single;

/**
 * Created by 13774 on 8/25/2017.
 */

public class ViewValueGetterOfSimpleAdapterView extends ViewValueGetterOfFixed<Single<? extends AdapterView>,ArrayList<String>,
        MultiViewValueGetter<Single<? extends AdapterView>,ArrayList<String>>>{
    private static ArrayList<String> sTypeIndicator=null;
    private ArrayAdapter<String> mAdapter;
    private ArrayList<String>   mData;

    public ViewValueGetterOfSimpleAdapterView(Context context,AdapterView view,@LayoutRes int layoutRes,@IdRes int id,ArrayList<String> data)
    {
        super(new Single<AdapterView>(view),null);
        mAdapter=new ArrayAdapter<String>(context,layoutRes, id,mData=data);
        view.setAdapter(mAdapter);
    }

    @Override
    public Class<ArrayList<String>> getValueType() {
        if(sTypeIndicator==null)
            sTypeIndicator=new ArrayList<String>();
        return (Class<ArrayList<String>>) sTypeIndicator.getClass();
    }

    @Override
    public void setValue(ArrayList<String> value) {
        if(mData!=value)
        {
            mData.clear();
            mData.addAll(value);
            mAdapter.notifyDataSetChanged();
        }

    }

    @Override
    public ArrayList<String> getValue() {
        return mData;
    }

    public void notifyDatasetChanged()
    {
        mAdapter.notifyDataSetChanged();
    }

    public ArrayAdapter<String> getAdapter() {
        return mAdapter;
    }
}
