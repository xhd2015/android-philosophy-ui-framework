package fulton.util.android.interfaces.valueproviders;

import fulton.util.android.interfaces.ValueGetter;
import fulton.util.android.public_interfaces.BaseSqliteHelper;
import fulton.util.android.utils.SqlUtil;

/**
 *  how they are transfered to String[]?
 *  not my responsiblity.
 *
 *
 *    this describes a scheme:
 *          if db.table[A] is an array,then this adapter can be used.
 */
public class ValueProviderOfDbMap extends ValueProvider<String[],String[]> {

    private String mTable;
    private String mIdCol= SqlUtil.COL_ID;
    /**
     *  primary key, sub key,subsub key.
     */
    private String[] mDescendKeys;
    private String[] mCols;
    private Class[]  mColTypes;
    private BaseSqliteHelper mHelper;


    /**
     * always returns the cursor type.
     * @param k
     * @param valueClass
     * @param <S>
     * @return
     */
    @Override
    public <S> S getValue(String[] k, Class<S> valueClass) {
        //make condition keys=obj, and query.
        //the return type is a cursor.
        return null;
    }

    @Override
    public <S> S getOrDefaultValue(String[] k, @ValueGetter.ValueGetterType Object defaultValue, Class<S> valueClass) {
        return null;
    }

    @Override
    public <S> S getOrPutDefaultValue(String[] k, @ValueGetter.ValueGetterType Object defaultValue, Class<S> valueClass) {
        return null;
    }

    @Override
    public void putValue(String[] k, Object value) {

    }

    @Override
    public boolean remove(String[] k) {
        return false;
    }


    @Override
    public String[] getKeys() {
        return new String[0];
    }

    @Override
    public boolean contains(String[] k) {
        return false;
    }

}
