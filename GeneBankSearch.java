/**
 * Driver class that searches through a given query
 * to match against a given B-Tree file
 * @author Karan Davis, Ally Oliphant, Cybil Lesbyn
 */
public class GeneBankSearch {

	private static boolean withCache;
	private static String btreeFile;
	private static String queryFile;
	private static long cacheSize;
	private static int debugLevel;
	private static boolean itDoes;
	private static String howMany = "Frequency = ";
	private static MemoryAccess rm;
	
	/**
	 * Deal with the arguments the user gives
	 * Assign them to the correct variables
	 * @param args
	 */
	public static void dealWithArgs(String[] args)
	{
		//there needs to be at least three arguments
		if (args.length >= 3)
		{
			//first required argument
			switch (args[0])
			{
				case "0":
					withCache = false;
					break;
				case "1":
					withCache = true;
					if (!(args.length >= 4))
					{
						//error - need the cache size if using a cache
						System.out.println("Error - need a fourth argument for the cache size");
						System.exit(1);
					}
					break;
				default:
					//error - invalid first argument
					System.out.println("Error - first argument must be 0 or 1");
					System.out.println("0: no cache");
					System.out.println("1: with cache (will need a fourth argument for cache size)");
					System.exit(1);
			}
			
			btreeFile = args[1];
			queryFile = args[2];
			
			//optional arguments
			if (args.length >= 4)
			{
				if (withCache)
				{
					//required fourth argument if using a cache
					cacheSize = Long.parseLong(args[3]);
					
					//user gave debug level
					if (args.length >= 5)
					{
						switch (args[4])
						{
							case "0":
								debugLevel = 0;
								break;
							default:
								//error - invalid fifth argument
								System.out.println("Error - fifth argument invalid");
								System.out.println("	0: output of queries printed on standard error stream and");
								System.out.println("		diagnostic, help, and status messages printed on standard error stream");
								System.exit(1);
						}
					}
				}
				else
				{
					//user gave debug level
					switch (args[3])
					{
						case "0":
							debugLevel = 0;
							break;
						default:
							//error - invalid fourth argument
							System.out.println("Error - fourth argument invalid");
							System.out.println("	0: output of queries printed on standard error stream and");
							System.out.println("		diagnostic, help, and status messages printed on standard error stream");
							System.exit(1);
					}
				}
			}
		}
		else
		{
			//error - tell user required and optional args
			System.out.println("Error - invalid number of arguments");
			System.out.println("1st args: using a cache?");
			System.out.println("	0: no");
			System.out.println("	1: yes");
			System.out.println("2nd args: B-Tree file name");
			System.out.println("3rd args: query file name");
			System.out.println("4th args: cache size");
			System.out.println("	must have if using cache");
			System.out.println("5th args: debug level (optional)");
			System.out.println("	0: output of queries printed on standard error stream and");
			System.out.println("		diagnostic, help, and status messages printed on standard error stream");
			System.out.println("Argument format:");
			System.out.println("<0/1> <btree file> <query file> [<cache size>] [<debug level>]");
			System.exit(1);
		}
	}
	
	
	public static void main(String[] args) {
		
		//get arguments for program
		dealWithArgs(args);
		rm = new MemoryAccess(btreeFile, 5);
		BTree tree = new BTree(queryFile, 5, 12);
		BTree superTree = new BTree(btreeFile, 5, 12);
		String results = "It appears " + doesItHave(superTree.getRoot(), tree.getRoot()) + " times.";
		System.out.println(results);
	}
	
	/**
	 * Counts how many times compare equals node
	 * @param node
	 * @param compare
	 * @return int
	 */
	public static int doesItHave(BTree.BTreeNode node, BTree.BTreeNode compare)
	{
		int frequency = 0;
		if(node.isLeaf())
		{
			for(int i = 0; i < compare.getChildren().length; i++)
			{
				for(int j = 0; j < node.getChildren().length; j++)
				{
					if(compare.getKey(i) == node.getKey(j))
					{
						frequency++;
					}						
				}
				return frequency;
			}
		}
		else
		{
			for(int i = 0; i < node.getChildren().length; i++)
			{
				BTree.BTreeNode temp = rm.readNode(node.getChild(i));
				frequency += doesItHave(temp, compare);
			}
		}
		return frequency;
	}

}
