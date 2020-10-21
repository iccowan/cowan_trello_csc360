package trello;

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
	 * @return String - The string of the file where the serialized object lives
	 */
	public String serializeToXML()
	{
		return XMLSerializer.<Label>serializeToXML(this);
	}
	
	/**
	 * @param objectFileName - File name where the object lives that we're going to deserialize
	 * @return BList - The list object that we want to return
	 */
	public static Label deserializeFromXML(String objectFileName)
	{
		return XMLSerializer.<Label>deserializeFromXML(objectFileName);
	}

}
