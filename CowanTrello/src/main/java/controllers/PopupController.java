package controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import views.ViewTransitionModelInterface;

/**
 * PopupController class
 *
 */
public class PopupController
{

	@FXML
	private Pane pane;

	@FXML
	private Label messageLabel;
	
	private ViewTransitionModelInterface vm;

	/**
	 * @param msg - Message to set for the popup
	 */
	public void setMessage(String msg)
	{
		messageLabel.setText(msg);
	}
	
	public void setViewTransitionModel(ViewTransitionModelInterface vm)
	{
		this.vm = vm;
	}

	/**
	 * Handles clicks on the closeButton
	 */
	@FXML
	void closeButton_OnClick()
	{
		// Get the stage and close
		Stage stage = (Stage) pane.getScene().getWindow();
		vm.closeStage(stage);
	}

}
