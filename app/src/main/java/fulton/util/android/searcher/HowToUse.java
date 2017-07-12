package fulton.util.android.searcher;

import java.util.ArrayList;
import java.util.HashMap;

public class HowToUse {
	public static void main(String...args)
	{	
		String word="doyou";
		String type="yahoo";
		
		ArrayList<HashMap<String,String>> res=ContentManager.searchFor(word, type);
		
		System.out.println("======res is");
		System.out.println(res.toString());
			
		System.out.println("END");
	}
}
