package controllers;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;

import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import models.trello.*;
import models.trelloServer.*;
import views.ViewTransitionModelInterface;

/**
 * LoginController class
 *
 */
public class LoginController
{

	@FXML
	private TextField serverAddressTextField;

	@FXML
	private TextField usernameTextField;

	@FXML
	private PasswordField passwordTextField;
	
	private ViewTransitionModelInterface vm;
	
	/**
	 * @param vm - ViewTransitionModelInterface
	 */
	public void setupController(ViewTransitionModelInterface vm)
	{
		// Set attributes
		this.vm = vm;
	}
	
	public void setServerAddressTextField(String newAddress)
	{
		this.serverAddressTextField.setText(newAddress);
	}
	
	public void setUsernameTextField(String username)
	{
		this.usernameTextField.setText(username);
	}
	
	public void setPasswordTextField(String password)
	{
		this.passwordTextField.setText(password);
	}

	/**
	 * @param reason - Reason that the login was invalid
	 * 
	 *               0: Server error; 1: User not found; 2: Incorrect password
	 */
	private void invalidLogin(int reason)
	{
		// Default label if some random integer gets passed for some reason
		String label = "Error logging in, please try again";
		
		// Set the label
		switch (reason)
		{
		case 0:
			label = "Unable to connect to server, please try again";
			break;
		case 1:
			label = "That user could not be found, please try again";
			break;
		case 2:
			label = "Incorrect password, please try again";
			break;
		}

		// Show the popup with the error
		vm.showPopup("Error Logging In", label);
	}

	/**
	 * @param stage - Current stage
	 * @param user - User that we're working with
	 * @param trelloClient - TrelloServerInterface that we're working with
	 */
	private void authenticated(Stage stage, User user, TrelloServerInterface trelloClient)
	{
		// Show the user page
		vm.showUserPage(stage, user, trelloClient);
	}

	/**
	 * Handles clicking the login button
	 */
	@FXML
	void loginButton_OnClick()
	{
		// Get the server address, username, and password for the requested login
		String serverAddress = serverAddressTextField.getText();
		String username = usernameTextField.getText();
		String password = passwordTextField.getText();
		serverAddressTextField.setText("");
		usernameTextField.setText("");
		passwordTextField.setText("");
		
		// Get the current stage
		Stage stage = (Stage) serverAddressTextField.getScene().getWindow();

		try
		{
			// Connect to the trello server that the user requested
			TrelloServerInterface trelloClient = new TrelloClient(serverAddress, "COWAN_TRELLO");
			
			// Setup the stylesheet
			Application.setUserAgentStylesheet(trelloClient.getDesign());
			
			// Attempt to authenticate the user
			User user = trelloClient.authenticateUser(username, password);

			// Make sure the user was actually authenticated
			if (user == null)
			{
				// Wrong password, but user found
				invalidLogin(2);
			} else
			{
				// Authenticated
				authenticated(stage, user, trelloClient);
			}
		} catch (RemoteException | NotBoundException e)
		{
			// Some server error
			invalidLogin(0);
		} catch (UserNotFoundException e)
		{
			// The user wasn't found
			invalidLogin(1);
		}
	}

}
