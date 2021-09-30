package controllers.save;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.Iterator;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import controllers.SetupServerForTest;
import models.trello.*;
import models.trelloServer.TrelloClient;

class SaveTrackControllerTest implements SaveTrackObserverInterface
{
	
	private SaveTrackController instance;
	private ArrayList<User> users;
	private TrelloClient trelloClient;
	private boolean hasChanges;
	private int numCallsFromTracker;

	@BeforeEach
	void setUp() throws Exception
	{
		// Get the instance and set everything up that we need to test
		instance = SaveTrackController.getInstance();
		users = SetupServerForTest.start();
		trelloClient = new TrelloClient("localhost", "COWAN_TRELLO");
		hasChanges = false;
		numCallsFromTracker = 0;
	}
	
	@AfterEach
	void tearDown()
	{
		SaveTrackController.reset();
	}
	
	public void saveTrackHasChangesChanged()
	{
		// Update the hasChanges and increment the number of calls
		hasChanges = instance.hasChanges();
		numCallsFromTracker++;
	}
	
	private Board createNewBoard(String boardName, User user)
	{
		try
		{
			return trelloClient.createBoard(boardName, user);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		
		return null;
	}

	@Test
	void testSubscribeToChangesHasChanges()
	{
		// Get a user and board to play with
		User u1 = users.get(0);
		Board b1 = createNewBoard("b1", u1);
		Board b1Clone = b1.clone();
		
		// Make a change to the board and push it to the save tracker
		new BList("l1", b1);
		instance.addBoardChange(b1Clone, b1, u1);
		
		// Make sure that nothing incremented
		assertFalse(hasChanges);
		assertEquals(0, numCallsFromTracker);
		
		// Now, subscribe to changes
		SaveTrackController.subscribeToChanges(this);
		
		// Now, let's add another change and make sure we're updated as appropriate
		Board b2 = createNewBoard("b2", u1);
		Board b2Clone = b2.clone();
		new BList("l2", b2);
		instance.addBoardChange(b2Clone, b2, u1);
		
		// Make sure we've been informed
		assertTrue(hasChanges);
		assertEquals(1, numCallsFromTracker);
	}

	@Test
	void testUnsubscribeFromChanges()
	{
		// Let's first make sure we cannot unsubscribe if we haven't subscribed
		assertFalse(SaveTrackController.unsubscribeFromChanges(this));
		
		// Let's subscribe to changes and make sure we're subscribed
		SaveTrackController.subscribeToChanges(this);
		
		// Let's add a board and make sure we're updated as appropriate
		User u1 = users.get(0);
		Board b1 = createNewBoard("b1", u1);
		Board b1Clone = b1.clone();
		new BList("l1", b1);
		instance.addBoardChange(b1Clone, b1, u1);
		
		// Make sure we've been informed
		assertTrue(hasChanges);
		assertEquals(1, numCallsFromTracker);
		
		// Now, let's unsubscribe and reset ourselves
		assertTrue(SaveTrackController.unsubscribeFromChanges(this));
		hasChanges = false;
		numCallsFromTracker = 0;
		
		// Let's add another change
		Board b2 = createNewBoard("b2", u1);
		Board b2Clone = b2.clone();
		new BList("l2", b2);
		instance.addBoardChange(b2Clone, b2, u1);
		
		// Make sure we were not informed
		assertFalse(hasChanges);
		assertEquals(0, numCallsFromTracker);
		
		// Now, make sure we cannot unsubscribe again
		assertFalse(SaveTrackController.unsubscribeFromChanges(this));
	}

	@Test
	void testAddBoardChangeRemoveBoardChange()
	{
		// Let's subscribe to changes
		SaveTrackController.subscribeToChanges(this);
		
		// Now, let's add a board change and make sure it actually gets pushed
		User u1 = users.get(0);
		Board b1 = createNewBoard("b1", u1);
		Board b1Clone = b1.clone();
		new BList("l1", b1);
		instance.addBoardChange(b1Clone, b1, u1);
		
		// Make sure the changes exist
		assertTrue(instance.hasChanges());
		
		// Now, let's remove the board change and make sure the changes no longer exist
		instance.removeBoardChange(b1);
		assertFalse(instance.hasChanges());
		
		// Let's add multiple board changes now
		ArrayList<Board> addedBoards = new ArrayList<Board>();
		for (int i = 0; i < 10; i++)
		{
			User u = users.get(0);
			Board b = createNewBoard("b" + i, u);
			addedBoards.add(b);
			Board bClone = b.clone();
			new BList("l" + i, b);
			instance.addBoardChange(bClone, b, u);
			
			assertTrue(instance.hasChanges());
		}
		
		// Make sure all of the changes are there
		assertTrue(instance.hasChanges());
		
		// Now, let's remove each of the boards that we just created
		Iterator<Board> it = addedBoards.iterator();
		while (it.hasNext())
		{
			Board b = it.next();
			instance.removeBoardChange(b);
			it.remove();
			
			// Make sure the has changes is accurate
			if (it.hasNext())
				assertTrue(instance.hasChanges());
			else
				assertFalse(instance.hasChanges());
		}
		
		// Make sure we no longer have changes
		assertFalse(instance.hasChanges());
	}

	@Test
	void testSaveAllChanges()
	{
		// Let's subscribe to changes
		SaveTrackController.subscribeToChanges(this);
		
		// Now, let's add some changes
		ArrayList<Board> addedBoards = new ArrayList<Board>();
		for (int i = 0; i < 10; i++)
		{
			User u = users.get(0);
			Board b = createNewBoard("b" + i, u);
			addedBoards.add(b);
			Board bClone = b.clone();
			new BList("l" + i, b);
			instance.addBoardChange(bClone, b, u);
			
			assertTrue(instance.hasChanges());
		}
		
		// Now, let's save all the changes
		instance.saveAllChanges(trelloClient);
		
		// Now, let's make sure that no changes exist
		assertFalse(instance.hasChanges());
		
		// Let's check the server and make sure all of the boards have been updates
		try
		{
			User serverUser = trelloClient.authenticateUser(users.get(0).getName(), users.get(0).getPassword());
			ArrayList<Board> userBoards = serverUser.getBoards().getMembers();
			
			// Make sure the boards match all of the new boards
			for (Board b : userBoards)
			{
				assertTrue(addedBoards.contains(b));
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

}
