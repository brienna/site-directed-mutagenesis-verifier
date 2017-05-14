package alignmentparser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
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
		System.out.println("Query is " + query);
		
		// Send search request and parse out request ID
		HashMap<String, String> putInfo = putBLAST(query);
		System.out.println("Request ID is " + putInfo.get("rid"));
		
		// Check and handle status of search request
		checkSearchStatus(putInfo);
		
		// Retrieve and display results
		JSONObject alignment = getBLAST(putInfo);
		AlignmentAnalyzer.beginAnalysis(alignment, args);
	}
	
	private static JSONObject getBLAST(HashMap<String, String> putInfo) throws IOException, InterruptedException {
		String rid = putInfo.get("rid");
		String getBLAST = "https://www.ncbi.nlm.nih.gov/blast/Blast.cgi?RID="+rid+"&FORMAT_TYPE=JSON2_S&CMD=Get";
		String connectionInfo = connectTo(getBLAST);
		
		System.out.println(connectionInfo);
		JSONObject json = new JSONObject(connectionInfo);
		return json;
	}
	
	private static String parseSearchStatus(HashMap<String, String> putInfo) throws IOException { 
		// Build the connection
		String rid = putInfo.get("rid");
		String url = "https://www.ncbi.nlm.nih.gov/blast/Blast.cgi?RID="+rid+"&FORMAT_OBJECT=SearchInfo&CMD=Get";
		String connectionInfo = connectTo(url);
		
		// Parse search status from QBlastInfo block
		String status = null;
		Pattern p = Pattern.compile("(?m)Status=(.*)");
		Matcher m = p.matcher(connectionInfo);
		if (m.find()) {
			status = m.group(1);
		}
		
		return status;
	}
	
	private static void checkSearchStatus(HashMap<String, String> putInfo) throws IOException, InterruptedException {
		// Sleep the estimated time to completion
		int sleepTime = Integer.parseInt(putInfo.get("rtoe"));
		System.out.println("Request processing, sleeping " + sleepTime + " seconds");
		Thread.sleep(sleepTime * 1000);  
		
		boolean ready = false;
		while (!ready) {
			// Sleep 10 seconds, then check status
			Thread.sleep(10000);
			String status = parseSearchStatus(putInfo);
			if (status.equals("WAITING")) {
				System.out.println("Still searching...");
			} else if (status.equals("FAILED")) {
				System.out.println("Search failed.");
				System.exit(0);
			} else if (status.equals("UNKNOWN")) {
				System.out.println("Search expired.");
				System.exit(0);
			} else if (status.equals("READY")) {
				System.out.println("Search complete, retrieving results...");
				ready = true;
			} else {
				System.out.println("Something unexpected has occurred.");
				System.exit(0);
			}
		}
	}
	
	private static HashMap<String, String> putBLAST(String query) throws IOException {
		System.out.print("Enter accession number: ");
		String accession = sc.nextLine();
		
		// Check if record exists for accession number
		JSONObject record = Subject.fetchRecord(accession);
		if (record.length() == 0) {
			System.out.println("No record found for entered accession number.");
			System.exit(0);
		} 

		System.out.println("Accession number is " + accession);
		System.out.println("Sending request to BLAST engine...");
		String putBLAST = "https://www.ncbi.nlm.nih.gov/blast/Blast.cgi?QUERY="+query
				+ "&ENTREZ_QUERY="+accession+"&DATABASE=nr&PROGRAM=blastn&WORD_SIZE=28&FORMAT=Text&CMD=Put";
		String connectionInfo = connectTo(putBLAST);
		
		// Parse rid and rtoe (estimated time to completion)
		String rid = null;
		String rtoe = null;
		Pattern p = Pattern.compile("(?m)^ {4}R(?:(ID|TOE)) = (.*)");
		Matcher m = p.matcher(connectionInfo);
		while (m.find()) {
			if (m.group(1).equals("ID")) {
				rid = m.group(2);
			} 
			if (m.group(1).equals("TOE")) {
				rtoe = m.group(2);
			}
		}
	
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("rid", rid);
		map.put("rtoe", rtoe);
		return map;
	}
	
	// Reusable URL connection function
	private static String connectTo(String link) throws IOException {
		URLConnection conn = new URL(link).openConnection();
		BufferedReader input = new BufferedReader(
							   new InputStreamReader(conn.getInputStream()));
		
		StringBuilder sb = new StringBuilder();
		String inputLine;
		
		while ((inputLine = input.readLine()) != null) {
			sb.append(inputLine + "\n");
		}
		
		input.close();
		return sb.toString();
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

