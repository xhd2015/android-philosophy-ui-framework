package fulton.shaw.android.x.viewgetter.database_auto;

/**
 * Created by 13774 on 8/13/2017.
 */

public class Tuple_3<E1,E2,E3> extends Tuple_2<E1,E2> {
    public E3 getE3() {
        return e3;
    }

    public void setE3(E3 e3) {
        this.e3 = e3;
    }

    public E3 e3;

    public Tuple_3(E1 e1, E2 e2,E3 e3) {
        super(e1, e2);
        this.e3=e3;
    }
}
