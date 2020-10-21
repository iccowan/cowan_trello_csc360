package trello;

/**
 * Component Superclass
 *
 */
public class Component
{

	private String desc;
	private int maxNum;
	
	/**
	 * Default constructor
	 */
	public Component() {}

	/**
	 * @param desc   - The description of the component
	 * @param maxNum - The max number of these components
	 */
	public Component(String desc, int maxNum)
	{
		this.desc = desc;
		this.maxNum = maxNum;
	}

	/**
	 * @return the desc
	 */
	public String getDesc()
	{
		return desc;
	}

	/**
	 * @param desc the desc to set
	 */
	public void setDesc(String desc)
	{
		this.desc = desc;
	}

	/**
	 * @return the maxNum
	 */
	public int getMaxNum()
	{
		return maxNum;
	}

	/**
	 * @param maxNum the maxNum to set
	 */
	public void setMaxNum(int maxNum)
	{
		this.maxNum = maxNum;
	}
	
	public boolean equals(Component that)
	{
		// Make sure the descriptions are the same
		if (this.desc != that.desc)
			return false;
		
		// The same
		return true;
	}
	
	/**
	 * @return String - The string of the file where the serialized object lives
	 */
	public String serializeToXML()
	{
		return XMLSerializer.<Component>serializeToXML(this);
	}
	
	/**
	 * @param objectFileName - File name where the object lives that we're going to deserialize
	 * @return Component - The list object that we want to return
	 */
	public static Component deserializeFromXML(String objectFileName)
	{
		return XMLSerializer.<Component>deserializeFromXML(objectFileName);
	}

}
