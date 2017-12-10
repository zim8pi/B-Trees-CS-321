import java.io.File;
import java.io.IOException;

public class teststuff {

	public static void main(String[] args) throws IOException {
		File fileName = new File("testing.txt");
		System.out.println(fileName.toString() + "  " +fileName.exists());
	
		if (!fileName.exists())
		{
			fileName.createNewFile();
			System.out.println("created a new file");
		}		
		
		MemoryAccess ma = new MemoryAccess(fileName, 5);
		BTree tree = new BTree(fileName, 5, 15);
		
		tree.bTreeCreate();
		tree.bTreeInsertNonFull(new BTree.BTreeNode(5, -1, 0, true), new NodeObject(00111001, 3));
		
		BTree.BTreeNode node = ma.readNode(0);
		System.out.println("node parent (-1): "+node.getParent() +"\nnode leaf (true): "+node.isLeaf()
				+"node numKeys (1): "+node.getNumKeys());

	}

}
