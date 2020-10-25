package trello;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


class ComponentTest
{
	
	Component c1, c1Copy, c2, c3, c4;

	@BeforeEach
	void setUp() throws Exception
	{
		c1 = new Component("c1", 1);
		c1Copy = new Component("c1", 1);
		c2 = new Component("c2", 4);
		c3 = new Component("c3", 2);
		c4 = new Component("c4", 8);
	}

	@Test
	void testEqualsObject()
	{
		// Make sure 2 coms are the same object are equal
		assertTrue(c1.equals(c1));
		
		// Make sure 2 coms that are not the same object are not equal
		assertFalse(c1.equals(c2));
		
		// Make sure 2 coms that are equal but not the same object are equal
		assertTrue(c1.equals(c1Copy));
		assertTrue(c1Copy.equals(c1));
	}

	@Test
	void testSerialization()
	{
		// Let's add all of the components to an array list
		ArrayList<Component> coms = new ArrayList<Component>();
		coms.add(c1);
		coms.add(c2);
		coms.add(c3);
		coms.add(c4);
		
		// Now, let's serialize and make sure it doesn't error
		Component.serializeToXML(coms);
		
		// Now, lets deserialize and look at the objects we get in return
		ArrayList<Component> sComs = Component.deserializeFromXML();
		
		// Make sure the list lengths are equal
		assertEquals(coms.size(), coms.size());
		
		// Let's loop through each com and make sure they're equal
		for (int i = 0; i < coms.size(); i++)
			assertTrue(coms.get(i).equals(sComs.get(i)));
	}

}
