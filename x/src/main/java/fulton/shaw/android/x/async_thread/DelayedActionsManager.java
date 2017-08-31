package fulton.shaw.android.x.async_thread;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.HashMap;

import fulton.util.android.interfaces.tuples.Pair;
import fulton.util.android.interfaces.ValueGetter;
import fulton.util.android.utils.DataUtils;

/**
 * Created by 13774 on 8/20/2017.
 */

public class DelayedActionsManager {
    public static class ReturnException extends Error{
        private Object mData;
        public ReturnException(){}
        public ReturnException(Object data)
        {
            mData=data;
        }

        public Object getData() {
            return mData;
        }

        public void setData(Object data) {
            mData = data;
        }
    } //this causes the process to return.

    private HashMap<String,Pair<ArrayList<Runnable>,Object>> mActions;
    public DelayedActionsManager()
    {
        mActions=new HashMap<>();
    }

    private @NonNull Pair<ArrayList<Runnable>, Object> getRunnableList(String key)
    {
        return DataUtils.getOrPutDefaultValueMap(mActions, key, new ValueGetter<String,Pair<ArrayList<Runnable>,Object>>() {
            @Override
            public Pair<ArrayList<Runnable>, Object> getValue(String s) {
                return new Pair<>(new ArrayList<Runnable>(),null);
            }
        });
    }

    public void run(String key)throws ReturnException
    {
        for(Runnable r: getRunnableList(key).first)
        {
            r.run();
        }
    }

    public void add(String key,Runnable r)
    {
        getRunnableList(key).first.add(r);
    }

    public void setArgument(String key,Object argument)
    {
        getRunnableList(key).second=argument;
    }
    public Object getArgument(String key)
    {
        return getRunnableList(key).second;
    }


}
