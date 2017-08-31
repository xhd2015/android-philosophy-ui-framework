package fulton.shaw.android.x.fragments;

import fulton.util.android.utils.SqlUtil;

/**
 * Created by 13774 on 8/23/2017.
 */

public abstract class MainTaggedViewProcessor<E> {
    protected E mData;
    public static class UnsupportedOneObjectErrorBaase extends Error{
        private Object mThing;
        public UnsupportedOneObjectErrorBaase(){}
        public UnsupportedOneObjectErrorBaase(Object tag) {
            super("it is:"+(tag));
            mThing=tag;
        }
    }
    public static class UnsupportedTableError extends UnsupportedOneObjectErrorBaase{
        public UnsupportedTableError(Object tag) {
            super(tag);
        }
    }
    public static class UnsupportedMainTagError extends UnsupportedOneObjectErrorBaase{
        public UnsupportedMainTagError() {
            super();
        }

        public UnsupportedMainTagError(Object tag) {
            super(tag);
        }
    }


    public void processMainTag(String mainTag)
    {
        if(SqlUtil.TAG_SAY_SOMETHING.equals(mainTag))
        {
            prcoessSaySomething();
        }else if(SqlUtil.TAG_BLOG.equals(mainTag)){
            prcoessBlog();
        }else {
            throw new UnsupportedMainTagError(mainTag);
        }
    }

    public void prcoessSaySomething(){
        throw new UnsupportedMainTagError();
    }
    public void prcoessBlog(){
        throw new UnsupportedMainTagError();
    }

    protected abstract E onInitData();
    public E getData() {
        if(mData==null)
            mData=onInitData();
        return mData;
    }

    public void setData(E data) {
        mData = data;
    }

}
