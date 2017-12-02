
public class BTree {
	private static int degree;
	private BTreeNode root;
	private int height;
	private BTreeNode[] nodes;
	
	public BTree() {
		
	}
	
	public void bTreeSearch(BTreeNode x, long key) {
		
	}
	public void insert(long key) {
		
	}
	public void bTreeCreate() {
		
	}
	
	//had to make it public for the MemoryAccess file.
	public class BTreeNode {
		private NodeObject[] nodePairs;
		private int[] children;
		private int parent;
		private int position; //for metadata
		
		public BTreeNode(int degree, int parent) {
			BTree.degree = degree;
			this.parent = parent;
			
		}
			
		public int getDegree() {
			return degree;
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
			
	}
}
