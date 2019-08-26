import java.util.*;
import java.io.*;

import com.memetix.mst.language.Language;
import com.memetix.mst.translate.Translate;

/**
 * @author Attila Torda
 *
 */
public class Translator {

	private HashMap<String, String> vocabulary;
	private final String path;
	private Language language;
	
	public Translator(String language) {
		if(language.toLowerCase().equals("german")) {
			this.language = Language.GERMAN;
			path = "data\\german.txt";
		}
		else if(language.toLowerCase().equals("french")) {
			this.language = Language.FRENCH;
			path = "data\\french.txt";
		}
		else path = null;
		
		//Construct a map from our pre stored dictionary file
		vocabulary = new HashMap<String, String>();
		try{
			BufferedReader csv =  new BufferedReader(new FileReader(path));
			String line = "";			
			while((line = csv.readLine()) != null)
				{//Do for each line
				String[] data = line.split("#");//Left side: source language, right side: english
				if(data.length == 2)
				  vocabulary.put(data[0], data[1]);
				}
			}//try
			catch(Exception e){e.printStackTrace();}		
	}
	
	public String toEnglish(String text) throws Exception {
		//Lookup the word in our text file if not found connect to Microsoft Translator and store it for later use
		
		String translatedText = vocabulary.get(text);
		if(translatedText != null) return translatedText;
		
	    Translate.setClientId("20b7ba37-801b-43d0-a84e-509d879e9326");
	    Translate.setClientSecret("qU3wTOjiMwde7E3Pj4qbE2GsBdob4hSciyDsPkoZa+g");
	    translatedText = Translate.execute(text, language, Language.ENGLISH);

	    System.out.println(translatedText);
	    
	    vocabulary.put(text, translatedText);
	    
	    return translatedText;
	}
	
	public void updateDict() throws Exception {
	//Delete the old file
	File file = new File(path);
	file.delete();
	file = null;

	//Write all the contents of the HashMap to the output file
	FileWriter fstream = new FileWriter(path);
	BufferedWriter out = new BufferedWriter(fstream);

	Iterator<Map.Entry<String, String>> it = vocabulary.entrySet().iterator();
	while (it.hasNext()) {
        Map.Entry<String, String> pairs = (Map.Entry<String, String>)it.next();
        out.write(pairs.getKey() + "#" + pairs.getValue() + "\n");
        it.remove(); // avoids a ConcurrentModificationException
    }
	out.close();
	}
	
}
