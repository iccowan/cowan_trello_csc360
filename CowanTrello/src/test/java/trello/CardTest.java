package trello;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


class CardTest
{
	
	Card c1, c1Copy, c2, c3, c4;
	String c1Name, c2Name;
	User c1Owner, c2Owner;
	ArrayList<BList> lists = new ArrayList<BList>();

	@BeforeEach
	void setUp() throws Exception
	{
		c1Name = "c1";
		c2Name = "c2";
		
		User[] owners = new User[4];

		for (int i = 0; i < 4; i++)
		{
			User o = new User("u" + String.valueOf(i + 1), "p");
			BList l = new BList("l" + String.valueOf(i + 1), new Board("b" + String.valueOf(i + 1), o));
			owners[i] = o;
			lists.add(l);
		}
		
		c1 = new Card(c1Name, lists.get(0));
		c1Copy = new Card(c1Name, new BList("l1", new Board("b1", new User("u1", "p"))));
		c2 = new Card(c2Name, lists.get(1));
		c3 = new Card("c3", lists.get(2));
		c4 = new Card("c4", lists.get(3));
		
		Card[] cards = new Card[] {c1, c2, c3, c4};
		
		for (int i = 0; i < 4; i++)
			lists.get(i).addCard(cards[i], owners[i]);
		
		c1Owner = c1.getList().getBoard().getOwner();
		c2Owner = c2.getList().getBoard().getOwner();
	}

	@Test
	void testSwitchList()
	{
		// Let's pick the two lists that we're going to be dealing with
		BList origList = c1.getList();
		BList newList = new BList("l12", c1.getList().getBoard());
		BList c2List = c2.getList();
		
		// Let's make sure that c1 is in the proper list
		assertTrue(origList.hasCard(c1));
		assertFalse(newList.hasCard(c1));
		
		// Now, let's move c1 to the new list
		assertTrue(c1.switchList(newList, 0, c1Owner));
		
		assertTrue(newList.hasCard(c1));
		assertFalse(origList.hasCard(c1));
		
		// Now, let's make sure we cannot move the card to a list that
		// belongs to another board
		assertFalse(c1.switchList(c2List, 0, c1Owner));
		
		assertTrue(newList.hasCard(c1));
		assertFalse(c2List.hasCard(c1));
	}

	@Test
	void testAddLabel()
	{
		// Let's make sure the owner can add a label
		Label l = new Label("l1");
		c1.addLabel(l, c1Owner);
		
		assertTrue(c1.hasLabel(l));
		
		// Let's create a new user and make sure they cannot add a label
		Label l2 = new Label("l2");
		User newUser = new User("newuser", "p");
		c1.addLabel(l2, newUser);
		
		assertFalse(c1.hasLabel(l2));
		
		// Now, let's add the user to the members and make sure they can
		// add the label now
		c1.getList().getBoard().addMember(newUser, c1Owner);
		c1.addLabel(l2, newUser);
		
		assertTrue(c1.hasLabel(l2));
		
		// Now, we're going to make sure if we remove the user,
		// they can no longer add a label
		c1.getList().getBoard().removeMember(newUser, c1Owner);
		Label l3 = new Label("l3");
		c1.addLabel(l3, newUser);
		
		assertFalse(c1.hasLabel(l3));
	}

	@Test
	void testRemoveLabel()
	{
		// Let's add some labels for testing
		ArrayList<Label> labels = new ArrayList<Label>();
		for (int i = 1; i <= 4; i++)
		{
			Label l = new Label("l" + String.valueOf(i));
			labels.add(l);
			c1.addLabel(l, c1Owner);
		}
		
		// Create another label that we'll never add to the card
		// for testing
		Label foreignLabel = new Label("foreign label");
		
		// Let's make sure the owner can remove a list
		assertTrue(c1.removeLabel(labels.get(3), c1Owner));
		
		assertFalse(c1.hasLabel(labels.get(3)));
		
		// Add it back
		c1.addLabel(labels.get(3), c1Owner);
		
		// Let's create a new user and make sure they cannot remove a label
		User newUser = new User("newuser", "p");
		assertFalse(c1.removeLabel(labels.get(3), newUser));
		
		assertTrue(c1.hasLabel(labels.get(3)));
		
		// Now, let's add the user to the members of the board and make sure they can
		// remove the label now
		c1.getList().getBoard().addMember(newUser, c1Owner);
		assertTrue(c1.removeLabel(labels.get(3), newUser));
		
		assertFalse(c1.hasLabel(labels.get(3)));
		
		// Add it back
		c1.addLabel(labels.get(3), c1Owner);
		
		// Now, we're going to make sure if we remove the user,
		// they can no longer add a label
		c1.getList().getBoard().removeMember(newUser, c1Owner);
		assertFalse(c1.removeLabel(labels.get(3), newUser));
		
		assertTrue(c1.hasLabel(labels.get(3)));
		
		// Now, let's make sure we can remove any label, not just the last
		assertTrue(c1.removeLabel(labels.get(1), c1Owner));
		assertFalse(c1.hasLabel(labels.get(1)));
		labels.remove(1); // Remove here now for testing
		
		// Remove more than one label
		assertTrue(c1.removeLabel(labels.get(0), c1Owner));
		assertFalse(c1.hasLabel(labels.get(0)));
		labels.remove(0); // Remove here now for testing
		
		// Let's make sure we can't remove a label that doesn't exist in the list
		assertFalse(c1.removeLabel(foreignLabel, c1Owner));
		
		// Now, remove the last 2 labels remaining and make sure we cannot remove
		// any lists once all labels are gone
		for (int i = 0; i < 3; i++)
		{
			if (i < 2)
			{
				assertTrue(c1.removeLabel(labels.get(0), c1Owner));
				assertFalse(c1.hasLabel(labels.get(0)));
				labels.remove(0); // Remove here now for testing
			}
			else
			{
				assertFalse(c1.removeLabel(foreignLabel, c1Owner));
			}
		}
	}

	@Test
	void testHasLabel()
	{
		// Let's add some labels for testing
		ArrayList<Label> labels = new ArrayList<Label>();
		for (int i = 1; i <= 4; i++)
		{
			Label l = new Label("l" + String.valueOf(i));
			labels.add(l);
			c1.addLabel(l, c1Owner);
		}
		
		// Create another label that we'll never add to the list
		// for testing
		Label foreignLabel = new Label("foreign label");
		Label temp;
		
		// Let's make sure that all of the labels that we added are actually
		// found in the list
		for (Label l : labels)
			assertTrue(c1.hasLabel(l));
		
		// Let's make sure that a label not in the list shows as not being there
		assertFalse(c1.hasLabel(foreignLabel));
		
		// Let's remove a label and make sure that the label doesn't show as being there
		c1.removeLabel(labels.get(0), c1Owner);
		temp = labels.get(0);
		labels.remove(0); // Keep this list the same for testing
		
		assertFalse(c1.hasLabel(temp));
	}

	@Test
	void testAddMember()
	{
		// Let's make sure the owner can add a member
		User m = new User("u1", "p");
		c1.addMember(m, c1Owner);
		
		assertTrue(c1.hasMember(m));
		
		// Let's create a new user and make sure they cannot add a member
		User m2 = new User("m2", "p");
		User newUser = new User("newuser", "p");
		c1.addMember(m2, newUser);
		
		assertFalse(c1.hasMember(m2));
	}

	@Test
	void testRemoveMember()
	{
		// Let's add some members for testing
		ArrayList<User> members = new ArrayList<User>();
		for (int i = 1; i <= 4; i++)
		{
			User u = new User("u" + String.valueOf(i), "p");
			members.add(u);
			c1.addMember(u, c1Owner);
		}
		
		// Create another user that we'll never add to the list
		// for testing
		User foreignUser = new User("foreign user", "p");
		
		// Let's make sure the owner can remove a member
		assertTrue(c1.removeMember(members.get(3), c1Owner));
		
		assertFalse(c1.hasMember(members.get(3)));
		
		// Add it back
		c1.addMember(members.get(3), c1Owner);
		
		// Let's create a new user and make sure they cannot remove a member
		User newUser = new User("newuser", "p");
		assertFalse(c1.removeMember(members.get(3), newUser));
		
		assertTrue(c1.hasMember(members.get(3)));
		
		// Now, let's make sure we can remove any user, not just the last
		assertTrue(c1.removeMember(members.get(1), c1Owner));
		assertFalse(c1.hasMember(members.get(1)));
		members.remove(1); // Remove here now for testing
		
		// Remove more than one member
		assertTrue(c1.removeMember(members.get(2), c1Owner));
		assertFalse(c1.hasMember(members.get(2)));
		members.remove(2); // Remove here now for testing
		
		// Let's make sure we can't remove a member that doesn't exist in the list
		assertFalse(c1.removeMember(foreignUser, c1Owner));
		
		// Let's now make sure we can remove all of the members
		for (User m : members)
		{
			assertTrue(c1.removeMember(m, c1Owner));
			assertFalse(c1.hasMember(m));
			members.remove(m);
		}
	}
	
	@Test
	void testHasMember()
	{
		// Let's add some members for testing
		ArrayList<User> members = new ArrayList<User>();
		for (int i = 1; i <= 4; i++)
		{
			User u = new User("u" + String.valueOf(i), "password" + String.valueOf(i));
			members.add(u);
			c1.addMember(u, c1Owner);
		}
		
		// Create another user that we'll never add to the members
		// for testing
		User foreignUser = new User("foreign user", "foreignPassword");
		User temp;
		
		// Let's make sure that all of the members that we added are actually
		// found in the list
		for (User m : members)
			assertTrue(c1.hasMember(m));
		
		// Let's make sure that a member not in the list shows as not being there
		assertFalse(c1.hasMember(foreignUser));
		
		// Let's remove a member and make sure that the member doesn't show as being there
		c1.removeMember(members.get(0), c1Owner);
		temp = members.get(0);
		members.remove(0); // Keep this list the same for testing
		
		assertFalse(c1.hasMember(temp));
	}

	@Test
	void testAddComponent()
	{
		// Let's make sure the owner can add a component
		Component com = new Component("com1", 1);
		c1.addComponent(com, c1Owner);
		
		assertTrue(c1.hasComponent(com));
		
		// Let's create a new user and make sure they cannot add a component
		Component com2 = new Component("com2", 1);
		User newUser = new User("newuser", "p");
		c1.addComponent(com2, newUser);
		
		assertFalse(c1.hasComponent(com2));
		
		// Now, let's add the user to the members and make sure they can
		// add the component now
		c1.getList().getBoard().addMember(newUser, c1Owner);
		c1.addComponent(com2, newUser);
		
		assertTrue(c1.hasComponent(com2));
		
		// Now, we're going to make sure if we remove the user,
		// they can no longer add a component
		c1.getList().getBoard().removeMember(newUser, c1Owner);
		Component com3 = new Component("com3", 1);
		c1.addComponent(com3, newUser);
		
		assertFalse(c1.hasComponent(com3));
	}

	@Test
	void testRemoveComponent()
	{
		// Let's add some components for testing
		ArrayList<Component> components = new ArrayList<Component>();
		for (int i = 1; i <= 4; i++)
		{
			Component com = new Component("com" + String.valueOf(i), 1);
			components.add(com);
			c1.addComponent(com, c1Owner);
		}
		
		// Create another component that we'll never add to the card
		// for testing
		Component foreignComponent = new Component("foreign com", 1);
		
		// Let's make sure the owner can remove a list
		assertTrue(c1.removeComponent(components.get(3), c1Owner));
		
		assertFalse(c1.hasComponent(components.get(3)));
		
		// Add it back
		c1.addComponent(components.get(3), c1Owner);
		
		// Let's create a new user and make sure they cannot remove a com
		User newUser = new User("newuser", "p");
		assertFalse(c1.removeComponent(components.get(3), newUser));
		
		assertTrue(c1.hasComponent(components.get(3)));
		
		// Now, let's add the user to the members of the board and make sure they can
		// remove the com now
		c1.getList().getBoard().addMember(newUser, c1Owner);
		assertTrue(c1.removeComponent(components.get(3), newUser));
		
		assertFalse(c1.hasComponent(components.get(3)));
		
		// Add it back
		c1.addComponent(components.get(3), c1Owner);
		
		// Now, we're going to make sure if we remove the user,
		// they can no longer remove a component
		c1.getList().getBoard().removeMember(newUser, c1Owner);
		assertFalse(c1.removeComponent(components.get(3), newUser));
		
		assertTrue(c1.hasComponent(components.get(3)));
		
		// Now, let's make sure we can remove any com, not just the last
		assertTrue(c1.removeComponent(components.get(1), c1Owner));
		assertFalse(c1.hasComponent(components.get(1)));
		components.remove(1); // Remove here now for testing
		
		// Remove more than one com
		assertTrue(c1.removeComponent(components.get(0), c1Owner));
		assertFalse(c1.hasComponent(components.get(0)));
		components.remove(0); // Remove here now for testing
		
		// Let's make sure we can't remove a label that doesn't exist in the list
		assertFalse(c1.removeComponent(foreignComponent, c1Owner));
		
		// Now, remove the last 2 labels remaining and make sure we cannot remove
		// any lists once all labels are gone
		for (int i = 0; i < 3; i++)
		{
			if (i < 2)
			{
				assertTrue(c1.removeComponent(components.get(0), c1Owner));
				assertFalse(c1.hasComponent(components.get(0)));
				components.remove(0); // Remove here now for testing
			}
			else
			{
				assertFalse(c1.removeComponent(foreignComponent, c1Owner));
			}
		}
	}

	@Test
	void testHasComponent()
	{
		// Let's add some components for testing
		ArrayList<Component> components = new ArrayList<Component>();
		for (int i = 1; i <= 4; i++)
		{
			Component com = new Component("com" + String.valueOf(i), 1);
			components.add(com);
			c1.addComponent(com, c1Owner);
		}
		
		// Create another component that we'll never add to the list
		// for testing
		Component foreignCom = new Component("foreign com", 1);
		Component temp;
		
		// Let's make sure that all of the coms that we added are actually
		// found in the list
		for (Component com : components)
			assertTrue(c1.hasComponent(com));
		
		// Let's make sure that a label not in the list shows as not being there
		assertFalse(c1.hasComponent(foreignCom));
		
		// Let's remove a component and make sure that the com doesn't show as being there
		c1.removeComponent(components.get(0), c1Owner);
		temp = components.get(0);
		components.remove(0); // Keep this list the same for testing
		
		assertFalse(c1.hasComponent(temp));
	}

	@Test
	void testEqualsObject()
	{
		// Make sure 2 cards are the same object are equal
		assertTrue(c1.equals(c1));
		
		// Make sure 2 cards that are not the same object are not equal
		assertFalse(c1.equals(c2));
		
		// Make sure 2 cards that are equal but not the same object are equal
		assertTrue(c1.equals(c1Copy));
		assertTrue(c1Copy.equals(c1));
	}

	@Test
	void testSerialization()
	{
		// Let's add all of the boards to an array list
		ArrayList<Card> cards = new ArrayList<Card>();
		cards.add(c1);
		cards.add(c2);
		cards.add(c3);
		cards.add(c4);
		
		// Add a some components and labels to the cards
		Component com1 = new Component("com1", 1);
		Component com2 = new Component("com2", 3);
		Component com3 = new Component("com3", 2);
		Label lab1 = new Label("lab1");
		Label lab2 = new Label("lab2");
		Label lab3 = new Label("lab3");
		
		c1.addComponent(com1, c1Owner);
		c1.addComponent(com2, c1Owner);
		c2.addComponent(com3, c2Owner);
		c1.addLabel(lab1, c1Owner);
		c1.addLabel(lab2, c1Owner);
		c2.addLabel(lab3, c2Owner);
		
		// Now, let's serialize and make sure it doesn't error
		Card.serializeToXML(cards);
		
		// Now, lets deserialize and look at the objects we get in return
		ArrayList<Card> sCards = Card.deserializeFromXML();
		
		// Make sure the list lengths are equal
		assertEquals(cards.size(), sCards.size());
		
		// Let's loop through each list and make sure they're equal
		for (int i = 0; i < cards.size(); i++)
			assertTrue(cards.get(i).equals(sCards.get(i)));
	}

}
