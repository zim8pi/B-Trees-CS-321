import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;


public class MemoryAccess 
{
	private RandomAccessFile randomFile;
	private long BTreeSize;  //space BTree takes in the file - holds degree, sequenceLength and root (including root's position)
	private long BTreeNodeSize;  //space each BTreeNode takes in the file - holds nodePairs, children, parent, leaf and numKeys
	private int degree;
	private long spaceTakenInFile;
	private String file;
	
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
	
	public BTree readTree()
	{
		BTree tree = new BTree(file, degree, 0);
		BTree.BTreeNode root = new BTree.BTreeNode(degree, -2, -2, false);
		int treeDegree;
		int sequenceLength;
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
		return tree;
	}
	
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
	
	
	
//	private RandomAccessFile rmFile;
//	private int degree;
//	//private BTree tree = new BTree();
//	private BTree.BTreeNode node, parent; //any instance of BTreeNode needs to be BTree.BTreeNode	
//	private int children;
//
//	public MemoryAccess(File file, int d) 
//	{
//		degree = d;
//		children = degree - 1;
//		try 
//		{
//			rmFile = new RandomAccessFile(file, "rw");
//		} 
//		catch (FileNotFoundException e) {
//			// TODO Auto-generated catch block
//			System.out.println("Couldn't find the file");
//			System.exit(1);
//		}
//	}
//	
//	public BTree.BTreeNode readNode(int offset) 
//	{
//		for (int i = 0; i < children; i++) 
//		{
//		
//			try 
//			{
//				rmFile.seek(0);
//				long data = rmFile.readLong();
//				this.node.addKeyPair(new NodeObject(data, 2));
//			} 
//			catch (IOException e) 
//			{
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		}
//		return this.node;
//	}
//	
//	public void writeNode(BTree.BTreeNode node) throws IOException 
//	{
//		if((rmFile.length() - rmFile.getFilePointer())/8 < node.getNodeSize()){
//			rmFile.setLength(rmFile.length()*2);
//			System.out.println("hey the file size check worked maybe or crashed horribly");
//		}
//		System.out.println("Inside write Node");
//		for(int i = 0; i < node.getNumKeys(); i++) 
//		{
//			System.out.println("Inside write node in the for loop");
//			long data = node.getKey(i);
//			try 
//			{
//				rmFile.seek(0);
//				rmFile.writeLong(data);
//			} 
//			catch (IOException e) 
//			{
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		}
//	}
//
//	//reserves space in the file for the Node
//	public BTree.BTreeNode allocateNode() 
//	{
//		//can use writeNode to create empty node, need to set position likely at the end of the file
//		BTree.BTreeNode empty = new BTree.BTreeNode(degree, 0, 0, false);
//		for (int i = 0; i < empty.getNumKeys(); i++) 
//		{
//			empty.setKeyPair(0, null);
//		}
//		try 
//		{
//			writePosition(rmFile.length());
//			writeNode(empty);
//			
//		} 
//		catch (IOException e) 
//		{
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		
//		return node;
//	}
//	
//	//get metadata
//	public int readDegree() 
//	{
//		return degree;
//	}
//	
//	//write metadata
//	public void writeDegree(int degree) 
//	{
//		this.degree = degree;
//	}
//	
//	//get metadata
//	public long readPosition() throws IOException 
//	{
//		return rmFile.getFilePointer();
//	}
//	
//	//write metadata
//	public void writePosition(long pos) 
//	{
//		try 
//		{
//			rmFile.seek(pos);
//		} 
//		catch (IOException e) 
//		{
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}
}
