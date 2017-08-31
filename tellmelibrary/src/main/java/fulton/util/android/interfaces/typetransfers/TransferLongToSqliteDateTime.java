package fulton.util.android.interfaces.typetransfers;

import java.util.Calendar;

/**
 * Created by 13774 on 8/21/2017.
 */

public class TransferLongToSqliteDateTime extends TransferDoubleChained<Long,Calendar,String> {
    public TransferLongToSqliteDateTime() {
        super(new TransferLongToCalendar(), new TransferCalendarToSqliteDateTime());
    }
}
