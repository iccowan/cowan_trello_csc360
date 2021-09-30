package controllers;

import java.rmi.RemoteException;

import controllers.save.SaveTrackController;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import models.trello.*;
import models.trelloServer.TrelloServerInterface;
import models.trelloServer.UserNotFoundException;
import views.ViewTransitionModelInterface;

/**
 * BoardMembersEditController class
 *
 */
public class BoardMembersEditController
{

	@FXML
	private Pane pane;

	@FXML
	private ListView<User> membersList;

	@FXML
	private Button removeMemberButton;

	@FXML
	private TextField newMemberNameTextField;

	private ViewTransitionModelInterface vm;
	private TrelloServerInterface trelloClient;
	private Board board;
	private User user;
	private SaveTrackController saveTracker = SaveTrackController.getInstance();

	/**
	 * @param board - Board that we're working with
	 * @param user - User that we're working with
	 * @param trelloClient - TrelloServerInterface that we're working with
	 */
	public void loadController(ViewTransitionModelInterface vm, Board board, User user, TrelloServerInterface trelloClient)
	{
		// Set the attributes
		this.vm = vm;
		this.board = board;
		this.user = user;
		this.trelloClient = trelloClient;
		
		// Bind the members to the members list
		Bindings.bindContentBidirectional(membersList.getItems(), board.getMembers().getObservableMembers());
	}
	
	public void setRemoveMemberButtonDisable(boolean isDisable)
	{
		this.removeMemberButton.setDisable(isDisable);
	}

	/**
	 * Handles clicking the new member button
	 */
	@FXML
	void addNewMemberButton_OnClick()
	{
		// Try adding a new member
		try
		{
			// Clone the board for saving
			Board oldBoard = board.clone();
			
			// Find the user on the server
			User newMem = trelloClient.findUser(newMemberNameTextField.getText());
			newMemberNameTextField.setText("");
			
			// Don't allow duplicating members
			if (! board.hasMember(newMem))
			{
				// Add the new member
				board.addMember(newMem, user);
				
				// We have a new change that may need to be saved
				saveTracker.addBoardChange(oldBoard, board, user);
			}
		}
		catch (RemoteException e)
		{
			// Some server error
			vm.showPopup("Error Adding Member", "There was an error connecting to the server, please try again");
		}
		catch (UserNotFoundException e)
		{
			// A user couldn't be found
			vm.showPopup("Error Adding Member", "That user could not be found");
			newMemberNameTextField.setText("");
		}
	}

	/**
	 * Handles clicking the close button
	 */
	@FXML
	void closeButton_OnClick()
	{
		// Get and close this stage
		Stage stage = (Stage) pane.getScene().getWindow();
		vm.closeStage(stage);
	}

	/**
	 * Handles clicking on the members view
	 */
	@FXML
	void membersView_OnClick()
	{
		// Check if there is a selection on the members view
		User selUser = membersList.getSelectionModel().getSelectedItem();
		
		// If there is a selection and it is not this user (the owner),
		// show the remove member button
		if (selUser != null && (! selUser.equals(user)))
			removeMemberButton.setDisable(false);
	}

	/**
	 * Handles clicking the remove member button
	 */
	@FXML
	void removeMemberButton_OnClick()
	{
		// Get the selected user
		User selUser = membersList.getSelectionModel().getSelectedItem();
		
		// If everything is working, the selected user should never be null,
		// but still check to prevent breaking anything
		if (selUser != null)
		{
			// Clone the board for saving
			Board oldBoard = board.clone();
			
			// Remove the member from the board
			board.removeMember(selUser, user);
			
			// Save the changes
			saveTracker.addBoardChange(oldBoard, board, user);
			
			// Now, disable the remove button since the user isn't there any more
			removeMemberButton.setDisable(true);
		}
		else
		{
			// Show a popup since there was some error selecting a member
			vm.showPopup("Error Removing Member", "Please select a user to remove");
		}
	}

}
