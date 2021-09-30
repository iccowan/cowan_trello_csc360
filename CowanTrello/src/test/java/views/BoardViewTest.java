package views;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.Random;
import java.util.Set;

import org.testfx.assertions.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import controllers.BoardController;
import controllers.SetupServerForTest;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Accordion;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import models.trello.BList;
import models.trello.Board;
import models.trello.Card;
import models.trello.Component;
import models.trello.User;
import models.trello.structures.HasMembersList;
import models.trelloServer.TrelloClient;

@ExtendWith(ApplicationExtension.class)
class BoardViewTest
{

	private BoardController cont;
	private ViewTransitionModelTest vm;
	private ViewTransitionModel vmAct;
	private ArrayList<User> users;
	private Board testBoard;
	private TrelloClient trelloClient;
	
	@Start
	private void start(Stage stage)
	{	
		// Try loading the view
		try
		{
			// Setup the server and our client proxy
			users = SetupServerForTest.start();
			trelloClient = new TrelloClient("localhost", "COWAN_TRELLO");
			
			// Load the view
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(ViewTransitionModel.class.getResource("BoardView.fxml"));
			Pane view = loader.load();
			
			User user0 = users.get(0);
			testBoard = trelloClient.createBoard("test board", user0);
			Board testBoardClone = testBoard.clone();
			
			// Add some lists to the board for testing
			new Card("Card1", new BList("List1", testBoard));
			new BList("List2", testBoard);
			trelloClient.updateBoard(testBoardClone, testBoard, user0);

			// Setup the scene
			Scene s = new Scene(view);
			stage.setScene(s);
			stage.setTitle(testBoard.getName());
			stage.setResizable(false);
			
			// We'll create a test VM for testing and an actual one so that we can
			// test some of the different things like editing names
			vm = new ViewTransitionModelTest();
			vmAct = new ViewTransitionModel();

			// Setup the controller
			// Initially we'll use an actual VM so everything sets up properly
			cont = loader.getController();
			cont.setupController(vmAct, user0, testBoard, trelloClient);
			
			// Now since everything will be setup, switch the VM to our testing model
			cont.setViewTransitionModel(vm);

			// Show the stage
			stage.show();
		} catch (Exception e)
		{
			// Something unhandled occurred
			e.printStackTrace();
		}
	}
	
	private void testEditBoardMembersButton(FxRobot robot)
	{
		// Click on the button
		robot.clickOn("#editMembersButton");
		
		// Make sure the proper view was called
		assertEquals(1, vm.showBoardMembersEditPage);
		vm.resetCounters();
		
		// We now need to test and make sure a non-member is not able
		// to click the edit members button
		robot.interact(new BoardViewTestCallController(this) {

			@Override
			public void run()
			{
				// Add a new user as a member and then reload the controller as that user
				User user1 = vt.users.get(1);
				vt.testBoard.addMember(user1, vt.users.get(0));
				
				vt.cont.setupController(vt.vmAct, user1, vt.testBoard, vt.trelloClient);
				vt.cont.setViewTransitionModel(vt.vm);
			}
			
		});
		
		// Now, ensure that clicking on the edit members button does nothing
		robot.clickOn("#editMembersButton");
		assertEquals(0, vm.showBoardMembersEditPage);
		
		// Now reload the controller back to the initial state with the owner
		robot.interact(new BoardViewTestCallController(this) {

			@Override
			public void run()
			{
				// Add a new user as a member and then reload the controller as that user
				User user0 = vt.users.get(0);
				
				vt.cont.setupController(vt.vmAct, user0, vt.testBoard, vt.trelloClient);
				vt.cont.setViewTransitionModel(vt.vm);
			}
			
		});
	}
	
	private void testEditBoardNameButton(FxRobot robot)
	{
		// Click on the button
		robot.clickOn("#editBoardNameButton");
		
		// Make sure the proper view was called
		assertEquals(1, vm.getNewView);
		vm.resetCounters();
	}
	
	private void testNewListButton(FxRobot robot)
	{
		// Click on the button
		robot.clickOn("#newListButton");
		
		// Make sure the proper view was called
		assertEquals(1, vm.getNewView);
		vm.resetCounters();
	}
	
	private void changeBoardName(FxRobot robot, String newName)
	{
		// Set the actual VM so the side view will load 
		cont.setViewTransitionModel(vmAct);
		
		// Click on the name change button
		robot.clickOn("#editBoardNameButton");
		
		// Now change the VM back so we can make sure the popup will show
		cont.setViewTransitionModel(vm);
		vm.resetCounters();
		
		// Get the old name
		String oldName = robot.lookup("#boardNameLabel").queryAs(Label.class).getText();
		
		// Type in a new name
		robot.clickOn("#createNewTextField").write(newName);
		
		// Click on the submit button
		robot.clickOn("#createNewSubmitButton");
		
		// Ensure the name changed or if the string is empty,
		// it hasn't changed
		if (newName == "")
		{
			Assertions.assertThat(robot.lookup("#boardNameLabel").queryAs(Label.class)).hasText(oldName);
			assertEquals(1, vm.showPopup);
			vm.resetCounters();
		}
		else
			Assertions.assertThat(robot.lookup("#boardNameLabel").queryAs(Label.class)).hasText(newName);
	}
	
	private void testBoardNameChange(FxRobot robot)
	{	
		// Now, the side pane will be open so let's
		// change the name
		changeBoardName(robot, "New Name 1");
		changeBoardName(robot, "New Name 2");
		changeBoardName(robot, "A very random string");
		changeBoardName(robot, "");
	}
	
	private void checkLists(FxRobot robot)
	{
		// Get the lists and panes
		ArrayList<BList> lists = testBoard.getLists().getMembers();
		ObservableList<Node> panes = robot.lookup("#gridPane").queryAs(GridPane.class).getChildren();
		
		// Make sure we have the same number of lists and panes
		assertEquals(lists.size(), panes.size());
		
		// Make sure all of the names of the lists and panes are the same
		for (int i = 0; i < lists.size(); i++)
		{
			BList l = lists.get(i);
			TitledPane p = (TitledPane) panes.get(i);
			assertEquals(l.getName(), p.getText());
		}
	}
	
	private void addList(FxRobot robot, String newListName)
	{
		// Set the actual VM so the side view will load 
		cont.setViewTransitionModel(vmAct);
		
		// Click on the name change button
		robot.clickOn("#newListButton");
		
		// Now change the VM back so we can make sure the popup will show
		// only do it here if the new name is nothing
		if (newListName == "")
		{
			cont.setViewTransitionModel(vm);
			vm.resetCounters();
		}
		
		// Get the old number of lists
		ObservableList<Node> panes = robot.lookup("#gridPane").queryAs(GridPane.class).getChildren();
		int oldNumLists = panes.size();
		
		// Type in a new name
		robot.clickOn("#createNewTextField").write(newListName);
		
		// Click on the submit button
		robot.clickOn("#createNewSubmitButton");
		
		// Ensure the name changed or if the string is empty,
		// it hasn't changed
		if (newListName == "")
		{
			assertEquals(oldNumLists, panes.size());
			assertEquals(1, vm.showPopup);
			vm.resetCounters();
		}
		else
		{
			assertEquals(oldNumLists + 1, panes.size());
			checkLists(robot);
		}
		
		// Make sure we set the VM back to the test one
		cont.setViewTransitionModel(vm);
		vm.resetCounters();
	}
	
	private void testAddList(FxRobot robot)
	{
		// Let's make sure that the lists are currently correct
		checkLists(robot);
		
		// Now, let's add a list and make sure it adds
		addList(robot, "A new list");
		addList(robot, "Another new list");
		addList(robot, "A very random string");
		addList(robot, "");
	}
	
	private int assignListId(FxRobot robot)
	{
		ObservableList<Node> panes = robot.lookup("#gridPane").queryAs(GridPane.class).getChildren();
		for (int i = 0; i < panes.size(); i++)
		{
			TitledPane p = (TitledPane) panes.get(i);
			p.setId("list" + i);
		}
		
		return panes.size();
	}
	
	private void dragLists(FxRobot robot, int sourceIndex, int targetIndex)
	{
		GridPane listsGridPane = robot.lookup("#gridPane").queryAs(GridPane.class);
		TitledPane sourceTP = (TitledPane) listsGridPane.getChildren().get(sourceIndex);
		TitledPane targetTP = (TitledPane) listsGridPane.getChildren().get(targetIndex);
		
		// Make sure the sourceList is in view
		ScrollPane boardSP = robot.lookup("#horizScrollPane").queryAs(ScrollPane.class);
		Bounds bounds = boardSP.getViewportBounds();
		boardSP.setHvalue(sourceTP.getLayoutX() * (1 / (listsGridPane.getWidth() - bounds.getWidth())));
		
		// Begin the drag
		robot.drag(sourceTP);
		
		// We have to move the robot somewhere or it doesn't actually register the drag event,
		// breaking the test
		robot.moveTo("#boardNameLabel");
		
		// Now, make sure the targetList is in view
		boardSP.setHvalue(targetTP.getLayoutX() * (1 / (listsGridPane.getWidth() - bounds.getWidth())));
		
		// Drop
		robot.dropTo(targetTP);
	}
	
	private void testDragLists(FxRobot robot)
	{
		// Let's reset the lists and have 10 for testing
		robot.interact(new BoardViewTestCallController(this) {
			@Override
			public void run()
			{
				// Clear and create new lists
				vt.testBoard.setLists(new HasMembersList<BList>());
				for (int i = 0; i < 10; i++)
					new BList("Dragging List " + i, vt.testBoard);
				
				// Resetup controller to show the lists
				vt.cont.setupController(vt.vmAct, vt.users.get(0), vt.testBoard, trelloClient);
			}
		});
		
		// Let's first check the order of the lists
		checkLists(robot);
		
		// Now, let's assign ID's to each of the lists
		int maxListIndex = assignListId(robot);
		
		// Now, let's randomly drag some lists around and make sure they actually are moving
		// We will use random integers, but we also want to make sure we test at least once
		// when the indices are the same
		Random rand = new Random();
		boolean indexHaveEqual = false;
		for (int i = 0; i < 10; i++)
		{
			// Set the ID's for lists
			assignListId(robot);
			
			int sourceIndex = rand.nextInt(maxListIndex);
			int targetIndex = rand.nextInt(maxListIndex);
			System.out.println(sourceIndex);
			System.out.println(targetIndex);
			
			// Let's get the old list of lists
			@SuppressWarnings("unchecked")
			ArrayList<BList> oldLists = (ArrayList<BList>) testBoard.getLists().getMembers().clone();
			
			// Drag the lists
			dragLists(robot, sourceIndex, targetIndex);
			
			// Now, we check differently if they're equal vs if they're not
			if (sourceIndex == targetIndex)
			{
				// We've tested that the indices are equal
				indexHaveEqual = true;
				
				// Ensure the old panes are the same as the new ones
				// and the old list is the same too
				assertEquals(oldLists, testBoard.getLists().getMembers());
				checkLists(robot);
			}
			else
			{
				// We know that things have changed, so let's make sure the old lists
				// are not equal to the new ones
				assertNotEquals(oldLists, testBoard.getLists().getMembers());
				
				// Let's move the items around in the lists to make sure we have the desired reordering action
				BList sourceList = oldLists.get(sourceIndex);
				oldLists.remove(sourceIndex);
				oldLists.add(targetIndex, sourceList);
				
				// Now, make sure the old list and the new list are equal
				assertEquals(oldLists, testBoard.getLists().getMembers());
				
				// Now, we need to check all of the lists again
				checkLists(robot);
			}
		}
		
		// When we get here, we need to make sure we've tested the same index at least once
		if (! indexHaveEqual)
		{
			int index = rand.nextInt(maxListIndex);
			dragLists(robot, index, index);
			
			// Let's get the old list of lists
			@SuppressWarnings("unchecked")
			ArrayList<BList> oldLists = (ArrayList<BList>) testBoard.getLists().getMembers().clone();
			
			// Ensure the old panes are the same as the new ones
			// and the old list is the same too
			assertEquals(oldLists, testBoard.getLists().getMembers());
			checkLists(robot);
		}
		
		// Set the VM back to the test
		cont.setViewTransitionModel(vm);
	}
	
	private void checkCards(FxRobot robot, BList list, TitledPane listPane)
	{
		// Get the cards and their panes
		ArrayList<Card> cards = list.getCards().getMembers();
		ObservableList<TitledPane> panes = robot.lookup("#accordion" + list.getName()).queryAs(Accordion.class).getPanes();
		
		// Make sure we have the same number of lists and panes
		assertEquals(cards.size(), panes.size());
		
		// Make sure all of the names of the lists and panes are the same
		for (int i = 0; i < cards.size(); i++)
		{
			Card l = cards.get(i);
			TitledPane p = panes.get(i);
			assertEquals(l.getName(), p.getText());
		}
	}
	
	private void addCard(FxRobot robot, BList list, TitledPane listPane, String newCardName)
	{
		// Set the actual VM so the side view will load 
		cont.setViewTransitionModel(vmAct);
		
		// Click on the name change button
		robot.clickOn("#newCardButton" + list.getName());
		
		// Now change the VM back so we can make sure the popup will show
		// only do it here if the new name is nothing
		if (newCardName == "")
		{
			cont.setViewTransitionModel(vm);
			vm.resetCounters();
		}
		
		// Get the old number of lists
		ObservableList<TitledPane> panes = robot.lookup("#accordion" + list.getName()).queryAs(Accordion.class).getPanes();
		int oldNumCards = panes.size();
		
		// Type in a new name
		robot.clickOn("#createNewTextField").write(newCardName);
		
		// Click on the submit button
		robot.clickOn("#createNewSubmitButton");
		
		// Update the panes
		// Not sure why this doesn't update, but it doesn't
		panes = robot.lookup("#accordion" + list.getName()).queryAs(Accordion.class).getPanes();
		
		// Ensure the name changed or if the string is empty,
		// it hasn't changed
		if (newCardName == "")
		{
			assertEquals(oldNumCards, panes.size());
			assertEquals(1, vm.showPopup);
			vm.resetCounters();
		}
		else
		{
			assertEquals(oldNumCards + 1, panes.size());
			checkCards(robot, list, listPane);
		}
		
		// Make sure we set the VM back to the test one
		cont.setViewTransitionModel(vm);
		vm.resetCounters();
	}
	
	private void testNewCardButton(FxRobot robot, BList list, TitledPane listPane)
	{
		// Let's make sure that the button clicks the correct location
		robot.clickOn("#newCardButton" + list.getName());
		assertEquals(1, vm.getNewView);
		vm.resetCounters();
		
		// Let's make sure that the lists are currently correct
		checkCards(robot, list, listPane);
		
		// Now, let's add a list and make sure it adds
		addCard(robot, list, listPane, "A new card");
		addCard(robot, list, listPane, "Another new card");
		addCard(robot, list, listPane, "A very random string");
		addCard(robot, list, listPane, "");
	}
	
	private void assignCardIds(FxRobot robot, BList testList, int listNum)
	{
		// Get all of the panes
		ObservableList<TitledPane> panes = robot.lookup("#accordion" + testList.getName()).queryAs(Accordion.class).getPanes();
		
		// Assign the ID's
		int i = 0;
		for (TitledPane p : panes)
		{
			p.setId(listNum + "card" + i);
			i++;
		}
	}
	
	private void closeAllPanes(FxRobot robot, BList testList)
	{
		// Get all of the panes
		ObservableList<TitledPane> panes = robot.lookup("#accordion" + testList.getName()).queryAs(Accordion.class).getPanes();
		
		// Assign the ID's
		for (TitledPane p : panes)
			p.setExpanded(false);
	}
	
	private void dragCards(FxRobot robot, BList sourceBList, BList targetBList, int sourceIndex, int targetIndex, int sourceListIndex, int targetListIndex)
	{
		// Get the gridpane and card titlepanes and list titlepanes
		GridPane gp = robot.lookup("#gridPane").queryAs(GridPane.class);
		BorderPane bp = (BorderPane) gp.getParent().getParent().getParent().getParent();
		StackPane spTop = (StackPane) bp.getTop();
		Button buttonBottom = (Button) bp.getBottom();
		TitledPane sourceTP = robot.lookup("#" + sourceListIndex + "card" + sourceIndex).queryAs(TitledPane.class);
		TitledPane targetTP = robot.lookup("#" + targetListIndex + "card" + targetIndex).queryAs(TitledPane.class);
		
		// Ensure all panes are closed
		robot.interact(() -> {
			closeAllPanes(robot, sourceBList);
			closeAllPanes(robot, targetBList);
		});
		
		// Make sure the sourceList is in view
		ScrollPane scrollPane = robot.lookup("#horizScrollPane").queryAs(ScrollPane.class);
		Bounds bounds = scrollPane.getViewportBounds();
		
		scrollPane.setVvalue(sourceTP.getLayoutY() * (1 / (bp.getHeight() + spTop.getHeight() + buttonBottom.getHeight() - bounds.getHeight())));
		
		// Begin the drag
		robot.drag(sourceTP);
		
		// We have to move the robot somewhere or it doesn't actually register the drag event,
		// breaking the test
		robot.moveTo("#boardNameLabel");
		
		// Ensure all panes are closed
		robot.interact(() -> {
			closeAllPanes(robot, sourceBList);
			closeAllPanes(robot, targetBList);
		});
		
		// Now, make sure the targetList is in view
		scrollPane.setVvalue(targetTP.getLayoutY() * (1 / (bp.getHeight() + spTop.getHeight() + buttonBottom.getHeight() - bounds.getHeight())));
		
		robot.dropTo(targetTP);
	}
	
	@SuppressWarnings("unchecked")
	private void testCardDragging(FxRobot robot, BList testList, BList testList2, TitledPane testListPane, TitledPane testList2Pane)
	{
		// Let's add 10 cards to the list
		robot.interact(new BoardViewTestCallController(this) {

			@Override
			public void run()
			{
				// Remove all cards
				BList testList = vt.testBoard.getLists().getMembers().get(0);
				BList testList2 = vt.testBoard.getLists().getMembers().get(1);
				testList.setCards(new HasMembersList<Card>());
				testList2.setCards(new HasMembersList<Card>());
				
				// Add 10 cards
				for (int i = 0; i < 10; i++)
				{
					new Card("L1 Dragging Card " + i, testList);
					new Card("L2 Dragging Card" + i, testList2);
				}
				
				// Resetup the controller
				vt.cont.setupController(vmAct, users.get(0), testBoard, trelloClient);
			}
			
		});
		
		// Make sure the cards are starting in the correct spot
		checkCards(robot, testList, testListPane);
		checkCards(robot, testList2, testList2Pane);
		
		// Now, let's do some dragging
		Random rand = new Random();
		boolean indexHaveEqual = false;
		for (int i = 0; i < 10; i++)
		{
			// Assign the ID
			//assignCardIds(robot, testList, 0);
			//assignCardIds(robot, testList2, 1);
			
			int sourceListIndex = rand.nextInt(2);
			int targetListIndex = rand.nextInt(2);
			
			BList sourceBList = testBoard.getLists().getMembers().get(sourceListIndex);
			BList targetBList = testBoard.getLists().getMembers().get(targetListIndex);
			
			int sourceIndex = rand.nextInt(sourceBList.getCards().getMembers().size());
			int targetIndex = rand.nextInt(targetBList.getCards().getMembers().size());
			
			// Let's get the old list of lists
			ArrayList<Card> oldCardsSource = (ArrayList<Card>) sourceBList.getCards().getMembers().clone();
			ArrayList<Card> oldCardsTarget;
			if (sourceListIndex != targetListIndex)
			{
				oldCardsTarget = (ArrayList<Card>) targetBList.getCards().getMembers().clone();
			}
			else
			{
				oldCardsTarget = oldCardsSource;
			}
			
			// Drag the lists
			dragCards(robot, sourceBList, targetBList, sourceIndex, targetIndex, sourceListIndex, targetListIndex);
			
			// Now, we check differently if they're equal vs if they're not
			if (sourceIndex == targetIndex && sourceListIndex == targetListIndex)
			{
				// We've tested that the indices are equal
				indexHaveEqual = true;
				
				// Ensure the old panes are the same as the new ones
				// and the old list is the same too
				assertEquals(oldCardsSource, sourceBList.getCards().getMembers());
				assertEquals(oldCardsTarget, targetBList.getCards().getMembers());
				checkCards(robot, testList, testListPane);
				checkCards(robot, testList2, testList2Pane);
			}
			else
			{
				// We know that things have changed, so let's make sure the old lists
				// are not equal to the new ones
				assertNotEquals(oldCardsSource, sourceBList.getCards().getMembers());
				assertNotEquals(oldCardsTarget, targetBList.getCards().getMembers());
				
				// Let's move the items around in the lists to make sure we have the desired reordering action
				Card sourceCard = oldCardsSource.get(sourceIndex);
				oldCardsSource.remove(sourceIndex);
				oldCardsTarget.add(targetIndex, sourceCard);
				sourceCard.setList(targetBList);
				
				// Now, make sure the old list and the new list are equal
				assertEquals(oldCardsSource, sourceBList.getCards().getMembers());
				assertEquals(oldCardsTarget, targetBList.getCards().getMembers());
				
				// Now, we need to check all of the lists again
				checkCards(robot, testList, testListPane);
				checkCards(robot, testList2, testList2Pane);
			}
		}
		
		// We want to make sure we test that a card was dropped on top of itself at least once
		// When we get here, we need to make sure we've tested the same index at least once
		if (! indexHaveEqual)
		{
			// Assign the ID
			//assignCardIds(robot, testList, 0);
			//assignCardIds(robot, testList2, 1);
			
			int index = rand.nextInt(testBoard.getLists().getMembers().get(0).getCards().getMembers().size());
			dragCards(robot, testList, testList, index, index, 0, 0);
			
			// Let's get the old list of lists
			ArrayList<Card> oldCards = (ArrayList<Card>) testList.getCards().getMembers().clone();
			
			// Ensure the old panes are the same as the new ones
			// and the old list is the same too
			assertEquals(oldCards, testList.getCards().getMembers());
			checkLists(robot);
		}
		
		// Set the VM back to the test
		cont.setViewTransitionModel(vm);
	}
	
	@SuppressWarnings("unchecked")
	private void checkLabels(FxRobot robot, Card card)
	{
		// Let's get the boards in the ListView and from the user
		ListView<models.trello.Label> listedLabels = robot.lookup("#labelsListView").queryAs(ListView.class);
		Set<models.trello.Label> cardLabels = card.getLabels().getMembers();
		
		// Make sure the 2 lists are the same size
		assertEquals(listedLabels.getItems().size(), cardLabels.size());
		
		// Now, let's make sure the 2 lists match
		for (models.trello.Label l : listedLabels.getItems())
			assertTrue(cardLabels.contains(l));
	}
	
	private void addLabel(FxRobot robot, String labelName, Card card)
	{
		// Set to the actual VM so views will show
		cont.setViewTransitionModel(vmAct);
		
		// Get the number of labels initially
		int oldLen = card.getLabels().getMembers().size();
		
		// Click on the new label button
		robot.clickOn("#addLabelButton");
		
		// Set the VM back
		cont.setViewTransitionModel(vm);
		
		// Now, click on the text field and submit button to create the label
		robot.clickOn("#createNewTextField").write(labelName);
		robot.clickOn("#createNewSubmitButton");
		
		// Now, check the labels again and make sure everything updated appropriately
		if (labelName == "")
		{
			assertEquals(oldLen, card.getLabels().getMembers().size());
			assertEquals(1, vm.showPopup);
			vm.resetCounters();
		}
		else
		{
			assertEquals(oldLen + 1, card.getLabels().getMembers().size());
		}
		
		// Now, make sure the labels are still displaying correctly
		checkLabels(robot, card);
	}
	
	private void testCardLabels(FxRobot robot, Card testCard)
	{
		// Let's begin by making sure the labels match what the card has
		checkLabels(robot, testCard);
		
		// Now, let's add some labels
		for (int i = 0; i < 5; i++)
			addLabel(robot, "Label " + i, testCard);
		
		// Now, add a label with no text and see what happens
		addLabel(robot, "", testCard);
		
		// Finally, make sure we can still add new labels
		addLabel(robot, "Finally, the last one!", testCard);
	}
	
	@SuppressWarnings("unchecked")
	private void checkComponents(FxRobot robot, Card card)
	{
		// Let's get the boards in the ListView and from the user
		ListView<Component> listedComps = robot.lookup("#componentsListView").queryAs(ListView.class);
		ArrayList<Component> cardComps = card.getComponents().getMembers();
		
		// Make sure the 2 lists are the same size
		assertEquals(listedComps.getItems().size(), cardComps.size());
		
		// Now, let's make sure the 2 lists match
		for (Component c : listedComps.getItems())
			assertTrue(cardComps.contains(c));
	}
	
	private void addComponent(FxRobot robot, String compName, Card card)
	{
		// Set to the actual VM so views will show
		cont.setViewTransitionModel(vmAct);
		
		// Get the number of comps initially
		int oldLen = card.getComponents().getMembers().size();
		
		// Click on the new comp button
		robot.clickOn("#addComponentButton");
		
		// Set the VM back
		cont.setViewTransitionModel(vm);
		
		// Now, click on the text field and submit button to create the comp
		robot.clickOn("#createNewTextField").write(compName);
		robot.clickOn("#createNewSubmitButton");
		
		// Now, check the comps again and make sure everything updated appropriately
		if (compName == "")
		{
			assertEquals(oldLen, card.getComponents().getMembers().size());
			assertEquals(1, vm.showPopup);
			vm.resetCounters();
		}
		else
		{
			assertEquals(oldLen + 1, card.getComponents().getMembers().size());
		}
		
		// Now, make sure the comps are still displaying correctly
		checkComponents(robot, card);
	}
	
	private void testCardComponents(FxRobot robot, Card testCard)
	{
		// Let's begin by making sure the components match what the card has
		checkComponents(robot, testCard);
		
		// Now, let's add some components
		for (int i = 0; i < 5; i++)
			addComponent(robot, "Component " + i, testCard);
		
		// Now, add a component with no text and see what happens
		addComponent(robot, "", testCard);
		
		// Finally, make sure we can still add new components
		addComponent(robot, "Finally, the last one comp!", testCard);
	}
	
	@SuppressWarnings("unchecked")
	private void checkCardMembers(FxRobot robot, Card card)
	{
		// Let's get the members in the ListView and from the user
		ListView<User> listedMems = robot.lookup("#membersListView").queryAs(ListView.class);
		Set<User> cardMems = card.getMembers().getMembers();
		
		// Make sure the 2 lists are the same size
		assertEquals(listedMems.getItems().size(), cardMems.size());
		
		// Now, let's make sure the 2 lists match
		for (User u : listedMems.getItems())
			assertTrue(cardMems.contains(u));
	}
	
	private void addCardMember(FxRobot robot, String username, Card card)
	{
		try
		{
			// Set to the actual VM so views will show
			cont.setViewTransitionModel(vmAct);
			
			// Get the number of memberss initially
			int oldLen = card.getMembers().getMembers().size();
			
			// Also get a clone of the members
			User user = trelloClient.findUser(username);
			
			// See if the user already exists as a member
			boolean isMember = card.hasMember(user);
			
			// Click on the new member button
			robot.clickOn("#addMemberButton");
			
			// Set the VM back
			cont.setViewTransitionModel(vm);
			
			// Now, click on the text field and submit button to create the mem
			robot.clickOn("#createNewTextField").write(username);
			robot.clickOn("#createNewSubmitButton");
			
			// Now, check the comps again and make sure everything updated appropriately
			if (username == "")
			{
				assertEquals(oldLen, card.getMembers().getMembers().size());
				assertEquals(1, vm.showPopup);
				vm.resetCounters();
			}
			else if (isMember)
			{
				assertEquals(oldLen, card.getMembers().getMembers().size());
			}
			else
			{
				assertEquals(oldLen + 1, card.getMembers().getMembers().size());
			}
			
			// Now, make sure the mems are still displaying correctly
			checkCardMembers(robot, card);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	private void testCardMembers(FxRobot robot, Card card)
	{
		// Let's begin by making sure the members match what the card has
		checkCardMembers(robot, card);
		
		// Now, let's add some members that exist on the server
		for (int i = 0; i < 5; i++)
			addCardMember(robot, "user" + i, card);
		
		// Let's see what happens if we try adding the same user twice
		addCardMember(robot, "user0", card);
		
		// Now, add a member with no text and see what happens
		addCardMember(robot, "", card);
		
		// Finally, make sure we can still add new members
		addCardMember(robot, "user20", card);
	}
	
	@Test
	public void boardTest(FxRobot robot)
	{
		// Make sure the board name is correct
		Assertions.assertThat(robot.lookup("#boardNameLabel").queryAs(Label.class)).hasText(testBoard.getName());
		
		// Test clicking the buttons
		testEditBoardMembersButton(robot);
		testEditBoardNameButton(robot);
		testNewListButton(robot);
		
		// Now, we know the proper methods in the VM are being called, so let's
		// try changing the name of the board
		testBoardNameChange(robot);
		
		// Now, let's see if we can add a list
		testAddList(robot);
	}
	
	@Test
	public void dragListsTest(FxRobot robot)
	{
		// Test dragging lists
		testDragLists(robot);
	}
	
	@Test
	public void listTest(FxRobot robot)
	{
		// For this, we're only going to pick on the first list
		BList testList = testBoard.getLists().getMembers().get(0);
		TitledPane testListPane = (TitledPane) robot.lookup("#gridPane").queryAs(GridPane.class).getChildren().get(0);
		
		// Test the new card button
		testNewCardButton(robot, testList, testListPane);
	}
	
	@Test
	public void cardDraggingTest(FxRobot robot)
	{
		// For this, we're only going to pick on the first list
		BList testList = testBoard.getLists().getMembers().get(0);
		BList testList2 = testBoard.getLists().getMembers().get(1);
		TitledPane testListPane = (TitledPane) robot.lookup("#gridPane").queryAs(GridPane.class).getChildren().get(0);
		TitledPane testList2Pane = (TitledPane) robot.lookup("#gridPane").queryAs(GridPane.class).getChildren().get(1);
		
		// Test dragging cards within the same list
		testCardDragging(robot, testList, testList2, testListPane, testList2Pane);
	}
	
	@Test
	public void cardtest(FxRobot robot)
	{	
		// Get the testCard
		Card testCard = testBoard.getLists().getMembers().get(0).getCards().getMembers().get(0);
		
		// Get to the card
		assignCardIds(robot, testBoard.getLists().getMembers().get(0), 0);
		TitledPane cardPane = robot.lookup("#0card0").queryAs(TitledPane.class);
		robot.clickOn(cardPane);
		
		// Since we're testing the buttons, let's scroll to the buttons
		ScrollPane scrollPane = robot.lookup("#horizScrollPane").queryAs(ScrollPane.class);
		scrollPane.setVvalue(1);
		
		// Test the labels
		testCardLabels(robot, testCard);
		
		// Test the components
		testCardComponents(robot, testCard);
		
		// Test the members
		testCardMembers(robot, testCard);
	}
	
	/**
	 * BoardViewTestCallController abstract class
	 * 
	 * This is used to make calls to the controller while the program is running
	 *
	 * @implements Runnable
	 */
	private abstract class BoardViewTestCallController implements Runnable
	{
		BoardViewTest vt;
		
		BoardViewTestCallController(BoardViewTest vt)
		{
			this.vt = vt;
		}
		
	}
}
