import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/**
 * 
 */

/**
 * @author Attila Torda
 *
 */
public class GermanDic {
	
	private String pathToDic = "data\\LIWC2001_German.dic";
	private HashMap<String, Boolean> dict;//True = Positive, False = Negative
	
	public GermanDic()
	{//Open file and create hashtable
		dict = new HashMap<String, Boolean>();
		try{
			BufferedReader csv =  new BufferedReader(new FileReader(pathToDic));
			String line = "";			
			while((line = csv.readLine()) != null)
			{
				String[] data = line.split("\t");//Split by tab
				if(data[0].endsWith("*"))
					data[0] = data[0].substring(0, data[0].length() - 1);
				String word = data[0];//First comes the word the others are the numbers of associations
				for(int i = 1; i < data.length; i++)
				{//13, 14 = positive, 16 = negative
					int value = Integer.parseInt(data[i]);
					if(value == 13 || value == 14) {
						dict.put(word, true);
						break; }
					else if(value == 16) {
						dict.put(word, false);
						break; }
				}
			}
		}//try
		catch(Exception e){e.printStackTrace();}
		System.out.println("Dictionary size: " + dict.size() +" words.");
	}
	
	public Extractor.FRating getWord(String word, int maxtrim)
	{
		if(!dict.containsKey(word))
		{
			//If no results then cut the end of the word
			if(maxtrim > 0 && word.length() > 1)
				return getWord(word.substring(0, word.length() - 1), maxtrim - 1);
		   return Extractor.FRating.NEUTRAL;
		}
		else if(dict.get(word).booleanValue())
			   return Extractor.FRating.POSITIVE;
		else
			   return Extractor.FRating.NEGATIVE;
	}
	public Extractor.FRating getMultipleWords(ArrayList<String> adjectives)
	{//Count all the positive and negative words and return the dominating one or neutral
		int pos = 0;
		int neg = 0;
		Iterator<String> it = adjectives.iterator();
        while(it.hasNext())
        {
        	String word = it.next();
        	Extractor.FRating result = getWord(word, 2);
    			if(result == Extractor.FRating.POSITIVE)
    				   pos++;
    			else if(result == Extractor.FRating.NEGATIVE)
    				   neg++;
        }
		if(pos > neg)
			return Extractor.FRating.POSITIVE;
		else if(neg > pos)
			return Extractor.FRating.NEGATIVE;
		return Extractor.FRating.NEUTRAL;
	}
	
}
