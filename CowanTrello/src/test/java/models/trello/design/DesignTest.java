package models.trello.design;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class DesignTest
{

	ArrayList<Design> designs = new ArrayList<Design>();
	
	@BeforeEach
	void setUp() throws Exception
	{
		// Add all of the designs to the array list
		for (String designKey : DesignFactory.designs.keySet())
			designs.add(DesignFactory.newDesign(DesignFactory.designs.get(designKey)));
	}

	@Test
	void testGetDesign()
	{
		// Let's loop through all of the designs and ensure
		// we get a design from all of them
		for (Design d : designs)
			assertNotEquals(d.getDesign(), "");
		
		// Let's make sure we don't have any duplicate designs
		for (int i = 0; i < designs.size(); i++)
			for (int j = 0; j < designs.size(); j++)
				if (i != j)
					assertNotEquals(designs.get(i).getDesign(), designs.get(j).getDesign());
	}
	
	@Test
	void testEquals()
	{
		// Make sure 2 of the same design are equal
		Design default1 = DesignFactory.newDesign("default");
		Design default1Clone = DesignFactory.newDesign("default");
		assertTrue(default1.equals(default1Clone));
		
		// Make sure 2 different designs are not equal
		for (Design d : designs)
			// Already tested this, so it's safe to use to get good results
			if (! d.equals(default1))
				assertFalse(default1.equals(d));
	}
	
	@Test
	void testNewDesign()
	{	
		// Clear the designs for this test
		designs.clear();

		// Get a default design for testing
		Design defaultDesign = DesignFactory.newDesign("default");
		
		Set<String> keySet = DesignFactory.designs.keySet();
		Iterator<String> it = keySet.iterator();
		
		// We need to ensure that every design in the HashMap
		// will return a design that isn't the default (except the default)
		while (it.hasNext())
		{
			// Get the next
			String next = it.next();
			Design nextDesign = DesignFactory.newDesign(DesignFactory.designs.get(next));
			
			// If the design isn't supposed to be default, let's
			// make sure it isn't the default
			if (! DesignFactory.designs.get(next).equals("default"))
				assertNotEquals(defaultDesign, nextDesign);
			
			// Make sure the design doesn't already exist
			assertFalse(designs.contains(nextDesign));
		}		
	}

}
