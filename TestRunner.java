class TestRunner {
  private String query = "ATCCTGTCTGTGTTAGGAG-AGTCTACTTC-TTAACNGAGGGATTCANTNTTTCCTGCANAGGCGGCCGTCNATGAANACCCTGTTTGTGGACAGCTACNGNGAGATGCTTTTNTTTCTGCAGTCACTGTTCATGCTGGCCACCGTGGTGCTGTACTTCANCCACCTCAAGGAGTATGTGGCTTCCATGGTATTCTCCCTGGCCTTGGGCTGGNCCAACATGCTCTACTACNCCCGCGGTTTCCAGCAGANGGGCATCTATGCCGTCATGATANANAANATGATCCTGAGAGACCTGTGCCGTTTCATGTTTGTCTACGTCGTCTTNTTGTTCGGNTTTTCCACAGCGGNGGTGACGCTGATTGAAGACGGNAANAATGACTCCCTGCCGTCTGAGTCCACGTCGCACAGGNGGNGGGGGCCTGCCTGCANGCCCCCCGATAGCTCCTACAACAGCCTGTACTCCACCTGCCTGGAGCTGTTCAAGTTCACCATCGGCATGGGCGACCTGGANTTCACTGANAACTATGACTTCAAGGCTGTCTTCATCATCCTGCTGCTGGCCTATGTAATTCTCACCTACATCCTCNTGCTCAACATGCTNATCGCCCTCNTGGGTGAGACTGTCAACAAGATCGCACAGNNAGAGCAAGAACATCTGGAANCTGCAGAGAGCCATCACCATCCTGNACACGGAGAAGAGCTTCCTTAAGTGCATGAGGAAGGCCTTCCGCTCAGGCAAGCTGCNTGCANGTGGGGTACACACCTGATGGCAAGGACGACTACCGG";
  private String midline = "||||||||||||||||||| |||||||||| ||  | |||||||||| | ||||||||| ||||||||||| ||||| ||||||||||||||||||||| | ||||||||||| |||||||||||||||||||||||||||||||||||||||||||||| |||||||||||||||||||||||||||||||||||||||||||||||||||| ||||||||||||||||| |||||||||||||||||| |||||||||||||||||||||| | || ||||||||||||||||||||||||||||||||||||||| ||||||| |||||||| ||||||||||||| ||||||||||||||||||||| || |||||||||||||||||||||||||||||||||||| || ||||||||||||||| ||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||| |||||||| |||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||| ||||||||||||| ||||||||| |||||||||||||||||||||||||||||  ||||||||||||||||||| |||||||||||||||||||||||| ||||||||||||||||||||||||||||||||||||||||||||||||||||||||| |||| ||||||||||||||||||||||||||||||||||||";
  private Subject subj = new Subject("NM_080704", 1710, 2495, "ATCCTGTCTGTGTTAGGAGGAGTCTACTTCTTTTTCCGAGGGATTCAGTATTTCCTGCAGAGGCGGCCGTCGATGAAGACCCTGTTTGTGGACAGCTACAGTGAGATGCTTTTCTTTCTGCAGTCACTGTTCATGCTGGCCACCGTGGTGCTGTACTTCAGCCACCTCAAGGAGTATGTGGCTTCCATGGTATTCTCCCTGGCCTTGGGCTGGACCAACATGCTCTACTACACCCGCGGTTTCCAGCAGATGGGCATCTATGCCGTCATGATAGAGAAGATGATCCTGAGAGACCTGTGCCGTTTCATGTTTGTCTACATCGTCTTCTTGTTCGGGTTTTCCACAGCGGTGGTGACGCTGATTGAAGACGGGAAGAATGACTCCCTGCCGTCTGAGTCCACGTCGCACAGGTGGCGGGGGCCTGCCTGCAGGCCCCCCGATAGCTCCTACAACAGCCTGTACTCCACCTGCCTGGAGCTGTTCAAGTTCACCATCGGCATGGGCGACCTGGAGTTCACTGAGAACTATGACTTCAAGGCTGTCTTCATCATCCTGCTGCTGGCCTATGTAATTCTCACCTACATCCTCCTGCTCAACATGCTCATCGCCCTCATGGGTGAGACTGTCAACAAGATCGCACAGG-AGAGCAAGAACATCTGGAAGCTGCAGAGAGCCATCACCATCCTGGACACGGAGAAGAGCTTCCTTAAGTGCATGAGGAAGGCCTTCCGCTCAGGCAAGCTGC-TGCAGGTGGGGTACACACCTGATGGCAAGGACGACTACCGG");
  
  public static void main(String[] args) {
    // NOTE: If I don't declare a new TestRunner instance it throws an error
    TestRunner analyzer = new TestRunner();
    analyzer.identifyMismatches();
  }
  
  /**
  * Prints out mismatches in the alignment of subject and query. Base positions are derived from subject.
  */
  private void identifyMismatches() {
    // Loops through each character in the midline
    for (int i = 0; i < midline.length(); i++) {
      // If character is a space, denoting a mismatch
      if (midline.charAt(i) == ' ' && query.charAt(i) != 'N') {
        // Print out the characters at the same position in subject & query
        System.out.println(subj.getStart() + i);
        System.out.println(subj.getSequence().charAt(i) + ">" + query.charAt(i));
      }
    }
  } 
}