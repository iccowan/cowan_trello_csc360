package views.trelloServer;

import javafx.stage.Stage;

/**
 * ViewTransitionModelInterface interface
 * 
 * This is the view transition model interface for the Trello Server Interface
 *
 */
public interface ViewTransitionModelInterface
{

	/**
	 * @param stage - The stage we're working with
	 */
	public void showTrelloServerInterface(Stage stage);
	
	/**
	 * @param title   - The title for the popup
	 * @param message - The message for the popup
	 */
	public void showPopup(String title, String message);
	
}
