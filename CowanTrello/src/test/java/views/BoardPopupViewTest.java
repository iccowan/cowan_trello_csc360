package views;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.util.ArrayList;

import org.testfx.assertions.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import controllers.BoardPopupController;
import controllers.SetupServerForTest;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import models.trello.Board;
import models.trello.User;
import models.trelloServer.TrelloClient;

@ExtendWith(ApplicationExtension.class)
class BoardPopupViewTest
{

	private ViewTransitionModelTest vm;
	private ArrayList<User> users;
	private Board testBoard;
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
			
			// Load the view
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(ViewTransitionModel.class.getResource("BoardPopupView.fxml"));
			Pane view = loader.load();
			
			User user0 = users.get(0);
			testBoard = trelloClient.createBoard("test board", user0);

			// Setup the scene
			Scene s = new Scene(view);
			stage.setScene(s);
			stage.setTitle(testBoard.getName());
			stage.setResizable(false);
			
			vm = new ViewTransitionModelTest();

			// Setup the controller
			BoardPopupController cont = loader.getController();
			cont.setStageUserBoardClient(vm, new Stage(), user0, testBoard, trelloClient);

			// Show the popup
			stage.show();
		} catch (Exception e)
		{
			// Something unhandled occurred
			e.printStackTrace();
		}
	}
	
	private void testEditButtonClick(FxRobot robot)
	{
		// Ensure that the view is called to switch when the edit button is clicked
		robot.clickOn("#editButton");
		assertEquals(1, vm.showBoardPage);
		
		// Ensure that the edit button now closes the popup
		assertEquals(1, vm.closeStage);
		vm.resetCounters();
	}
	
	private void testDeleteButtonClick(FxRobot robot)
	{
		// Click on the button
		robot.clickOn("#deleteButton");
		
		// Ensure that the delete button actually removes the board
		assertFalse(users.get(0).getBoards().hasMember(testBoard));
		
		// Ensure that the delete button closes the popup
		assertEquals(1, vm.closeStage);
		vm.resetCounters();
	}
	
	private void testCancelButtonClick(FxRobot robot)
	{
		// Click on the button
		robot.clickOn("#cancelButton");
		
		// Ensure the cancel button closes the popup
		assertEquals(1, vm.closeStage);
		vm.resetCounters();
	}
	
	@Test
	public void test(FxRobot robot)
	{
		// Make sure the name was set properly
		Assertions.assertThat(robot.lookup("#boardNameLabel").queryAs(Label.class)).hasText(testBoard.getName());
		
		// Now, test the buttons
		testEditButtonClick(robot);
		testDeleteButtonClick(robot);
		testCancelButtonClick(robot);
	}
}
