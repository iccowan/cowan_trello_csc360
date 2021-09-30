package controllers;

import controllers.save.SaveTrackController;
import javafx.fxml.FXML;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import models.trelloServer.TrelloServerInterface;
import views.ViewTransitionModelInterface;

/**
 * ExitSavePopupController class
 *
 */
public class ExitSavePopupController
{

	@FXML
	private Pane pane;
	
	private Stage stage;
	private TrelloServerInterface trelloClient;
	private ViewTransitionModelInterface vm;
	
	/**
	 * @param stage - Current stage that we're working with
	 * @param trelloClient - TrelloServerInterface that we're working with
	 */
	public void loadController(ViewTransitionModelInterface vm, Stage stage, TrelloServerInterface trelloClient)
	{
		// Setup the attributes
		this.stage = stage;
		this.trelloClient = trelloClient;
		this.vm = vm;
	}
	
	/**
	 * Closes the popup
	 */
	private void closePopup()
	{
		// Get our stage and close the stage
		Stage gotStage = (Stage) pane.getScene().getWindow();
    	vm.closeStage(gotStage);
	}
	
	/**
	 * Close all stages
	 */
	private void closeAll()
	{
		// Close this popup
		closePopup();
		
		// Now, close the stage and make sure everything else in the
		// platform is closed as well
		vm.closeProgram(stage);
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
	 * Handles clicking on the don't save button
	 */
	@FXML
	void dontSaveButton_OnClick()
	{
		// Just quit and don't save anything
		closeAll();
	}

	// Handles clicking on the save button
	@FXML
	void saveButton_OnClick()
	{
		// Save everything and then we will quit
		SaveTrackController.getInstance().saveAllChanges(trelloClient);
		closeAll();
	}

}
