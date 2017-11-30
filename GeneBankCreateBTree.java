
public class GeneBankCreateBTree {

	private static boolean withCache;
	private static int degree;
	private static String gdkFile;
	private static int sequenceLength;
	private static long cacheSize;
	private static int debugLevel = 0;  //default value is 0
	
	/**
	 * Deal with the arguments the user gives
	 * Assign them to the correct variables
	 * @param args
	 */
	public static void dealWithArgs(String[] args)
	{
		//there needs to be at least 4 arguments
		if (args.length >= 4)
		{
			//first required argument
			switch (args[0])
			{
				case "0":
					withCache = false;
					break;
				case "1":
					withCache = true;
					if (!(args.length >= 5))
					{
						//error - need the cache size if using a cache
						System.out.println("Error - need a fifth argument for the cache size");
						System.exit(1);
					}
					break;
				default:
					//error - invalid first argument
					System.out.println("Error - first argument must be 0 or 1");
					System.out.println("0: no cache");
					System.out.println("1: with cache (will need a fifth argument for cache size)");
					System.exit(1);
			}
			
			//second required argument
			degree = Integer.parseInt(args[1]);
			if (degree == 0)
			{
				//TODO choose the optimum degree based on a disk block size of
				//4096 bytes and the size of your B-Tree node on disk
				//TODO what's the size of our B-Tree node on disk?????
			}
			
			//third required argument
			gdkFile = args[2];
			
			//fourth required argument
			sequenceLength = Integer.parseInt(args[3]);
			if (sequenceLength < 1 || sequenceLength > 31)
			{
				//error - sequenceLength is not in the required range
				System.out.println("Error - fourth argument (sequence length) must be between 1 and 31 (inclusive)");
				System.exit(1);
			}
			
			//optional arguments
			if (args.length >= 5)
			{
				if (withCache)
				{
					//required fifth argument if using a cache
					cacheSize = Long.parseLong(args[4]);
					
					//user gave debug level
					if (args.length >= 6)
					{
						switch (args[5])
						{
							case "0":
								debugLevel = 0;
								break;
							case "1":
								debugLevel = 1;
								break;
							default:
								//error - invalid sixth arguments
								System.out.println("Error - sixth argument must be 0 or 1");
								System.out.println("0: diagnostic, help, and status messages printed on standard error stream");
								System.out.println("1: write a text file named 'dump' with DNA string and its frequency in an in-order traversal");
								System.exit(1);
						}
					}
				}
				else
				{
					//user gave debug level
					switch (args[4])
					{
						case "0":
							debugLevel = 0;
							break;
						case "1":
							debugLevel = 1;
							break;
						default:
							//error - invalid fifth argument
							System.out.println("Error - fifth argument must be 0 or 1");
							System.out.println("0: diagnostic, help, and status messages printed on standard error stream");
							System.out.println("1: write a text file named 'dump' with DNA string and its frequency in an in-order traversal");
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
			System.out.println("2nd args: degree (t)");
			System.out.println("	0 if you want the optimum degree for a disk block size of 4096 bytes");
			System.out.println("3rd args: gdk file name");
			System.out.println("4th args: sequence length");
			System.out.println("	must be between 1 and 31 (inclusive)");
			System.out.println("5th args: cache size");
			System.out.println("	must have if using cache");
			System.out.println("6th args: debug level (optional)");
			System.out.println("	0: diagnostic, help, and status messages printed on standard error stream (default)");
			System.out.println("	1: write a text file named 'dump' with DNA string and its frequency in an in-order traversal");
			System.out.println("Argument format:");
			System.out.println("<0/1> <degree> <gdk file> <sequence length> [<cache size>] [<debug level>]");
			System.exit(1);
		}
	}
	
	
	public static void main(String[] args) {
		
		//get arguments for program
		dealWithArgs(args);		
	}
}
