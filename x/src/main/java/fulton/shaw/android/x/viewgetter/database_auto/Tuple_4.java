package fulton.shaw.android.x.viewgetter.database_auto;

/**
 * Created by 13774 on 8/13/2017.
 */

public class Tuple_4<E1,E2,E3,E4> extends Tuple_3<E1,E2,E3> {
    public E4 getE4() {
        return e4;
    }

    public void setE4(E4 e4) {
        this.e4 = e4;
    }

    public E4 e4;

    public Tuple_4(E1 e1, E2 e2, E3 e3,E4 e4) {
        super(e1,e2,e3);
        this.e4=e4;
    }
}
