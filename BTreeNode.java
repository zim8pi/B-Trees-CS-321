
public class BTreeNode {
	private int numKeys = 0;
	private int degree = 0;
	private NodeObject[] nodePairs;
	private int[] children;
	private int parent = -1;
	private boolean isLeaf = false;
	private int filePosition = -1;
	
	public BTreeNode(int t) {
		degree = t;
		nodePairs = new NodeObject[2*degree - 1];
		children = new int[2*degree];
	}
	
	public void addNodePair(NodeObject nodePair, int index) {
		nodePairs[index] = nodePair;
	}
	
	public NodeObject getNodePair(int index) {
		return nodePairs[index];
	}
	
	public void addChild(int child, int index) {
		children[index] = child;
	}
	
	public int getChild(int index) {
		return children[index];
	}
	
	public int getNumKeys() {
		return numKeys;
	}
	
	public void setNumKeys(int k) {
		numKeys = k;
	}
	
	public void setDegree(int t) {
		degree = t;
	}
	
	public int getDegree() {
		return degree;
	}

}
