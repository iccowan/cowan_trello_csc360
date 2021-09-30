package models.trello.design;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javafx.application.Application;

class DefaultCaspianDesignTest
{
	
	Design design;

	@BeforeEach
	void setUp() throws Exception
	{
		// Create a new DefaultModenaDesign
		design = DesignFactory.newDesign(DesignFactory.designs.get("Default - Caspian"));
	}

	@Test
	void testStylesheet()
	{
		// Ensure that we get the correct stylesheet from the design
		assertEquals(design.getDesign(), Application.STYLESHEET_CASPIAN);
	}

}
