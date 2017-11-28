
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
	
	private class BTreeNode {
		private NodeObject[] nodePairs;
		private int[] children;
		private int parent;
		
		public BTreeNode(int degree, int parent) {
			BTree.degree = degree;
			this.parent = parent;
			
		}
			
		public int getDegree() {
			return degree;
		}
		
		public boolean isLeaf() {
			if (children.length == 0)
				return true;
			else
				return false;
		}
		public void add(int key) {
			if (nodePairs.length == 0) {
				nodePairs[0].setKey(key);
			}
			else {
				int i = nodePairs.length;
				while (key < nodePairs[i].getKey()) {
					nodePairs[i+1] = nodePairs[i];
					i--;
				}
				nodePairs[i].setKey(key);
			}
		}
		
		public int getParent() {
			return parent;
		}
			
	}
}
