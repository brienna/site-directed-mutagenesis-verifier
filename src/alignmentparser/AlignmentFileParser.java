package alignmentparser;

import java.nio.file.*;
import java.util.Scanner;
import java.io.*;
import alignmentparser.json.*;

/**
 * Prompts user to upload alignment file, then parses the file for query, 
 * midline, and subject sequences. 
 * @author Brienna
 *
 */
public class AlignmentFileParser {
	private static Scanner sc = new Scanner(System.in);
	private File alignmentFile;
	
	public void uploadFile() {
		// Prompt user to input a Path object for the file
		System.out.print("Input path of BLAST alignment file: ");
		String s = sc.nextLine();  // read the whole line
		// Get Path object for the file
		Path alignmentFilePath = Paths.get(s);
		// Quit the program if the path leads to no file
		if (Files.exists(alignmentFilePath)) {  
			// Convert the Path object to a File object
			alignmentFile = alignmentFilePath.toFile();
		} else {
			System.out.println("File doesn't exist.");
			System.exit(0);
		}
	}
	
	// change the try and catch block to throwing
	// because i'm not actually doing anything here to handle the exception?
	public JSONObject readFile() {
		StringBuilder sb = new StringBuilder();
		// Connect character input stream to file
		try (BufferedReader in = new BufferedReader(
								 new FileReader(alignmentFile))) {
			// Read and append records in file to StringBuilder
			String line;
			while ((line = in.readLine()) != null) {  
				sb.append(line);
			}
		} catch (IOException e) {  
			System.out.println(e);
		}
		// Convert StringBuilder to JSON object
		JSONObject json = new JSONObject(sb.toString());
		return json;
	}
	
	public void parseFile() {
		JSONObject json = readFile();
		// Access nested fields
		JSONArray level2 = json.getJSONArray("BlastOutput2");
		JSONObject level3 = level2.getJSONObject(0);
		JSONObject level4 = level3.getJSONObject("report");
		JSONObject level5 = level4.getJSONObject("results");
		JSONArray level6 = level5.getJSONArray("bl2seq");
		JSONObject level7 = level6.getJSONObject(0);
		JSONArray level8 = level7.getJSONArray("hits");
		JSONObject level9 = level8.getJSONObject(0);
		JSONArray level10 = level9.getJSONArray("hsps");
		JSONObject level11 = level10.getJSONObject(0);
		
		String querySeq = level11.getString("qseq");
		String subjectSeq = level11.getString("hseq");
		String midline = level11.getString("midline");
		System.out.println(querySeq);
		System.out.println(midline);
		System.out.println(subjectSeq);
	}
	
	public static void main(String[] args) {
		AlignmentFileParser parser = new AlignmentFileParser();	
		parser.uploadFile();
		parser.parseFile();
	}
}
