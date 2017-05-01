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
		// Check that user specified mutations, quit application if false
		// NOTE: WILL NEED TO ADD CHECK FOR INCORRECTLY FORMATTED ARGUMENTS?
		if (args.length < 1) {
			System.out.println("Incorrect number of arguments. Please try again.");
			System.exit(0);
		} 
		
		// Print program usage info
		System.out.println();
		System.out.println("USAGE OPTIONS:");
		System.out.println("1) Upload an alignment file downloaded from BLASTN.");
		System.out.println("2) Copy and paste query sequence and reference sequence accession number.");
		System.out.println();
		
		// Prompt user for how to use program
		int choice = 0;
		while (choice != 1 && choice != 2) {
			System.out.print("Enter 1 or 2 to choose an option: ");
			choice = sc.nextInt();
		}
		
		// Proceed with user's choice of how to use program
		if (choice == 1) {
			AlignmentAnalyzer.beginAnalysis(args);
		} 
		
		if (choice == 2) {
			System.out.println("This option is under construction.");
		}
	
	}
	
	

}
