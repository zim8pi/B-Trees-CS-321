import java.io.File;
import java.util.Arrays;

public class BTree 
{
	private int degree;
	private int sequenceLength;
	private BTreeNode root;
	private int bTreeByteSize;  //space BTree takes in the file - holds degree, sequenceLength and root
	private int bTreeNodeByteSize;  //space each BTreeNode takes in the file - holds nodePairs, children, parent, leaf and numKeys
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
			//key was not found but not at leaf yet
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
			//TODO check for max number of key-value pairs in x
			x.addKeyPair(k.getKey(), k.getFrequency());
			rm.writeNode(x);
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
				bTreeSplitChild(x, i);
				if(k.getKey() > x.getKey(i)) {
					i = i + 1;
				}
				//TODO might have to reset child to the correct node after splitting
			}
			bTreeInsertNonFull(child, k);
		}		
	}
	
	public void bTreeInsert(BTree T, NodeObject key) 
	{
		BTreeNode r = T.getRoot();
		
		//root is full
		if (r.getNumKeys() == (2 * degree - 1)) 
		{
			//split root
			BTreeNode left = new BTreeNode(degree, r.getPosition(), numOfNodes, true);
			numOfNodes++;
			BTreeNode right = new BTreeNode(degree, r.getPosition(), numOfNodes, true);
			numOfNodes++;
			r.setLeaf(false);
						
			left.setChildren(Arrays.copyOfRange(r.getChildren(), 0, (r.getNumKeys() / 2) - 1));
			left.setAllKeyPairs(Arrays.copyOfRange(r.getAllKeyPairs(), 0, (r.getNumKeys() / 2) - 1));
			right.setChildren(Arrays.copyOfRange(r.getChildren(), r.getNumKeys() / 2, r.getNumKeys()));
			right.setAllKeyPairs(Arrays.copyOfRange(r.getAllKeyPairs(), r.getNumKeys() / 2, r.getNumKeys()));
			r.setChildren(Arrays.copyOfRange(r.getChildren(), (r.getNumKeys() / 2) - 1, r.getNumKeys() / 2));
			r.setAllKeyPairs(Arrays.copyOfRange(r.getAllKeyPairs(), r.getNumKeys() / 2, r.getNumKeys()));
						
			
			s = rm.allocateNode();
			T.root = s;
			s.setPosition(0);
			s.setLeaf(false);
			s.setNumKeys(0);
			s.setChild(r, 0);
			r.setParent(0);
			r.setPosition(1);
			bTreeSplitChild(s, 1);
			bTreeInsertNonFull(s, key);
		}
		else 
		{
			bTreeInsertNonFull(r, key);
		}
	}
	
	public void bTreeCreate(BTree T) {
		BTreeNode x = new BTreeNode(degree, -1);
		x = rm.allocateNode();
		x.setChild(x, 0);
		x.setLeaf(false);
		rm.writeNode(x);
		T.root = x;
		x.setPosition(0);
	}
	
	//This is definitely not done. There's a lot of stuff that requires accessing the child of nodes, and I'm drawing a blank on how to do that
	public void bTreeSplitChild(BTreeNode x, int i) {
		BTreeNode z = new BTreeNode();
		BTreeNode y = new BTreeNode();
		int j;
		y = x.getChild(0);
		z.setLeaf(true);
		y.setLeaf(true);
		z.setNumKeys(2*degree);
		for(j = 1; j < z.getNumKeys(); j++) {
			z.getKey(j) = y.getKey(j + degree);
		}
		if(!y.isLeaf()) {
			for (j = 1; j < degree; j++) {
				z.child(j) = y.child(j+degree);
			}
		}
		y.setNumKeys(degree-1);
		
		for (j = x.getNumKeys() +1; j > i+1; j--) {
			x.child(j+1) = x.child(j);
		}
		x.child(i+1) = z;
		for (j = x.getNumKeys(); j > i; j--) {
			x.key(j+1) = x.key(j);
		}
		x.key(i) = y.key(degree);
		x.setNumKeys(x.getNumKeys()+1);
		rm.writeNode(y);
		rm.writeNode(z);
		rm.writeNode(x);
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
		public BTreeNode(int d, int dad, int spot, boolean l) 
		{
			degree = d;
			parent = dad;
			nodePairs = new NodeObject[2 * d - 1];
			children = new int[2 * d];
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
		 * Adds a key-value pair to the node
		 * Doesn't check if max number of key-value pairs is reached
		 * That should be checked for and dealt with before calling this method
		 * @param data - the key of the key-value pair
		 * @param frequency - the frequency of the key
		 */
		public void addKeyPair(long data, int frequency) 
		{
			if (numKeys == 0) {
				//first key-value pair in node
				nodePairs[0] = new NodeObject(data, frequency);
			}
			else {
				int i = numKeys;
				//find spot for the new key-value pair
				while (i > -1 && data < nodePairs[i].getKey()) {
					nodePairs[i+1] = nodePairs[i];
					i--;
				}
				nodePairs[i] = new NodeObject(data, frequency);
			}
			numKeys++;
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
