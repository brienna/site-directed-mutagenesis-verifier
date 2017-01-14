public class Subject {
  private String id;
  private String sequence;
  // Position on ref sequence where alignment with query begins, inclusive 
  private int start;
  // Position on ref sequence where alignment with query ends, inclusive
  private int end;
  
  public Subject(String accessNum, int hitFrom, int hitTo, String seq) {
    id = accessNum;
    sequence = seq;
    start = hitFrom;
    end = hitTo;
  }
  
  // Getters
  public String getSequence() {
    return sequence;
  }
  public int getStart() {
    return start;
  }
  public int getEnd() {
    return end;
  }
}