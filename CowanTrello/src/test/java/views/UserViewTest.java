package views;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import controllers.SetupServerForTest;
import controllers.UserController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import models.trello.Board;
import models.trello.User;
import models.trelloServer.TrelloClient;

@ExtendWith(ApplicationExtension.class)
class UserViewTest
{

	private UserController cont;
	private ViewTransitionModelTest vm;
	private ArrayList<User> users;
	private User testUser;
	private TrelloClient trelloClient;
	
	@Start
	private void start(Stage stage)
	{	
		// Try loading the view
		try
		{
			// Setup the server and our client proxy
			users = SetupServerForTest.start();
			trelloClient = new TrelloClient("localhost", "COWAN_TRELLO");
			vm = new ViewTransitionModelTest();
			
			// Load the view
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(ViewTransitionModel.class.getResource("UserView.fxml"));
			BorderPane view = loader.load();

			// Get the controller
			cont = (UserController) loader.getController();

			// Setup the controller
			testUser = users.get(0);
			cont.setUser(testUser);
			cont.setTrelloClient(trelloClient);
			cont.setViewTransitionModel(vm);

			// Load the scene and show the page if it isn't already showing
			Scene s = new Scene(view);
			stage.setScene(s);
			stage.show();
		} catch (Exception e)
		{
			// Something unhandled occurred
			e.printStackTrace();
		}
	}
	
	private void checkUserName(FxRobot robot)
	{
		// Get the text in the username label
		String displayUsername = robot.lookup("#usernameLabel").queryAs(Label.class).getText();
		
		// Make sure the displayed username matches
		assertEquals(testUser.getName(), displayUsername);
	}
	
	@SuppressWarnings("unchecked")
	private void checkBoards(FxRobot robot)
	{
		// Let's get the boards in the ListView and from the user
		ListView<Board> listedBoards = robot.lookup("#boardsListView").queryAs(ListView.class);
		ArrayList<Board> userBoards = testUser.getBoards().getMembers();
		
		// Make sure the 2 lists are the same size
		assertEquals(listedBoards.getItems().size(), userBoards.size());
		
		// Now, let's make sure the 2 lists match
		for (Board b : listedBoards.getItems())
			assertTrue(userBoards.contains(b));
	}
	
	private void addBoard(FxRobot robot, String boardName)
	{
		// Get the number of boards currently
		int oldLen = testUser.getBoards().getMembers().size();
		
		// Write in the new board name
		robot.clickOn("#boardNameTextField").write(boardName);
		
		// Click on the submit button
		robot.clickOn("#newBoardSubmitButton");
		
		// Now, we need to check and see what happened
		// If the boardName was empty string, a popup should have been called
		// If not, the boards should match and there should be an additional board
		if (boardName == "")
		{
			// Same length as old
			assertEquals(oldLen, testUser.getBoards().getMembers().size());
			
			// Make sure the board lists still match
			checkBoards(robot);
			
			// Make sure we got a popup
			assertEquals(1, vm.showPopup);
			vm.resetCounters();
		}
		else
		{
			// Check new length
			assertEquals(oldLen + 1, testUser.getBoards().getMembers().size());
			
			// Make sure the board lists match
			checkBoards(robot);
		}
	}
	
	private void testAddBoards(FxRobot robot)
	{
		// Make sure the boards still match
		checkBoards(robot);
		
		// Now, let's create some boards
		for (int i = 0; i < 10; i++)
			addBoard(robot, "New Board " + i);
		
		// Now, let's see what happens when we try to create a board
		// with no name
		addBoard(robot, "");
		
		// Make sure we can still add a new board now
		addBoard(robot, "New board again!");
	}
	
	@Test
	public void test(FxRobot robot)
	{
		// First, check and make sure the user's name is correct
		checkUserName(robot);
		
		// Let's also check and make sure the boards are displaying correctly to begin with
		checkBoards(robot);
		
		// Now, let's test adding a board
		testAddBoards(robot);
	}
}