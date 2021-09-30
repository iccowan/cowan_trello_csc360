package controllers;

import javafx.application.Application;
import javafx.stage.Stage;
import views.ViewTransitionModel;

/**
 * MainController class
 *
 * @extends Application
 */
public class MainController extends Application
{

	/**
	 * @param stage - Stage of the application
	 */
	@Override
	public void start(Stage stage) throws Exception
	{
		// Show the login to start the application
		ViewTransitionModel vm = new ViewTransitionModel();
		vm.showLogin(stage);
	}

	/**
	 * @param args - Main args
	 */
	public static void main(String[] args)
	{
		// Launch the application
		launch(args);
	}

}
