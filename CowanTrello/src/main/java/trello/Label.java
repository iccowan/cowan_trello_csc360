package trello;

import java.util.ArrayList;

/**
 * Label Class
 *
 */
public class Label
{

	private String text;
	
	/**
	 * Default constructor
	 */
	public Label() {}

	/**
	 * @param text
	 */
	public Label(String text)
	{
		this.text = text;
	}
	
	@Override
	public boolean equals(Object thatObj)
	{
		Label that = (Label) thatObj;
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
