/**
 * 
 */

import cc.mallet.classify.Classifier;

import java.util.*;
import java.io.*;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

/**
 * 
 * @author Attila Torda
 * A rating of one word (most commonly adjective) used by Senti
 */
class Rating {
	public double positive;
	public double negative;
	Rating() {}
	Rating(double positive, double negative) {
		this.positive = positive;
		this.negative = negative;
	}
	@Override
	public boolean equals(Object other) {
		if(this == other) return true;
		if(!(other instanceof Rating)) return false;
		Rating rother = (Rating) other;
		if(rother.positive == positive && rother.negative == negative) return true;
		return false;
	}
}

/**
 * A comment and it's properties
 */
class Comment {
	public String text;//What really makes a comment
	public ArrayList<String> adjectives;//List of adjectives and adverbs
	public Extractor.FRating rating;//Rating assigned by the application
	public Extractor.FRating corRating;//Rating in the Excel file
	public String topic;//Topic associated with this comment 
	public int topicNum;
	public int corTopic;//Topic in the Excel file
}
/**
 * 
 * @author Attila Torda
 * A structure aggregating the final score.
 */
class CommentStats {
	public int positive;
	public int negative;
	public int neutral;
	public int mildpos;
	public int mildneg;
	
	//Correct score, if known
	public int corPos;//Number of correct positives
	public int corNeg;
	public int corNeu;
	
	public int accOP;// corPos / positive + ...
	public int accT;//Accuracy of topic assignment
	
	CommentStats () {}
	CommentStats(int positive, int negative, int neutral) {
		this.positive = positive;
		this.negative = negative;
		this.neutral = neutral;
	}
	CommentStats(int positive, int negative, int neutral, int mildpos, int mildneg) {
		this.positive = positive;
		this.negative = negative;
		this.neutral = neutral;
		this.mildpos = mildpos;
		this.mildneg = mildneg;
	}
}

/**
 * @author Attila Torda
 *Main class
 */
public class Extractor {

	private ArrayList<Comment> comments;
	public enum FRating {POSITIVE, NEGATIVE, NEUTRAL, MILDPOS, MILDNEG};
	private CommentStats opinions;
	
	/**
	 * 
	 */
	
	public void loadExcel(String path) throws Exception {
		FileInputStream file = new FileInputStream(new File(path));
		//Get the workbook instance for XLS file 
		HSSFWorkbook workbook = new HSSFWorkbook(file);
		//Get first sheet from the workbook
		HSSFSheet sheet = workbook.getSheetAt(0);
		
		
		Iterator<Row> rowIterator = sheet.iterator();
		
		//Process top (header) element
		Row toprow = rowIterator.next();

		
		
		while(rowIterator.hasNext())
		{
			Row row = rowIterator.next();
			Iterator<Cell> cellIterator = row.cellIterator();
			int position = 0, positive = 0, negative = 0, neutral = 0, issue1 = 0, issue2 = 0;
			Comment comment = new Comment();
			
			while(cellIterator.hasNext())
			{
				Cell cell = cellIterator.next();
				switch (position)
				{
				case 3:
				{//Text field
					comment.text = cell.getStringCellValue();
					System.out.println(comment.text);//TEST!!!!
					break;
				}
				case 4:
				  positive = (int) cell.getNumericCellValue();
				  break;
				case 5:
				  neutral = (int) cell.getNumericCellValue();
				  break;
				case 6:
				  negative = (int) cell.getNumericCellValue();
				  break;
				case 7:
				  issue1 = (int) cell.getNumericCellValue();
				  break;
				case 8:
				  issue2 = (int) cell.getNumericCellValue();
				  break;
				
				default:
					break;
				}
				position++;
			}
			//Process data
			if(positive > negative && positive > neutral)
				comment.corRating = FRating.POSITIVE;
			else if(negative > neutral && negative > positive)
				comment.corRating = FRating.NEGATIVE;
			else
				comment.corRating = FRating.NEUTRAL;
			
			
			comments.add(comment);
		}
		
	}
	
	
	public Extractor(String inputfile, int numTopics, String language) throws Exception {
		// TODO Auto-generated constructor stub


		comments = new ArrayList<Comment>();
		opinions = new CommentStats();
		
		//Translate words to English TEST
		//Translator translator = new Translator("German");
		//translator.toEnglish("langweilig");
		//translator.updateDict();
		
		//Step 1: convert into text files
		
		if(inputfile.contains("txt"))
		{
		//Read txt files
		FileInputStream fin = new FileInputStream (inputfile);
	    // Read a line of text
		 BufferedReader br
	     = new BufferedReader(new InputStreamReader(fin));

		String act;
		while((act = br.readLine()) != null)
		{
		 Comment c = new Comment();
		 c.text = act;
		 comments.add(c);
		}
		 
	    // Close our input stream
	    fin.close();
		}//end txt
		else if(inputfile.contains("xls"))
			loadExcel(inputfile);
		
		if(language.toLowerCase().startsWith("ge"))
		{//German language
			for(int i = 0; i < comments.size(); i++)
			{
				Comment c = comments.get(i);
				//TO DO
			}
		}
		
		//Step 2: Extract topics
		TopicExtract te = new TopicExtract(inputfile, numTopics);//Works but I don't know what it does
		te = null;//Free memory
		
		
		//Step 3: tag adjectives and adverbs
		POSTagger pt = new POSTagger(language);//100% working
		for(int i = 0; i < comments.size(); i++)
		  comments.get(i).adjectives = pt.getAdj(comments.get(i).text);
		pt = null;
		
		System.out.println("");
		
		
		//Step 4: extract opinions
		
		if(language.equals("English")){
		Senti senti = new Senti();//Working
		for(int i = 0; i < comments.size(); i++)
		{
			FRating result = senti.extractMultiple2(comments.get(i).adjectives);
			comments.get(i).rating = result;
			if(result == FRating.POSITIVE || result == FRating.MILDPOS)
				{
				opinions.positive++;
				System.out.print("+");
				}
			else if(result == FRating.NEGATIVE || result == FRating.MILDNEG)
				{
				opinions.negative++;
				System.out.print("-");
				}
			else {opinions.neutral++;
				System.out.print("0");
				}
		}
		senti = null;
		}//end of English language
		else if(language.equals("German"))
	    {
	    	GermanDic gd = new GermanDic();
			for(int i = 0; i < comments.size(); i++)
			{
				FRating result = gd.getMultipleWords(comments.get(i).adjectives);
				comments.get(i).rating = result;
				if(result == FRating.POSITIVE || result == FRating.MILDPOS)
					{
					opinions.positive++;
					System.out.print("+");
					}
				else if(result == FRating.NEGATIVE || result == FRating.MILDNEG)
					{
					opinions.negative++;
					System.out.print("-");
					}
				else {opinions.neutral++;
					System.out.print("0");
					}
			}
	    }//end of German language
		
		//Step 5: Visualise results
		
		Visualiser vis = new Visualiser();//100% working
		vis.VisOpinion(opinions);
		vis = null;
	}


	
	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		String language = new String("English");
		
		if(args.length < 2 || args.length > 3)
			System.out.println("Please supply at least 2 arguments: inputfile, numTopic," +
					"(language) eg: simpletest01.txt 2");
		else if(args.length == 3)
			language = new String(args[2]);
		//We already set the language to English so there is no need to further check for length 2
		
		String inputfile = args[0];
		int numTopics = Integer.parseInt(args[1]);
		
		
        //Call tagger
        new Extractor(inputfile, numTopics, language);
	}

}
