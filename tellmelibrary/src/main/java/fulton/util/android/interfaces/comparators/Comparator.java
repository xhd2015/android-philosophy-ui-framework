package fulton.util.android.interfaces.comparators;

import android.view.View;

import fulton.util.android.interfaces.ViewInfo;
import fulton.util.android.notations.LazyGetter;

/**
 * Created by 13774 on 8/22/2017.
 */

public abstract class Comparator<E1,E2> {
    /**
     * used with string & etc.
     */
    private static Comparator sEqualsComparator=null;
    private static Comparator<String,String> sStringComparator=null;
    private static Comparator<ViewInfo,String> sViewInfoKeyComparator=null;
    private static Comparator<View,View> sViewComparator=null;

    public static class CannotComputeDifference extends Error{}


    public abstract boolean equalsWith(E1 e1,E2 e2);
    public int computeDiff(E1 e1,E2 e2) throws CannotComputeDifference{
     throw new CannotComputeDifference();
    }


    @LazyGetter
    public static Comparator getEqualsComparator()
    {
        if(sEqualsComparator==null)
            sEqualsComparator=new Comparator() {
                @Override
                public boolean equalsWith(Object o, Object o2) {
                    return o==null?o==o2:o.equals(o2);
                }
            };
        return sEqualsComparator;
    }

    /**
     * this is a module
     */
//    @LazyGetter
//    public static Comparator getByGetter()
//    {
//        int x
//        if(x==null)
//            x=new Comparator() {
//                @Override
//                public boolean equalsWith(Object o, Object o2) {
//                    return o==null?o==o2:o.equals(o2);
//                }
//            };
//        return x;
//    }


        @LazyGetter
    public static Comparator getViewInfoKeyComparator()
    {
        if(sViewInfoKeyComparator==null)
            sViewInfoKeyComparator=new Comparator<ViewInfo, String>() {
                @Override
                public boolean equalsWith(ViewInfo viewInfo, String s) {
                    return viewInfo.key.equals(s);
                }
            };
        return sViewInfoKeyComparator;
    }

        @LazyGetter
    public static Comparator getViewComparator()
    {
        if(sViewComparator==null)
            sViewComparator=new Comparator() {
                @Override
                public boolean equalsWith(Object o, Object o2) {
                    return o==o2;
                }
            };
        return sViewComparator;
    }



}
