package views;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import controllers.PopupController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

@ExtendWith(ApplicationExtension.class)
class PopupViewTest
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
			loader.setLocation(ViewTransitionModel.class.getResource("PopupView.fxml"));
			Pane view = loader.load();

			// Setup the scene
			Scene s = new Scene(view);
			stage.setScene(s);
			stage.setTitle("Test Popup");
			stage.setResizable(false);

			// Setup the controller for the popup
			PopupController puCont = loader.getController();
			puCont.setMessage("Testing the popup...");
			puCont.setViewTransitionModel(vm);

			// Show the popup
			stage.show();
		} catch (Exception e)
		{
			// Something unhandled occurred
			e.printStackTrace();
		}
	}
	
	private void checkLabel(FxRobot robot)
	{
		// Get the label text
		String labelText = robot.lookup("#messageLabel").queryAs(Label.class).getText();
		
		// Make sure it's what we set it to
		assertEquals("Testing the popup...", labelText);
	}
	
	private void testCloseButton(FxRobot robot)
	{
		// Click the close button
		robot.clickOn("#closeButton");
		
		// Make sure the proper call was made
		assertEquals(1, vm.closeStage);
		vm.resetCounters();
	}
	
	@Test
	public void test(FxRobot robot)
	{
		// Check all of the labels
		checkLabel(robot);
		
		// Test the close button
		testCloseButton(robot);
	}
}