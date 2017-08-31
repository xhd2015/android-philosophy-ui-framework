package fulton.util.android.interfaces.tuples;

import fulton.util.android.interfaces.tuples.Pair;

/**
 * Created by 13774 on 8/21/2017.
 */

public class Triple<A,B,C> extends Pair<A,B> {
    public C third;

    public Triple(){}

    public Triple(A f, B s, C third) {
        super(f, s);
        this.third = third;
    }
}
