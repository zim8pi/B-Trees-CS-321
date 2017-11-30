
public class NodeObject{
	private long key;
	private int frequency;
	
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
	public void setKey(long key) {
		this.key = key;
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
	public void setFrequency(int frequency) {
		this.frequency = frequency;
	}
	
	

}
