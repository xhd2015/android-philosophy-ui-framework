package fulton.shaw.android.x.views.viewhelpers.functions;

import android.widget.AdapterView;

import java.util.ArrayList;

import fulton.shaw.android.x.views.viewhelpers.DatabaseAdapterView;

/**
 * Created by 13774 on 8/18/2017.
 */

public class FunctionCommitAdderViewToDatabase<V extends AdapterView> extends AdapterViewFunction<V> {
    private FunctionUpdateAdderViewToDatabase<V> sUpdater;
    private FunctionAddAdderViewToDatabase<V> sAdder;
    private ArrayList mClear;
    private String[] mUpdateKeys;
    public FunctionCommitAdderViewToDatabase(ArrayList clear)
    {
        this(clear,(String[])null);//the same with clear
    }
    public FunctionCommitAdderViewToDatabase(ArrayList clear,String... updateKeys)
    {
        mClear=clear;
        mUpdateKeys=updateKeys;
    }
    @Override
    public void apply(DatabaseAdapterView<V> wrapper, int position) {
        if(position<0)//add
        {
            getAdder().apply(wrapper,position);
        }else{
            getUpdater().apply(wrapper,position);
        }
    }

    private FunctionUpdateAdderViewToDatabase<V> getUpdater()
    {
        if(sUpdater==null)
            sUpdater=new FunctionUpdateAdderViewToDatabase<>(mClear,mUpdateKeys);
        return sUpdater;
    }
    private FunctionAddAdderViewToDatabase<V> getAdder()
    {
        if(sAdder==null)
            sAdder= new FunctionAddAdderViewToDatabase<>(mClear);
        return sAdder;
    }
}
