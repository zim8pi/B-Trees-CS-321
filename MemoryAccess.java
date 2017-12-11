import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * Class that writes to and reads from a file
 * @author Karan Davis, Ally Oliphant, Cybil Lesbyn
 */
public class MemoryAccess 
{
	private RandomAccessFile randomFile;
	private long BTreeSize;  //space BTree takes in the file - holds degree, sequenceLength and root (including root's position)
	private long BTreeNodeSize;  //space each BTreeNode takes in the file - holds nodePairs, children, parent, leaf and numKeys
	private int degree;
	private long spaceTakenInFile;
	private String file;
	
	/**
	 * Default constructor
	 * @param fileName
	 * @param d - the degree
	 */
	public MemoryAccess(String fileName, int d)
	{
		try 
		{
			file = fileName;
			randomFile = new RandomAccessFile(fileName, "rw");
			BTreeSize = (32 * d) + 9;
			BTreeNodeSize = (32 * d) - 3;
			degree = d;
			spaceTakenInFile = 0;
			randomFile.setLength(BTreeSize + BTreeNodeSize);
		} 
		catch (IOException e) {
			System.out.println("MemoryAccess constuctor - file not found");
			e.printStackTrace();
		}		
	}
	
	/**
	 * Write the BTree meta-data in the order of degree, sequencyLength and root
	 * @param degree - int
	 * @param sequenceLength - int
	 * @param root - BTreeNode
	 */
	public void writeBTreeData(int degree, int sequenceLength, BTree.BTreeNode root)
	{
		try 
		{
			if (spaceTakenInFile == randomFile.length())
			{
				increaseFile(spaceTakenInFile + BTreeSize);
			}
			
			spaceTakenInFile += BTreeSize;
			
			randomFile.seek(0);
			randomFile.writeInt(degree);
			randomFile.writeInt(sequenceLength);
			randomFile.seek(4);
			
			//write in the root
			//write in key-value pairs
			for (int i = 0; i < (2 * degree) - 1; i++)
			{
				NodeObject pair = root.getKeyPair(i);
				if (pair != null)
				{
					randomFile.writeLong(pair.getKey());
					randomFile.writeInt(pair.getFrequency());
				}
				else
				{						
					randomFile.writeLong(0);
					randomFile.writeInt(0);
				}
			}			
			randomFile.writeInt(root.getParent());			
			//write in children positions
			for (int i = 0; i < 2 * degree; i++)
			{
				randomFile.writeInt(root.getChild(i));
			}			
			randomFile.writeBoolean(root.isLeaf());
			randomFile.writeInt(root.getNumKeys());	
			randomFile.writeInt(root.getPosition());
		} 
		catch (IOException e) {
			System.out.println("MemoryAccess - writeBTreeData");
			e.printStackTrace();
		}
		
	}
	
	/**
	 * Write a BTreeNode in the order of key-value pairs (key then frequency), parent, children, leaf and numKeys
	 * @param node - BTreeNode
	 * @param nodePosition - int
	 */
	public void writeNode(BTree.BTreeNode node, int nodePosition)
	{
		try 
		{
			if (spaceTakenInFile == randomFile.length())
			{
				increaseFile(spaceTakenInFile + BTreeNodeSize);
			}
			
			spaceTakenInFile += BTreeNodeSize;
			
			long offSet = BTreeSize + (BTreeNodeSize * nodePosition);
			randomFile.seek(offSet);			
			//write in key-value pairs
			for (int i = 0; i < (2 * degree) - 1; i++)
			{
				NodeObject pair = node.getKeyPair(i);
				randomFile.writeLong(pair.getKey());
				randomFile.writeInt(pair.getFrequency());
			}			
			randomFile.writeInt(node.getParent());			
			//write in children positions
			for (int i = 0; i < 2 * degree; i++)
			{
				randomFile.writeInt(node.getChild(i));
			}
			randomFile.writeBoolean(node.isLeaf());
			randomFile.writeInt(node.getNumKeys());		
		} 
		catch (IOException e) 
		{
			System.out.println("MemoryAccess - writeNode");
			e.printStackTrace();
		}
	}
	
	/**
	 * Read a BTreeNode in the order of key-value pairs (key then frequency), parent, children, leaf and numKeys 
	 * @param nodePosition - int
	 * @return the node read in - BTreeNode
	 */
	public BTree.BTreeNode readNode(int nodePosition)
	{
		BTree.BTreeNode node = new BTree.BTreeNode(degree, -2, nodePosition, false);
		NodeObject[] pairs = new NodeObject[2 * degree - 1];
		long key;
		int frequency;
		int parent;
		int[] children = new int[2 * degree];
		boolean leaf;
		int numKeys;
		
		try 
		{
			long offSet = BTreeSize + (BTreeNodeSize * nodePosition);
			randomFile.seek(offSet);			
			//read in key-value pairs
			System.out.println("degree in readNode:"+degree);
			for (int i = 0; i < (2 * degree) - 1; i++)
			{
				key = randomFile.readLong();
				frequency = randomFile.readInt();
				pairs[i] = new NodeObject(key, frequency);
				System.out.println(pairs[i].getFrequency() +" "+frequency+ " " + pairs[i].getKey()+ " " +key);
			}			
			parent = randomFile.readInt();			
			//read in children positions
			for (int i = 0; i < 2 * degree; i++)
			{
				children[i] = randomFile.readInt();
			}			
			leaf = randomFile.readBoolean();
			numKeys = randomFile.readInt();		
			
			node = new BTree.BTreeNode(degree, parent, nodePosition, leaf);
			node.setNumKeys(numKeys);
			node.setAllKeyPairs(pairs);
			node.setChildren(children);
		} 
		catch (IOException e) 
		{
			System.out.println("MemoryAccess - readNode");
			e.printStackTrace();
		}
		return node;
	}
	
	/**
	 * Read the B-Tree's meta-data
	 * @return BTree
	 */
	public BTree readTree()
	{
		BTree.BTreeNode root = new BTree.BTreeNode(degree, -2, -2, false);
		int treeDegree;
		int sequenceLength = 0;
		NodeObject[] pairs = new NodeObject[2 * degree - 1];
		long key;
		int frequency;
		int parent;
		int[] children = new int[2 * degree];
		boolean leaf;
		int numKeys;
		int rootPosition;
		
		try 
		{
			randomFile.seek(0);	
			treeDegree = randomFile.readInt();
			System.out.println("degree:"+degree);
			sequenceLength = randomFile.readInt();
			System.out.println("sequenceLength:"+sequenceLength);			
						
			//read root
			//read in key-value pairs
			System.out.println("degree in readNode:"+degree);
			for (int i = 0; i < (2 * degree) - 1; i++)
			{
				key = randomFile.readLong();
				System.out.println("key at "+i+":"+key);
				frequency = randomFile.readInt();
				System.out.println("frequency at "+i+":"+frequency);
				pairs[i] = new NodeObject(key, frequency);
				System.out.println(pairs[i].getFrequency() +" "+frequency+ " " + pairs[i].getKey()+ " " +key);
			}			
			parent = randomFile.readInt();			
			//read in children positions
			for (int i = 0; i < 2 * degree; i++)
			{
				children[i] = randomFile.readInt();
				System.out.println("child at "+i+":"+children[i]);
			}			
			leaf = randomFile.readBoolean();
			System.out.println("leaf:"+leaf);
			numKeys = randomFile.readInt();	
			System.out.println("numKeys:"+numKeys);
			rootPosition = randomFile.readInt();
			System.out.println("rootPosition:"+rootPosition);
			
			root = new BTree.BTreeNode(degree, parent, rootPosition, leaf);
			root.setNumKeys(numKeys);
			root.setAllKeyPairs(pairs);
			root.setChildren(children);
		} 
		catch (IOException e) 
		{
			System.out.println("MemoryAccess - readNode");
			e.printStackTrace();
		}
		BTree tree = new BTree(file, degree, sequenceLength);
		tree.setRoot(root);
		return tree;
	}
	
	/**
	 * Increase the length of the file if more space is needed
	 * @param newLength
	 */
	private void increaseFile(long newLength)
	{
		try 
		{
			randomFile.setLength(newLength);
		} 
		catch (IOException e) 
		{
			System.out.println("MemoryAccess - increaseFile");
			e.printStackTrace();
		}
	}
}