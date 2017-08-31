package fulton.shaw.android.x.viewgetter.database_auto;

import java.util.ArrayList;

import fulton.shaw.android.x.viewgetter.transferview.ShareableViewValueTransfer;
import fulton.util.android.interfaces.ViewInfo;
import fulton.util.android.interfaces.valueproviders.ShareableValueProvider;

/**
 * Created by 13774 on 8/22/2017.
 */

public class CompletorSingleViewShareable<E,KeyType,KeySetType> extends ShareableSingleViewAutoCollector {

    private ShareableValueProvider<E,KeyType,KeySetType> mValueProvider;

    public CompletorSingleViewShareable(ArrayList<ViewInfo> infoList, ArrayList<ViewGetter> viewGetters) {
        super(infoList, viewGetters);
    }

    public CompletorSingleViewShareable(ArrayList<ViewInfo> infoList, ArrayList<ViewGetter> viewGetters, ArrayList<ShareableViewValueTransfer> transfers) {
        super(infoList, viewGetters, transfers);
    }

    public CompletorSingleViewShareable(ArrayList<ViewInfo> infoList) {
        super(infoList);
    }









}
