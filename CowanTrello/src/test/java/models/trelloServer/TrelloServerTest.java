package models.trelloServer;

import static org.junit.jupiter.api.Assertions.*;

import java.rmi.ConnectException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import models.trello.*;
import models.trello.design.*;

class TrelloServerTest
{

	TrelloServer trelloServer;
	TrelloServerInterface trelloClient;
	Registry rmiregistry;
	ArrayList<User> users = new ArrayList<User>();

	@BeforeEach
	void setUp() throws Exception
	{	
		try
		{
			try
			{
				rmiregistry = LocateRegistry.getRegistry(1099);
				rmiregistry.lookup("SERVER");
			}
			catch (ConnectException e)
			{
				rmiregistry = LocateRegistry.createRegistry(1099);
			}
			catch (NotBoundException e)
			{
				// Just continue to finally, no need to handle anything here
			}
			finally
			{
				trelloServer = new TrelloServer("default");
				rmiregistry.rebind("TRELLO_SERVER", trelloServer);

				// Add an arraylist of users for testing
				for (int i = 0; i < 30; i++)
				{
					User newUser = new User("user" + String.valueOf(i), "password" + String.valueOf(i));
					users.add(newUser);
				}

				trelloServer.setUsers(users);
			}
			
			// Now, connect the client to the server
			trelloClient = new TrelloClient("localhost", "TRELLO_SERVER");
		}
		catch (RemoteException e)
		{
			fail(e.getStackTrace().toString());
		}
	}
	
	@AfterEach
	void tearDown()
	{
		try
		{
			// The registry should be running when this is called
			Registry rmiregistry = LocateRegistry.getRegistry(1099);

			rmiregistry.unbind("TRELLO_SERVER");
		}
		catch (RemoteException | NotBoundException e)
		{
			fail(e.getStackTrace().toString());
		}
	}

	@Test
	void testAuthenticateUser()
	{
		try
		{
			// Let's try to authenticate all the users in the list
			for (User u : users)
				assertEquals(u, trelloClient.authenticateUser(u.getName(), u.getPassword()));

			// Let's try to authenticate a user that doesn't exist
			// and make sure the appropriate exception is thrown
			assertThrows(UserNotFoundException.class, () ->
			{
				trelloClient.authenticateUser("user100", "password");
			});

			// Now, let's try to authenticate a user that does exist
			// with the incorrect password and make sure null is returned
			assertNull(trelloClient.authenticateUser("user0", "password"));
		}
		catch (UserNotFoundException | RemoteException e)
		{
			fail(e.getStackTrace().toString());
		}
	}

	@Test
	void testGetBoard()
	{
		try
		{
			// Get some guinea pigs
			User gp1 = users.get(0);
			User gp2 = users.get(3);
			User gp3 = users.get(15);

			// Let's create a few boards for gp1
			trelloClient.createBoard("board1", gp1);
			trelloClient.createBoard("board2", gp1);
			trelloClient.createBoard("board3", gp1);

			// Now, let's proceed with testing
			// Make sure a user that has no boards cannot get a board
			assertThrows(BoardNotFoundException.class, () ->
			{
				trelloClient.getBoard("anyboard", gp2);
			});

			// Make sure a user cannot get a board that doesn't exist
			assertThrows(BoardNotFoundException.class, () ->
			{
				trelloClient.getBoard("anyotherboard", gp1);
			});

			// Make sure a user can get one of their boards
			Board b1 = trelloClient.getBoard("board1", gp1);
			Board b2 = trelloClient.getBoard("board2", gp1);

			assertEquals("board1", b1.getName());
			assertEquals("board2", b2.getName());

			// Now, let's add a board and make sure we can get it
			trelloClient.createBoard("anyboard", gp2);

			Board anyboard = trelloClient.getBoard("anyboard", gp2);

			assertEquals("anyboard", anyboard.getName());

			// Now, let's do some behind the scenes editing to ensure the members work
			// correctly
			// And that we cannot get a board that has been removed
			ArrayList<User> users = trelloServer.getUsers();
			Board newBoard1 = new Board("newboard1", users.get(3));
			newBoard1.addMember(gp2, gp3);
			trelloServer.setUsers(users);;

			// Now, make sure a member of the board can get the board
			Board getnb1 = trelloClient.getBoard("newboard1", gp2);
			assertEquals("newboard1", getnb1.getName());
		}
		catch (BoardNotFoundException | UserNotFoundException | RemoteException e)
		{
			fail(e.getStackTrace().toString());
		}
	}

	@Test
	void testCreateBoard()
	{
		try
		{
			// Create a few boards for testing
			User u1 = users.get(0);
			trelloClient.createBoard("new board", u1);

			// Make sure the board is there
			Board newBoard = trelloClient.getBoard("new board", u1);
			assertEquals("new board", newBoard.getName());

			// Make sure it's only created for that user
			assertThrows(BoardNotFoundException.class, () ->
			{
				trelloClient.getBoard("new board", users.get(2));
			});
		}
		catch (BoardNotFoundException | UserNotFoundException | RemoteException e)
		{
			e.printStackTrace();
			fail(e.getStackTrace().toString());
		}
	}

	@Test
	void testUpdateBoard()
	{
		try
		{
			// Create a few boards for testing
			User u1 = users.get(0);
			Board board1 = trelloClient.createBoard("board1", u1);
			Board b1Clone = board1.clone();
			Board board2 = trelloClient.createBoard("board2", u1);
			Board b2Clone = board2.clone();

			// Let's change the name of board1
			board1.setName("updated_board1");

			// Call update and make sure it actually updated
			assertTrue(trelloClient.updateBoard(b1Clone, board1, u1));
			assertEquals("updated_board1", trelloClient.getBoard("updated_board1", u1).getName());
			assertThrows(BoardNotFoundException.class, () ->
			{
				trelloClient.getBoard("board1", u1);
			});

			// Make sure another user cannot update a board
			board2.setName("updated_board2");
			assertFalse(trelloClient.updateBoard(b2Clone, board2, users.get(3)));
			assertNotEquals("updated_board2", trelloClient.getBoard("board2", u1).getName());
		}
		catch (BoardNotFoundException | UserNotFoundException | RemoteException e)
		{
			e.printStackTrace();
			fail(e.getStackTrace().toString());
		}
	}
	
	@Test
	void testGetDesign()
	{
		// Let's loop through each design and make sure we can create
		// a server with that design and get the correct design
		for (String d : DesignFactory.designs.keySet())
		{
			Design design = DesignFactory.newDesign(DesignFactory.designs.get(d));
			TrelloServer.startServer(d, 1099, DesignFactory.designs.get(d));
			try
			{
				TrelloClient newClient = new TrelloClient("localhost", d);
				assertEquals(design.getDesign(), newClient.getDesign());
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
			finally
			{
				TrelloServer.closeServer(d, 1099);
			}
		}
	}

}
