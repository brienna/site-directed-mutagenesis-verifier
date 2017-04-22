package alignmentparser;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Analyzes the alignment object created by AlignmentFileParser.
 * @author Brienna
 *
 */
public class AlignmentAnalyzer {
	private Query query;
	private Subject subject;
	private String midline;
	
	public static void main(String[] args) {			
		// Create alignment object
		AlignmentFileParser parser = new AlignmentFileParser();	
		parser.uploadFile();
		parser.parseFile();
		
		// Create alignment object
		Query q = parser.getQuery();
		Subject s = parser.getSubject();
		String m = parser.getMidline();
		AlignmentAnalyzer analyzer = new AlignmentAnalyzer(q, s, m);

		// Analyze alignment object
		// analyzer.identifyMismatches();
		analyzer.identifyResidue(547);
	}
	
	public AlignmentAnalyzer(Query q, Subject s, String m) {
		query = q;
		subject = s;
		midline = m;
	}
	
	private void identifyResidue(int pos) {
		HashMap<String, ArrayList<String>> codons = assembleCodonMap();
		
		// Set base position of first codon in the CDS 
		// (this will be retrieved from GenBank later)
	    int posOfCDS = 276;
	    // Adjust given codon count to account for any difference between CDS / query length
	    // NOTE: Subtract 3 to account for conversion between codon count and base count
	    int adjustedPos = (pos * 3) - (subject.getStart() - posOfCDS) - 3;
	    System.out.println(adjustedPos + subject.getStart());

	    if (adjustedPos < 0) {
	      System.out.println("Codon " + pos + " is not viewable in this alignment.");
	      //System.out.println("You can view codons " + (posFirstCodon + 1) + " through " + ((2495 - 276) / 3));
	    }

	    // Get codon
	    String found = "";
	    String seq = query.getSequence();
	    for (int i = 0; i < seq.length(); i += 3) {
	      if (i == adjustedPos) {
	        found = seq.substring(i, i + 3);
	        System.out.println(found + " at " + (i + subject.getStart()));
	      }
	    }

	    // Identify residue that codon translates to
	    for (String codon : codons.keySet()) {
	      if (codons.get(codon).contains(found)) {
	        System.out.println("Residue is " + codon);
	      }
	    }
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
