# Site-Directed Mutagenesis Verifier (SDMV)

SDMV quickly verifies the success of site-directed mutagenesis when given target mutation(s), query sequence(s), and reference sequence accession number.

### To begin the program: 

Command line: `java SDMV.Main [mutation1]`

At least one mutation argument must be provided and formatted in amino acid mutation notation, e.g. L547M. 

### How to reproduce alignment in Web Blast:

This example uses Safari, a query sequence copied from a .seq file, and the reference sequence with the accession number NM_080704. The query sequence is a segment of the human TRPV1 protein that is supposed to have undergone site-directed mutagenesis with the mutation L547M. 

Instructions:

![Steps for reproducing results in Web Blast](/img/blastn.png)

1. Open your browser and navigate to the [BLASTN page](https://blast.ncbi.nlm.nih.gov/Blast.cgi?PROGRAM=blastn&PAGE_TYPE=BlastSearch&LINK_LOC=blasthome).
2. Copy and paste the query sequence in the "Enter Query Sequence" input field.
3. Enter the reference sequence accession number in the "Entrez Query" input field.
4. Press "BLAST".

![Steps for reproducing results in Web Blast, part 2](img/blastn2.png)

5. Open the "Download" drop-down menu. 
6. Download the alignment formatted as single-file JSON.
