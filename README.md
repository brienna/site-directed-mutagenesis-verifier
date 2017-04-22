# BLAST Alignment Analyzer 

This program analyzes a JSON file containing a BLAST alignment of two DNA sequences to determine the success of mutagenesis. 

For sample usage of BLAST Alignment Analyzer, view mTRPV1-M547L.md. 

### Classes:

`Subject` — represents a subject sequence, aka the reference sequence.

`Query` — represents a query sequence, aka the sequence on which you performed mutagenesis.

`AlignmentFileParser` — parses an alignment file for information to create the subject and query objects, as well as the midline string. 

`AlignmentAnalyzer` — analyzes the alignment, using information contained in the object created by `AlignmentFileParser`, to determine success of mutagenesis.