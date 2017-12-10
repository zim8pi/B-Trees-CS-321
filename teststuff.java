import java.io.File;
import java.io.IOException;

public class teststuff {

	public static void main(String[] args) throws IOException {
		String fileName = "testing.txt";
		File file = new File(fileName);
		System.out.println(fileName.toString() + " " +file.exists());
	
		if (!file.exists())
		{
			file.createNewFile();
			System.out.println("create a new file");
		}		
		
		int degree = 5;
		int sequenceLength = 15;
		
		MemoryAccess ma = new MemoryAccess(fileName, degree);
		BTree tree = new BTree(fileName, degree, sequenceLength);
		long key = 00111001;
		NodeObject pair = new NodeObject(key, 2);
		tree.bTreeInsertNonFull(tree.getRoot(), pair);		
		
		BTree tree2 = ma.readTree();
		
		BTree.BTreeNode root = tree2.getRoot();
		NodeObject pair2 = root.getKeyPair(0);
		long key2 = pair2.getKey();
		long frequency2 = pair2.getFrequency();
		
		
		
		System.out.println("degree (5):"+tree2.getDegree()+"\nsequenceLength (15): "+tree2.getSequenceLength() +
				"\nroot position (0):"+tree2.getRoot().getPosition() +
				"\nroot parent (-1):"+tree2.getRoot().getParent() +
				"\nroot numKeys (1):"+tree2.getRoot().getNumKeys() +
				"\nroot pair (00111001, 2):"+key2+", "+frequency2);
		
		
	}

}
