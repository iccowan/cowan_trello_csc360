package views;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import controllers.LoginController;
import controllers.SetupServerForTest;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import models.trello.User;

@ExtendWith(ApplicationExtension.class)
class LoginViewTest
{

	private ViewTransitionModelTest vm;
	private ArrayList<User> users;
	LoginController cont;
	
	@Start
	private void start(Stage stage)
	{	
		// Try loading the view
		try
		{
			// Setup the server and our client proxy
			users = SetupServerForTest.start();
			vm = new ViewTransitionModelTest();
			
			// Load the loader
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(ViewTransitionModel.class.getResource("LoginView.fxml"));
			AnchorPane view = loader.load();
			
			cont = loader.getController();
			cont.setupController(vm);

			// Setup and show the scene
			Scene s = new Scene(view);
			stage.setScene(s);
			stage.setTitle("Cowan Trello");
			stage.setResizable(false);
			stage.show();
		} catch (Exception e)
		{
			// Something unhandled occurred
			e.printStackTrace();
		}
	}
	
	private void writeServerAddress(FxRobot robot, String address)
	{	
		// Put in the new address
		robot.clickOn("#serverAddressTextField").write(address);
	}
	
	private void writeUsernamePassword(FxRobot robot, String username, String password)
	{	
		// Write in the new username and password
		robot.clickOn("#usernameTextField").write(username);
		robot.clickOn("#passwordTextField").write(password);
	}
	
	private void clickOnLogin(FxRobot robot)
	{
		// click on the login button
		robot.clickOn("#loginButton");
	}
	
	private void attemptLogin(FxRobot robot, String serverName, String username, String password)
	{
		// Try to connect to a random server
		writeServerAddress(robot, serverName);
		
		// Login the user
		writeUsernamePassword(robot, username, password);
		clickOnLogin(robot);
	}
	
	private void testBadServerAddress(FxRobot robot, User user)
	{	
		// Try to connect to a random server
		attemptLogin(robot, "somerandomserver.com", user.getName(), user.getPassword());
		
		// Make sure that a popup was called and that the user page was not called
		assertEquals(1, vm.showPopup);
		assertEquals(0, vm.showUserPage);
		vm.resetCounters();
	}
	
	private void testBadUsername(FxRobot robot, User user)
	{
		// Try a username that doesn't exist on the server
		attemptLogin(robot, "localhost", "foreign user", user.getPassword());
		
		// There should have been a popup called, but the user page should not have been called
		assertEquals(1, vm.showPopup);
		assertEquals(0, vm.showUserPage);
		vm.resetCounters();
		
		// Now, we test with a username that just doesn't match this user's
		attemptLogin(robot, "localhost", "user10", user.getPassword());
		
		// Same as earlier
		// There should have been a popup called, but the user page should not have been called
		assertEquals(1, vm.showPopup);
		assertEquals(0, vm.showUserPage);
		vm.resetCounters();
	}
	
	private void testBadPassword(FxRobot robot, User user)
	{
		// Try a password that doesn't exist on the server
		attemptLogin(robot, "localhost", user.getName(), "foreign password");
		
		// There should have been a popup called, but the user page should not have been called
		assertEquals(1, vm.showPopup);
		assertEquals(0, vm.showUserPage);
		vm.resetCounters();
		
		// Now, we test with a username that just doesn't match this user's
		attemptLogin(robot, "localhost", user.getName(), "password10");
		
		// Same as earlier
		// There should have been a popup called, but the user page should not have been called
		assertEquals(1, vm.showPopup);
		assertEquals(0, vm.showUserPage);
		vm.resetCounters();
	}
	
	private void testLogin(FxRobot robot, User user)
	{
		// Login a user that exists
		attemptLogin(robot, "localhost", user.getName(), user.getPassword());
		
		// Should have been no calls to a popup and there should have been a call to the user page
		assertEquals(0, vm.showPopup);
		assertEquals(1, vm.showUserPage);
		vm.resetCounters();
		
		// Let's just try with one more user
		User user2 = users.get(10);
		attemptLogin(robot, "localhost", user2.getName(), user2.getPassword());
		
		// Same as earlier
		// Should have been no calls to a popup and there should have been a call to the user page
		assertEquals(0, vm.showPopup);
		assertEquals(1, vm.showUserPage);
		vm.resetCounters();
	}
	
	@Test
	public void test(FxRobot robot)
	{
		// Get a guinea pig
		User user = users.get(0);
		
		// We begin by testing bad things and then we'll test good things
		// Test the hostname
		testBadServerAddress(robot, user);
		
		// Test the username
		testBadUsername(robot, user);
		
		// Test the password
		testBadPassword(robot, user);
		
		// Now, we test good login!
		testLogin(robot, user);
	}
}
