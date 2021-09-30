package views;

import controllers.*;
import controllers.save.SaveTrackController;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Accordion;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import models.trello.*;
import models.trelloServer.*;

/**
 * ViewTransitionModel class
 * 
 * This class is used to transition between views in the application
 *
 */
public class ViewTransitionModel implements ViewTransitionModelInterface
{
	
	/**
	 * @param stage        - Stage to display the master view within
	 * @param user         - User that we are working with
	 * @param trelloClient - TrelloServerInterface that we are working with
	 * @param isBoardPage  - Used to (de)activate the back button
	 * @return the master view BorderPane
	 */
	private BorderPane getMaster(Stage stage, User user, TrelloServerInterface trelloClient, boolean isBoardPage)
	{
		// Try loading
		try
		{
			// Load the view
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(ViewTransitionModel.class.getResource("MasterView.fxml"));
			BorderPane masterView = loader.load();

			// Get the controller and load the controller
			MasterController cont = (MasterController) loader.getController();
			cont.loadController(this, stage, user, trelloClient, isBoardPage);

			// Return the view
			return masterView;
		} catch (Exception e)
		{
			// Something unhandled occurred
			e.printStackTrace();
			showPopup("Unhandled Exception", e.getLocalizedMessage());
		}

		// Unable to load master view
		return null;
	}

	/**
	 * @param cont - BoardController that should be assigned when displayed
	 * @return the VBox of the new view
	 */
	public VBox getNewView(BoardController cont)
	{
		// Try loading the view
		try
		{
			// Get the loader
			FXMLLoader loader = new FXMLLoader();

			// Set the controller to the one we're working with
			loader.setLocation(ViewTransitionModel.class.getResource("NewView.fxml"));
			loader.setController(cont);

			// Load the view
			VBox view = loader.load();

			// Return the view
			return view;
		} catch (Exception e)
		{
			// something unhandled occurred
			e.printStackTrace();
			showPopup("Unhandled Exception", e.getLocalizedMessage());
		}

		// Unable to load the view
		return null;
	}

	/**
	 * @param cont - BoardController that we're working with
	 * @param c    - Card to display the BorderPane for
	 * @return the card's BorderPane
	 */
	@SuppressWarnings("unchecked")
	private BorderPane getCardBorderPane(BoardController cont, Card c)
	{
		// Try loading the view
		try
		{
			// Load the loader
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(ViewTransitionModel.class.getResource("list/BorderPaneAccordion.fxml"));

			// Set the controller that we're working with
			loader.setController(cont);

			// Load the view
			BorderPane bp = loader.load();

			// Get all of the ListView items lists for binding
			ObservableList<Label> labelsListView = ((ListView<Label>) bp.lookup("#labelsListView")).getItems();
			ObservableList<Component> componentsListView = ((ListView<Component>) bp.lookup("#componentsListView"))
					.getItems();
			ObservableList<User> membersListView = ((ListView<User>) bp.lookup("#membersListView")).getItems();

			// Bind all of the lists for the labels, components, and users
			Bindings.bindContentBidirectional(labelsListView, c.getLabels().getObservableMembers());
			Bindings.bindContentBidirectional(componentsListView, c.getComponents().getObservableMembers());
			Bindings.bindContentBidirectional(membersListView, c.getMembers().getObservableMembers());

			// Return the BorderPane
			return bp;
		} catch (Exception e)
		{
			// Something unhandled occurred
			e.printStackTrace();
			showPopup("Unhandled Exception", e.getLocalizedMessage());
		}

		// Unable to load the view
		return null;
	}

	/**
	 * @param cont - BoardController that we're working with
	 * @param c    - Card that we are setting up
	 * @return the TitledPane for the card
	 */
	private TitledPane setupCard(BoardController cont, Card c)
	{
		// Try to load the view
		try
		{
			// Load the loader
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(ViewTransitionModel.class.getResource("list/AccordionTitledPane.fxml"));

			// Set the controller we're working with
			loader.setController(cont);

			// Load the view
			TitledPane tp = loader.load();

			// Setup the TitledPane
			tp.setText(c.getName());
			tp.setContent(getCardBorderPane(cont, c));
			
			int listIndex = c.getList().getBoard().getLists().getMembers().indexOf(c.getList());
			int cardIndex = c.getList().getCards().getMembers().indexOf(c);
			tp.setId(listIndex + "card" + cardIndex);

			// Return the card's TitledPane
			return tp;
		} catch (Exception e)
		{
			// Something unhandled occurred
			e.printStackTrace();
			showPopup("Unhandled Exception", e.getLocalizedMessage());
		}

		// Unable to load the card's view
		return null;
	}

	/**
	 * @param cont - BoardController that we're working with
	 * @param list - List that we are setting up cards for
	 * @return the Accordion of cards
	 */
	private Accordion setupCards(BoardController cont, BList list)
	{
		// Try loading the view
		try
		{
			// Load the loader
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(ViewTransitionModel.class.getResource("list/ListAccordion.fxml"));

			// Set the controller we're working with
			loader.setController(cont);

			// Load the accordion
			Accordion acc = loader.load();
			acc.setId("accordion" + list.getName());

			// Foreach of the cards of the list, we are going to add a pane
			// of the setup card
			// This is necessary because there is no observable list that we can use with
			// this
			// form of displaying the cards. Creates some extra work, but the end result is
			// worth it
			for (Card c : list.getCards())
				acc.getPanes().add(setupCard(cont, c));

			// Return the accordion
			return acc;
		} catch (Exception e)
		{
			// Something unhandled occurred
			e.printStackTrace();
			showPopup("Unhandled Exception", e.getLocalizedMessage());
		}

		// Unable to load the view
		return null;
	}

	/**
	 * @param cont - BoardController that we're working with
	 * @param list - BList that we're generating a VBox for
	 * @return the VBox for the list
	 */
	private VBox cardsVBox(BoardController cont, BList list)
	{
		// Try loading the view
		try
		{
			// Load the loader
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(ViewTransitionModel.class.getResource("list/ListVBox.fxml"));

			// Set the controller that we're working with
			loader.setController(cont);

			// Load the view
			VBox vb = loader.load();
			Button newCardButton = (Button) vb.lookup("#newCardButton");
			newCardButton.setId("newCardButton" + list.getName());

			// Add the accordion of the cards
			vb.getChildren().add(0, setupCards(cont, list));

			// Return the VBox
			return vb;
		} catch (Exception e)
		{
			// Something unhandled occurred
			e.printStackTrace();
			showPopup("Unhandled Exception", e.getLocalizedMessage());
		}

		// Unable to load the view
		return null;
	}

	/**
	 * @param cont - BoardController that we're working with
	 * @param list - BList that we want the TitledPane for
	 * @return the BList's TitledPane
	 */
	public TitledPane getListTitledPane(BoardController cont, BList list)
	{
		// Try loading the view
		try
		{
			// Load the loader
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(ViewTransitionModel.class.getResource("list/ListTitledPane.fxml"));

			// Set the controller that we're working with
			loader.setController(cont);
			TitledPane tp = loader.load();

			// Setup the name of the TitledPane
			tp.setText(list.getName());

			// Setup the cards within the list
			tp.setContent(cardsVBox(cont, list));

			// Return the TitledPane
			return tp;
		} catch (Exception e)
		{
			// Something unhandled occurred
			e.printStackTrace();
			showPopup("Unhandled Exception", e.getLocalizedMessage());
		}

		// Unable to load the view
		return null;
	}

	/**
	 * @param stage - Stage for the login view
	 */
	public void showLogin(Stage stage)
	{	
		// Try loading the view
		try
		{
			// Load the loader
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(ViewTransitionModel.class.getResource("LoginView.fxml"));
			AnchorPane view = loader.load();
			
			LoginController cont = loader.getController();
			cont.setupController(this);

			// Setup and show the scene
			Scene s = new Scene(view);
			stage.setScene(s);
			stage.setTitle("Cowan Trello");
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
	 * @param stage        - Stage to show the view within
	 * @param user         - User that we are displaying
	 * @param trelloClient - TrelloServerInterface that we are working with
	 */
	public void showUserPage(Stage stage, User user, TrelloServerInterface trelloClient)
	{
		// Setup the stage so when it closes, it checks for changes
		stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
			
			public void handle(WindowEvent we) {
				// See if there are changes
				SaveTrackController saveTracker = SaveTrackController.getInstance();
				if (saveTracker.hasChanges())
				{
					// If we have changes, show the exit popup
					ViewTransitionModel vm = new ViewTransitionModel();
					vm.showExitSavePopup(stage, trelloClient);
				}
			}
			
		});
		
		// Try loading the view
		try
		{
			// Load the view
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(ViewTransitionModel.class.getResource("UserView.fxml"));
			BorderPane view = loader.load();

			// Get the controller
			UserController cont = (UserController) loader.getController();

			// Setup the controller
			cont.setUser(user);
			cont.setTrelloClient(trelloClient);
			cont.setViewTransitionModel(this);

			// Get the master BorderPane and put this view in the center of it
			BorderPane master = getMaster(stage, user, trelloClient, false);
			master.setCenter(view);

			// Load the scene and show the page if it isn't already showing
			Scene s = new Scene(master);
			//s.getStylesheets().add(ViewTransitionModel.styleSheet);
			stage.setScene(s);
			stage.setResizable(true);
			if (!stage.isShowing())
				stage.show();
		} catch (Exception e)
		{
			// Something unhandled occurred
			e.printStackTrace();
			showPopup("Unhandled Exception", e.getLocalizedMessage());
		}
	}

	/**
	 * @param title   - Title of the popup
	 * @param message - Message for the popup
	 */
	public void showPopup(String title, String message)
	{
		// Try loading the view
		try
		{
			// Create a new stage for the popup
			Stage stage = new Stage();

			// Load the view
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(ViewTransitionModel.class.getResource("PopupView.fxml"));
			Pane view = loader.load();

			// Setup the scene
			Scene s = new Scene(view);
			stage.setScene(s);
			stage.setTitle(title);
			stage.setResizable(false);

			// Setup the controller for the popup
			PopupController puCont = loader.getController();
			puCont.setMessage(message);
			puCont.setViewTransitionModel(this);

			// Show the popup
			stage.show();
		} catch (Exception e)
		{
			// Something unhandled occurred, but this is what we'd use to show the
			// issue so simply print the stack trace
			e.printStackTrace();
		}
	}

	/**
	 * @param mainStage     - The main stage that is displayed
	 * @param selectedBoard - The board that was selected
	 * @param user          - The user that we're working with
	 * @param trelloClient  - The TrelloServerInterface that we're working with
	 */
	public void showBoardPopup(Stage mainStage, Board selectedBoard, User user, TrelloServerInterface trelloClient)
	{
		// Try loading the view
		try
		{
			// New stage for the popup
			Stage stage = new Stage();

			// Load the view
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(ViewTransitionModel.class.getResource("BoardPopupView.fxml"));
			Pane view = loader.load();

			// Setup the scene
			Scene s = new Scene(view);
			stage.setScene(s);
			stage.setTitle(selectedBoard.getName());
			stage.setResizable(false);

			// Setup the controller
			BoardPopupController cont = loader.getController();
			cont.setStageUserBoardClient(this, mainStage, user, selectedBoard, trelloClient);

			// Show the popup
			stage.show();
		} catch (Exception e)
		{
			// Something unhandled occurred
			e.printStackTrace();
			showPopup("Unhandled Exception", e.getLocalizedMessage());
		}
	}

	/**
	 * @param stage        - Stage the is currently active
	 * @param user         - User that we are working with
	 * @param board        - Board that was selected
	 * @param trelloClient - TrelloServerInterface that we're working with
	 */
	public void showBoardPage(Stage stage, User user, Board board, TrelloServerInterface trelloClient)
	{
		// Try loading the view
		try
		{
			// Load the view
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(ViewTransitionModel.class.getResource("BoardView.fxml"));
			BorderPane view = loader.load();

			// Get the controller and setup the controller
			BoardController cont = (BoardController) loader.getController();
			cont.setupController(this, user, board, trelloClient);

			// Get the master view and set this view inside the center of master
			BorderPane master = getMaster(stage, user, trelloClient, true);
			master.setCenter(view);

			// Setup the scene and show the stage if it is not showing already
			Scene s = new Scene(master);
			stage.setScene(s);
			stage.setResizable(true);
			if (!stage.isShowing())
				stage.show();
		} catch (Exception e)
		{
			// Something unhandled occurred
			e.printStackTrace();
			showPopup("Unhandled Exception", e.getLocalizedMessage());
		}
	}

	/**
	 * @param board        - Board we're working with
	 * @param user         - User that we're working with
	 * @param trelloClient - TrelloServerInterface that we're working with
	 */
	public void showBoardMembersEditPage(Board board, User user, TrelloServerInterface trelloClient)
	{
		// Try loading the view
		try
		{
			// Create a new stage for the page
			Stage stage = new Stage();

			// Load the view
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(ViewTransitionModel.class.getResource("EditBoardMembersView.fxml"));
			Pane view = loader.load();

			// Setup the controller
			BoardMembersEditController cont = loader.getController();
			cont.loadController(this, board, user, trelloClient);

			// Setup the scene
			Scene s = new Scene(view);
			stage.setScene(s);
			stage.setTitle("Edit Board Members");
			stage.setResizable(false);

			// Show the page
			stage.show();
		} catch (Exception e)
		{
			// Something unhandled occurred
			e.printStackTrace();
			showPopup("Unhandled Exception", e.getLocalizedMessage());
		}
	}

	/**
	 * @param mainStage    - Main stage that we're working with
	 * @param trelloClient - TrelloServerInterface that we're working with
	 */
	public void showExitSavePopup(Stage mainStage, TrelloServerInterface trelloClient)
	{
		// Try loading the view
		try
		{
			// Create a new stage for the popup
			Stage stage = new Stage();

			// Load the view
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(ViewTransitionModel.class.getResource("ExitSavePopupView.fxml"));
			Pane view = loader.load();

			// Setup the controller
			ExitSavePopupController cont = loader.getController();
			cont.loadController(this, mainStage, trelloClient);

			// Setup the scene
			Scene s = new Scene(view);
			stage.setScene(s);
			stage.setTitle("Exit");
			stage.setResizable(false);

			// Show the popup
			stage.show();
		} catch (Exception e)
		{
			// Something unhandled occurred
			e.printStackTrace();
			showPopup("Unhandled Exception", e.getLocalizedMessage());
		}
	}
	
	/**
	 * @param stage - The stage to close
	 */
	public void closeStage(Stage stage)
	{
		stage.close();
	}
	
	/**
	 * stage - The main Stage that is being used
	 */
	public void closeProgram(Stage stage)
	{
		closeStage(stage);
		Platform.exit();
	}

}
