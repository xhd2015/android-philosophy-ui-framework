package fulton.util.android.interfaces.tuples;

/**
 * Created by 13774 on 8/25/2017.
 */

public class Penta<A,B,C,D,E> extends Quad<A,B,C,D> {
    public E fiveth;

    public Penta(A f, B s, C third, D fourth, E fiveth) {
        super(f, s, third, fourth);
        this.fiveth = fiveth;
    }

    public Penta() {
    }

}
