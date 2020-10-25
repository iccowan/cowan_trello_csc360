package trello;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


class LabelTest
{

	Label l1, l1Copy, l2, l3, l4;
	
	@BeforeEach
	void setUp() throws Exception
	{
		l1 = new Label("l1");
		l1Copy = new Label("l1");
		l2 = new Label("l2");
		l3 = new Label("l3");
		l4 = new Label("l4");
	}

	@Test
	void testEqualsObject()
	{
		// Make sure 2 labs are the same object are equal
		assertTrue(l1.equals(l1));
		
		// Make sure 2 labs that are not the same object are not equal
		assertFalse(l1.equals(l2));
		
		// Make sure 2 labs that are equal but not the same object are equal
		assertTrue(l1.equals(l1Copy));
		assertTrue(l1Copy.equals(l1));
	}

	@Test
	void testSerialization()
	{
		// Let's add all of the lists to an array list
		ArrayList<Label> labs = new ArrayList<Label>();
		labs.add(l1);
		labs.add(l2);
		labs.add(l3);
		labs.add(l4);
		
		// Now, let's serialize and make sure it doesn't error
		Label.serializeToXML(labs);
		
		// Now, lets deserialize and look at the objects we get in return
		ArrayList<Label> sLabs = Label.deserializeFromXML();
		
		// Make sure the list lengths are equal
		assertEquals(labs.size(), sLabs.size());
		
		// Let's loop through each lab and make sure they're equal
		for (int i = 0; i < labs.size(); i++)
			assertTrue(labs.get(i).equals(sLabs.get(i)));
	}

}
