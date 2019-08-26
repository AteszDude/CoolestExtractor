import java.util.ArrayList;

import edu.stanford.nlp.tagger.maxent.MaxentTagger;

/**
 * 
 */

/**
 * @author Attila Torda
 *We are mostly interested in adjectives (melléknév), these are: JJ, JJR, JJS
 *and in adverbs (határozószó): RB, RBR, RBS
 *http://www.computing.dcu.ie/~acahill/tagset.html
 */
class POSTagger {
	private ArrayList<String> adjectives;
	MaxentTagger tagger;
	private int tagset;//Tagger type: 0 - English (Penn Treebank), 1 - German (STTS)
	
	public POSTagger(String language) throws Exception {
		adjectives = new ArrayList<String>();
		
        // Initialize the tagger
		switch(language.toLowerCase())
		{
		case "german":
			tagger = new MaxentTagger(
                "C:\\Developer\\Stanford POS\\models\\german-fast.tagger");
			tagset = 1;
			break;
		default:
	        tagger = new MaxentTagger(
	                "C:\\Developer\\Stanford POS\\models\\english-left3words-distsim.tagger");
	        tagset = 0;
	        break;
		}
	}
	
	public ArrayList<String> getAdj(String sentence) {
        //TO DO rest
        
		String tagged = tagger.tagString(sentence);
		String[] words = tagged.split(" ");
		ArrayList<String> result = new ArrayList<String>();
		
		switch(tagset)
		{
		case 1: //German
			for(int i = 0; i < words.length; i++)
				if(words[i].contains("ADJA") || words[i].contains("ADJD") || words[i].contains("ADV"))
				{
					String[] splitadj = words[i].split("_");//Chop the unneeded end off
					result.add(splitadj[0].toLowerCase());
				}			
		default://English
		for(int i = 0; i < words.length; i++)
			if(words[i].contains("_JJ") || words[i].contains("_JJR") || words[i].contains("_JJS") ||
				words[i].contains("_RB") || words[i].contains("_RBR") || words[i].contains("_RBS"))
			{
				String[] splitadj = words[i].split("_");//Chop the unneeded end off
				result.add(splitadj[0].toLowerCase());
			}
		break;
		}
		//Test print
		System.out.println(result);
		
		return result;

	}
}
