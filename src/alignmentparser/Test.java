package alignmentparser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.regex.Pattern;
import java.util.Scanner;
import java.util.regex.Matcher;

public class Test {
	private static Scanner sc = new Scanner(System.in);
	private static File queryFile;
	private static String query;
	
	public static void uploadQueryFile() {
		// Prompt user to input a Path object for the file
		System.out.print("Input path of query file: ");
		String s = sc.nextLine();  // read the whole line
		// Get Path object for the file
		Path queryFilePath = Paths.get(s);
		// Quit the program if the path leads to no file
		if (Files.exists(queryFilePath)) {
			// Convert the Path object to a File object
			queryFile = queryFilePath.toFile();
		} else {
			System.out.println("File doesn't exist.");
			System.exit(0);
		}
	}
	
	public static void readQueryFile() {
		StringBuilder sb = new StringBuilder();
		try (BufferedReader in = new BufferedReader(
				 new FileReader(queryFile))) {
			// Read and append records in file to StringBuilder
			String line;
			while ((line = in.readLine()) != null) {
				sb.append(line);
			}
		} catch (IOException e) {  
			System.out.println(e);
		}
		query = sb.toString();
	}
	
	// https://biostars.org/p/83158/
	// https://ncbi.github.io/blast-cloud/dev/api.html
	// might be worth looking at alextblog.blogspot.cz/2012/05/ncbi-blast-jaxb-biojava-blasting-like.html
	public static void main(String[] args) throws IOException, InterruptedException {
		uploadQueryFile();
		readQueryFile();
		
		System.out.println(query);
		String putBLAST = "https://www.ncbi.nlm.nih.gov/blast/Blast.cgi?QUERY="+query
				+ "&ENTREZ_QUERY=NM_080704&DATABASE=nr&PROGRAM=blastn&WORD_SIZE=28&FORMAT=Text&CMD=Put";
		
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

		// Get request
		String getBLAST = "https://www.ncbi.nlm.nih.gov/blast/Blast.cgi?RID="+rid+"&FORMAT_TYPE=JSON2_S&CMD=Get";
		System.out.println("Request processing, sleeping one minute...");
		Thread.sleep(60000);
		connectionInfo = connectTo(getBLAST);
		
		while (!connectionInfo[1].equals("application/json")) {
			System.out.println("Requested page is incorrectly formatted as " + connectionInfo[1]);
			System.out.println("Request processing, sleeping one minute...");
			Thread.sleep(60000);
			connectionInfo = connectTo(getBLAST);
		}
		
		System.out.println(connectionInfo[0] + " " + connectionInfo[1]);
		// NOTE: LIKELY WILL NEED TO FIX THE WHILE LOOP TO ACCOUNT FOR ERRORS, SUCH AS 
		// ERROR 502 OR PERPETUAL "Status=WAITING" 
		// identical problem to latter: https://www.biostars.org/p/237886/
		
		// NOTE: Will need to fix AlignmentFileParser so that the parsing is 
		// flexible that it searches until it finds "hsps" or "description" etc, instead 
		// of specifically being told to go down level by level 
		// IMPORTANT NOTE: Need to add a second thread to monitor progress of Put, because 
		// Get won't work until Put is done, sometimes quickly sometimes slowly. 
		// Get worked with my last edit which used request id from old
		// Put request that had already completed. (request ids are good for a day or so)
	}
	
	// returns input
	public static String[] connectTo(String link) throws IOException {
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