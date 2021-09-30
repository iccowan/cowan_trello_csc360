package controllers.trelloServer;

import javafx.application.Application;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import models.trello.design.*;
import models.trelloServer.TrelloServer;
import views.trelloServer.ViewTransitionModelInterface;

/**
 * TrelloServerInterfaceController class
 *
 */
public class TrelloServerInterfaceController
{
	
	@FXML
	private VBox mainVBox;

	@FXML
	private TextField serverNameTextField;

	@FXML
	private TextField serverPortTextField;

	@FXML
	private ChoiceBox<String> serverDesignDropDown;

	@FXML
	private Button startButton;

	@FXML
	private Button stopButton;
	
	private ViewTransitionModelInterface vm;
	private String serverName;
	private int serverPort;
	
	/**
	 * @param vm - ViewTransitionModelInterface that we're working with
	 */
	public void setupController(ViewTransitionModelInterface vm)
	{
		// Set the VM and scene
		this.vm = vm;
		
		// Setup the server design dropdown
		serverDesignDropDown.getItems().setAll(DesignFactory.designs.keySet());
		serverDesignDropDown.getSelectionModel().select("Default - Modena");
		
		// I hate adding a listener this way, but there is not other way to do it
		// through FXML...
		serverDesignDropDown.getSelectionModel().selectedItemProperty()
							.addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
								choiceBox_OnSelect(newValue);
							});
	}
	
	/**
	 * @param message - Message for the startup error
	 */
	private void startError(String message)
	{
		mainVBox.setDisable(false);
		vm.showPopup("Error Starting Server", message);
	}

	/**
	 * Runs when the start button is clicked
	 */
	@FXML
	void startButton_OnClick()
	{
		// Disable everything
		mainVBox.setDisable(true);
		
		// Get everything we're going to need to start the server
		serverName = serverNameTextField.getText();
		String serverPortString = serverPortTextField.getText();
		String serverDesign = DesignFactory.designs.get(serverDesignDropDown.getSelectionModel().getSelectedItem());
		
		// Do some validation
		if (serverName.equals(""))
		{
			startError("The server name cannot be blank");
			return;
		}
		
		try
		{
			serverPort = Integer.parseInt(serverPortString);
		}
		catch (NumberFormatException e)
		{
			startError("The server port must be a valid integer value");
			return;
		}
		
		// Start the server
		TrelloServer.startServer(serverName, serverPort, serverDesign);
		
		// The server started, so enable the stop button and disable the start button
		stopButton.setDisable(false);
		startButton.setDisable(true);
	}

	/**
	 * Runs when the stop button is clicked
	 */
	@FXML
	void stopButton_OnClick()
	{
		// Disable the stop button
		stopButton.setDisable(true);
		
		// Close the server
		TrelloServer.closeServer(serverName, serverPort);
		serverName = null;
		serverPort = 0;
		
		// Enable everything again
		startButton.setDisable(false);
		mainVBox.setDisable(false);
	}
	
	/**
	 * Runs when a selection is made in the ChoiceBox
	 * @param value - Value that is selected in the dropdown
	 */
	void choiceBox_OnSelect(String value)
	{
		// Let's allow the user to preview the selected design
		String designString = DesignFactory.designs.get(serverDesignDropDown.getSelectionModel().getSelectedItem());
		Design newDesign = DesignFactory.newDesign(designString);
		
		Application.setUserAgentStylesheet(newDesign.getDesign());
	}

}
