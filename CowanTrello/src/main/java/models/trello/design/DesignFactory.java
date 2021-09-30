package models.trello.design;

import java.util.HashMap;
import java.util.Map;

public class DesignFactory
{
	
	// Normally I don't do public attributes, but this makes life easier
	// if it is publicly accessible
	public static final Map<String, String> designs = new HashMap<String, String>()
	{

		private static final long serialVersionUID = 8869126764957892084L;

		{
			// List all of the designs available in the factory here
			put("Default - Modena", "default");
			put("Default - Caspian", "defaultc");
			put("Red Buttons", "redbuttons");
			put("Cowan Trello", "cowanTrello");
		}
		
	};

	/**
	 * @param designName - Name of the design requested
	 * @param cssFile    - URL of the css file for the design (for custom designs)
	 * @return the Design object
	 */
	public static Design newDesign(String designName)
	{
		// Switch for the design name
		switch (designName)
		{
		case "defaultc":
			// Default - Caspian design
			return new DefaultCaspianDesign();
		case "redbuttons":
			return new RedButtonDesign();
		case "cowanTrello":
			return new CowanTrelloDesign();
		default:
			// Default - Modena design
			// This is our default because this is JavaFX's global default
			return new DefaultModenaDesign();
		}
	}
	
}
