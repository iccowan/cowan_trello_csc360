package views;

import controllers.BoardController;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import models.trello.BList;
import models.trello.Board;
import models.trello.User;
import models.trelloServer.TrelloServerInterface;

public class ViewTransitionModelTest implements ViewTransitionModelInterface
{
	
	int getNewView = 0;
	int getListTitledPane = 0;
	int showLogin = 0;
	int showUserPage = 0;
	int showPopup = 0;
	int showBoardPopup = 0;
	int showBoardPage = 0;
	int showBoardMembersEditPage = 0;
	int showExitSavePopup = 0;
	int closeStage = 0;
	int closeProgram = 0;

	@Override
	public VBox getNewView(BoardController cont)
	{
		getNewView++;
		cont.setCreateNewTitleLabel(new Label());
		cont.setCreateNewSubmitButton(new Button());
		return new VBox();
	}

	@Override
	public TitledPane getListTitledPane(BoardController cont, BList list)
	{
		getListTitledPane++;
		return new TitledPane();
	}

	@Override
	public void showLogin(Stage stage)
	{
		showLogin++;
		
	}

	@Override
	public void showUserPage(Stage stage, User user, TrelloServerInterface trelloClient)
	{
		showUserPage++;
		
	}

	@Override
	public void showPopup(String title, String message)
	{
		showPopup++;
		
	}

	@Override
	public void showBoardPopup(Stage mainStage, Board selectedBoard, User user, TrelloServerInterface trelloClient)
	{
		showBoardPopup++;
		
	}

	@Override
	public void showBoardPage(Stage stage, User user, Board board, TrelloServerInterface trelloClient)
	{
		showBoardPage++;
		
	}

	@Override
	public void showBoardMembersEditPage(Board board, User user, TrelloServerInterface trelloClient)
	{
		showBoardMembersEditPage++;
		
	}

	@Override
	public void showExitSavePopup(Stage mainStage, TrelloServerInterface trelloClient)
	{
		showExitSavePopup++;
		
	}
	
	@Override
	public void closeStage(Stage stage)
	{
		closeStage++;
	}
	
	@Override
	public void closeProgram(Stage stage)
	{
		closeProgram++;
	}
	
	public void resetCounters()
	{
		getNewView = 0;
		getListTitledPane = 0;
		showLogin = 0;
		showUserPage = 0;
		showPopup = 0;
		showBoardPopup = 0;
		showBoardPage = 0;
		showBoardMembersEditPage = 0;
		showExitSavePopup = 0;
		closeStage = 0;
		closeProgram = 0;
	}

}
