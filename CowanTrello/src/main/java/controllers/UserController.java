package controllers;

import java.rmi.RemoteException;

import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import models.trello.*;
import models.trelloServer.*;
import views.ViewTransitionModelInterface;

/**
 * UserController class
 *
 */
public class UserController
{

	@FXML
	private Label usernameLabel;

	@FXML
	private ListView<Board> boardsList;

	@FXML
	private TextField newBoardTextField;

	private ViewTransitionModelInterface vm;
	private User user;
	private TrelloServerInterface trelloClient;

	/**
	 * @param user - User that we are working with
	 */
	public void setUser(User user)
	{
		// Set the user
		this.user = user;

		// Set the name of the user
		usernameLabel.setText(user.getName());

		// Make sure the observable members are updated
		Bindings.bindContentBidirectional(boardsList.getItems(), user.getBoards().getObservableMembers());
	}

	/**
	 * @param trelloClient - TrelloServerInterface that we are working with
	 */
	public void setTrelloClient(TrelloServerInterface trelloClient)
	{
		this.trelloClient = trelloClient;
	}
	
	/**
	 * @param vm - ViewTransitionModelInterface
	 */
	public void setViewTransitionModel(ViewTransitionModelInterface vm)
	{
		this.vm = vm;
	}

	/**
	 * Handles clicks on the newBoardSubmit button
	 */
	@FXML
	void newBoardSubmit_OnClick()
	{
		// If the text field is empty, this is an error
		if (newBoardTextField.getText().equals(""))
		{
			vm.showPopup("Invalid Board Name", "Please enter a name for the board");
			return;
		}

		// Try creating the new board
		try
		{
			// Get the new board from the server
			Board newBoard = trelloClient.createBoard(newBoardTextField.getText(), user);

			// Add the new board to the user
			user.addBoard(newBoard);

			// Clear the text field for the new board name
			newBoardTextField.setText("");
		} catch (UserNotFoundException e)
		{
			// No user found
			vm.showPopup("User Error",
					"User could not be found, please try logging out and loggin back in.");
		} catch (RemoteException e)
		{
			// Server error
			vm.showPopup("Server Error",
					"Could not connect to the server, please try logging out and loggin back in.");
		}
	}

	/**
	 * Handles clicks on the boards list
	 */
	@FXML
	void boardsList_OnClick()
	{
		// Get the selected board from the list
		Board selectedBoard = boardsList.getSelectionModel().getSelectedItem();

		// If the selectedBoard is null, no board was selected
		if (selectedBoard != null)
		{
			// Get our current stage
			Stage stage = (Stage) usernameLabel.getScene().getWindow();

			// Show the popup for the board
			vm.showBoardPopup(stage, selectedBoard, user, trelloClient);

			// Clear the selection
			boardsList.getSelectionModel().clearSelection();
		} else
		{
			// Error selecting the board
			vm.showPopup("Error Selecting Board",
					"There was an error selecting the board, please try again.");
		}
	}

}
