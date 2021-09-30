package controllers.trelloServer;

import javafx.application.Application;
import javafx.stage.Stage;
import views.trelloServer.ViewTransitionModel;

/**
 * MainController class
 * 
 * This is the main controller for the Trello Server Interface
 * 
 * @extends Application
 *
 */
public class MainController extends Application
{

	/**
	 * @param stage - The stage we're working with
	 */
	@Override
	public void start(Stage stage) throws Exception
	{
		// Startup the application
		ViewTransitionModel vm = new ViewTransitionModel();
		vm.showTrelloServerInterface(stage);
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		launch(args);
	}

}
