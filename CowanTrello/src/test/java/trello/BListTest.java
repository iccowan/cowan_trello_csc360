package trello;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


class BListTest
{
	
	BList l1, l2, l3, l4;
	BList l1Copy;
	Board Boardl1;
	User Ownerl1, Ownerl2;
	String l1Name;

	@BeforeEach
	void setUp() throws Exception
	{
		// Setup each list
		Ownerl1 = new User("l1owner", "p");
		Ownerl2 = new User("l2owner", "p");
		Boardl1 = new Board("l1Board", Ownerl1);
		
		Board Boardl1Copy = new Board("l1Board", new User("l1owner", "p"));
		Board Boardl2 = new Board("l2Board", Ownerl2);
		Board Boardl3 = new Board("l3Board", new User("l3owner", "p"));
		Board Boardl4 = new Board("l4Board", new User("l4owner", "p"));
		
		l1Name = "List1";
		
		// Create the lists
		l1 = new BList(l1Name, Boardl1);
		l1Copy = new BList(l1Name, Boardl1Copy);
		l2 = new BList("List2", Boardl2);
		l3 = new BList("List3", Boardl3);
		l4 = new BList("List4", Boardl4);
	}

	@Test
	void testAddCardCardUser()
	{
		// Let's make sure the owner can add a card
		Card card = new Card("card1", l1);
		l1.addCard(card, Ownerl1);
		
		assertTrue(l1.hasCard(card));
		
		// Let's create a new user and make sure they cannot add a card
		Card card2 = new Card("card2", l1);
		User newUser = new User("newuser", "p");
		l1.addCard(card2, newUser);
		
		assertFalse(l1.hasCard(card2));
		
		// Now, let's add the user to the members and make sure they can
		// add the card now
		Boardl1.addMember(newUser, Ownerl1);
		l1.addCard(card2, newUser);
		
		assertTrue(l1.hasCard(card2));
		
		// Now, we're going to make sure if we remove the user,
		// they can no longer add a card
		Boardl1.removeMember(newUser, Ownerl1);
		Card card3 = new Card("card3", l1);
		l1.addCard(card3, newUser);
		
		assertFalse(l1.hasCard(card3));
	}

	@Test
	void testAddCardCardIntUser()
	{
		// Let's make sure the owner can add a card
		Card card = new Card("card1", l1);
		l1.addCard(card, 0, Ownerl1);
		
		assertTrue(l1.hasCard(card));
		
		// Let's create a new user and make sure they cannot add a card
		Card card2 = new Card("card2", l1);
		User newUser = new User("newuser", "p");
		l1.addCard(card2, 0, newUser);
		
		assertFalse(l1.hasCard(card2));
		
		// Now, let's add the user to the members and make sure they can
		// add the card now
		Boardl1.addMember(newUser, Ownerl1);
		l1.addCard(card2, 0, newUser);
		
		assertTrue(l1.hasCard(card2));
		
		// Now, we're going to make sure if we remove the user,
		// they can no longer add a card
		Boardl1.removeMember(newUser, Ownerl1);
		Card card3 = new Card("card3", l1);
		l1.addCard(card3, 0, newUser);
		
		assertFalse(l1.hasCard(card3));
		
		// Now, we know that the user verification works, so let's
		// test where the card is being inserted
		
		// Make sure we can add a card at the beginning
		l1.addCard(card3, 0, Ownerl1);
		ArrayList<Card> l1Cards = l1.getCards().getMembers();
		
		assertEquals(0, l1Cards.indexOf(card3));
		
		// Now, make sure we can add a card at the end
		Card card4 = new Card("card4", l1);
		int index = l1Cards.size() - 1;
		l1.addCard(card4, index, Ownerl1);
		
		assertEquals(index, l1Cards.indexOf(card4));
		
		// Now, make sure that if we put in a broken index, it puts it at the end
		Card card5 = new Card("card5", l1);
		l1.addCard(card5, -1, Ownerl1);
		
		assertEquals(l1Cards.size() - 1, l1Cards.indexOf(card5));
		
		Card card6 = new Card("card6", l1);
		l1.addCard(card6, l1Cards.size(), Ownerl1);
		
		assertEquals(l1Cards.size() - 1, l1Cards.indexOf(card6));
		
		// Finally, make sure we can insert a card into an index in the middle
		l1.addCard(card5, 2, Ownerl1);
		
		assertEquals(2, l1Cards.indexOf(card5));
	}

	@Test
	void testRemoveCard()
	{
		// Let's add some cards for testing
		ArrayList<Card> cards = new ArrayList<Card>();
		for (int i = 1; i <= 4; i++)
		{
			Card c = new Card("c" + String.valueOf(i), l1);
			cards.add(c);
			l1.addCard(c, Ownerl1);
		}
		
		// Create another card that we'll never add to the list
		// for testing
		Card foreignCard = new Card("foreign card", l2);
		
		// Let's make sure the owner can remove a card
		assertTrue(l1.removeCard(cards.get(3), Ownerl1));
		
		assertFalse(l1.hasCard(cards.get(3)));
		
		// Add it back
		l1.addCard(cards.get(3), Ownerl1);
		
		// Let's create a new user and make sure they cannot remove a card
		User newUser = new User("newuser", "p");
		assertFalse(l1.removeCard(cards.get(3), newUser));
		
		assertTrue(l1.hasCard(cards.get(3)));
		
		// Now, let's add the user to the members and make sure they can
		// remove the card now
		Boardl1.addMember(newUser, Ownerl1);
		assertTrue(l1.removeCard(cards.get(3), newUser));
		
		assertFalse(l1.hasCard(cards.get(3)));
		
		// Add it back
		l1.addCard(cards.get(3), Ownerl1);
		
		// Now, we're going to make sure if we remove the user,
		// they can no longer add a card
		Boardl1.removeMember(newUser, Ownerl1);
		assertFalse(l1.removeCard(cards.get(3), newUser));
		
		assertTrue(l1.hasCard(cards.get(3)));
		
		// Now, let's make sure we can remove any card, not just the last
		assertTrue(l1.removeCard(cards.get(1), Ownerl1));
		assertFalse(l1.hasCard(cards.get(1)));
		cards.remove(cards.get(1)); // Remove here now for testing
		
		// Remove more than one card
		assertTrue(l1.removeCard(cards.get(0), Ownerl1));
		assertFalse(l1.hasCard(cards.get(0)));
		cards.remove(cards.get(0)); // Remove here now for testing
		
		// Let's make sure we can't remove a card that doesn't exist in the list
		assertFalse(l1.removeCard(foreignCard, Ownerl1));
		
		// Now, remove the last 2 cards remaining and make sure we cannot remove
		// any cards once all cards are gone
		for (int i = 0; i < 3; i++)
		{
			if (i < 2)
			{
				assertTrue(l1.removeCard(cards.get(0), Ownerl1));
				assertFalse(l1.hasCard(cards.get(0)));
				cards.remove(cards.get(0)); // Remove here now for testing
			}
			else
			{
				assertFalse(l1.removeCard(foreignCard, Ownerl1));
			}
		}
	}

	@Test
	void testMoveCard()
	{
		// Let's add some cards for testing
		ArrayList<Card> cards = new ArrayList<Card>();
		for (int i = 1; i <= 4; i++)
		{
			Card c = new Card("c" + String.valueOf(i), l1);
			cards.add(c);
			l1.addCard(c, Ownerl1);
		}
		
		// Create another card that we'll never add to the list
		// for testing
		Card foreignCard = new Card("foreign card", l2);
		Card temp;
		
		// Let's make sure the owner can move a card
		assertTrue(l1.moveCard(cards.get(3), 2, Ownerl1));
		assertTrue(l1.hasCard(cards.get(3)));
		assertEquals(2, l1.getCards().getMembers().indexOf(cards.get(3)));
		temp = cards.get(3);
		cards.remove(3);
		cards.add(2, temp); // Keep the ordering the same in our test list
		
		// Let's create a new user and make sure they cannot move a card
		User newUser = new User("newuser", "p");
		assertFalse(l1.moveCard(cards.get(3), 2, newUser));
		assertTrue(l1.hasCard(cards.get(3)));
		assertEquals(3, l1.getCards().getMembers().indexOf(cards.get(3)));
		
		// Now, let's add the new user to the Board and make sure they can now move the card
		Boardl1.addMember(newUser, Ownerl1);
		assertTrue(l1.moveCard(cards.get(3), 2, newUser));
		assertTrue(l1.hasCard(cards.get(3)));
		assertEquals(2, l1.getCards().getMembers().indexOf(cards.get(3)));
		temp = cards.get(3);
		cards.remove(3);
		cards.add(2, temp); // Keep the ordering the same in our test list
		
		// Let's remove the new user and ensure that they are no longer able to move the card
		Boardl1.removeMember(newUser, Ownerl1);
		assertFalse(l1.moveCard(cards.get(3), 2, newUser));
		assertTrue(l1.hasCard(cards.get(3)));
		assertEquals(3, l1.getCards().getMembers().indexOf(cards.get(3)));

		// Let's make sure that we can move a card to the beginning of the list
		assertTrue(l1.moveCard(cards.get(3), 0, Ownerl1));
		assertTrue(l1.hasCard(cards.get(3)));
		assertEquals(0, l1.getCards().getMembers().indexOf(cards.get(3)));
		temp = cards.get(3);
		cards.remove(3);
		cards.add(0, temp); // Keep the ordering the same in our test list
		
		// Let's make sure we can move a card to the end of the list
		int index = cards.size() - 1;
		assertTrue(l1.moveCard(cards.get(1), index, Ownerl1));
		assertTrue(l1.hasCard(cards.get(1)));
		assertEquals(index, l1.getCards().getMembers().indexOf(cards.get(1)));
		temp = cards.get(1);
		cards.remove(1);
		cards.add(index, temp); // Keep the ordering the same in our test list
		
		// Now, let's make sure that if addCard() fails, the card gets moved to the end
		// Since we test addCard() elsewhere, we will simply pick one case that will cause
		// this to occur
		l1.moveCard(cards.get(1), cards.size(), Ownerl1);
		assertEquals(cards.size() - 1, l1.getCards().getMembers().indexOf(cards.get(1)));
		temp = cards.get(1);
		cards.remove(1);
		cards.add(cards.size() - 1, temp); // Keep the ordering the same in our test list
		
		// Let's ensure that this fails if the remove card method fails
		// (remove card testing is done elsewhere, so we'll pick one
		// instance that it should fail to test this)
		assertFalse(l1.moveCard(foreignCard, 2, Ownerl1));
		assertFalse(l1.hasCard(foreignCard)); // Since we also use add in the method
	}

	@Test
	void testHasCard()
	{
		// Let's add some cards for testing
		ArrayList<Card> cards = new ArrayList<Card>();
		for (int i = 1; i <= 4; i++)
		{
			Card c = new Card("c" + String.valueOf(i), l1);
			cards.add(c);
			l1.addCard(c, Ownerl1);
		}
		
		// Create another card that we'll never add to the list
		// for testing
		Card foreignCard = new Card("foreign card", l2);
		Card temp;
		
		// Let's make sure that all of the cards that we added are actually
		// found in the list
		for (Card c : cards)
			assertTrue(l1.hasCard(c));
		
		// Let's make sure that a card not in the list shows as not being there
		assertFalse(l1.hasCard(foreignCard));
		
		// Let's remove a card and make sure that the card doesn't show as being there
		l1.removeCard(cards.get(0), Ownerl1);
		temp = cards.get(0);
		cards.remove(0); // Keep this list the same for testing
		
		assertFalse(l1.hasCard(temp));
		
		// Now, let's add a new card at a random index and make sure it exists
		Card newCard = new Card("new card", l1);
		l1.addCard(newCard, 1, Ownerl1);
		
		assertTrue(l1.hasCard(newCard));
	}

	@Test
	void testEqualsObject()
	{
		// Make sure 2 lists are the same object are equal
		assertTrue(l1.equals(l1));
		
		// Make sure 2 lists that are not the same object are not equal
		assertFalse(l1.equals(l2));
		
		// Make sure 2 lists that are equal but not the same object are equal
		assertTrue(l1.equals(l1Copy));
		assertTrue(l1Copy.equals(l1));
	}

	@Test
	void testSerialization()
	{
		// Let's add all of the lists to an array list
		ArrayList<BList> lists = new ArrayList<BList>();
		lists.add(l1);
		lists.add(l2);
		lists.add(l3);
		lists.add(l4);
		
		// Add some cards
		Card c = new Card("c1", l1);
		Card c2 = new Card("c2", l2);
		Card c3 = new Card("c3", l2);
		l1.addCard(c, Ownerl1);
		l2.addCard(c2, Ownerl2);
		l2.addCard(c3, Ownerl2);
		
		// Now, let's serialize and make sure it doesn't error
		BList.serializeToXML(lists);
		
		// Now, lets deserialize and look at the objects we get in return
		ArrayList<BList> sLists = BList.deserializeFromXML();
		
		// Make sure the list lengths are equal
		assertEquals(lists.size(), sLists.size());
		
		// Let's loop through each list and make sure they're equal
		for (int i = 0; i < lists.size(); i++)
			assertTrue(lists.get(i).equals(sLists.get(i)));
	}

}
