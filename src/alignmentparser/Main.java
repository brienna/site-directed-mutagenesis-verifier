package alignmentparser;

import java.io.IOException;
import alignmentparser.json.JSONObject;

/**
 * Starts the Site-Directed Mutagenesis Verifier program.
 * 
 * @author Brienna
 *
 */
public class Main {
	public static void main(String[] args) throws IOException, InterruptedException {
		// Check that user specified mutations, quit application if false
		// NOTE: WILL NEED TO ADD CHECK FOR INCORRECTLY FORMATTED ARGUMENTS?
		if (args.length < 1) {
			System.out.println("Incorrect number of arguments. Please try again.");
			System.exit(0);
		}
		
		// Print program info
		System.out.println("Running Site-Directed Mutagenesis Verifier...");
		
		// Upload query sequence
		String query = Query.uploadQueryFile();
		System.out.println("Query is " + query);
		
		// Send search request and parse out request ID
		WebBlast blaster = new WebBlast(query);
		blaster.putRequest();
		System.out.println("Request ID is " + blaster.getRequestID());
		
		// Check and handle status of search request
		blaster.checkSearchStatus();
		
		// Retrieve and display results
		JSONObject alignment = blaster.getRequest();
		AlignmentAnalyzer.beginAnalysis(alignment, args);
	}	
}

/* 
 * NOTES:
 * https://biostars.org/p/83158/
 * https://ncbi.github.io/blast-cloud/dev/api.html
 * might be worth looking at alextblog.blogspot.cz/2012/05/ncbi-blast-jaxb-biojava-blasting-like.html
 * NOTE: LIKELY WILL NEED TO FIX THE WHILE LOOP TO ACCOUNT FOR ERRORS, SUCH AS 
 * ERROR 502 OR PERPETUAL "Status=WAITING" 
 * identical problem to latter: https://www.biostars.org/p/237886/
*/

