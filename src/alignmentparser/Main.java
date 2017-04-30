package alignmentparser;

import java.io.IOException;
import java.util.Scanner;

/**
 * Starts the Site-Directed Mutagenesis Verifier program.
 * 
 * @author Brienna
 *
 */
public class Main {

	private static Scanner sc = new Scanner(System.in);

	public static void main(String[] args) throws IOException {
		// Print program usage info
		System.out.println("Site-Directed Mutagenesis Verifier");
		System.out.println("This program quickly verifies the success of site-directed mutagenesis.");
		System.out.println("There are two usage options:");
		System.out.println("1) Upload an alignment file downloaded from BLASTN.");
		System.out.println("2) Copy and paste query sequence and reference sequence accession number.");
		
		// Prompt user for how to use program
		int choice = 0;
		while (choice != 1 && choice != 2) {
			System.out.print("How would you like to use this program? Enter 1 or 2: ");
			choice = sc.nextInt();
		}
		
		// Proceed with user's choice of how to use program
		if (choice == 1) {
			System.out.println("You chose to upload an alignment file downloaded from BLASTN.");
			AlignmentAnalyzer.beginAnalysis();
		} 
		
		if (choice == 2) {
			System.out.println("This option is under construction.");
		}
	
	}
	
	

}
