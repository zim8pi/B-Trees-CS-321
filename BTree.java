import java.io.File;

public class BTree {
	private int degree;
	private int sequenceLength;
	private BTreeNode root;
	private MemoryAccess rm;
	private File file;
	

	
	public BTree(File file) {
		this.file = file;
		rm = new MemoryAccess(file, degree);
		
	}
	
	public NodeObject bTreeSearch(BTreeNode x, long key) {
		int i = 1;
		while (i <= x.getNumKeys() && key > x.getKey(i)) {
			i = i+1;
		}
		
		if(i < x.getNumKeys() && key == x.getKey(i)) {
			return x.getObject(i);
		} else if(x.isLeaf()) {
			return null;
		}else {
			//not sure how to implement this... but, the disk needs to read the child of node x, and then re-search for it using 
			// x.child(i) (where i is the index of the child), and use the key of the child to recursively search through the tree
			rm.readNode(x.getChild(i));
			return bTreeSearch(x.getChild(i), x.getChild(i).getKey(i));
		}
		
	}
	public void bTreeInsertNonFull(BTreeNode x, long k) {
		int i = x.getNumKeys();
		if(x.isLeaf()) {
			while(i >= 1 && k < x.getKey(i)) {
				x.getObject(i+1).setKey(x.getObject(i).getKey());
				i--;
			}
			x.getObject(i+1).setKey(k);
			x.setNumKeys(x.getNumKeys() + 1);
			rm.writeNode(x);
		}else {
			while(i >= 1 && k < x.getKey(i)) {
				i--;
			}
			i++;
			rm.readNode(x.getChild(i));
			if(x.getChild(i).getNumKeys() == (2*degree - 1)) {
				bTreeSplitChild(x, i);
				if(k > x.getKey(i)) {
					i = i+1;
				}
			}
			bTreeInsertNonFull(x.getChild(i), k);
		}
		
	}
	
	public void bTreeInsert(BTree T, long key) {
		BTreeNode r = new BTreeNode(degree, null);
		BTreeNode s = new BTreeNode(degree, null);
		r = T.root;
		r.setPosition(0);
		if (r.getNumKeys() == (2*degree - 1)) {
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
		}else {
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
	
	public long convertOffset() {
		return 40*degree + 13;
	}
	
	//had to make it public for the MemoryAccess file.
	public static class BTreeNode {
		private NodeObject[] nodePairs;  //holds the key-value pairs
		private int[] children;
		private int parent;
		private int degree;
		private int numKeys;
		private int maxPairs;
		private int maxChildren;
		private int position; //for metadata
		private boolean leaf;
		
		public BTreeNode(int degree, int parent) {
			maxChildren = 2*degree;
			maxPairs = 2*degree-1;
			this.degree = degree;
			this.parent = parent;
		}
			
		public int getDegree() {
			return degree;
		}
		//I added this and the setChild because I think we need to be able to acces the children to each node, but 
		//again, I'm not too sure how to implement it
		public BTreeNode getChild(int i) {
			return children[i];
		}
		
		public void setChild(BTreeNode x, int i) {
			children[i] = x;
		}
		
		public int getChildren() {
			return children.length;
		}
		
		public void setDegree(int degree) {
			this.degree = degree;
		}
		
		public void setLeaf(boolean leafy) {
			leaf = leafy;
		}
		
		public boolean isLeaf() {
			if (children.length == 0)
				return true;
			else
				return false;
		}
		
		public void add(long data) {
			if (nodePairs.length == 0) {
				nodePairs[0].setKey(data);
			}
			else {
				int i = nodePairs.length;
				while (data < nodePairs[i].getKey()) {
					nodePairs[i+1] = nodePairs[i];
					i--;
				}
				nodePairs[i].setKey(data);
			}
			numKeys++;
		}
		
		public int getParent() {
			return parent;
		}
		
		public void setParent(int dad) {
			parent = dad;
		}
		
		public NodeObject getObject(int i) {
			return nodePairs[i];
		}
		
		public long getKey(int child) {
			return nodePairs[child].getKey();
		}
		
		public int getNumKeys() {
			return numKeys;
		}
		
		public void setNumKeys(int k) {
			numKeys = k;
		}
		
		public int getMaxChildren() {
			return maxChildren;
		}
		
		public int getMaxPairs() {
			return maxPairs;
		}
		
		public int getPosition() {
			return position;
		}
		
		public void setPosition(int pos) {
			position = pos;
		}
			
	}
}
