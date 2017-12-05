
public class BTree {
	private static int degree;
	private BTreeNode root;
	private int height;
	private BTreeNode[] nodes;
	
	public BTree() {
		
	}
	
	public void bTreeSearch(BTreeNode x, long key) {
		int i = 1;
		while (i <= x.getNumKeys() && k > x.getKey(i)) {
			i = i+1;
		}
		
		if(i < x.getNumKeys() && k == x.getKey(i)) {
			return x, i;
		} else if(x.isLeaf()) {
			return null;
		}else {
			//not sure how to implement this... but, the disk needs to read the child of node x, and then re-search for it using 
			// x.child(i) (where i is the index of the child), and use the key of the child to recursively search through the tree
			readNode(x.children(i));
			return bTreeSearch(x.children(i), x.children.getKey())
		}
		
	}
	public void bTreeInsertNonFull(BTreeNode x, long k) {
		int i = x.getNumKeys();
		if(x.isLeaf()) {
			while(i >= 1 && k < x.key(i)) {
				x.key(i+1) = x.key(i);
				i--;
			}
			x.setKey(i+1, k);
			x.setNumKeys(x.getNumKeys() + 1);
			writeNode(x);
		}else {
			while(i >= 1 && k < x.key(i)) {
				i--;
			}
			i++;
			readNode(x.getChild(i));
			if(x.getChild(i).getNumKeys() == (2*degree - 1)) {
				bTreeSplitChild(x, i);
				if(k > x.key(i)) {
					i = i+1;
				}
			}
			bTreeInsertNonFull(x.child(i), k);
		}
		
	}
	
	public void bTreeInsert(BTree T, long key) {
		BTreeNode r = new BTreeNode();
		BTreeNode s = new BTreeNode();
		r = T.root;
		if (r.getNumKeys() == (2*degree - 1)) {
			s = allocateNode();
			T.root = s;
			s.isLeaf() = false;
			s.setNumKeys(0);
			s.addChild(r);
			bTreeSplitChild(s, 1);
			bTreeInsertNonFull(s, s.key);
		}else {
			bTreeInsertNonFull(r, r.key);
		}
	}
	
	public void bTreeCreate() {
		BTree T = new BTree();
		BTreeNode x = new BTreeNode();
		x = allocateNode();
		x.children() = 0;
		x.isLeaf();
		writeNode(x);
		T.root = x;
	}
	//This is definitely not done. There's a lot of stuff that requires accessing the child of nodes, and I'm drawing a blank on how to do that
	public void bTreeSplitChild(BTreeNode x, int i) {
		BTreeNode z = new BTreeNode();
		BTreeNode y = new BTreeNode();
		int j;
		y = x.children();
		z.isLeaf() = y.isLeaf();
		z.setNumKeys() = degree - 1;
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
		writeNode(y);
		writeNode(z);
		writeNode(x);
	}
	
	//had to make it public for the MemoryAccess file.
	public class BTreeNode {
		private NodeObject[] nodePairs;
		private int[] children;
		private int parent;
		private int numKeys;
		private int position; //for metadata
		
		public BTreeNode(int degree, int parent) {
			BTree.degree = degree;
			this.parent = parent;
		}
			
		public int getDegree() {
			return degree;
		}
		//I added this and the setChild because I think we need to be able to acces the children to each node, but 
		//again, I'm not too sure how to implement it
		public void getChild(int i) {
			return children[i];
		}
		
		public void setChild(BTreeNode x, int i) {
			children[i] = x;
		}
		
		public void setDegree(int degree) {
			BTree.degree = degree;
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
		}
		
		public int getParent() {
			return parent;
		}
		
		public long getKey(int child) {
			return nodePairs[child].getKey();
		}
		
		public void getNumKeys() {
			return numKeys;
		}
		
		public void setNumKeys(int k) {
			numKeys = k;
		}
			
	}
}
