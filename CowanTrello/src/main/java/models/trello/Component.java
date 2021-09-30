package models.trello;

import java.io.Serializable;
import java.util.ArrayList;

import models.trello.structures.CloneableInterface;

/**
 * Component Superclass
 *
 */
public class Component implements Serializable, CloneableInterface
{

	private static final long serialVersionUID = -6480996884749251103L;

	private String desc;
	private int maxNum;

	/**
	 * Default constructor
	 */
	public Component()
	{
	}

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

	/**
	 * @return Component
	 */
	@Override
	public Component clone()
	{
		Component newCom = new Component(this.desc, this.maxNum);

		return newCom;
	}

	/**
	 * @param thatObj - The object to compare for equality
	 * @return boolean - Whether or not the objects are equal
	 */
	@Override
	public boolean equals(Object thatObj)
	{
		Component that = (Component) thatObj;
		if (that == null)
			return false;

		// Make sure the descriptions are the same
		if (!this.desc.equals(that.desc))
			return false;

		// The same
		return true;
	}

	/**
	 * @Overrides the Object toString() method
	 */
	@Override
	public String toString()
	{
		return this.desc;
	}

	/**
	 * @param all - Array list of all objects to serialize
	 */
	public static void serializeToXML(ArrayList<Component> all)
	{
		XMLSerializer.<Component>serializeToXML(all, "Component");
	}

	/**
	 * @return ArrayList<Component> - The array list of objects that we want to
	 *         return
	 */
	public static ArrayList<Component> deserializeFromXML()
	{
		return XMLSerializer.<Component>deserializeFromXML("Component");
	}

}
