package fulton.shaw.android.x.viewgetter.database_auto;

/**
 * Created by 13774 on 8/13/2017.
 */

public class Tuple_5<E1,E2,E3,E4,E5> extends Tuple_4<E1,E2,E3,E4> {
    public E5 getE5() {
        return e5;
    }

    public void setE5(E5 e5) {
        this.e5 = e5;
    }

    public E5 e5;

    public Tuple_5(E1 e1, E2 e2, E3 e3, E4 e4,E5 e5) {
        super(e1,e2,e3,e4);
        this.e5=e5;
    }
}
