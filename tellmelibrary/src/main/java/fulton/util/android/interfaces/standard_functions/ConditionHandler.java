package fulton.util.android.interfaces.standard_functions;

/**
 * Created by 13774 on 8/25/2017.
 */

public abstract class ConditionHandler<F> extends StandardFunction<F,Void> {
    public ConditionHandler() {
        super(null,null,null,null);
    }

    @Override
    public void apply() {
        handle();
    }

    public abstract void handle();
}
