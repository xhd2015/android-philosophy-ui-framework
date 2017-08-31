package fulton.shaw.android.tellme.experiment.sql;

import android.database.Cursor;

import java.util.ArrayList;

/**
 * Created by 13774 on 7/21/2017.
 */

public class ConnectedCursors {

    private  ArrayList<Cursor> mData;
    private  Cursor mCurCursor;
    private  int mCurPos;

    public ConnectedCursors()
    {
        mData=new ArrayList<>();
        mCurPos=-1;
        mCurCursor=null;
    }

    public void add(Cursor cursor)
    {
        mData.add(cursor);
    }

    public void close()
    {
        for(Cursor c:mData)
            c.close();
        mData.clear();
        mCurPos=-1;
        mCurCursor=null;
    }

    public int getCount()
    {
        int count=0;
        for(Cursor c:mData)
            count+=c.getCount();
        return count;
    }

    public boolean moveToNext()
    {
        if(mCurPos==-1)
        {
            if(mData.size()>0 && (mCurCursor=mData.get(0)).getCount()>0)return mCurCursor.moveToFirst();
        }
        if(!mCurCursor.moveToNext())
        {
            while(mCurPos < mData.size() && mData.get(mCurPos).getCount()==0)mCurPos++;
            if(mCurPos==mData.size())return false;
            mCurCursor=mData.get(mCurPos);
            return mCurCursor.moveToFirst();//must be true
        }else{
            return true;
        }
    }

    public boolean moveToPosition(int pos)
    {
        int tempCurPos=0;
        int tempCount=0;
        while(pos >= (tempCount=mData.get(tempCurPos).getCount()))
        {
            pos -= tempCount;
            tempCurPos++;
            if(tempCurPos>=mData.size())
            {
                return false;
            }
        }
        mCurPos=tempCurPos;
        mCurCursor=mData.get(mCurPos);
        return mCurCursor.moveToPosition(pos);
    }

    public boolean moveToFirst()
    {
        mCurPos=0;
        mCurCursor=mData.get(mCurPos);
        return mCurCursor.moveToFirst();
    }

    public Cursor currentCursor()
    {
        return mCurCursor;
    }




}
