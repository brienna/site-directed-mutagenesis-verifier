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

public class WebBlast {
	private static Scanner sc = new Scanner(System.in);
	private String query;
	private String requestID;
	private int estimatedTime;
	
	// Create WebBlast instance
	public WebBlast(String sequence) {
		query = sequence;
	}
	
	// Send search request and parse out request ID and estimated time to completion
	public void putRequest() throws IOException {
		System.out.print("Enter accession number: ");
		String accession = sc.nextLine();
		
		// Check if record exists for accession number
		System.out.println("Checking if record exists for given accession number...");
		JSONObject record = Subject.fetchRecord(accession);
		if (record.length() == 0) {
			System.out.println("No record found for entered accession number.");
			System.exit(0);
		} 
		
		System.out.println("Record exists for accession number " + accession);
		System.out.println("Sending request to BLAST engine...");
		String putBLAST = "https://www.ncbi.nlm.nih.gov/blast/Blast.cgi?QUERY="+query
				+ "&ENTREZ_QUERY="+accession+"&DATABASE=nr&PROGRAM=blastn&WORD_SIZE=28&FORMAT=Text&CMD=Put";
		String connectionInfo = connectTo(putBLAST);
		
		// Parse out requestID and estimated time to completion
		Pattern p = Pattern.compile("(?m)^ {4}R(?:(ID|TOE)) = (.*)");
		Matcher m = p.matcher(connectionInfo);
		while (m.find()) {
			if (m.group(1).equals("ID")) {
				requestID = m.group(2);
			} 
			if (m.group(1).equals("TOE")) {
				estimatedTime = Integer.parseInt(m.group(2));
			}
		}
	}
	
	public void checkSearchStatus() throws IOException, InterruptedException {
		// Sleep the estimated time to completion
		System.out.println("Request processing, sleeping " + estimatedTime + " seconds");
		Thread.sleep(estimatedTime * 1000);  
		
		boolean ready = false;
		while (!ready) {
			// Sleep 10 seconds, then check status
			Thread.sleep(10000);
			String status = getSearchStatus();
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
	
	private String getSearchStatus() throws IOException { 
		// Build the connection
		String url = "https://www.ncbi.nlm.nih.gov/blast/Blast.cgi?RID="+requestID+"&FORMAT_OBJECT=SearchInfo&CMD=Get";
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
	
	public JSONObject getRequest() throws IOException, InterruptedException {
		String url = "https://www.ncbi.nlm.nih.gov/blast/Blast.cgi?RID="+requestID+"&FORMAT_TYPE=JSON2_S&CMD=Get";
		String connectionInfo = connectTo(url);
		System.out.println(connectionInfo);
		JSONObject json = new JSONObject(connectionInfo);
		return json;
	}
	
	// Reusable URL connection function
	public static String connectTo(String link) throws IOException {
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
	
	// Getters
	public String getQuery() {
		return query;
	}
	
	public String getRequestID() {
		return requestID;
	}
	
	public int getEstimatedTime() {
		return estimatedTime;
	}
}
