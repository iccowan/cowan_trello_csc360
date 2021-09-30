package views.trelloServer;

import javafx.stage.Stage;

public class ViewTransitionModelTest implements ViewTransitionModelInterface
{
	
	int showTrelloServerInterface = 0;
	int showPopup = 0;

	@Override
	public void showTrelloServerInterface(Stage stage)
	{
		showTrelloServerInterface++;
	}

	@Override
	public void showPopup(String title, String message)
	{
		showPopup++;
	}
	
	void resetCounters()
	{
		showTrelloServerInterface = 0;
		showPopup = 0;
	}

}
