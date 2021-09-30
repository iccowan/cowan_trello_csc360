package views.trelloServer;

import static org.junit.jupiter.api.Assertions.*;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import controllers.trelloServer.TrelloServerInterfaceController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import models.trello.design.*;
import models.trelloServer.TrelloClient;
import models.trelloServer.TrelloServer;

@ExtendWith(ApplicationExtension.class)
class TrelloServerInterfaceTest
{

	private ViewTransitionModelTest vm;
	
	@Start
	private void start(Stage stage)
	{	
		// Try loading the view
		try
		{
			// Get a view transition model tester
			vm = new ViewTransitionModelTest();
			
			// Load the view
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(ViewTransitionModel.class.getResource("TrelloServerInterface.fxml"));
			Pane view = loader.load();

			// Setup the scene
			Scene s = new Scene(view);
			stage.setScene(s);
			stage.setTitle("Cowan Trello Server");
			stage.setResizable(false);

			// Setup the controller
			TrelloServerInterfaceController cont = loader.getController();
			cont.setupController(vm);

			// Show the stage
			stage.show();
		} catch (Exception e)
		{
			// Something unhandled occurred
			e.printStackTrace();
		}
	}
	
	private void selectDesign(FxRobot robot, String design)
	{	
		// Now, click the choicebox to register the selection
		// Then click on the title to get the mouse away to prevent selecting something else
		robot.clickOn("#serverDesignChoiceBox");
		robot.clickOn(design);
	}
	
	private void attemptStartServer(FxRobot robot, String serverName, String serverPort)
	{
		// Fill in the server name and port
		robot.interact(() -> {
			robot.lookup("#serverNameTextField").queryAs(TextField.class).setText("");
			robot.lookup("#serverPortTextField").queryAs(TextField.class).setText("");
		});
		robot.clickOn("#serverNameTextField").write(serverName);
		robot.clickOn("#serverPortTextField").write(serverPort);
		
		// Now, click the start button
		robot.clickOn("#startButton");
	}
	
	private boolean isServerRunning(String serverName)
	{
		try
		{
			// Try to get a client, aka connect to the server
			new TrelloClient("localhost", serverName);
			
			// We connected, so let's close the server and return true
			return true;
			
		}
		catch (RemoteException | NotBoundException e)
		{
			// Unable to connect
			return false;
		}
	}
	
	private void checkRunningDisables(FxRobot robot)
	{
		// Make sure all of the appropriate things are disabled
		assertTrue(robot.lookup("#centerVBox").queryAs(VBox.class).isDisable());
		assertTrue(robot.lookup("#startButton").queryAs(Button.class).isDisable());
		
		// Make sure we can stop the server
		assertFalse(robot.lookup("#stopButton").queryAs(Button.class).isDisable());
	}
	
	private void checkNotRunningDisables(FxRobot robot)
	{
		// Make sure all of the appropriate things are enabled
		assertFalse(robot.lookup("#centerVBox").queryAs(VBox.class).isDisable());
		assertFalse(robot.lookup("#startButton").queryAs(Button.class).isDisable());
		
		// Make sure we can not stop the server
		assertTrue(robot.lookup("#stopButton").queryAs(Button.class).isDisable());
	}
	
	private void attemptStopServer(FxRobot robot)
	{
		robot.clickOn("#stopButton");
	}
	
	@Test
	public void testDesignSelect(FxRobot robot)
	{
		// Let's select each design and make sure the designs switch
		for (String designKey : DesignFactory.designs.keySet())
		{
			Design actualDesign = DesignFactory.newDesign(DesignFactory.designs.get(designKey));
			selectDesign(robot, designKey);
			assertEquals(actualDesign.getDesign(), Application.getUserAgentStylesheet());
		}
	}
	
	@Test
	public void testStart(FxRobot robot)
	{
		// Try starting without typing in a server name
		attemptStartServer(robot, "", "1099");
		assertEquals(1, vm.showPopup);
		vm.resetCounters();
		assertFalse(isServerRunning(""));
		
		// Try to start a server without typing in a server port
		attemptStartServer(robot, "TRELLO_INT_TEST", "");
		assertEquals(1, vm.showPopup);
		vm.resetCounters();
		assertFalse(isServerRunning("TRELLO_INT_TEST"));
		
		// Try to start a server without a server name or port
		attemptStartServer(robot, "", "");
		assertEquals(1, vm.showPopup);
		vm.resetCounters();
		assertFalse(isServerRunning(""));
		
		// Now, let's try starting a server with a name and a non-integer port
		attemptStartServer(robot, "TRELLO_INT_TEST", "PORT");
		assertEquals(1, vm.showPopup);
		vm.resetCounters();
		assertFalse(isServerRunning("TRELLO_INT_TEST"));
		
		// Now, let's try starting a server with a good name and port
		attemptStartServer(robot, "TRELLO_INT_TEST", "1099");
		assertTrue(isServerRunning("TRELLO_INT_TEST"));
		
		// Let's make sure we cannot click on the start button or edit anything
		checkRunningDisables(robot);
		
		// Close the server so we don't break things for future tests
		TrelloServer.closeServer("TRELLO_INT_TEST", 1099);
	}
	
	@Test
	public void testStop(FxRobot robot)
	{
		// Make sure we cannot stop the server yet
		checkNotRunningDisables(robot);
		
		// Go ahead and start the server
		// Let's also make sure we actually started it
		attemptStartServer(robot, "TRELLO_INT_TEST", "1099");
		assertTrue(isServerRunning("TRELLO_INT_TEST"));
		
		// Now, let's try to stop the server
		attemptStopServer(robot);
		assertFalse(isServerRunning("TRELLO_INT_TEST"));
		
		// Make sure the buttons have reset as appropriate
		checkNotRunningDisables(robot);
	}
	
}