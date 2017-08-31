package fulton.util.android.interfaces.tuples;

/**
 * Created by 13774 on 8/21/2017.
 */

public class Quad<A,B,C,D> extends Triple<A,B,C> {
    public D fourth;
    public Quad(A f, B s, C third,D fourth) {
        super(f, s, third);
        this.fourth=fourth;
    }

    public Quad() {
    }
}
