package alignmentparser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import alignmentparser.json.JSONObject;

/**
 * Starts the Site-Directed Mutagenesis Verifier program.
 * 
 * @author Brienna
 *
 */
public class Main {
	private static Scanner sc = new Scanner(System.in);

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
		System.out.println(query);
		
		// Send put request to BLAST engine to get request ID
		String rid = putBLAST(query);
		
		// Send get request to BLAST engine to get alignment file
		JSONObject alignment = getBLAST(rid);
		AlignmentAnalyzer.beginAnalysis(alignment, args);
	}
	
	private static JSONObject getBLAST(String rid) throws IOException, InterruptedException {
		String getBLAST = "https://www.ncbi.nlm.nih.gov/blast/Blast.cgi?RID="+rid+"&FORMAT_TYPE=JSON2_S&CMD=Get";
		System.out.println("Request processing, sleeping one minute...");
		Thread.sleep(60000);
		String[] connectionInfo = connectTo(getBLAST);
		
		while (!connectionInfo[1].equals("application/json")) {
			System.out.println("Requested page is incorrectly formatted as " + connectionInfo[1]);
			System.out.println("Request processing, sleeping one minute...");
			Thread.sleep(60000);
			connectionInfo = connectTo(getBLAST);
		}
		
		System.out.println(connectionInfo[0] + " " + connectionInfo[1]);
		JSONObject json = new JSONObject(connectionInfo[0]);
		return json;
	}
	
	private static String putBLAST(String query) throws IOException {
		System.out.print("Enter accession number: ");
		String accession = sc.nextLine();
		System.out.println("Accession number: " + accession);
		System.out.println("Sending request to BLAST engine...");
		String putBLAST = "https://www.ncbi.nlm.nih.gov/blast/Blast.cgi?QUERY="+query
				+ "&ENTREZ_QUERY="+accession+"&DATABASE=nr&PROGRAM=blastn&WORD_SIZE=28&FORMAT=Text&CMD=Put";
		String[] connectionInfo = connectTo(putBLAST);
		// Extract request ID from value attribute in <label for="rid"> tag
		String rid = null;
		String target = "<label for=\"rid\">";
		Pattern p = Pattern.compile("value=\"(.*?)\"");
		Matcher m;
		String[] lines = connectionInfo[0].split("\n");
		for (String line : lines) {
			if (line.contains(target)) {
				m = p.matcher(line);
				if (m.find()) {
					rid = m.group(1);
				}	
			}
		}
		System.out.println("Request ID: " + rid);
		return rid;
	}
	
	// Reusable URL connection function
	private static String[] connectTo(String link) throws IOException {
		String[] result = new String[2];
		URLConnection conn = new URL(link).openConnection();
		BufferedReader input = new BufferedReader(
							   new InputStreamReader(conn.getInputStream()));
		
		StringBuilder sb = new StringBuilder();
		String inputLine;
		
		while ((inputLine = input.readLine()) != null) {
			sb.append(inputLine + "\n");
		}
		
		input.close();
		result[0] = sb.toString();
		result[1] = conn.getContentType();
		return result;
	}
}

/* NOTES:
 * // https://biostars.org/p/83158/
	// https://ncbi.github.io/blast-cloud/dev/api.html
	// might be worth looking at alextblog.blogspot.cz/2012/05/ncbi-blast-jaxb-biojava-blasting-like.html
 * 		// NOTE: LIKELY WILL NEED TO FIX THE WHILE LOOP TO ACCOUNT FOR ERRORS, SUCH AS 
		// ERROR 502 OR PERPETUAL "Status=WAITING" 
		// identical problem to latter: https://www.biostars.org/p/237886/
		
		// NOTE: Will need to fix AlignmentFileParser so that the parsing is 
		// flexible that it searches until it finds "hsps" or "description" etc, instead 
		// of specifically being told to go down level by level 
		// IMPORTANT NOTE: Need to add a second thread to monitor progress of Put, because 
		// Get won't work until Put is done, sometimes quickly sometimes slowly. 
		// Get worked with my last edit which used request id from old
		// Put request that had already completed. (request ids are good for a day or so)
		 */

