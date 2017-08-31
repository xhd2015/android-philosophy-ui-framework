package fulton.shaw.android.x.views;

import fulton.util.android.notations.MayConsumeTimePleaseUtilize;

/**
 * Created by 13774 on 8/10/2017.
 */

public abstract class SwitchUtil<E> {

    public E[] mList;
    public Object mShareVariable;

    public SwitchUtil(E[] list)
    {
        mList=list;
        init();
    }
    public void init(){}
    public SwitchUtil<E> setElementList(E[] list) {
        mList = list;
        return this;
    }

    @MayConsumeTimePleaseUtilize("the if..else if...else will continue until on failed.So it is very slow.")
    public SwitchUtil<E> switchElement(E e)
    {
        if(mList.length > 0 && mList[0].equals(e))
            switchOnElement0(e);
        else if(mList.length>1 && mList[1].equals(e))
            switchOnElement1(e);
        else if(mList.length>2 && mList[2].equals(e))
            switchOnElement2(e);
        else if(mList.length>3 && mList[3].equals(e))
            switchOnElement3(e);
        else if(mList.length>4 && mList[4].equals(e))
            switchOnElement4(e);
        else if(mList.length>5 && mList[5].equals(e))
            switchOnElement5(e);
        else if(mList.length>6 && mList[6].equals(e))
            switchOnElement6(e);
        else if(mList.length>7 && mList[7].equals(e))
            switchOnElement7(e);
        else if(mList.length>8 && mList[8].equals(e))
            switchOnElement8(e);
        else if(mList.length>9 && mList[9].equals(e))
            switchOnElement9(e);
        else if(mList.length>10 && mList[10].equals(e))
            switchOnElement10(e);
        else if(mList.length>11 && mList[11].equals(e))
            switchOnElement11(e);
        else if(mList.length>12 && mList[12].equals(e))
            switchOnElement12(e);
        return this;
    }
    public abstract void switchOnElement0(E e);
    public abstract void switchOnElement1(E e);
    public abstract void switchOnElement2(E e);
    public  void switchOnElement3(E e){}
    public  void switchOnElement4(E e){}
    public  void switchOnElement5(E e){}
    public  void switchOnElement6(E e){}
    public  void switchOnElement7(E e){}
    public  void switchOnElement8(E e){}
    public  void switchOnElement9(E e){}
    public  void switchOnElement10(E e){}
    public  void switchOnElement11(E e){}
    public  void switchOnElement12(E e){}


}
