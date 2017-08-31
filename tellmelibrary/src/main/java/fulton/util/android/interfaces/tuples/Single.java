package fulton.util.android.interfaces.tuples;

import fulton.util.android.notations.IsAFactory;
import fulton.util.android.utils.Util;

/**
 * Created by 13774 on 8/24/2017.
 */

public class Single<A> {

    public A first;

    public Single()
    {}

    public Single(A first) {
        this.first = first;
    }

    @IsAFactory
    public static <S extends  Single>  S getNewInstance(Class<S> clz)
    {
        if(clz==Single.class)
            return (S) new Single<>();
        else if(clz==Pair.class)
            return (S) new Pair<>();
        else if(clz==Triple.class)
            return (S)new Triple<>();
        else if(clz==Quad.class)
            return (S)new Quad<>();
        else if(clz==Penta.class)
            return (S)new Penta<>();
        else
            throw new UnsupportedClassVersionError(clz +" unsupported");
    }

    public static <S extends Single> S[] makeArray(int dim,int length)
    {
        if(dim==1)
            return (S[]) new Single[length];
        else if(dim==2)
            return (S[]) new Pair[length];
        else if(dim==3)
            return (S[]) new Triple[length];
        else if(dim==4)
            return (S[]) new Quad[length];
        else if(dim==4)
            return (S[]) new Penta[length];
        else
            throw new UnsupportedClassVersionError("tuple of "+(dim)+" dimension is not supported");
    }

    @IsAFactory
    public static <S extends Single> S getNewInstance(int size)
    {
        if(size==1)
            return (S) new Single<>();
        else if(size==2)
            return (S) new Pair<>();
        else if(size==3)
            return (S) new Triple<>();
        else if(size==4)
            return (S) new Quad<>();
        else if(size==4)
            return (S) new Penta<>();
        else
            throw new UnsupportedClassVersionError("tuple of "+(size)+" dimension is not supported");
    }

    public static <S extends Single> S initInstance(Object...o)
    {
        S s=getNewInstance(o.length);
        fillFields(s,o);
        return s;
    }
    public static <S extends Single> void fillFields(S s,Object...o)
    {
        for(int i=0;i<o.length;i++)
            set(s,i,o[i]);
    }

    @IsAFactory
    public static <S extends Single,E> E get(S s,int index,Class<E> clz)
    {
        if(index==0)
            return (E) s.first;
        else if(index==1)
            return (E) ((Pair)s).second;
        else if(index==2)
            return (E) ((Triple)s).third;
        else if(index==3)
            return (E) ((Quad)s).fourth;
        else if(index==4)
            return (E) ((Penta)s).fiveth;
        else
            throw new UnsupportedClassVersionError("tuple of "+(index+1)+" dimension is not supported");
    }


    @IsAFactory
    public static <S extends Single,E> void set(S s,int index,E e)
    {
        if(index==0)
            s.first=e;
        else if(index==1)
            ((Pair)s).second=e;
        else if(index==2)
            ((Triple)s).third=e;
        else if(index==3)
            ((Quad)s).fourth=e;
        else if(index==4)
            ((Penta)s).fiveth=e;
        else
            throw new UnsupportedClassVersionError("tuple of "+(index+1)+" dimension is not supported");
    }

    @IsAFactory
    public static <S extends  Single> int size(S s)
    {
        Class clz=s.getClass();
        if(clz==Single.class)
            return 1;
        else if(clz==Pair.class)
            return 2;
        else if(clz==Triple.class)
            return 3;
        else if(clz==Quad.class)
            return 4;
        else  if(clz==Penta.class)
            return 5;
        else
            throw new UnsupportedClassVersionError(clz +" unsupported");
    }

    public static <S extends Single,E> E firstOf(S s,Class<E> clz)
    {
        return (E)s.first;
    }
    @IsAFactory
    public static <S extends Single,E extends Single> E restOf(S s)
    {
        int sizes=size(s);
        if(sizes==1)
            throw new UnsupportedOperationException("get restOf(#size=1) is not supported,use firstOf instead");
        E e=getNewInstance(sizes-1);
        for(int i=1;i<sizes;i++)
            set(e,i-1,get(s,i,Object.class));
        return e;
    }

    public static int getMaxSizeSupported()
    {
        return 5;
    }

    /**
     *
     * @param data
     * @param <I>
     * @return
     */
    public static <I extends Single> I[] zip(Object[]...data)
    {
        if(data.length<2)
            throw new UnsupportedOperationException("Please do not kid me,for single or empty array,you do not need zip.");
        int maxLen=-1;
        for(int i=0;i<data.length;i++)
            if(maxLen<data[i].length)
                maxLen=data[i].length;
        I[] arr=makeArray(data.length,maxLen);
        for(int i=0;i<arr.length;i++)
        {
            arr[i]=getNewInstance(data.length);
            for(int j=0;j<data.length;j++) {
                set(arr[i], j, data[j].length<=i?null:data[j][i]);
            }
        }
        return arr;
    }
}
