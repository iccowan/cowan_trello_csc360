package models.trello.design;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CowanTrelloDesignTest
{
	
	Design design;

	@BeforeEach
	void setUp() throws Exception
	{
		// Create a new CowanTrelloDesign
		design = DesignFactory.newDesign(DesignFactory.designs.get("Cowan Trello"));
	}

	@Test
	void testStylesheet()
	{
		// Ensure that we get the correct stylesheet from the design
		assertEquals(design.getDesign(), DesignFactory.class.getResource("css/CowanTrello.css").toExternalForm());
	}

}
