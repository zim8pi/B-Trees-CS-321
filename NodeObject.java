/**
 * Class that holds a key-value pair for a B-Tree
 * @author Karan Davis, Ally Oliphant, Cybil Lesbyn
 */
public class NodeObject{
	private long key;
	private int frequency;
	
	/**
	 * Default constructor
	 * @param k - key value
	 * @param f - frequency value
	 */
	public NodeObject (long k, int f)
	{
		setKey(k);
		setFrequency(f);
	}
	
	/**
	 * @return the key
	 */
	public long getKey() {
		return key;
	}
	/**
	 * @param key the key to set
	 */
	public void setKey(long k) {
		key = k;
	}
	/**
	 * @return the frequency
	 */
	public int getFrequency() {
		return frequency;
	}
	/**
	 * @param frequency the frequency to set
	 */
	public void setFrequency(int f) {
		frequency = f;
	}
}
