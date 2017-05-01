package alignmentparser;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.*;

/**
 * Analyzes the alignment object created by AlignmentFileParser.
 * @author Brienna
 *
 */
public class AlignmentAnalyzer {
	private Query query;
	private Subject subject;
	private String midline;
	
	public AlignmentAnalyzer(Query q, Subject s, String m) {
		query = q;
		subject = s;
		midline = m;
	}
	
	public static void beginAnalysis(String[] targetMutations) throws IOException {			
		// Create alignment parser
		AlignmentFileParser parser = new AlignmentFileParser();	
		parser.uploadFile();
		parser.parseFile();
		
		// Create alignment object
		Query q = parser.getQuery();
		Subject s = parser.getSubject();
		String m = parser.getMidline();
		AlignmentAnalyzer analyzer = new AlignmentAnalyzer(q, s, m);

		// Analyze alignment object
		String mismatches = analyzer.identifyMismatches();
		
		String[] residues = analyzer.identifyResidue(targetMutations);
		StringBuilder sb = new StringBuilder("TARGET MUTATIONS:\n");
		for (int i = 0; i < residues.length; i++) {
			// Get symbol of target mutation (last character usually, might need to fix this for errors)
			String targetResidue = targetMutations[i].substring(targetMutations[i].length() - 1, targetMutations[i].length());
			if (residues[i] != null) {
				sb.append(targetMutations[i]);
				if (!residues[i].equals(targetResidue)) {
					sb.append(" - failed");
				} else {
					sb.append(" - succeeded");
				}
			}
		}
		String residuesString = sb.toString();
		String result = "Site-Directed Mutagenesis Verifier\n\n" + residuesString + mismatches;
		
		// Print analysis
		analyzer.printAnalysisToFile(result);
	}
	
	/**
	 * Writes result of analysis to file.
	 * @param result
	 * @throws IOException
	 */
	public void printAnalysisToFile(String result) throws IOException {
		String fileName = "result.txt";
		// Connect to a file with a buffer
		PrintWriter out = new PrintWriter(
						   new BufferedWriter(  
						   new FileWriter(fileName)));
		// NOTE: If the output file doesn't exist when the FileWriter object
		// is created, it's created automatically. If it does exist, it's overwritten.
		
		out.print(result);
		// Flush data to the file and close the output stream
		out.close();
		System.out.println("Your result has been printed to " + fileName);
	}
	
	/**
	  * Returns a string containing information about mismatches in the alignment,
	  * which are denoted by spaces in the midline.
	  */
	  private String identifyMismatches() {
		  String querySeq = query.getSequence();
		  String subjectSeq = subject.getSequence();
		  String mismatches = "MISMATCHES:\n";
		  int total = 0;
		
	    // Loop through each character in the midline
	    for (int i = 0; i < midline.length(); i++) {
	      // If midline character is a space (mismatch) and query character is one of these, ACTG-
	      // (the latter ensures we ignore sequencing errors manifesting as N or other letters)
	      if (midline.charAt(i) == ' ' && querySeq.substring(i, i + 1).matches("[ACTG-]")) {
	    	  total++;
	    	  // Print mutation in mutation notation 
	    	  String mismatch = new String();
	    	  if (subjectSeq.charAt(i) == '-') {
	    		  mismatch = "ins" + querySeq.charAt(i);
	    	  } else if (querySeq.charAt(i) == '-') {
	    		  mismatch = "del" + subjectSeq.charAt(i);
	    	  } else {
	    		  mismatch = subjectSeq.charAt(i) + ">" + querySeq.charAt(i);
	    	  }
	    	  int pos = subject.getStart() + i;
	    	  mismatches = mismatches + pos + mismatch + "\n";
	      }
	    }
	    
	    mismatches = "\n\n" + total + " " + mismatches;
	    return mismatches;
	  }
	
	private String[] identifyResidue(String[] targetMutations) {
		// Extract position from each mutation
		int[] positions = new int[targetMutations.length];
		Pattern pattern = Pattern.compile("\\d+");
		Matcher matcher;
		for (int i = 0; i < targetMutations.length; i++) {
			String mutation = targetMutations[i];
			matcher = pattern.matcher(mutation);
			if (matcher.find()) {
				positions[i] = Integer.parseInt(matcher.group(0));
			}
		}
	
		String[] residues = new String[positions.length];
		HashMap<String, ArrayList<String>> map = AminoAcidMap.assembleMap();
		
		// Calculate base position in CDS that alignment begins at
		int posOfQuery = subject.getStart() - subject.getPosOfCDS()[0];
		
		// For each position,
		for (int j = 0; j < positions.length; j++) {
			// Convert count from codon to base
			int pos = (positions[j] * 3) - 3;
			// Adjust position relative to alignment
			int adjustedPos = pos - posOfQuery;	
			// If position is inside alignment, extract target codon from alignment
			if (adjustedPos < 0) {
				System.out.println("Target mutation " + targetMutations[j] + " is outside alignment scope.");
			} else {
				String targetCodon = query.getSequence().substring(adjustedPos, adjustedPos + 3);
				// Identify residue that target codon translates to 
				for (String codon : map.keySet()) {
					if (map.get(codon).contains(targetCodon)) {
						residues[j] = codon;
					}
				}
			}
		}
		
	    return residues;
	}
}
