package fulton.util.android.utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONStringer;

import java.util.ArrayList;

/**
 * Created by 13774 on 8/12/2017.
 */

public class JsonUtil {

    public static JSONArray toJsonArray(ArrayList list)
    {
        JSONArray array=new JSONArray();
        for(int i=0;i<list.size();i++)
            array.put(list.get(i));
        return  array;
    }

    public static ArrayList jsonArrayToArrayList(String jsonArray) {
        try {
            JSONArray array = new JSONArray(jsonArray);
            ArrayList arrayList = new ArrayList(array.length());
            for (int i = 0; i < array.length(); i++)
                arrayList.add(array.get(i));
            return arrayList;
        } catch (JSONException e) {
            return null;
        }
    }
}
