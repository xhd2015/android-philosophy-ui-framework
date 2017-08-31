package fulton.shaw.android.x.viewgetter.database_auto;

/**
 * Created by 13774 on 8/13/2017.
 */

public class Tuple_2<E1,E2> extends Tuple_1<E1>{
    public E2 getE2() {
        return e2;
    }

    public void setE2(E2 e2) {
        this.e2 = e2;
    }

    public E2 e2;

    public Tuple_2(E1 e1,E2 e2) {
        super(e1);
        this.e2=e2;
    }

}
