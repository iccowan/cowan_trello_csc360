package trello;

/**
 * Label Class
 *
 */
public class Label
{

	private String text;

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

}
