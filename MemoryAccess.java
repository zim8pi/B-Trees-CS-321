import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

import BTree.BTreeNode;



public class MemoryAccess {
private RandomAccessFile rmFile;
private int degree;
private BTree tree = new BTree();
private BTree.BTreeNode node; //any instance of BTreeNode needs to be BTree.BTreeNode

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
	
	public BTree.BTreeNode readNode() {
		for (int i = 0; i < children; i++) {
		node = tree.new BTreeNode(degree, 0);
		try {
			long data = rmFile.readLong();
			node.add(data);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			}
		}
		return node;
	}
	
	public void writeNode() {
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
	public void writeNode(BTree.BTreeNode zero){
		try {
			rmFile.writeLong(zero.getKey(0));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	//reserves space in the file for the Node
	public BTree.BTreeNode allocateNode() {
		//can use writeNode to create empty node, need to set position likely at the end of the file
		node = tree.new BTreeNode(degree, 0);
		node.add(0);
		try {
			writePosition(rmFile.length());
			writeNode(node);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return node;
	}
	//get metadata
	public int readDegree() {
		return node.getDegree();
	}
	//write metadata
	public void writeDegree(int degree) {
		node.setDegree(degree);
	}
	//get metadata
	public int readPosition() throws IOException {
		return rmFile.getFilePointer();
	}
	//write metadata
	public void writePosition() {
		try {
			rmFile.seek(pos);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
