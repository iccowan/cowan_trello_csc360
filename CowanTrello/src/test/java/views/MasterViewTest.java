package views;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import controllers.MasterController;
import controllers.SetupServerForTest;
import controllers.save.SaveTrackController;
import controllers.save.SaveTrackObserverInterface;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import models.trello.BList;
import models.trello.Board;
import models.trello.User;
import models.trelloServer.TrelloClient;

@ExtendWith(ApplicationExtension.class)
class MasterViewTest implements SaveTrackObserverInterface
{

	private ViewTransitionModelTest vm;
	private MasterController cont;
	private ArrayList<User> users;
	private TrelloClient trelloClient;
	private boolean backButtonActive = true;
	private SaveTrackController saveTracker = SaveTrackController.getInstance();
	private boolean hasChanges = false;
	private int saveTrackerUpdates = 0;
	private Stage stage;
	
	@Start
	private void start(Stage stage)
	{	
		// Try loading the view
		try
		{
			// Setup the server and our client proxy
			users = SetupServerForTest.start();
			this.stage = stage;
			trelloClient = new TrelloClient("localhost", "COWAN_TRELLO");
			vm = new ViewTransitionModelTest();
			
			// Reset the save tracker each time
			SaveTrackController.reset();
			SaveTrackController.subscribeToChanges(this);
			
			// Load the view
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(ViewTransitionModel.class.getResource("MasterView.fxml"));
			BorderPane masterView = loader.load();

			// Get the controller and load the controller
			cont = (MasterController) loader.getController();
			cont.loadController(vm, stage, users.get(0), trelloClient, backButtonActive);
			
			Scene s = new Scene(masterView);
			stage.setScene(s);
			stage.setTitle("Master View");

			stage.show();
		} catch (Exception e)
		{
			// Something unhandled occurred
			e.printStackTrace();
		}
	}
	
	@Override
	public void saveTrackHasChangesChanged()
	{
		hasChanges = saveTracker.hasChanges();
		saveTrackerUpdates++;
	}
	
	private void clickBackButton(FxRobot robot)
	{
		// Click on the button
		robot.clickOn("#backButton");
		
		// Let's check whether or not anything should've happened
		if (backButtonActive)
		{
			// If we're on the board page, the user page should have been requested
			assertEquals(1, vm.showUserPage);
			vm.resetCounters();
		}
		else
		{
			// Anywhere else, there should have been no request to the user page
			assertEquals(0, vm.showUserPage);
			vm.resetCounters();
		}
	}
	
	private void clickSaveButton(FxRobot robot)
	{
		// Let's see if there are any changes that need to be made
		boolean changes = hasChanges;
		
		// Now, let's click the save button
		robot.clickOn("#saveButton");
		
		// Let's test depending on whether or not there were changes
		if (changes)
		{
			// There should have been an update and there should be no more changes
			assertEquals(1, saveTrackerUpdates);
			assertFalse(hasChanges);
			saveTrackerUpdates = 0;
		}
		else
		{
			// There should have been no update and there should still be no changes
			assertEquals(0, saveTrackerUpdates);
			assertFalse(hasChanges);
			saveTrackerUpdates = 0;
		}
	}
	
	private void clickExitButton(FxRobot robot)
	{
		// Click the exit button
		robot.clickOn("#exitButton");
		
		// If there are changes, then it should have opened the exit popup
		// If not, it should have called the exit program
		if (hasChanges)
		{
			assertEquals(1, vm.showExitSavePopup);
			vm.resetCounters();
		}
		else
		{
			assertEquals(1, vm.closeProgram);
			vm.resetCounters();
		}
	}
	
	// We'll run all of the tests separately so the saveTrackerUpdates always starts at 0
	@Test
	public void testSaveButton(FxRobot robot)
	{
		// Try clicking the save button
		clickSaveButton(robot);
		
		// Now, let's add a change to the save tracker
		try
		{
			// Create a new board
			User user = users.get(0);
			Board newBoard = trelloClient.createBoard("test board", user);
			Board newBoardClone = newBoard.clone();
			new BList("new list", newBoard);
			
			// Push the change to the save tracker
			saveTracker.addBoardChange(newBoard, newBoardClone, user);
			saveTrackerUpdates = 0; // this will push a change to the save tracker, but we don't want to track this
			
			// Now, let's try clicking the save button again
			clickSaveButton(robot);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		
		// One more time to ensure everything reset correctly
		clickSaveButton(robot);
	}
	
	@Test
	public void testBackButton(FxRobot robot)
	{
		// Try clicking the back button
		clickBackButton(robot);
		
		// Now, let's reload the controller assuming we are not on a board page
		backButtonActive = false;
		cont.loadController(vm, stage, users.get(0), trelloClient, backButtonActive);
		
		// Try clicking the back button again
		clickBackButton(robot);
	}
	
	@Test
	public void testExitButton(FxRobot robot)
	{
		// Try clicking the exit button
		clickExitButton(robot);
		
		// Now, let's push some changes to the save tracker
		try
		{
			// Create a new board
			User user = users.get(0);
			Board newBoard = trelloClient.createBoard("test board", user);
			Board newBoardClone = newBoard.clone();
			new BList("new list", newBoard);
			
			// Push the change to the save tracker
			saveTracker.addBoardChange(newBoard, newBoardClone, user);
			
			clickExitButton(robot);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		
		// Save changes then click the button again
		saveTracker.saveAllChanges(trelloClient);
		clickExitButton(robot);
	}

}