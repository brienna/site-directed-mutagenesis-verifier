package alignmentparser;

public class Subject {
	private String id;
	private String sequence;
	// Position on subject where alignment with query begins, inclusive
	private int start;
	// Position on subject where alignment with query ends, inclusive
	private int end;
	
	public Subject(String accession, String hseq, int from, int to) {
		id = accession;
		sequence = hseq;
		start = from;
		end = to;
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
	
	public String toString() {
		return String.format("id: %s\n"
				+ "hseq: %s\n"
				+ "hit_from: %d\n"
				+ "hit_to: %d", id, sequence, start, end);
    }
}
