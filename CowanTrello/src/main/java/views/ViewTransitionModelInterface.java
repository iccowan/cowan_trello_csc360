package views;

import controllers.BoardController;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import models.trello.*;
import models.trelloServer.*;

/**
 * ViewTransitionModelInterface interface
 * 
 * This class is used to transition between views in the application
 *
 */
public interface ViewTransitionModelInterface
{
	
	/**
	 * @param cont - BoardController that should be assigned when displayed
	 * @return the VBox of the new view
	 */
	public VBox getNewView(BoardController cont);
	
	/**
	 * @param cont - BoardController that we're working with
	 * @param list - BList that we want the TitledPane for
	 * @return the BList's TitledPane
	 */
	public TitledPane getListTitledPane(BoardController cont, BList list);

	/**
	 * @param stage - Stage for the login view
	 */
	public void showLogin(Stage stage);

	/**
	 * @param stage        - Stage to show the view within
	 * @param user         - User that we are displaying
	 * @param trelloClient - TrelloClient that we are working with
	 */
	public void showUserPage(Stage stage, User user, TrelloServerInterface trelloClient);

	/**
	 * @param title   - Title of the popup
	 * @param message - Message for the popup
	 */
	public void showPopup(String title, String message);

	/**
	 * @param mainStage     - The main stage that is displayed
	 * @param selectedBoard - The board that was selected
	 * @param user          - The user that we're working with
	 * @param trelloClient  - The TrelloClient that we're working with
	 */
	public void showBoardPopup(Stage mainStage, Board selectedBoard, User user, TrelloServerInterface trelloClient);

	/**
	 * @param stage        - Stage the is currently active
	 * @param user         - User that we are working with
	 * @param board        - Board that was selected
	 * @param trelloClient - TrelloClient that we're working with
	 */
	public void showBoardPage(Stage stage, User user, Board board, TrelloServerInterface trelloClient);

	/**
	 * @param board        - Board we're working with
	 * @param user         - User that we're working with
	 * @param trelloClient - TrelloClient that we're working with
	 */
	public void showBoardMembersEditPage(Board board, User user, TrelloServerInterface trelloClient);

	/**
	 * @param mainStage    - Main stage that we're working with
	 * @param trelloClient - TrelloClient that we're working with
	 */
	public void showExitSavePopup(Stage mainStage, TrelloServerInterface trelloClient);
	
	/**
	 * @param stage - The Stage to close
	 */
	public void closeStage(Stage stage);
	
	/**
	 * @param stage - The main Stage in use to close
	 */
	public void closeProgram(Stage stage);
	
}
