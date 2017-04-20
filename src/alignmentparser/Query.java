package alignmentparser;

public class Query {
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
}
