- Lab 4 - BTree
- CS 321
- 12/10/2017
- Karan Davis, Ally Oliphant, Cybil Lesbyn

OVERVIEW:

	This program is supposed to take a given GeneBank file and convert it into a BTree with each 
	object being a DNA sequence of a specified length.The BTree is a tree that is continually
        balanced as things are inserted, such that it is easy to search through. The GeneBank file
        gets broken into sequences and then each sub-sequence is stored into the BTree. This program 
	will also be able to search for sequences of a given length, and will return the frequency and
	occurence of the querey string. 

INCLUDED FILES:

	README - this file
	BTree.java - source file
	GeneBankCreateBTree.java - source file
	GeneBankSearch.java - source file
	MemoryAccess.java - source file
	ParsingFile.java - source file
	

COMPILING AND RUNNING:

	In order to compile this program, the user must type in the command line:

	javac GeneBankCreateBTree.java
	javac GeneBankSearch.java

	In order to run the user must use the following key for each program they would like to use:

	java GeneBankCreateBTree 0 <degree> <gbk file> <sequence length> [<debug level>]
	java GeneBan kSearch 0 <btree file> <query file> [<debug level>]
	
	where degree is the minimum degree to be used for the BTree, the gbk file is the file
	that will be used to put sub-sequences into the BTree, and the sequence length is the
	user's desired sequence length for each sub-sequence. If so desired,the user can type 0
	for the debug level to see any diagnostic messages and 1 for the program to write a text
	file named dump that has the following line format <frequency> <DNA string>. The dump
	file contains frequency and DNA string (corresponding to the key stored) in an in-order
	traversal.

PROGRAM DESIGN AND IMPORTANT CONCEPTS:
	
	This program takes a GeneBank file that has a bunch of annotations followed by the keyword
	ORIGIN, which marks the beginning of the DNA sequences. As mentioned in the overview, this 
	program processes said GeneBank file but "cutting" the sequences into a given length, k, and 
	storing the sub-sequences into the BTree. The BTree has a root node which is stored in memory, 
	and it writes all subsequent nodes to the disk in order to keep the BTree from becoming bogged
	down by holding every node with all the sub-sequences in memory. There is metadata that is stored	 on the disk that gives information about the degree of the tree, the byte offset of the root 
	node (so it can be found), the number of nodes, the key value, and other such information.

	The GeneBank file gives the sequences in letters (A,T,C, or G), and the program converts them
	into binary (again, in order to save space/processing power). Each sub-sequence will result
	in a unique 64-bit integer value, which is used directly as the key value (which determines the 
	storage order within the BTree). GeneBankCreateBTree.java creates a BTree using the given
	gbk file to construct a BTree (which is handled within BTree.java), by parsing the gbk file 
	(using, you guessed it, ParsingFile.java). Then by calling various methods from MemoryAccess.java 	 the BTree is constructed with the root in memory, and the nodes written to, and read from, the 
	disk.  
	
	The BTree is stored as a binary data file on the disk, and the name of the BTree file is 
	determined by the name of the GeneBank file, the sequence length, and the BTree's degree. So, if
	the GeneBank file is xyz.gbk, the sequence length is k, and the degree is t, the BTree file will 	 be xyz.gbk.btree.data.k.t. 

TESTING:

	In order to test this program, we wrote a handful of test cases in order to see how our code 
	handled small BTree creation. It did well in small cases, but once we started using the .gbk
	files, we ran into a lot of issues. We spent over 50 hours debugging, but were unable to find
	exactly what was wrong. We fixed everything as we found it, and wrote new test cases when 
	applicable.

	Many of the tests included testing of the ParsingFile, the GeneBankCreateBTree using various
	cases, testing of the GeneBankSearch, and even some unit tests for the BTreeNode innerclass, and
	the BTree itself. Every time we fixed one thing, it seemed like another bug would pop up. We've
	been working on this project since before Thanksgiving break, and as many tests as we could do, 
	we still couln't get it to work like it needed to.  	

DISCUSSION:
	
	This code does not work as it is supposed to. Despite spending hours and hours on this project
	we were unable to get it to be fully operational. We spent many afternoons and evenings in the 
	CS building with tutors trying to get things working, but even the tutors were unable to help 
	at a certain point. To be honest, we felt very unprepared for this particular project, and 
	had to teach ourselves so many things with very little guidance. We had no idea how the 
	RandomAccessFile that java uses worked, and we had literally never heard of it before we were
	told that it was needed to read/write to the disk. We spent a lot of time trying to understand
	how it worked, and ultimately never had a full understanding of it and were just doing what we 
	could to try and mitigate all our problems.  
	
	It is 8:30 on Sunday night. The code is due in 3.5 hours and we're going to do our best to get
	it working, but we've been working on this non-stop for the last five days, and have had no 
	major breakthroughs. We feel like our code is very close to completion, but at this point, 
	don't know what to do or how to do it. 
