package alignmentparser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class Test {
	// https://biostars.org/p/83158/
	// https://ncbi.github.io/blast-cloud/dev/api.html
	// might be worth looking at alextblog.blogspot.cz/2012/05/ncbi-blast-jaxb-biojava-blasting-like.html
	public static void main(String[] args) throws IOException {
		// Put 
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
				+ "&QUERY_BELIEVE_DEFLINE=false&ENTREZ_QUERY=NM_080704&DATABASE=nr&PROGRAM=blastn&FORMAT=Text&CMD=Put");
		URLConnection connection = toBLAST.openConnection();
		BufferedReader input = new BufferedReader(
							new InputStreamReader(connection.getInputStream()));
		String inputLine;
		// Retrieve Request ID
		String rid = null;
		Pattern p = Pattern.compile("value=\"(.*?)\"");  // use lazy not greedy
		Matcher m;
		while ((inputLine = input.readLine()) != null) {
			if (inputLine.contains("<label for=\"rid\">")) {
				System.out.println(inputLine);
				m = p.matcher(inputLine);
				if (m.find()) {
					rid = m.group(1);
				}	
			}
		}
		
		System.out.println(rid);
		input.close();
		
		// rid = "GJGH4HFK015"; for testing purposes to ensure the get request works, 
		// until i have a second thread to monitor when put request completes
		
		// Get
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
		
		
		// NOTE: Will need to fine tune request, because it seems to be sending back 
		// fragmented alignments whereas Web Blast doesn't?
		// NOTE 2: Will need to fix AlignmentFileParser so that the parsing is 
		// flexible that it searches until it finds "hsps" or "description" etc, instead 
		// of specifically being told to go down level by level 
		// IMPORTANT NOTE: Need to add a second thread to monitor progress of Put, because 
		// Get won't work until Put is done, sometimes quickly sometimes slowly. 
		// Get worked with my last edit which used request id from old
		// Put request that had already completed. (request ids are good for a day or so)
	}
}