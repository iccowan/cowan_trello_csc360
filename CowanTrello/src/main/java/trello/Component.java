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

}
