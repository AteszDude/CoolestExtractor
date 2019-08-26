import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;


/**
 * Source: http://sentiwordnet.isti.cnr.it
 */
class Senti {
	private String pathToSWN = "SentiWordNet_3.0.0.txt";
	private HashMap<String, ArrayList<Rating>> dict;

	public Senti(){

		dict = new HashMap<String,  ArrayList<Rating>>();

		try{
			BufferedReader csv =  new BufferedReader(new FileReader(pathToSWN));
			String line = "";			
			while((line = csv.readLine()) != null)
			{//Do for each line
				String[] data = line.split("\t");//Split by using tab
				if(data.length >= 5) {//Process only correct line
				Rating score = new Rating(Double.parseDouble(data[2]), Double.parseDouble(data[3]));//Pos/neg
				
				String[] words = data[4].split(" ");//The synonyms
				
				//if(data[0].startsWith("a"))//We are only interested in adjectives - gives different result!
				for(String w:words)
				{
				String[] wact = w.split("#");
				String act = wact[0];//Chop the unneeded end of the word

				if(dict.containsKey(act))
				{//If it contains it already add to it
					 ArrayList<Rating> oldscore = dict.get(act);
					 oldscore.add(score);
					
				}
				else
				{//Insert a new value
					ArrayList<Rating> ratinglist = new ArrayList<Rating>();
					ratinglist.add(score);
				dict.put(act, ratinglist);
				}
				}//for
				}//if
			}//while
			csv.close();
			System.out.println("Dictionary size: " + dict.size());
		}//try
		catch(Exception e){e.printStackTrace();}		
	}

	public Extractor.FRating extractMultiple(ArrayList<String> adjectives)
	{
		int rating = 0;
		
		Iterator<String> it = adjectives.iterator();
        while(it.hasNext())
        {
        	Extractor.FRating erating = getRating(it.next());
        	if(erating == Extractor.FRating.POSITIVE)
        		rating++;
        	else if(erating == Extractor.FRating.NEGATIVE)
        		rating--;
        }
        if(rating > 0) return Extractor.FRating.POSITIVE;
        else if (rating < 0) return Extractor.FRating.NEGATIVE;
        
        return Extractor.FRating.NEUTRAL;
	}

	public Extractor.FRating extractMultiple2(ArrayList<String> adjectives)
	{
		double rating = 0;
		
		Iterator<String> it = adjectives.iterator();
        while(it.hasNext())
        {
        	ArrayList<Rating> ratinglist = getRatingArray(it.next());
        	if(ratinglist != null)
        	{
        	Iterator<Rating> it2 = ratinglist.iterator();
        	while(it2.hasNext())
        	{
        		Rating r = it2.next();
        		rating += r.positive - r.negative;
        	}
        	}
        }
        if(rating > 0.5) return Extractor.FRating.POSITIVE;
        else if (rating < -0.5) return Extractor.FRating.NEGATIVE;
        else if(rating > 0.2) return Extractor.FRating.MILDPOS;
        else if(rating < -0.2) return Extractor.FRating.MILDNEG;
        
        return Extractor.FRating.NEUTRAL;
	}	
	
	
	public ArrayList<Rating> getRatingArray(String adj)
	{
				
        return dict.get(adj);
	}
	
	public Extractor.FRating getRating(String adj)
	{
		ArrayList<Rating> ratings = getRatingArray(adj);
		double negative = 0, positive = 0, neutral = 0;
		
		if(ratings == null) return Extractor.FRating.NEUTRAL;
				
		Iterator<Rating> it = ratings.iterator();
		while(it.hasNext())
		{
			Rating r = it.next();
			positive += r.positive;
			negative += r.negative;
			neutral += 1 - (r.positive + r.negative);
		}
				
		if(positive > negative && positive > neutral)
			return Extractor.FRating.POSITIVE;
		else if(negative > positive && negative > neutral)
			return Extractor.FRating.NEGATIVE;
		
		return Extractor.FRating.NEUTRAL;
	}
}