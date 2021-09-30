package controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import models.trello.User;
import models.trelloServer.TrelloServerInterface;
import views.ViewTransitionModelInterface;
import controllers.save.*;

/**
 * MasterController class
 *
 * @Implements SaveTrackObserverInterface
 */
public class MasterController implements SaveTrackObserverInterface
{

	@FXML
	private BorderPane borderPane;

	@FXML
	private Button saveButton;

	@FXML
	private Button backButton;

	private ViewTransitionModelInterface vm;
	private SaveTrackController saveTracker;
	private TrelloServerInterface trelloClient;
	private User user;
	private Stage stage;

	/**
	 * Updates the save button depending on whether or not there are changes
	 */
	public void updateSaveButton()
	{
		saveButton.setDisable(!saveTracker.hasChanges());
	}

	/**
	 * @param stage            - The stage that we're working with
	 * @param user             - User that we're working with
	 * @param trelloClient     - TrelloServerInterface that we're working with
	 * @param backButtonActive - Whether or not the back button should be active
	 */
	public void loadController(ViewTransitionModelInterface vm, Stage stage, User user, TrelloServerInterface trelloClient, boolean backButtonActive)
	{
		// Get the instance of the save tracker and subscribe to changes as an observer
		saveTracker = SaveTrackController.getInstance();
		SaveTrackController.subscribeToChanges(this);

		// Setup the object's attributes
		this.vm = vm;
		this.trelloClient = trelloClient;
		this.stage = stage;
		this.user = user;

		// Set the back button disable
		if (backButtonActive)
			backButton.setDisable(false);
		else
			backButton.setDisable(true);

		// Update the save button
		updateSaveButton();
	}

	/**
	 * This is called by the observable save tracker when there may be changes and
	 * the save button may need to be updated
	 */
	@Override
	public void saveTrackHasChangesChanged()
	{
		// Update the save button
		updateSaveButton();
	}

	/**
	 * This handles clicking on the exit button
	 */
	@FXML
	void exitButton_OnClick()
	{
		// Check if something has been unsaved
		if (saveTracker.hasChanges())
		{
			// If there are changes, prompt the user to either save, not save, or cancel
			vm.showExitSavePopup(stage, trelloClient);
		} else
		{
			// If there are no changes, let's just exit
			vm.closeProgram(stage);
		}
	}

	/**
	 * Handles clicking on the save button
	 */
	@FXML
	void saveButton_OnClick()
	{
		// Save all of the changes to this client
		saveTracker.saveAllChanges(trelloClient);
	}

	/**
	 * Handles clicking on the back button
	 */
	@FXML
	void backButton_OnClick()
	{
		// Go back to the user page
		vm.showUserPage(stage, user, trelloClient);
	}

}
