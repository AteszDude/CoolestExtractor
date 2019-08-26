import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;

/**
 * @author Attila Torda
 *
 */
class Visualiser {
	
	FileInputStream fin;
	FileOutputStream fout;		
	String template;
	
	Visualiser() throws Exception {
	template = new String();
		
	fin = new FileInputStream ("template.html");
    // Read a line of text
	 BufferedReader br
     = new BufferedReader(new InputStreamReader(fin));

	String act;
	while((act = br.readLine()) != null)
	 template = template.concat(act + "\n");
	 
    // Close our input stream
    fin.close();
	}
	/**
	 * 
	 * @param positive: sum of positive comments
	 * @param negative: same
	 * @param neutral: same
	 */
	void VisOpinion(int positive, int negative, int neutral) throws Exception {
	template = template.replaceFirst("positive = 1;", "positive = " + positive + ";");
	template = template.replaceFirst("negative = 1;", "negative = " + negative + ";");
	template = template.replaceFirst("neutral = 1;", "neutral = " + neutral + ";");
		
    // Open an output stream
    fout = new FileOutputStream ("result.html");

    // Print a line of text
    new PrintStream(fout).println (template);

    // Close our output stream
    fout.close();		

	}
	
	void finish() {
		//Prints the results in a file and closes the output stream
		
	}
	
	void VisOpinion(CommentStats o) throws Exception {
		VisOpinion(o.positive, o.negative, o.neutral);
	}

}
