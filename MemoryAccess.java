import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;



public class MemoryAccess {
private RandomAccessFile rmFile;
private int degree;
//private BTree tree = new BTree();
private BTree.BTreeNode node, parent; //any instance of BTreeNode needs to be BTree.BTreeNode

private int children;

	public MemoryAccess(File file, int degree) {
		this.degree = degree;
		children = degree -1;
		try {
			rmFile = new RandomAccessFile(file, "rw");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			System.out.println("Couldn't find the file");
			System.exit(1);
		}
	}
	
	public BTree.BTreeNode readNode(int offset) {
		for (int i = 0; i < children; i++) {
		
		try {
			long data = rmFile.readLong();
			this.node.addKeyPair(data, rmFile.readInt());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			}
		}
		return this.node;
	}
	
	public void writeNode(BTree.BTreeNode node) {
		for(int i = 0; i < children; i++) {
			long data = node.getKey(i);
			try {
				rmFile.writeLong(data);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	//reserves space in the file for the Node
	public BTree.BTreeNode allocateNode() {
		//can use writeNode to create empty node, need to set position likely at the end of the file
		BTree.BTreeNode empty = new BTree.BTreeNode(degree, 0, 0, false);
		for (int i = 0; i < empty.getNumKeys(); i++) {
			empty.setKeyPair(0, null);
		}
		try {
			writePosition(rmFile.length());
			writeNode(empty);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return node;
	}
	//get metadata
	public int readDegree() {
		return degree;
	}
	//write metadata
	public void writeDegree(int degree) {
		this.degree = degree;
	}
	//get metadata
	public long readPosition() throws IOException {
		return rmFile.getFilePointer();
	}
	//write metadata
	public void writePosition(long pos) {
		try {
			rmFile.seek(pos);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
