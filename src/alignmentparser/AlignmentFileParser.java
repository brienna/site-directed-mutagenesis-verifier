package alignmentparser;

import alignmentparser.json.*;

/**
 * Prompts user to provide path to alignment file, then parses the file for 
 * information needed to create subject and query and alignment objects.
 * @author Brienna
 *
 */
public class AlignmentFileParser {
	private Query query;
	private Subject subject;
	private String midline;
	
	public void parseFile(JSONObject alignment) {
		JSONObject json = alignment;
		
		// Access nested fields
		JSONArray level2 = json.getJSONArray("BlastOutput2");
		JSONObject level3 = level2.getJSONObject(0);
		JSONObject level4 = level3.getJSONObject("report");
		JSONObject level5 = level4.getJSONObject("results");
		JSONObject level6 = level5.getJSONObject("search");
		JSONArray level7 = level6.getJSONArray("hits");
		JSONObject level8 = level7.getJSONObject(0);
		// Get hsps info section
		JSONArray level9 = level8.getJSONArray("hsps");
		JSONObject hsps = level9.getJSONObject(0);
		// Get description info section
		JSONArray level8_2 = level8.getJSONArray("description");
		JSONObject description = level8_2.getJSONObject(0);
		
		// Set query, subject, and midline objects
		setQuery(hsps);
		setSubject(hsps, description);
		midline = hsps.getString("midline");
	}
	
	private void setQuery(JSONObject info) {
		String qseq = info.getString("qseq");
		int from = info.getInt("query_from");
		int to = info.getInt("query_to");
		query = new Query(qseq, from, to);
		//System.out.println(query);
	}
	
	private void setSubject(JSONObject info, JSONObject moreInfo) {
		String hseq = info.getString("hseq");
		String id = moreInfo.getString("accession");  
		int from = info.getInt("hit_from");
		int to = info.getInt("hit_to");
		subject = new Subject(id, hseq, from, to);
		//System.out.println(subject);
	}
	
	public Query getQuery() {
		return query;
	}
	
	public Subject getSubject() {
		return subject;
	}
	
	public String getMidline() {
		return midline;
	}
}
