package alignmentparser;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import alignmentparser.json.JSONObject;
import alignmentparser.json.XML;

public class Subject {
	private String id;  // GenBank accession number
	private String sequence; 
	// Position on subject where alignment with query begins, inclusive
	private int start;
	// Position on subject where alignment with query ends, inclusive
	private int end;
	private JSONObject record; // GenBank record
	private int[] posOfCDS;  // nucleotide position at which cds begins
	
	public Subject(String accession, String hseq, int from, int to) {
		id = accession;
		sequence = hseq;
		start = from;
		end = to;
		record = fetchRecord();
		posOfCDS = parseForCDS();
	}
	
	public String getId() {
		return id;
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
	
	public int[] getPosOfCDS() {
		return posOfCDS;
	}
	
	public String toString() {
		return String.format("id: %s\n"
				+ "hseq: %s\n"
				+ "hit_from: %d\n"
				+ "hit_to: %d", id, sequence, start, end);
    }
	
	private int[] parseForCDS() {
		int[] posOfCDS = new int[2];
		JSONObject seqInterval = record.getJSONObject("Bioseq-set").getJSONObject("Bioseq-set_seq-set").getJSONObject("Seq-entry").getJSONObject("Seq-entry_set")
				.getJSONObject("Bioseq-set").getJSONObject("Bioseq-set_annot").getJSONObject("Seq-annot").getJSONObject("Seq-annot_data")
				.getJSONObject("Seq-annot_data_ftable").getJSONObject("Seq-feat").getJSONObject("Seq-feat_location").getJSONObject("Seq-loc")
				.getJSONObject("Seq-loc_int").getJSONObject("Seq-interval");

		// seems like cds is one position off frame compared to genbank?
		posOfCDS[0] = seqInterval.getInt("Seq-interval_from") + 1;
		posOfCDS[1] = seqInterval.getInt("Seq-interval_to") + 1;
		return posOfCDS;
	}

	private JSONObject fetchRecord() {
		JSONObject rec = new JSONObject();
		try {
	      // e post to Entrez
		  // // https://www.ncbi.nlm.nih.gov/books/NBK25498/#chapter3.Application_2_Converting_access
	      String link = "https://eutils.ncbi.nlm.nih.gov/entrez/eutils/epost.fcgi?db=nucleotide&id=" + id;
	      URLConnection connection = new URL(link).openConnection();
	      connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");
	      connection.connect(); // inputstreamreader does this, can delete
	      BufferedReader r  = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));

	      String webEnv = "";
	      String queryKey = "";
	      // Put together string to be scanned to find patterns
	      StringBuilder sb = new StringBuilder();
	      String line;
	      while ((line = r.readLine()) != null) {
	        sb.append(line);
	      }
	      String result = sb.toString();
	      // Specify patterns to search for
	      String queryKeyRegEx = "<QueryKey>(\\d+)<\\/QueryKey>";
	      String webEnvRegEx = "<WebEnv>(\\S+)<\\/WebEnv>";
	      // Create Pattern objects
	      Pattern queryKeyPattern = Pattern.compile(queryKeyRegEx);
	      Pattern webEnvPattern = Pattern.compile(webEnvRegEx);
	      // Create Matcher objects
	      Matcher queryKeyMatcher = queryKeyPattern.matcher(result);
	      if (queryKeyMatcher.find()) {
	        queryKey = queryKeyMatcher.group(1);
	        //System.out.println(queryKey);
	      }
	      Matcher webEnvMatcher = webEnvPattern.matcher(result);
	      if (webEnvMatcher.find()) {
	        webEnv = webEnvMatcher.group(1);
	        //System.out.println(webEnv);
	      }

	      // e fetch to Entrez
	      // https://dataguide.nlm.nih.gov/eutilities/utilities.html#efetch
	      // https://www.ncbi.nlm.nih.gov/books/NBK25499/table/chapter4.T._valid_values_of__retmode_and/?report=objectonly
	      link = "https://eutils.ncbi.nlm.nih.gov/entrez/eutils/efetch.fcgi?db=nucleotide&query_key="+queryKey+"&WebEnv="+webEnv+"&usehistory=y&rettype=native&retmode=xml";
	      connection = new URL(link).openConnection();
	      connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");
	      connection.connect();
	      r  = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));

	      sb = new StringBuilder();
	      while ((line = r.readLine()) != null) {
	        sb.append(line);
	      }
	      String s = sb.toString();
	      rec = XML.toJSONObject(s);
	      //System.out.println(rec);
	    } catch (Exception e) {
	      System.out.println(e);
	    }
		return rec;
	}
}
