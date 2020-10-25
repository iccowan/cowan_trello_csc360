package trello;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


class UserTest
{
	
	User u1, u1Copy, u2, u3, u4;

	@BeforeEach
	void setUp() throws Exception
	{
		u1 = new User("user1", "password1");
		u1Copy = new User("user1", "password1");
		u2 = new User("user2", "password2");
		u3 = new User("user3", "password3");
		u4 = new User("user4", "password4");
	}

	@Test
	void testLogin()
	{
		// Make sure that user can login with their username and password
		assertTrue(u1.login("user1", "password1"));
		
		// Make sure that the user cannot login with a different username
		assertFalse(u1.login("user2", "password1"));
		
		// Make sure that the user cannot login with a different password
		assertFalse(u1.login("user1", "password2"));
		
		// Make sure if both the username and password are incorrect, the user cannot login
		assertFalse(u1.login("user2", "password2"));
		
		// Make sure if any fields are blank the user cannot login
		assertFalse(u1.login("", "password1"));
		assertFalse(u1.login("user1", ""));
		assertFalse(u1.login("", ""));
	}

	@Test
	void testAddBoard()
	{
		// Let's make sure a new board is added to the user
		Board b1 = new Board("b1", u1);
		
		assertTrue(u1.getBoards().hasMember(b1));

		// Let's remove the board from the user and then make sure addBoard()
		// actually works
		u1.removeBoard(b1);
		assertFalse(u1.getBoards().hasMember(b1));
		
		u1.addBoard(b1);
		assertTrue(u1.getBoards().hasMember(b1));
		
		// Now that we know creating a board actually adds it to the user,
		// let's make sure we can add more than one board
		Board b2 = new Board("b2", u1);
		assertTrue(u1.getBoards().hasMember(b2));
	}

	@Test
	void testRemoveBoard()
	{
		// Let's create a few boards for testing
		ArrayList<Board> boards = new ArrayList<Board>();
		for (int i = 0; i < 4; i++)
			boards.add(new Board("b" + String.valueOf(i), u1));
		
		// Let's create another board assigned to another user for testing
		Board foreignBoard = new Board("b10", u2);
		
		// Let's make sure we cannot remove the foreign board
		assertFalse(u1.removeBoard(foreignBoard));
		
		// Let's make sure we can loop through and remove all of the boards
		for (Board b : boards)
		{
			assertTrue(u1.removeBoard(b));
			assertFalse(u1.getBoards().hasMember(b));
		}
		
		// Now, let's make sure we cannot doubly-remove each board
		for (Board b : boards)
		{
			assertFalse(u1.removeBoard(b));
			assertFalse(u1.getBoards().hasMember(b));
		}
		
		
		// Finally, let's make sure we still cannot remove the foreign board
		assertFalse(u1.removeBoard(foreignBoard));
	}

	@Test
	void testEqualsObject()
	{
		// Make sure 2 users are the same object are equal
		assertTrue(u1.equals(u1));
		
		// Make sure 2 users that are not the same object are not equal
		assertFalse(u1.equals(u2));
		
		// Make sure 2 users that are equal but not the same object are equal
		assertTrue(u1.equals(u1Copy));
		assertTrue(u1Copy.equals(u1));
	}

	@Test
	void testSerialization()
	{
		// Let's add all of the lists to an array list
		ArrayList<User> users = new ArrayList<User>();
		users.add(u1);
		users.add(u2);
		users.add(u3);
		users.add(u4);
		
		// Add some boards
		u1.addBoard(new Board("b1", u1));
		u1.addBoard(new Board("b2", u1));
		u2.addBoard(new Board("b3", u2));

		Board b4 = new Board("b4", u3);
		BList l = new BList("list", b4);
		Card c = new Card("card", l);
		l.addCard(c, u3);
		u3.addBoard(b4);
		
		// Now, let's serialize and make sure it doesn't error
		User.serializeToXML(users);
		
		// Now, lets deserialize and look at the objects we get in return
		ArrayList<User> sUsers = User.deserializeFromXML();
		
		// Make sure the list lengths are equal
		assertEquals(users.size(), sUsers.size());
		
		// Let's loop through each list and make sure they're equal
		// We'll also make sure that they're boards are the same
		// so we know no information is being lost
		for (int i = 0; i < users.size(); i++)
		{
			assertTrue(users.get(i).equals(sUsers.get(i)));
			
			HasMembersList<Board> boardUsers = users.get(i).getBoards();
			HasMembersList<Board> boardSUsers = sUsers.get(i).getBoards();
			
			assertEquals(boardUsers.getMembers().size(), boardSUsers.getMembers().size());
			
			for (int j = 0; j < boardUsers.getMembers().size(); j++)
				assertTrue(boardUsers.getMembers().get(j).equals(boardSUsers.getMembers().get(j)));
		}
	}

}
