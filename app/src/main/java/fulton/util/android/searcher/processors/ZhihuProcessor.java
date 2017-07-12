package fulton.util.android.searcher.processors;

import java.util.ArrayList;
import java.util.HashMap;

import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import fulton.util.android.searcher.ContentProcessor;

public class ZhihuProcessor implements ContentProcessor {

	@Override
	public ArrayList<HashMap<String, String>> process(Document doc) {
		// TODO Auto-generated method stub
		ArrayList<HashMap<String,String>> res=new ArrayList<HashMap<String,String>>();
		HashMap<String,String> one=null;
		
		Elements base=doc.select(".contents");
		Elements title=base.select(".title").select("a");
		Elements href=base.select(".title").select("a");
		Elements brief=base.select(".summary");
		String temp;
		
		for(int i=0;i!=title.size();i++)
		{
			one=new HashMap<String,String>();
			one.put("title",title.get(i).text());
			temp=href.get(i).attr("href");
			if(!temp.startsWith("http"))
			{
				temp=getDomain()+temp;
			}
			one.put("url",temp);
			one.put("brief",brief.get(i).text());
			res.add(one);
		}
		
		return res;
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "zhihu";
	}

	@Override
	public String getBaseUrl() {
		// TODO Auto-generated method stub
		return "http://www.zhihu.com/search";
	}
	
	public String getDomain(){
		
		return "http://www.zhihu.com";
	}

	@Override
	public String getParameterFormater() {
		// TODO Auto-generated method stub
		return "type=content&q=%s";
	}

}
