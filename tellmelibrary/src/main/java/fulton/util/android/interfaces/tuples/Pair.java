package fulton.util.android.interfaces.tuples;

/**
 * Created by 13774 on 8/20/2017.
 */

public class Pair<A, B> extends Single<A> {
    public B second;

    public Pair(){}
    public Pair(A f, B s)
    {
        super(f);
        second=s;
    }

}
