package fulton.util.android.interfaces.standard_functions;

import fulton.util.android.interfaces.tuples.Single;

/**
 *  this class ensures that calling its #apply method will never raises exception and error.
 * @param <I>
 * @param <O>
 */
public abstract class StandardFunction<I,O> {
    private ConditionHandler mOnExternalError;
    private ConditionHandler mOnInteralError;
    private ConditionHandler mOnSucceed;
    private ConditionHandler mOnFailed;
    private I mInput;
    private O mOutput;

    public StandardFunction(ConditionHandler<? extends StandardFunction<?,?>> onSucceed,
                            ConditionHandler<? extends StandardFunction<?,?>> onInteralError,
                            ConditionHandler<? extends StandardFunction<?,?>> onExternalError,
                            ConditionHandler<? extends StandardFunction<?,?>> onFailed) {
        mOnSucceed = onSucceed;
        mOnInteralError = onInteralError;
        mOnExternalError = onExternalError;
        mOnFailed = onFailed;
    }

    public abstract void apply();

    public I getInput() {
        return mInput;
    }

    public O getOutput() {
        return mOutput;
    }

    public void setInput(I input) {
        mInput = input;
    }

    /**
     *  this is used when input has type of {@link Single}
     * @param o
     */
    public void setInput(Object...o)
    {
        for(int i=0;i<o.length;i++)
            Single.set((Single)mInput,i,o[i]);
    }

    /**
     *  this is used when input has type of {@link Single}
     * @param i
     * @param o
     */
    public void setInput(int i,Object o)
    {
        Single.set((Single)mInput,i,o);
    }

    public void ensureInputAsSingle(int maxIndex)
    {
        if(mInput==null)
        {
            mInput=(I)Single.getNewInstance(maxIndex+1);
        }
    }

    public void setOutput(O output) {
        mOutput = output;
    }



    protected static <F extends StandardFunction<?,?>> void runOnCondtionHandler(F who, ConditionHandler handler)
    {
        if(handler!=null)
        {
            handler.setInput(who);
            handler.handle();
        }
    }

    public ConditionHandler<? extends StandardFunction<?,?>> getOnExternalError() {
        return mOnExternalError;
    }

    public ConditionHandler<? extends StandardFunction<?,?>> getOnInteralError() {
        return mOnInteralError;
    }

    public ConditionHandler<? extends StandardFunction<?,?>> getOnSucceed() {
        return mOnSucceed;
    }

    public ConditionHandler<? extends StandardFunction<?,?>> getOnFailed() {
        return mOnFailed;
    }
}
