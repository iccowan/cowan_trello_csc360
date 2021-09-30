package views;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import controllers.ExitSavePopupController;
import controllers.SetupServerForTest;
import controllers.save.SaveTrackController;
import controllers.save.SaveTrackObserverInterface;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import models.trello.BList;
import models.trello.Board;
import models.trello.User;
import models.trelloServer.TrelloClient;

@ExtendWith(ApplicationExtension.class)
class ExitSavePopupViewTest implements SaveTrackObserverInterface
{

	private ViewTransitionModelTest vm;
	private ArrayList<User> users;
	private TrelloClient trelloClient;
	private boolean saveDetected = false;
	private SaveTrackController saveTracker = SaveTrackController.getInstance();
	
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
			
			// Subscribe to save track controller updates
			SaveTrackController.subscribeToChanges(this);
			
			// Load the view
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(ViewTransitionModel.class.getResource("ExitSavePopupView.fxml"));
			Pane view = loader.load();

			// Setup the controller
			ExitSavePopupController cont = loader.getController();
			cont.loadController(vm, stage, trelloClient);

			// Setup the scene
			Scene s = new Scene(view);
			stage.setScene(s);
			stage.setTitle("Exit");
			stage.setResizable(false);

			// Show the popup
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
		// A save has been detected
		saveDetected = true;
	}
	
	private void addNewChange()
	{
		// Let's put a change into the save track controller instance
		try
		{
			User user = users.get(0);
			Board newBoard = trelloClient.createBoard("Test", user);
			Board newBoardClone = newBoard.clone();
			
			new BList("new list", newBoard);
			saveTracker.addBoardChange(newBoard, newBoardClone, user);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	private void testSaveButton(FxRobot robot)
	{	
		addNewChange();
		
		// Now, let's make sure that we know no saves have occurred
		saveDetected = false;
		
		// Now click on the save button and we should see that it calls the close function for the program
		// and a save should be detected
		robot.clickOn("#saveButton");
		
		assertTrue(saveDetected);
		assertEquals(1, vm.closeProgram);
		vm.resetCounters();
	}
	
	private void testDontSaveButton(FxRobot robot)
	{
		addNewChange();
		
		// Make sure we know that no saves have occurred
		saveDetected = false;
		
		// Now click on the button and the only thing that should be called is the close program method
		robot.clickOn("#dontSaveButton");
		
		assertFalse(saveDetected);
		assertEquals(1, vm.closeProgram);
		vm.resetCounters();
	}
	
	private void testCancelButton(FxRobot robot)
	{
		addNewChange();
		
		// Make sure we know no saves have occurred
		saveDetected = false;
		
		// Now click the button and only the popup should close now
		robot.clickOn("#cancelButton");
		assertEquals(1, vm.closeStage);
		assertEquals(0, vm.closeProgram);
		vm.resetCounters();
	}
	
	@Test
	public void test(FxRobot robot)
	{
		// Test the save button
		testSaveButton(robot);
		
		// Test the don't save button
		testDontSaveButton(robot);
		
		// Test the cancel button
		testCancelButton(robot);
	}
}