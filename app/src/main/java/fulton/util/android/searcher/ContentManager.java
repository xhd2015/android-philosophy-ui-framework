package fulton.util.android.searcher;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import android.util.Log;
import fulton.util.android.searcher.processors.AskubuntuProcessor;
import fulton.util.android.searcher.processors.BaiduZhidaoProcessor;
import fulton.util.android.searcher.processors.GuokrProcessor;
import fulton.util.android.searcher.processors.StackOverflowProcessor;
import fulton.util.android.searcher.processors.YahooProcessor;
import fulton.util.android.searcher.processors.ZhihuProcessor;

public class ContentManager {
	static String userAgent="Mozilla/5.0 (JSoup)";
	static String encoding="UTF8";
	public static HashMap<String, Class> mapper; 
	public static HashMap<String, ContentProcessor> pool;
	
	static{
		pool=new HashMap<String, ContentProcessor>();
		mapper=new HashMap<String, Class>();
		mapper.put("yahoo", YahooProcessor.class);
		mapper.put("stackoverflow", StackOverflowProcessor.class);
		mapper.put("baiduzhidao",BaiduZhidaoProcessor.class);
		mapper.put("askubuntu", AskubuntuProcessor.class);
		mapper.put("guokr", GuokrProcessor.class);
		mapper.put("zhihu", ZhihuProcessor.class);
	}
	public static ContentProcessor getProcessor(String type)
	{
		if(!pool.containsKey(type))
		{
			Class c=mapper.get(type);
			try {
				pool.put(type,(ContentProcessor) c.newInstance());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return null;
			}
		}
		return pool.get(type);
	}
	public static Document getDocument(String url)
	{
		try {
			
			return Jsoup.connect(url).userAgent(userAgent).get();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Log.d("*Exception*", e.toString());
			return null;
		}
	}
	public static String getUrl(String word,String type)
	{
		ContentProcessor p=getProcessor(type);
		String nword;
		try {
			nword = URLEncoder.encode(word,encoding);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			nword=word;
		}
		return p.getBaseUrl()+"?"+String.format(p.getParameterFormater(),nword);
	}
	public static void saveAsFile(String filename,String content)
	{
		try {
			BufferedWriter w=new BufferedWriter(new FileWriter(new File(filename)));
			w.write(content);
			w.flush();
			w.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	//--------------- These ARE NOT original (means they're based on the functions previously defined------------
	public static ArrayList<HashMap<String,String>> searchFor(String word,String type)
	{
		String url=ContentManager.getUrl(word, type);
		Log.v("SearchFragment", "URL="+url);
		Document doc=ContentManager.getDocument(url);

		ContentProcessor p=getProcessor(type);
		if(doc==null || p==null)
		{
			return new ArrayList<HashMap<String,String>>();
		}
		return p.process(doc);
	}
}
