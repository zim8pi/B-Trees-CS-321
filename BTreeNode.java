
public class BTreeNode {
	private int numKeys;
	private int degree;
	private NodeObject[] nodePairs;
	private int[] children;
	private int parent;
	
	public int numKeys() {
		return 0;
	}
	
	public boolean isLeaf() {
		return children == null;
	}

}
