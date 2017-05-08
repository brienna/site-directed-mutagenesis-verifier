package alignmentparser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

public class Query {
	private static Scanner sc = new Scanner(System.in);
	private String sequence;
	private int start;
	private int end; 
	
	public Query(String qseq, int from, int to) {
		sequence = qseq;
		start = from;
		end = to;
	}
	
	public String getSequence() {
		return sequence;
	}
	
	public int getStart() {
		return start;
	}
	
	public int getEnd() {
		return end;
	}
	
	public String toString() {
		return String.format("qseq: %s\n"
				+ "query_from: %d\n"
				+ "query_to: %d", sequence, start, end);
    }
	
	// Not bound to any single object 
	public static String uploadQueryFile() {
		String querySeq = null;
		// Prompt user to input a Path object for the file
		System.out.print("Input path of query file: ");
		String s = sc.nextLine();  // read the whole line
		// Get Path object for the file
		Path queryFilePath = Paths.get(s);
		// Quit the program if the path leads to no file
		if (Files.exists(queryFilePath)) {
			// Convert the Path object to a File object
			File queryFile = queryFilePath.toFile();
			// And read it, save its sequence to this object
			querySeq = readQueryFile(queryFile);
		} else {
			System.out.println("File doesn't exist.");
			System.exit(0);
		}
		return querySeq;
	}
	
	// Not bound to any single object
	public static String readQueryFile(File queryFile) {
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
		return sb.toString();
	}
}
