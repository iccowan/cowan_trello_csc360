package views.trelloServer;

import controllers.trelloServer.TrelloServerInterfaceController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

/**
 * ViewTransitionModel class
 *
 * This is the view transition model for the Trello Server's interface
 * 
 * @implements ViewTransitionModelInterface
 * 
 */
public class ViewTransitionModel implements ViewTransitionModelInterface
{

	private views.ViewTransitionModel popupVM = new views.ViewTransitionModel();

	/**
	 * @param stage - The stage we're working with
	 */
	public void showTrelloServerInterface(Stage stage)
	{
		// Try loading the view
		try
		{
			// Load the loader
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(ViewTransitionModel.class.getResource("TrelloServerInterface.fxml"));
			BorderPane view = loader.load();

			// Setup and show the scene
			Scene s = new Scene(view);

			TrelloServerInterfaceController cont = loader.getController();
			cont.setupController(this);

			stage.setScene(s);
			stage.setTitle("Cowan Trello Server");
			stage.setResizable(false);
			stage.show();
		} catch (Exception e)
		{
			// Something unhandled occurred
			e.printStackTrace();
			showPopup("Unhandled Exception", e.getLocalizedMessage());
		}
	}

	/**
	 * @param title   - The title for the popup
	 * @param message - The message for the popup
	 */
	public void showPopup(String title, String message)
	{
		// Show the popup
		// Yes, I'm just using the code from the actually Trello program
		// because work smarter, not harder ;)
		popupVM.showPopup(title, message);
	}

}
