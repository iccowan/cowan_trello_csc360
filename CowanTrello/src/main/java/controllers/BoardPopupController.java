package controllers;

import java.rmi.RemoteException;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import models.trello.*;
import models.trelloServer.TrelloServerInterface;
import models.trelloServer.UserNotFoundException;
import views.ViewTransitionModelInterface;

/**
 * BoardPopupController class
 *
 */
public class BoardPopupController
{

	@FXML
	private Pane pane;

	@FXML
	private Label boardNameLabel;
	
	private ViewTransitionModelInterface vm;
	private Stage stage;
	private User user;
	private Board selectedBoard;
	private TrelloServerInterface trelloClient;
	
	/**
	 * @param stage - Current stage that's showing
	 * @param user - User that we're working with
	 * @param selectedBoard - The selected board
	 * @param trelloClient - The TrelloServerInterface that we're working with
	 */
	public void setStageUserBoardClient(ViewTransitionModelInterface vm, Stage stage, User user, Board selectedBoard, TrelloServerInterface trelloClient)
	{
		// Setup the attributes
		this.vm = vm;
		this.stage = stage;
		this.user = user;
		this.selectedBoard = selectedBoard;
		this.trelloClient = trelloClient;
		
		// Set the name of the board on the label
		boardNameLabel.setText(selectedBoard.getName());
	}
	
	/**
	 * Closes the popup
	 */
	private void closePopup()
	{
		// Get and close this stage
		Stage stage = (Stage) pane.getScene().getWindow();
		vm.closeStage(stage);
	}

	/**
	 * Handles clicking the cancel button
	 */
	@FXML
	void cancelButton_OnClick()
	{
		// Close the popup
		closePopup();
	}

	/**
	 * Handles clicking the delete button
	 */
	@FXML
	void deleteButton_OnClick()
	{
		// Try deleting
		try
		{
			// Ping a request to delete the board to the working client
			boolean success = trelloClient.deleteBoard(selectedBoard, user);
			
			if (! success)
				// If not successful, show a popup
				vm.showPopup("Error Deleting Board", "There was an error deleting the board, please try again");
			else
				// If successful, remove the board locally so it doesn't show up and break things
				user.removeBoard(selectedBoard);
		}
		catch (RemoteException | UserNotFoundException e)
		{
			// Some error deleting the board
			vm.showPopup("Error Deleting Board", "Error connecting to the server, please try logging out and back in");
		}
		
		// Closes this popup
		closePopup();
	}

	/**
	 * Handles clicking the edit button
	 */
	@FXML
	void editButton_OnClick()
	{
		// Show the board page with the selected board
		vm.showBoardPage(stage, user, selectedBoard, trelloClient);
		
		// Close this popup
		closePopup();
	}

}
