package models.trello.design;

/**
 * Design class
 *
 */
public class Design
{
	private String cssFile;

	/**
	 * @param cssFile - URL of the css file for the design
	 */
	protected Design(String cssFile)
	{		
		this.cssFile = cssFile;
	}

	/**
	 * @return the css file URL
	 */
	public String getDesign()
	{
		return cssFile;
	}
	
	/**
	 * @param that - The object to compare to
	 * @return whether or not this and that are equal
	 */
	@Override
	public boolean equals(Object that)
	{
		// Caste the object to a Design
		Design thatDesign = (Design) that;
		
		// If that is null, false
		if (thatDesign == null)
			return false;
		
		// If the designs are the same, true
		if (this.cssFile.equals(thatDesign.cssFile))
			return true;
		
		// Else, false
		return false;
	}
}
