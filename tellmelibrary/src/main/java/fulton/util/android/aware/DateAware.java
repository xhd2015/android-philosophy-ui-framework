package fulton.util.android.aware;

/**
 * Created by 13774 on 7/22/2017.
 */

public interface DateAware {
    void setYear(int year);
    void setMonth(int month);
    void setDate(int date);
    int getYear();
    int getMonth();
    int getDate();

}
