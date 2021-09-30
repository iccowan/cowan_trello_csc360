package models.trello;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


class BoardTest
{
	
	Board b1, b1Copy, b2, b3, b4;
	User Ownerb1, Ownerb2;

	@BeforeEach
	void setUp() throws Exception
	{
		Ownerb1 = new User("Owner B1", "password");
		Ownerb2 = new User("Owner b2", "password");
		
		b1 = new Board("b1", Ownerb1);
		b1Copy = new Board("b1", new User("Owner B1", "password"));
		b2 = new Board("b2", Ownerb2);
		b3 = new Board("b3", new User("Owner b3", "password"));
		b4 = new Board("b4", new User("Owner b4", "password"));
	}

	@Test
	void testAddList()
	{
		// Let's make sure the owner can add a list
		BList l = new BList("l1", b1);
		
		b1.removeList(l, Ownerb1);
		assertFalse(b1.hasList(l));
		
		b1.addList(l, Ownerb1);
		assertTrue(b1.hasList(l));
		
		// Let's create a new user and make sure they cannot add a list
		BList l2 = new BList("l2", b1);
		
		b1.removeList(l2, Ownerb1);
		assertFalse(b1.hasList(l2));
		
		User newUser = new User("newuser", "p");
		
		b1.addList(l2, newUser);
		assertFalse(b1.hasList(l2));
		
		// Now, let's add the user to the members and make sure they can
		// add the list now
		b1.addMember(newUser, Ownerb1);
		b1.addList(l2, newUser);
		
		assertTrue(b1.hasList(l2));
		
		// Now, we're going to make sure if we remove the user,
		// they can no longer add a card
		b1.removeMember(newUser, Ownerb1);
		BList l3 = new BList("l3", b1);
		
		b1.removeList(l3, Ownerb1);
		assertFalse(b1.hasList(l3));
		
		b1.addList(l3, newUser);
		assertFalse(b1.hasList(l3));
	}

	@Test
	void testRemoveList()
	{
		// Let's add some lists for testing
		ArrayList<BList> lists = new ArrayList<BList>();
		for (int i = 1; i <= 4; i++)
		{
			BList l = new BList("l" + String.valueOf(i), b1);
			lists.add(l);
		}
		
		// Create another list that we'll never add to the board
		// for testing
		BList foreignList = new BList("foreign list", b2);
		
		// Let's make sure the owner can remove a list
		assertTrue(b1.removeList(lists.get(3), Ownerb1));
		
		assertFalse(b1.hasList(lists.get(3)));
		
		// Add it back
		b1.addList(lists.get(3), Ownerb1);
		
		// Let's create a new user and make sure they cannot remove a list
		User newUser = new User("newuser", "p");
		assertFalse(b1.removeList(lists.get(3), newUser));
		
		assertTrue(b1.hasList(lists.get(3)));
		
		// Now, let's add the user to the members and make sure they can
		// remove the list now
		b1.addMember(newUser, Ownerb1);
		assertTrue(b1.removeList(lists.get(3), newUser));
		
		assertFalse(b1.hasList(lists.get(3)));
		
		// Add it back
		b1.addList(lists.get(3), Ownerb1);
		
		// Now, we're going to make sure if we remove the user,
		// they can no longer add a list
		b1.removeMember(newUser, Ownerb1);
		assertFalse(b1.removeList(lists.get(3), newUser));
		
		assertTrue(b1.hasList(lists.get(3)));
		
		// Now, let's make sure we can remove any list, not just the last
		assertTrue(b1.removeList(lists.get(1), Ownerb1));
		assertFalse(b1.hasList(lists.get(1)));
		lists.remove(lists.get(1)); // Remove here now for testing
		
		// Remove more than one card
		assertTrue(b1.removeList(lists.get(0), Ownerb1));
		assertFalse(b1.hasList(lists.get(0)));
		lists.remove(lists.get(0)); // Remove here now for testing
		
		// Let's make sure we can't remove a card that doesn't exist in the list
		assertFalse(b1.removeList(foreignList, Ownerb1));
		
		// Now, remove the last 2 lists remaining and make sure we cannot remove
		// any lists once all lists are gone
		for (int i = 0; i < 3; i++)
		{
			if (i < 2)
			{
				assertTrue(b1.removeList(lists.get(0), Ownerb1));
				assertFalse(b1.hasList(lists.get(0)));
				lists.remove(lists.get(0)); // Remove here now for testing
			}
			else
			{
				assertFalse(b1.removeList(foreignList, Ownerb1));
			}
		}
	}

	@Test
	void testHasList()
	{
		// Let's add some lists for testing
		ArrayList<BList> lists = new ArrayList<BList>();
		for (int i = 1; i <= 4; i++)
		{
			BList l = new BList("l" + String.valueOf(i), b1);
			lists.add(l);
		}
		
		// Create another list that we'll never add to the list
		// for testing
		BList foreignList = new BList("foreign list", b2);
		BList temp;
		
		// Let's make sure that all of the lists that we added are actually
		// found in the list
		for (BList l : lists)
			assertTrue(b1.hasList(l));
		
		// Let's make sure that a list not in the list shows as not being there
		assertFalse(b1.hasList(foreignList));
		
		// Let's remove a list and make sure that the card doesn't show as being there
		b1.removeList(lists.get(0), Ownerb1);
		temp = lists.get(0);
		lists.remove(0); // Keep this list the same for testing
		
		assertFalse(b1.hasList(temp));
	}

	@Test
	void testAddMember()
	{
		// Let's make sure the owner can add a member
		User m = new User("u1", "p");
		b1.addMember(m, Ownerb1);
		
		assertTrue(b1.hasMember(m));
		
		// Let's create a new user and make sure they cannot add a member
		User m2 = new User("m2", "p");
		User newUser = new User("newuser", "p");
		b1.addMember(m2, newUser);
		
		assertFalse(b1.hasMember(m2));
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
			b1.addMember(u, Ownerb1);
		}
		
		// Create another user that we'll never add to the list
		// for testing
		User foreignUser = new User("foreign user", "p");
		
		// Let's make sure the owner can remove a member
		assertTrue(b1.removeMember(members.get(3), Ownerb1));
		
		assertFalse(b1.hasMember(members.get(3)));
		
		// Add it back
		b1.addMember(members.get(3), Ownerb1);
		
		// Let's create a new user and make sure they cannot remove a member
		User newUser = new User("newuser", "p");
		assertFalse(b1.removeMember(members.get(3), newUser));
		
		assertTrue(b1.hasMember(members.get(3)));
		
		// Now, let's make sure just a member cannot remove another member
		assertFalse(b1.removeMember(members.get(1), members.get(2)));
		assertTrue(b1.hasMember(members.get(1)));
		
		// Make sure the owner cannot remove themselves as a member
		assertFalse(b1.removeMember(Ownerb1, Ownerb1));
		assertTrue(b1.hasMember(Ownerb1));
		
		// Now, let's make sure we can remove any user (other than the owner), not just the last
		assertTrue(b1.removeMember(members.get(1), Ownerb1));
		assertFalse(b1.hasMember(members.get(1)));
		members.remove(1); // Remove here now for testing
		
		// Remove more than one card
		assertTrue(b1.removeMember(members.get(2), Ownerb1));
		assertFalse(b1.hasMember(members.get(2)));
		members.remove(2); // Remove here now for testing
		
		// Let's make sure we can't remove a card that doesn't exist in the list
		assertFalse(b1.removeMember(foreignUser, Ownerb1));
		
		// Let's remove every member but the owner, and make sure that
		// we can
		// 1) remove every member but the owner and
		// 2) not remove the owner when they're the only member remaining
		for (User m : members)
		{
			assertTrue(b1.removeMember(m, Ownerb1));
			assertFalse(b1.hasMember(m));
			members.remove(m);
		}
		
		assertFalse(b1.removeMember(Ownerb1, Ownerb1));
		assertTrue(b1.hasMember(Ownerb1));
		
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
			b1.addMember(u, Ownerb1);
		}
		
		// Create another user that we'll never add to the members
		// for testing
		User foreignUser = new User("foreign user", "foreignPassword");
		User temp;
		
		// Let's make sure that all of the members that we added are actually
		// found in the list
		for (User m : members)
			assertTrue(b1.hasMember(m));
		
		// Let's make sure that a member not in the list shows as not being there
		assertFalse(b1.hasMember(foreignUser));
		
		// Let's remove a member and make sure that the member doesn't show as being there
		b1.removeMember(members.get(0), Ownerb1);
		temp = members.get(0);
		members.remove(0); // Keep this list the same for testing
		
		assertFalse(b1.hasMember(temp));
	}

	@Test
	void testEqualsObject()
	{
		// Make sure 2 lists are the same object are equal
		assertTrue(b1.equals(b1));
		
		// Make sure 2 lists that are not the same object are not equal
		assertFalse(b1.equals(b2));
		
		// Make sure 2 lists that are equal but not the same object are equal
		assertTrue(b1.equals(b1Copy));
		assertTrue(b1Copy.equals(b1));
	}

	@Test
	void testSerialization()
	{
		// Let's add all of the boards to an array list
		ArrayList<Board> boards = new ArrayList<Board>();
		boards.add(b1);
		boards.add(b2);
		boards.add(b3);
		boards.add(b4);
		
		// Add a some lists to boards
		BList l1 = new BList("l1", b1);
		BList l2 = new BList("l2", b2);
		BList l3 = new BList("l3", b2);
		b1.addList(l1, Ownerb1);
		b2.addList(l2, Ownerb2);
		b2.addList(l3, Ownerb2);
		
		// Now, let's serialize and make sure it doesn't error
		Board.serializeToXML(boards);
		
		// Now, lets deserialize and look at the objects we get in return
		ArrayList<Board> sBoards = Board.deserializeFromXML();
		
		// Make sure the list lengths are equal
		assertEquals(boards.size(), boards.size());
		
		// Let's loop through each list and make sure they're equal
		for (int i = 0; i < boards.size(); i++)
			assertTrue(boards.get(i).equals(sBoards.get(i)));
	}

}
