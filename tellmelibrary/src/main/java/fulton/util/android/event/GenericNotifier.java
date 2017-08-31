package fulton.util.android.event;

import android.support.v4.util.Pair;

import java.util.LinkedList;

/**
 * Created by 13774 on 7/16/2017.
 */

public class GenericNotifier implements GenericNotifierInterface{


    private LinkedList<Pair<Integer,GenericListener>>[] mListenersList;

    /**
     * if this called,then initListeners must be later called.
     */
    public GenericNotifier()
    {}

    public GenericNotifier(int eventCount)
    {
        initListeners(eventCount);
    }

    protected void initListeners(int eventCount)
    {
        mListenersList = new LinkedList[eventCount];
        for(int i=0;i<eventCount;i++)
            mListenersList[i]=new LinkedList<>();
    }

    @Override
    public void setOnNotifyListener(int listenerId, int typeIndex, GenericListener listener)
    {
      mListenersList[typeIndex].add(new Pair<Integer, GenericListener>(listenerId,listener));
    }

    @Override
    public void notifyListeners(int typeIndex,Object...args)
    {
            for(Pair<Integer,GenericListener> listener:mListenersList[typeIndex])
                listener.second.applyListen(listener.first,typeIndex, args);
    }

    @Override
    public void notifyListener(int listenerId, int typeIndex, GenericListener listener, Object... args) {
        if(listener!=null)
         listener.applyListen(listenerId, typeIndex,args);
    }

    @Override
    public int getEventCount()
    {
        return mListenersList.length;
    }


}
