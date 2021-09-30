package models.trello.design;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class RedButtonDesignTest
{
	
	Design design;

	@BeforeEach
	void setUp() throws Exception
	{
		// Create a new DefaultModenaDesign
		design = DesignFactory.newDesign(DesignFactory.designs.get("Red Buttons"));
	}

	@Test
	void testStylesheet()
	{
		// Ensure that we get the correct stylesheet from the design
		assertEquals(design.getDesign(), DesignFactory.class.getResource("css/styles.css").toExternalForm());
	}

}
