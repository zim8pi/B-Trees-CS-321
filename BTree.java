import java.io.File;
import java.io.IOException;
import java.util.Arrays;

public class BTree 
{
	private int degree;
	private int sequenceLength;
	private BTreeNode root;
	private int bTreeByteSize;  //space BTree takes in the file - holds degree, sequenceLength and root
	private static int bTreeNodeByteSize;  //space each BTreeNode takes in the file - holds nodePairs, children, parent, leaf and numKeys
	private int numOfNodes;  //number of nodes in the tree - used to determine position of new nodes
	private MemoryAccess rm;
	private File file;
	
	
	public BTree(File file, int d, int s) 
	{
		this.file = file;
		rm = new MemoryAccess(file, degree);
		degree = d;
		sequenceLength = s;
		bTreeByteSize = 32 * d + 5;
		bTreeNodeByteSize = 32 * d - 3;	
		numOfNodes = 0;
	}
	
	/**
	 * @return root of B-Tree
	 */
	public BTreeNode getRoot()
	{
		return root;
	}
	
	/**
	 * Search the BTree for a key
	 * @param x - position of the BTreeNode (0 for root)
	 * @param key - the key that is being searched for
	 * @return an int array - index 0 is the position of the BTreeNode and index 1 is the index of the key in the node
	 * null is returned if not found
	 */
	public int[] bTreeSearch(int x, long key) 
	{
		int[] BTreeNodeWithKeyIndex = new int[2];
		
		//read the BTreeNode at position x
		BTreeNode node = rm.readNode(x);
		
		int i = 0;
		//search BTreeNode for key
		while (i < node.getNumKeys() && key > node.getKey(i)) 
		{
			i = i+1;
		}
		
		//check if key was found
		if(i < node.getNumKeys() && key == node.getKey(i)) 
		{
			//key was found
			//return BTreeNode that has the key plus the index of the key in nodePairs
			BTreeNodeWithKeyIndex[0] = node.getPosition();  //the node with the key
			BTreeNodeWithKeyIndex[1] = i;  //the index of the key in the node
			return BTreeNodeWithKeyIndex;
		} 
		else if(node.isLeaf()) 
		{
			//key is not in the tree
			return null;
		}
		else 
		{
			//key was not found but not at leaf yet - continue searching tree
			//the disk needs to read the child of node x, and then re-search for it using x.child(i)
			//(where i is the index of the child), and recursively search through the tree
			return bTreeSearch(node.getChild(i), key);
		}		
	}
	
	/**
	 * Inserting a NodeObject into the Tree
	 * @param x - BTreeNode key is inserted into
	 * @param k - NodeObject being inserted
	 */
	public void bTreeInsertNonFull(BTreeNode x, NodeObject k) 
	{
		int i = x.getNumKeys() - 1;
		
		if(x.isLeaf()) 
		{
			//if max number of key-value pairs is not reached
			if (x.getNumKeys() < 2 * degree - 1)
			{
				x.addKeyPair(k);
				try {
					rm.writeNode(x);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			else
			{
				bTreeSplitNode(x.getPosition());
				bTreeInsertNonFull(x, k);
			}
		}
		else 
		{
			//get correct index of child
			while(i >= 0 && k.getKey() < x.getKey(i)) 
			{
				i--;
			}
			i++;
			BTreeNode child = rm.readNode(x.getChild(i));
			if(child.getNumKeys() == (2*degree - 1)) 
			{
				bTreeSplitNode(child.getPosition());
				bTreeInsertNonFull(x, k);
			}
			else
			{
				bTreeInsertNonFull(child, k);
			}
		}		
	}
	
//	//Do we even need this method????
//	public void bTreeInsert(BTree T, NodeObject key) 
//	{
//		BTreeNode r = T.getRoot();
//		
//		//root is full
//		if (r.getNumKeys() == (2 * degree - 1)) 
//		{
//			//split root
//			BTreeNode left = new BTreeNode(degree, r.getPosition(), numOfNodes, true);
//			numOfNodes++;
//			BTreeNode right = new BTreeNode(degree, r.getPosition(), numOfNodes, true);
//			numOfNodes++;
//			r.setLeaf(false);
//						
//			left.setChildren(Arrays.copyOfRange(r.getChildren(), 0, (r.getNumKeys() / 2) - 1));
//			left.setAllKeyPairs(Arrays.copyOfRange(r.getAllKeyPairs(), 0, (r.getNumKeys() / 2) - 1));
//			right.setChildren(Arrays.copyOfRange(r.getChildren(), r.getNumKeys() / 2, r.getNumKeys()));
//			right.setAllKeyPairs(Arrays.copyOfRange(r.getAllKeyPairs(), r.getNumKeys() / 2, r.getNumKeys()));
//			int[] temp = new int[2];
//			temp[0] = left.getPosition();
//			temp[1] = right.getPosition();
//			r.setChildren(temp);
////			r.setChildren(Arrays.copyOfRange(r.getChildren(), (r.getNumKeys() / 2) - 1, r.getNumKeys() / 2));
//			r.setAllKeyPairs(Arrays.copyOfRange(r.getAllKeyPairs(), (r.getNumKeys() / 2) - 1, r.getNumKeys() / 2));
////			r.setAllKeyPairs(Arrays.copyOfRange(r.getAllKeyPairs(), r.getNumKeys() / 2, r.getNumKeys()));
//						
//			
////			s = rm.allocateNode();
////			T.root = s;
////			s.setPosition(0);
////			s.setLeaf(false);
////			s.setNumKeys(0);
////			s.setChilden();
//			r.setParent(-1);
//			r.setPosition(0);
//			bTreeSplitNode(r.getPosition());
//			bTreeInsertNonFull(r.getPosition(), key);
//		}
//		else 
//		{
//			bTreeInsertNonFull(r.getPosition(), key);
//		}
//	}
	
	/**
	 * Creates an empty root node
	 */
	public void bTreeCreate() 
	{
		BTreeNode x = new BTreeNode(degree, -1, numOfNodes, true);
		rm.allocateNode();
		try {
			rm.writeNode(x);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		root = x;
		numOfNodes++;
	}
	
	/**
	 * Splitss a node
	 * @param nodePosition - position of the node being split
	 */
	public void bTreeSplitNode(int nodePosition) 
	{	
		BTreeNode splittingNode = rm.readNode(nodePosition);  //node that is being split
		BTreeNode parent;
				
		//get the new node - will be directly to the right of the split node
		BTreeNode right = new BTreeNode(degree, splittingNode.getParent(), numOfNodes, true);
		numOfNodes++;		
		right.setChildren(Arrays.copyOfRange(splittingNode.getChildren(), (int) Math.floor(splittingNode.getNumKeys() / 2) + 1, splittingNode.getNumKeys()));
		right.setAllKeyPairs(Arrays.copyOfRange(splittingNode.getAllKeyPairs(), (int) Math.floor(splittingNode.getNumKeys() / 2) + 1, splittingNode.getNumKeys()));
		right.setNumKeys(splittingNode.getNumKeys() - ((int) Math.floor(splittingNode.getNumKeys() / 2) + 1));	
		
		//if right has children (right is not a leaf)
		if (right.getChild(0) != -2)
		{
			right.setLeaf(false);
		}
		
		//if split node is not the root
		if (splittingNode.getParent() != -1)
		{
			parent = rm.readNode(splittingNode.getParent());  //node's parent
			
			//if parent is full
			if (parent.getNumKeys() >= (2 * degree) - 1)
			{
				bTreeSplitNode(parent.getPosition());
			}
			else
			{
				int addSpot = parent.addKeyPair(splittingNode.getKeyPair((int) Math.floor(splittingNode.getNumKeys() / 2)));
				parent.addChild(right.getPosition(), addSpot + 1);
			}
		}
		else
		{
			parent = new BTreeNode(degree, -1, numOfNodes, false);
			numOfNodes++;
			root = parent;
			splittingNode.setParent(parent.getPosition());
			right.setParent(parent.getPosition());
			parent.setChildren(new int[]{splittingNode.getPosition(), right.getPosition()});
			parent.addKeyPair(splittingNode.getKeyPair((int) Math.floor(splittingNode.getNumKeys() / 2)));
		}
		
		//update split node
		splittingNode.setChildren(Arrays.copyOfRange(splittingNode.getChildren(), 0, (int) Math.floor(splittingNode.getNumKeys() / 2)));
		splittingNode.setAllKeyPairs(Arrays.copyOfRange(splittingNode.getAllKeyPairs(), 0, (int) Math.floor(splittingNode.getNumKeys() / 2)));		
		splittingNode.setNumKeys((int) Math.floor(splittingNode.getNumKeys() / 2));
		

		try {
			rm.writeNode(right);
			rm.writeNode(parent);
			rm.writeNode(splittingNode);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	
	
	//had to make it public for the MemoryAccess file.
	public static class BTreeNode 
	{
		private NodeObject[] nodePairs;  //holds the key-value pairs
		private int[] children;  //holds the node's children's position
		private int parent;  //holds the node's parent's position
		private int degree;
		private int numKeys;  //current number of key-value pairs in the node
		private int position;  //node's position
		private boolean leaf;  //whether the node is a leaf or not
		
		/**
		 * BTreeNode constructor
		 * @param d - degree of the tree
		 * @param dad - position of the parent
		 * @param spot - position of the node
		 * @param l - if node is a leaf
		 */
		public int getNodeSize(){
			return bTreeNodeByteSize;
		}
		public BTreeNode(int d, int dad, int spot, boolean l) 
		{
			degree = d;
			parent = dad;
			nodePairs = new NodeObject[9];
			children = new int[2 * d];
			for (int i = 0; i < 2 * d; i++)
			{
				children[i] = -2;
			}
			numKeys = 0;
			position = spot;
			leaf = l;
		}
		
		/**
		 * @return true is node is a leaf, false otherwise
		 */
		public boolean isLeaf() 
		{
			return leaf;
		}
		
		/**
		 * Get one of the node's children
		 * @param i - int
		 * @return the child at index i
		 */
		public int getChild(int i) 
		{
			return children[i];
		}
		
		/**
		 * Set one of the node's children
		 * @param x - new position of child
		 * @param i - index of child to be set
		 */
		public void setChild(int x, int i) 
		{
			children[i] = x;
		}
		
		/**
		 * Adding a new child at index i
		 * Assuming that the node has space for more children
		 * @param x - position of new child
		 * @param i - index of new child
		 */
		public void addChild(int x, int i) 
		{
			//move other children over
			for (int j = 2 * degree; j > i; j--)
			{
				children[j] = children[j-1];
			}
			children[i] = x;
		}
		
		/**
		 * Get all of the node's children
		 * @return children - int[]
		 */
		public int[] getChildren() 
		{
			return children;
		}
		
		/**
		 * Set all of the node's children
		 * @param c - new array of children
		 */
		public void setChildren(int[] c) 
		{
			children = c;
		}
		
		/**
		 * Set node's leaf value
		 * @param leafy
		 */
		public void setLeaf(boolean leafy) 
		{
			leaf = leafy;
		}
		
		/**
		 * Get position of the node's parent
		 * @return parent - int
		 */
		public int getParent() 
		{
			return parent;
		}
		
		/**
		 * Set the position of the node's parent
		 * @param dad - new position of parent
		 */
		public void setParent(int dad) 
		{
			parent = dad;
		}
		
		/**
		 * Get one key-value pair of the node at index i
		 * @param i
		 * @return key-value pair - NodeObject
		 */
		public NodeObject getKeyPair(int i) 
		{
			return nodePairs[i];
		}
		
		/**
		 * Set one key-value pair of the node at index i
		 * @param i - index being set
		 * @param pair - new key-value pair
		 */
		public void setKeyPair(int i, NodeObject pair) 
		{
			nodePairs[i] = pair;
		}
		
		/**
		 * Adds a key-value pair to the node
		 * Doesn't check if max number of key-value pairs is reached
		 * That should be checked for and dealt with before calling this method
		 * @param data - the key of the key-value pair
		 * @param frequency - the frequency of the key
		 */
		public int addKeyPair(NodeObject pair) 
		{
			int i = 0;  //spot key-value pair is added
			if (numKeys == 0) {
				//first key-value pair in node
				nodePairs[0] = pair;
			}
			else {
				i = numKeys;
				//find spot for the new key-value pair
				//TODO might need to compare differently
				while (i > -1 && pair.getKey() < nodePairs[i].getKey()) {
					nodePairs[i+1] = nodePairs[i];
					i--;
				}
				nodePairs[i] = pair;
			}
			numKeys++;
			return i;
		}
		
		/**
		 * Get all key-value pair of the node at index i
		 * @return key-value pairs - NodeObject[]
		 */
		public NodeObject[] getAllKeyPairs() 
		{
			return nodePairs;
		}
		
		/**
		 * Set all key-value pairs in node
		 * @param pairs
		 */
		public void setAllKeyPairs(NodeObject[] pairs) 
		{
			nodePairs = pairs;
		}
		
		/**
		 * Get the key of one of the node's key-value pair
		 * @param child - index of child
		 * @return key - long
		 */
		public long getKey(int child) 
		{
			return nodePairs[child].getKey();
		}
		
		/**
		 * Get the total number of key-value pairs in the node
		 * @return numKeys - int
		 */
		public int getNumKeys() 
		{
			return numKeys;
		}
		
		/**
		 * Set the number of key-value pairs in the node
		 * @param k
		 */
		public void setNumKeys(int k) 
		{
			numKeys = k;
		}
		
		/**
		 * Get the position of the node
		 * @return position - int
		 */
		public int getPosition() 
		{
			return position;
		}
		
		/**
		 * Set the position of the node
		 * @param pos - new position
		 */
		public void setPosition(int pos) 
		{
			position = pos;
		}			
	}
}
