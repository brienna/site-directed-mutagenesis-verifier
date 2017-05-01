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
		StringBuilder sb = new StringBuilder();
		for (String residue : residues) {
			sb.append(residue + " ");
		}
		String residuesString = sb.toString();
		String result = "Site-Directed Mutagenesis Verifier\n" + residuesString + mismatches;
		
		// Print analysis
		analyzer.printAnalysisToFile(result);
	}
	
	/**
	 * Writes result of analysis to file.
	 * @param result
	 * @throws IOException
	 */
	public void printAnalysisToFile(String result) throws IOException {
		// Connect to a file with a buffer
		PrintWriter out = new PrintWriter(
						   new BufferedWriter(  
						   new FileWriter("result.txt")));
		// NOTE: If the output file doesn't exist when the FileWriter object
		// is created, it's created automatically. If it does exist, it's overwritten.
		
		out.print(result);
		
		// Flush data to the file and close the output stream
		out.close();
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
		HashMap<String, ArrayList<String>> codons = assembleCodonMap();
		
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
				for (String codon : codons.keySet()) {
					if (codons.get(codon).contains(targetCodon)) {
						residues[j] = codon;
					}
				}
			}
		}
		
	    return residues;
	}
	
	private HashMap<String, ArrayList<String>> assembleCodonMap() {
	    ArrayList<String> ala = new ArrayList<String>();
	    ala.add("GCT");
	    ala.add("GCC");
	    ala.add("GCA");
	    ala.add("GCG");
	    ArrayList<String> arg = new ArrayList<String>();
	    arg.add("CGT");
	    arg.add("CGC");
	    arg.add("CGA");
	    arg.add("CGG");
	    arg.add("AGA");
	    arg.add("AGG");
	    ArrayList<String> asn = new ArrayList<String>();
	    asn.add("AAT");
	    asn.add("AAC");
	    ArrayList<String> asp = new ArrayList<String>();
	    asp.add("GAT");
	    asp.add("GAC");
	    ArrayList<String> cys = new ArrayList<String>();
	    cys.add("TGT");
	    cys.add("TGC");
	    ArrayList<String> gln = new ArrayList<String>();
	    gln.add("CAA");
	    gln.add("CAG");
	    ArrayList<String> glu = new ArrayList<String>();
	    glu.add("GAA");
	    glu.add("GAG");
	    ArrayList<String> gly = new ArrayList<String>();
	    gly.add("GGT");
	    gly.add("GGC");
	    gly.add("GGA");
	    gly.add("GGG");
	    ArrayList<String> his = new ArrayList<String>();
	    his.add("CAT");
	    his.add("CAC");
	    ArrayList<String> ile = new ArrayList<String>();
	    ile.add("ATT");
	    ile.add("ATC");
	    ile.add("ATA");
	    ArrayList<String> leu = new ArrayList<String>();
	    leu.add("TTA");
	    leu.add("TTG");
	    leu.add("CTT");
	    leu.add("CTC");
	    leu.add("CTA");
	    leu.add("CTG");
	    ArrayList<String> lys = new ArrayList<String>();
	    lys.add("AAA");
	    lys.add("AAG");
	    ArrayList<String> met = new ArrayList<String>();
	    met.add("ATG");
	    ArrayList<String> phe = new ArrayList<String>();
	    phe.add("TTT");
	    phe.add("TTC");
	    ArrayList<String> pro = new ArrayList<String>();
	    pro.add("CCT");
	    pro.add("CCC");
	    pro.add("CCA");
	    pro.add("CCG");
	    ArrayList<String> ser = new ArrayList<String>();
	    ser.add("TCT");
	    ser.add("TCC");
	    ser.add("TCA");
	    ser.add("TCG");
	    ser.add("AGT");
	    ser.add("AGC");
	    ArrayList<String> thr = new ArrayList<String>();
	    thr.add("ACT");
	    thr.add("ACC");
	    thr.add("ACA");
	    thr.add("ACG");
	    ArrayList<String> trp = new ArrayList<String>();
	    trp.add("TGG");
	    ArrayList<String> tyr = new ArrayList<String>();
	    tyr.add("TAT");
	    tyr.add("TAC");
	    ArrayList<String> val = new ArrayList<String>();
	    val.add("GTT");
	    val.add("GTC");
	    val.add("GTA");
	    val.add("GTG");

	    HashMap<String, ArrayList<String>> codons = new HashMap<String, ArrayList<String>>();
	    codons.put("alanine", ala);
	    codons.put("arginine", arg);
	    codons.put("asparagine", asn);
	    codons.put("aspartic acid", asp);
	    codons.put("cysteine", cys);
	    codons.put("glutamine", gln);
	    codons.put("glutamic acid", glu);
	    codons.put("glycine", gly);
	    codons.put("histidine", his);
	    codons.put("isoleucine", ile);
	    codons.put("leucine", leu);
	    codons.put("lysine", lys);
	    codons.put("methionine", met);
	    codons.put("phenylalanine", phe);
	    codons.put("proline", pro);
	    codons.put("serine", ser);
	    codons.put("threonine", thr);
	    codons.put("tryptophan", trp);
	    codons.put("tyrosine", tyr);
	    codons.put("valine", val);

	    return codons;
	}
}
