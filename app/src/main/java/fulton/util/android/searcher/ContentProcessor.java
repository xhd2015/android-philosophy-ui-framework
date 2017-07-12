package fulton.util.android.searcher;

import java.util.ArrayList;
import java.util.HashMap;

import org.jsoup.nodes.Document;


public interface ContentProcessor {
	public ArrayList<HashMap<String, String>> process(Document doc);
	public String getName();
	public String getBaseUrl();
	public String getDomain(); //when no prefix specified, this is used
	public String getParameterFormater();
}
