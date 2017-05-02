package alignmentparser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

public class Test {
	// https://biostars.org/p/83158/
	// https://ncbi.github.io/blast-cloud/dev/api.html
	// might be worth looking at alextblog.blogspot.cz/2012/05/ncbi-blast-jaxb-biojava-blasting-like.html
	public static void main(String[] args) throws IOException {
		// Send 
		// NOTE: prompt user to input query
		String query = "ANNNAAGNNTGNNNATCCTGTCTGTGTTAGGAGAGTCTACTTCTTAACNGAGGGATTCANTNTTTCCTGCANAGGCGGCC"
				+ "GTCNATGAANACCCTGTTTGTGGACAGCTACNGNGAGATGCTTTTNTTTCTGCAGTCACTGTTCATGCTGGCCACCGTGG"
				+ "TGCTGTACTTCANCCACCTCAAGGAGTATGTGGCTTCCATGGTATTCTCCCTGGCCTTGGGCTGGNCCAACATGCTCTAC"
				+ "TACNCCCGCGGTTTCCAGCAGANGGGCATCTATGCCGTCATGATANANAANATGATCCTGAGAGACCTGTGCCGTTTCAT"
				+ "GTTTGTCTACGTCGTCTTNTTGTTCGGNTTTTCCACAGCGGNGGTGACGCTGATTGAAGACGGNAANAATGACTCCCTGC"
				+ "CGTCTGAGTCCACGTCGCACAGGNGGNGGGGGCCTGCCTGCANGCCCCCCGATAGCTCCTACAACAGCCTGTACTCCACC"
				+ "TGCCTGGAGCTGTTCAAGTTCACCATCGGCATGGGCGACCTGGANTTCACTGANAACTATGACTTCAAGGCTGTCTTCAT"
				+ "CATCCTGCTGCTGGCCTATGTAATTCTCACCTACATCCTCNTGCTCAACATGCTNATCGCCCTCNTGGGTGAGACTGTCA"
				+ "ACAAGATCGCACAGNNAGAGCAAGAACATCTGGAANCTGCAGAGAGCCATCACCATCCTGNACACGGAGAAGAGCTTCCT"
				+ "TAAGTGCATGAGGAAGGCCTTCCGCTCAGGCAAGCTGCNTGCANGTGGGGTACACACCTGATGGCAAGGACGACTACCGG";
		URL toBLAST = new URL("https://www.ncbi.nlm.nih.gov/blast/Blast.cgi?QUERY="+query
				+ "&QUERY_BELIEVE_DEFLINE=false&ENTREZ_QUERY=NM_080704&DATABASE=nr&PROGRAM=blastn&CMD=Put");
		URLConnection connection = toBLAST.openConnection();
		BufferedReader input = new BufferedReader(
							new InputStreamReader(connection.getInputStream()));
		String inputLine;
		StringBuilder sb = new StringBuilder();
		while ((inputLine = input.readLine()) != null) {
			System.out.println(inputLine);
			sb.append(inputLine);
		}
		
		System.out.println(sb);
		
		input.close();
		
		// Retrieve Request ID
		// NEED TO RETRIEVE IT INSTEAD OF SETTING IT HERE
		String rid = "GHUDC95S015";
		
		// 
		URL fromBLAST = new URL("https://www.ncbi.nlm.nih.gov/blast/Blast.cgi?CMD=GET&RID="+rid+"&FORMAT_TYPE=JSON2_S");
		URLConnection connection2 = fromBLAST.openConnection();
		input = new BufferedReader(
				new InputStreamReader(connection2.getInputStream()));
		StringBuilder sb2 = new StringBuilder();
		while ((inputLine = input.readLine()) != null) {
			System.out.println(inputLine);
			sb2.append(inputLine);
		}
		
		input.close();
		
	}
}