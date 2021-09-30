package models.trello;

import java.io.Serializable;
import java.util.ArrayList;

import models.trello.structures.CloneableInterface;

/**
 * Label Class
 *
 */
public class Label implements Serializable, CloneableInterface
{

	private static final long serialVersionUID = 6328716265330861027L;

	private String text;

	/**
	 * Default constructor
	 */
	public Label()
	{
	}

	/**
	 * @param text
	 */
	public Label(String text)
	{
		this.text = text;
	}

	/**
	 * @return Label
	 */
	@Override
	public Label clone()
	{
		Label newLabel = new Label(this.text);

		return newLabel;
	}

	/**
	 * @param thatObj - The object to compare for equality
	 * @return boolean - Whether or not the objects are equal
	 */
	@Override
	public boolean equals(Object thatObj)
	{
		Label that = (Label) thatObj;
		if (that == null)
			return false;

		return this.text.equals(that.text);
	}

	/**
	 * @return the text
	 */
	public String getText()
	{
		return text;
	}

	/**
	 * @param text the text to set
	 */
	public void setText(String text)
	{
		this.text = text;
	}

	/**
	 * @Overrides the Object toString() method
	 */
	@Override
	public String toString()
	{
		return this.text;
	}

	/**
	 * @param all - Array list of all objects to serialize
	 */
	public static void serializeToXML(ArrayList<Label> all)
	{
		XMLSerializer.<Label>serializeToXML(all, "Label");
	}

	/**
	 * @return ArrayList<Label> - The array list of objects that we want to return
	 */
	public static ArrayList<Label> deserializeFromXML()
	{
		return XMLSerializer.<Label>deserializeFromXML("Label");
	}

}
