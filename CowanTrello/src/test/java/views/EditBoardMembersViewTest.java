package views;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import controllers.BoardMembersEditController;
import controllers.SetupServerForTest;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import models.trello.Board;
import models.trello.User;
import models.trelloServer.TrelloClient;

@ExtendWith(ApplicationExtension.class)
class EditBoardMembersViewTest
{

	private ViewTransitionModelTest vm;
	private ArrayList<User> users;
	private Board testBoard;
	private TrelloClient trelloClient;
	private BoardMembersEditController cont;
	
	@Start
	private void start(Stage stage)
	{	
		// Try loading the view
		try
		{
			// Setup the server and our client proxy
			users = SetupServerForTest.start();
			trelloClient = new TrelloClient("localhost", "COWAN_TRELLO");
			
			testBoard = new Board("Test Board", users.get(0));
			vm = new ViewTransitionModelTest();
			
			// Load the view
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(ViewTransitionModel.class.getResource("EditBoardMembersView.fxml"));
			Pane view = loader.load();

			// Setup the controller
			cont = loader.getController();
			cont.loadController(vm, testBoard, users.get(0), trelloClient);

			// Setup the scene
			Scene s = new Scene(view);
			stage.setScene(s);
			stage.setTitle("Edit Board Members");
			stage.setResizable(false);

			// Show the page
			stage.show();
		} catch (Exception e)
		{
			// Something unhandled occurred
			e.printStackTrace();
		}
	}
	
	@SuppressWarnings("unchecked")
	private void checkMembersListView(FxRobot robot)
	{
		// Get the lists
		ObservableList<User> membersListView = robot.lookup("#membersListView").queryAs(ListView.class).getItems();
		Set<User> boardMembers = testBoard.getMembers().getMembers();
		
		// Make sure the lists are the same
		for (User mem : boardMembers)
			assertTrue(membersListView.contains(mem));
		
		for (User mem : membersListView)
			assertTrue(boardMembers.contains(mem));
			
	}
	
	private void addMember(FxRobot robot, User user, String userName, boolean exists)
	{
		// Get the length of the old set of users
		int oldLen = testBoard.getMembers().getMembers().size();
		
		// If the user exists, let's see if they already exist as a member
		boolean isMember = false;
		if (exists)
			isMember = testBoard.hasMember(user);
		
		// Add the user
		robot.clickOn("#addTextField").write(userName).clickOn("#addButton");
		
		// See if the user should exist
		if (exists)
		{
			if (! isMember)
			{
				assertEquals(oldLen + 1, testBoard.getMembers().getMembers().size());
			}
			else
			{
				assertEquals(oldLen, testBoard.getMembers().getMembers().size());
			}
		}
		else
		{
			assertEquals(oldLen, testBoard.getMembers().getMembers().size());
			assertEquals(1, vm.showPopup);
			vm.resetCounters();
		}
		
		// Make sure the members are showing correctly
		checkMembersListView(robot);
	}
	
	private void testAddMembers(FxRobot robot)
	{
		// Let's make sure that the members are showing correctly to start
		checkMembersListView(robot);
		
		// Now, add some users that exist and try adding a user that already exists
		addMember(robot, users.get(1), users.get(1).getName(), true);
		addMember(robot, users.get(10), users.get(10).getName(), true);
		addMember(robot, users.get(9), users.get(9).getName(), true);
		addMember(robot, users.get(1), users.get(1).getName(), true);
		addMember(robot, new User("foreign user", "p"), "foreign user", false);
	}
	
	@SuppressWarnings("unchecked")
	private void removeMember(FxRobot robot, User user)
	{
		// Get the ListView
		ListView<User> membersListView = robot.lookup("#membersListView").queryAs(ListView.class);
		
		// Get the old length of the list
		int oldLen = membersListView.getItems().size();
		
		// Click on the user to remove
		membersListView.getSelectionModel().select(user);
		cont.setRemoveMemberButtonDisable(false); // Have to manually disable because of TestFX limitations
		robot.clickOn("#removeButton");
		
		// Check and see if the user is themselves or the owner
		if (testBoard.getOwner().equals(user) || users.get(0).equals(user))
		{
			// Nothing should happen, not even a popup
			assertEquals(0, vm.showPopup);
			assertEquals(oldLen, membersListView.getItems().size());
		}
		else
		{
			// The member should be removed now
			assertEquals(oldLen - 1, membersListView.getItems().size());
		}
		
		// Make sure the members are still showing correctly
		checkMembersListView(robot);
	}
	
	private void testRemoveMembers(FxRobot robot)
	{
		// Go ahead and setup the users for this test
		for (int i = 1; i < 5; i++)
			addMember(robot, users.get(i), users.get(i).getName(), true);
		
		// Make sure the members setup properly
		checkMembersListView(robot);
		
		// Now, make sure the remove button is currently disabled
		// No members are selected, so a popup should so if it gets clicked here
		robot.clickOn("#removeButton");
		assertEquals(0, vm.showPopup);
		
		// Now, let's test removing
		removeMember(robot, users.get(2));
		removeMember(robot, users.get(4));
		removeMember(robot, users.get(0));
		removeMember(robot, users.get(1));
		removeMember(robot, users.get(3));
	}
	
	private void testCloseButton(FxRobot robot)
	{
		// Click on the close button and make sure the correct method is called
		robot.clickOn("#closeButton");
		assertEquals(1, vm.closeStage);
		vm.resetCounters();
	}
	
	@Test
	public void test(FxRobot robot)
	{
		// Let's make sure the correct members are showing
		checkMembersListView(robot);
		
		// Now, test adding a member
		testAddMembers(robot);
		
		// Now, test removing members
		testRemoveMembers(robot);
		
		// Finally, test the close button
		testCloseButton(robot);
	}
}
