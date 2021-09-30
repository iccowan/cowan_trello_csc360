package controllers;

import models.trello.*;
import models.trello.structures.HasMembersList;
import models.trelloServer.*;
import views.ViewTransitionModelInterface;

import java.rmi.RemoteException;
import java.util.Random;

import controllers.save.SaveTrackController;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Accordion;
import javafx.scene.control.Button;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.TitledPane;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DataFormat;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.control.TextField;

public class BoardController
{

	@FXML
	private GridPane listsGridPane;

	@FXML
	private Label boardNameLabel;

	@FXML
	private TextField newNameTextField;

	@FXML
	private Label createNewTitleLabel;

	@FXML
	private Button createNewSubmitButton;

	@FXML
	private Button editBoardMembersButton;

	private ViewTransitionModelInterface vm;
	private User user;
	private Board board;
	private TrelloServerInterface trelloClient;
	private SaveTrackController saveTracker = SaveTrackController.getInstance();

	private String creating;
	private BList workingList;
	private Card workingCard;

	/**
	 * Handles initializing the members edit button
	 */
	public void setupMembersEditButton()
	{
		// Only allow editing members if the user is the board's owner
		editBoardMembersButton.setDisable(!board.getOwner().equals(user));
	}
	
	public void setBoardNameLabel(Label l)
	{
		boardNameLabel = l;
	}

	/**
	 * @param user         - User that we're working with
	 * @param board        - Board that we're working with
	 * @param trelloClient - TrelloServerInterface that we're working with
	 */
	public void setupController(ViewTransitionModelInterface vm, User user, Board board, TrelloServerInterface trelloClient)
	{
		// Set the attributes
		this.vm = vm;
		this.user = user;
		this.board = board;
		this.trelloClient = trelloClient;

		// Setup the board name
		boardNameLabel.setText(board.getName());

		// Load the lists
		updateLists();

		// Now, setup the members edit button
		setupMembersEditButton();
	}
	
	public void setCreateNewTitleLabel(Label l)
	{
		this.createNewTitleLabel = l;
	}
	
	public void setCreateNewSubmitButton(Button b)
	{
		this.createNewSubmitButton = b;
	}
	
	public void setViewTransitionModel(ViewTransitionModelInterface vm)
	{
		this.vm = vm;
	}
	
	private TitledPane getTitledListPane(BList list)
	{
		return vm.getListTitledPane(this, list);
	}
	
	public void clearLists()
	{
		board.setLists(new HasMembersList<BList>());
		updateLists();
	}
	
	public void setupColumnConstraints()
	{
		listsGridPane.getColumnConstraints().setAll();
		for (int i = 0; i < board.getLists().getMembers().size(); i++)
		{
			// Create new column constraints
			// This has to be done every time or each list gets a different size for some
			// reason
			// (cannot be setup in the view)
			ColumnConstraints column = new ColumnConstraints();
			column.setMinWidth(200);
			column.setMaxWidth(Control.USE_COMPUTED_SIZE);
			column.setPrefWidth(Control.USE_COMPUTED_SIZE);
			column.setFillWidth(true);

			// Set the column constraints
			listsGridPane.getColumnConstraints().add(column);
		}
	}
	
	public void addListPane(TitledPane listPane, int index)
	{
		listsGridPane.add(listPane, index, 0);
	}

	/**
	 * @param list  - BList to add to the GridPane
	 * @param index - Column to add the list to in the pane
	 */
	public void addList(BList list, int index)
	{
		// Generate the list pane to add
		TitledPane listPane = getTitledListPane(list);

		// Clear column constraints
		setupColumnConstraints();

		// Add the list pane to the GridPane
		addListPane(listPane, index);
	}

	/**
	 * Updates all of the lists in the view
	 */
	private void updateLists()
	{
		// Clear all of the lists
		listsGridPane.getChildren().setAll();

		// Iterate through all of the lists, and add them to the GridPane
		int i = 0;
		for (BList l : board.getLists())
		{
			// Add the list
			addList(l, i);
			i++;
		}
	}

	/**
	 * @param prompt - Prompt for the create panel
	 */
	private void openCreatePanel(String prompt)
	{
		// Get the BorderPane of the page
		BorderPane bp = (BorderPane) boardNameLabel.getParent().getParent();

		// Get the new view and set it up
		VBox createNewVBox = vm.getNewView(this);
		createNewTitleLabel.setText(prompt);

		// Add the new view to the right side of the BorderPane
		bp.setRight(createNewVBox);
	}

	/**
	 * Handles clicking submit on the new create button
	 */
	@FXML
	void newCreate_OnClick()
	{
		// We must be "creating" something
		if (creating != null)
		{
			// Get a clone of the board for saving purporses
			Board oldBoard = board.clone();

			// Find what we're actually "creating"
			if (newNameTextField.getText().equals(""))
			{
				// Text field was empty which is not allowed
				vm.showPopup("Error Creating", "Please enter a name for the new " + creating + ".");
			} else if (creating.equals("list"))
			{
				// Create a new list
				new BList(newNameTextField.getText(), board);

				// Update the lists
				updateLists();
			} else if (creating.equals("card"))
			{
				// Create a new card
				new Card(newNameTextField.getText(), workingList);

				// Update the lists
				updateLists();
			} else if (creating.equals("label"))
			{
				// Create a new Label
				models.trello.Label newLabel = new models.trello.Label(newNameTextField.getText());

				// Add the label, updates automatically via binding
				workingCard.addLabel(newLabel, user);
			} else if (creating.equals("component"))
			{
				// Create a new component
				Component newComp = new Component(newNameTextField.getText(), 1);

				// Add the component, updates automatically via binding
				workingCard.addComponent(newComp, user);
			} else if (creating.equals("member"))
			{
				// Add a new member
				try
				{
					// Find the new member on the server
					User newMem = trelloClient.findUser(newNameTextField.getText());

					// If found, add the member if they don't already exist
					if (! workingCard.hasMember(newMem))
						workingCard.addMember(newMem, user);
				} catch (UserNotFoundException e)
				{
					// New member not found on the server
					vm.showPopup("User not found",
							"The user, " + newNameTextField.getText() + " could not be found");
				} catch (RemoteException e)
				{
					// Server error
					vm.showPopup("Server Error",
							"Error connecting to the server, please try logging back in");
				}
			} else if (creating.equals("board name change"))
			{
				// Change the name of the board
				board.setName(newNameTextField.getText());

				// Update the label
				boardNameLabel.setText(newNameTextField.getText());
			}

			// Now, add the change to the save tracker
			saveTracker.addBoardChange(oldBoard, board, user);

			// No longer "creating" anything
			creating = null;
		}

		// Close the form on the right side
		createNewSubmitButton.setText("Create");
		BorderPane bp = (BorderPane) boardNameLabel.getParent().getParent();
		bp.setRight(null);

		// If we have a working list or card, set these to null
		if (workingList != null)
			workingList = null;

		if (workingCard != null)
			workingCard = null;
	}

	/**
	 * Handles clicking the new list button
	 */
	@FXML
	void newListButton_OnClick()
	{
		// Set what we're "creating"
		creating = "list";

		// Open the create panel
		openCreatePanel("Create a New List");
	}

	/**
	 * Sets the working card by a button click within a card
	 * 
	 * @param event
	 */
	private void setWorkingListCardByCardButtonClick(Event event)
	{
		// Get the clicked button
		Button clickedButton = (Button) event.getSource();

		// Get the TitledPane of the card
		TitledPane cardPane = (TitledPane) clickedButton.getParent().getParent().getParent().getParent();

		// Get the accordion of the card
		Accordion acc = (Accordion) cardPane.getParent();

		// Get the TitledPane of the list
		TitledPane listPane = (TitledPane) cardPane.getParent().getParent().getParent().getParent();

		// Get the index of the list and the card
		int workingListIndex = GridPane.getColumnIndex(listPane);
		int workingCardIndex = acc.getPanes().indexOf(cardPane);

		// Get and set the working list and card
		workingList = board.getLists().getMembers().get(workingListIndex);
		workingCard = workingList.getCards().getMembers().get(workingCardIndex);
	}

	/**
	 * Sets the working list by a new card button click
	 * 
	 * @param event
	 */
	private void setWorkingListByNewCardButtonClick(Event event)
	{
		// Get the clicked button
		Button clickedButton = (Button) event.getSource();

		// Get the TitledPane of the list for the button that was clicked
		TitledPane parentPane = (TitledPane) clickedButton.getParent().getParent().getParent();

		// Get the index of the working list
		int workingListIndex = GridPane.getColumnIndex(parentPane);

		// Get and set the working list
		workingList = board.getLists().getMembers().get(workingListIndex);
	}

	/**
	 * Handles clicking on the new card button
	 * 
	 * @param event - Event
	 */
	@FXML
	void newCardButton_OnClick(Event event)
	{
		// Set the working list
		setWorkingListByNewCardButtonClick(event);

		// Set what we're "creating"
		creating = "card";

		// Open the create panel
		openCreatePanel("Create a New Card");
	}

	/**
	 * Handles clicking the new label button
	 * 
	 * @param event - Event
	 */
	@FXML
	void newLabelButton_OnClick(Event event)
	{
		// Set the working card
		setWorkingListCardByCardButtonClick(event);

		// Set what we're "creating"
		creating = "label";

		// Open the create panel
		openCreatePanel("Create a New Label");
	}

	/**
	 * Handles clicking the new component button
	 * 
	 * @param event - Event
	 */
	@FXML
	void newComponentButton_OnClick(Event event)
	{
		// Set the working card
		setWorkingListCardByCardButtonClick(event);

		// Set what we're "creating"
		creating = "component";

		// Open the create panel
		openCreatePanel("Create a New Component");
	}

	/**
	 * Handles clicking on the new member button
	 * 
	 * @param event - Event
	 */
	@FXML
	void newMemberButton_OnClick(Event event)
	{
		// Set the working card
		setWorkingListCardByCardButtonClick(event);

		// Set what we're "creating"
		creating = "member";

		// Show the create panel
		openCreatePanel("Add a New Member");

		// Change the submit button text
		createNewSubmitButton.setText("Add");
	}

	/**
	 * Handles clicking on the edit board name button
	 */
	@FXML
	void editBoardNameButton_OnClick()
	{
		// Set what we're "creating"
		creating = "board name change";

		// Show the create panel
		openCreatePanel("New Board Name");

		// Change the submit button text
		createNewSubmitButton.setText("Submit");
	}

	/**
	 * Handles clicking the edit members button
	 */
	@FXML
	void editBoardMembersButton_OnClick()
	{
		// Show the members edit page
		vm.showBoardMembersEditPage(board, user, trelloClient);
	}

	/**
	 * Everything below here is solely for dragging purposes. I am keeping this code
	 * separate because I demoed it in a different project and am simply copying it
	 * over.
	 */

	private DataFormat paneFormat;
	private DataFormat accFormat;

	/**
	 * @param event - MouseEvent of the detected drag
	 * 
	 *              This is activated when a drag is detected on a TitledPane (the
	 *              encasing ones, TitledPane1, TitledPane2, TitledPane3)
	 */
	@FXML
	public void TitledPane_OnDragDetected(MouseEvent event)
	{
		// Begin the dragging event
		TitledPane dragPane = (TitledPane) event.getSource();
		Dragboard db = dragPane.startDragAndDrop(TransferMode.ANY);
		db.setDragView(dragPane.snapshot(null, null));

		// Add the pane format to the clipboard
		// We're using the clipboard for the actual dragging
		ClipboardContent clip = new ClipboardContent();
		Long randNum = (new Random()).nextLong();
		String paneFormatName = "TitledPaneDragging" + String.valueOf(randNum);
		paneFormat = new DataFormat(paneFormatName);
		clip.put(paneFormat, " ");

		// Set the content of the dragboard to the clip that we created
		db.setContent(clip);
	}

	/**
	 * @param event - DragEvent of the drag on drop
	 * @return the TitledPane that it was dropped inside of. If none found, returns
	 *         null
	 * 
	 *         This handles finding the TitledPane that the dragging pane was
	 *         dropped inside of. This is more complicated than you would think
	 *         because there are automatically generated labels and other nodes
	 *         inside of the TitledPane that we have to consider.
	 */
	private TitledPane getDroppedTitledPane(DragEvent event)
	{
		// Get the event target
		Node target = (Node) event.getTarget();

		// If our target ever gets to the GridPane or null, then we know
		// that we haven't dropped inside of a TitledPane within the
		// GridPane
		//
		// This could also probably be done with recursion, but this is
		// how I sporadically wrote this method, so this is what I'm going with
		while (!target.equals(listsGridPane))
		{
			// If the parent of the element is the GridPane, we know
			// that we have a TitledPane that we can cast from Node,
			// and also we have the TitledPane that we dropped inside of
			// so let's return it
			if (target.getParent().equals(listsGridPane))
				return (TitledPane) target;

			// Get the next level up parent
			target = target.getParent();
		}

		// At this point we know that we don't have the TitledPane, so we just
		// return null
		return null;
	}

	/**
	 * @param event - DragEvent to complete
	 */
	private void completeDrag(DragEvent event)
	{
		Dragboard db = event.getDragboard();
		db.clear();
		// Set the drag event complete and nullify the paneFormat
		event.setDropCompleted(true);
		paneFormat = null;
	}

	/**
	 * @param event - DragEvent that we are working with
	 */
	@FXML
	public void GridPane_OnDragDropped(DragEvent event)
	{
		if (paneFormat != null)
		{
			// Get the dragboard and the replacement and dragging panes
			Dragboard db = event.getDragboard();
			TitledPane replacement = getDroppedTitledPane(event);
			TitledPane dragPane = (TitledPane) event.getGestureSource();

			// Make sure the dragboard has the pane and make sure the replacement
			// and dragPane are not null. If they're null, the drag & drop was invalid
			// so we just end it and do nothing
			if (db.hasContent(paneFormat) && replacement != null && dragPane != null)
			{
				// Make sure we save
				Board oldBoard = board.clone();

				// Set the replacement and dragPane column indices to 0
				int colReplacement = 0;
				int colDragPane = 0;

				// For some reason, only if the index of the Pane is 0 in the GridPane,
				// it throws a NullPointerException, so if this happens, leave the index
				// as 0. If it works though, we get the new index
				try
				{
					colReplacement = GridPane.getColumnIndex(replacement);
				} catch (NullPointerException e)
				{
				}

				try
				{
					colDragPane = GridPane.getColumnIndex(dragPane);
				} catch (NullPointerException e)
				{
				}

				// If the indices are the same, the list was dropped on top of itself
				// so nothing should happen and we're done
				if (colDragPane == colReplacement)
				{
					completeDrag(event);
					return;
				}

				// Let's get the list at the source index from the board
				BList sourceList = board.getLists().getMembers().get(colDragPane);

				// Now, let's insert the board into the list where the replacement is
				board.moveList(sourceList, colReplacement, user);

				// Now, update the lists
				updateLists();

				// Don't forget to save! I've lost a many of hours of work for making this
				// mistake!
				saveTracker.addBoardChange(oldBoard, board, user);

				// Complete the drag
				completeDrag(event);
			} else
			{
				// Complete the drag
				completeDrag(event);
			}
		}
	}

	/**
	 * @param event - DragEvent that is going on
	 * 
	 *              I'm not 100% sure why this is necessary, but if we don't have
	 *              it, the dragging all breaks.
	 */
	@FXML
	public void GridPane_OnDragOver(DragEvent event)
	{
		// Get the dragboard
		Dragboard db = event.getDragboard();

		// Confirm that the drag is still going on
		if (paneFormat != null && db.hasContent(paneFormat))
			event.acceptTransferModes(TransferMode.ANY);
	}

	/*************************************************************************/
	/* END GridPane Dragging */
	/* BEGIN Accordion Dragging */
	/*************************************************************************/

	/**
	 * @param event - MouseEvent of the dragging
	 * 
	 *              This works exactly the same as above, just for the accordion.
	 */
	@FXML
	public void Accordion_OnDragDetected(MouseEvent event)
	{
		// Begin the dragging event
		TitledPane dragPane = (TitledPane) event.getSource();
		Dragboard db = dragPane.startDragAndDrop(TransferMode.ANY);
		db.setDragView(dragPane.snapshot(null, null));

		// Add the pane format to the clipboard
		ClipboardContent clip = new ClipboardContent();
		Long randNum = (new Random()).nextLong();
		String paneFormatName = "AccordionDragging" + String.valueOf(randNum);
		accFormat = new DataFormat(paneFormatName);
		clip.put(accFormat, " ");
		db.setContent(clip);
	}

	/**
	 * @param event - DragEvent of the dragging that is occuring
	 * @return the TitledPane that was dropped inside of. Returns null if none found
	 * 
	 *         This again works exactly the same as above, except this time we are
	 *         looking for children of an Accordion
	 */
	private TitledPane getDroppedAccordion(DragEvent event)
	{
		Node target = (Node) event.getTarget();

		while (!target.equals(listsGridPane))
		{
			if (target.getParent().getClass().equals(Accordion.class))
			{
				return (TitledPane) target;
			}

			target = target.getParent();
		}

		return null;
	}

	/**
	 * @param event - DragEvent to complete
	 */
	private void completeDragAccordion(DragEvent event)
	{
		Dragboard dp = event.getDragboard();
		dp.clear();
		event.setDropCompleted(true);
		accFormat = null;
	}

	/**
	 * @param event - DragEvent that is dropped
	 */
	@FXML
	public void Accordion_OnDragDropped(DragEvent event)
	{
		if (accFormat != null)
		{
			// Get the dragboard, the dragPane, and the replacement
			Dragboard db = event.getDragboard();
			TitledPane replacement = getDroppedAccordion(event);
			TitledPane dragPane = (TitledPane) event.getGestureSource();

			// Make sure the replacement and dragPane are not null and the
			// drag was actually occuring
			if (db.hasContent(accFormat) && replacement != null && dragPane != null)
			{
				// Make sure we save
				Board oldBoard = board.clone();

				// Get the parent of both the dragPane and the replacement.
				// Remember since we allow switching between different cards,
				// they may not have the same parent
				Accordion parentReplacement = (Accordion) replacement.getParent();
				Accordion parentDragPane = (Accordion) dragPane.getParent();

				// Switching is going to be interesting here...
				//
				int dragPaneIndex = parentDragPane.getPanes().indexOf(dragPane);
				int replacementIndex = parentReplacement.getPanes().indexOf(replacement);

				// Get the TitledPane for the list that each card is in
				TitledPane dragPaneList = (TitledPane) parentDragPane.getParent().getParent().getParent();
				TitledPane replacementList = (TitledPane) parentReplacement.getParent().getParent().getParent();

				// The weird thing with the exception with index of 0 again
				int dragPaneListIndex = 0;
				int replacementListIndex = 0;
				try
				{
					dragPaneListIndex = GridPane.getColumnIndex(dragPaneList);
				} catch (NullPointerException e)
				{
				}
				try
				{
					replacementListIndex = GridPane.getColumnIndex(replacementList);
				} catch (NullPointerException e)
				{
				}

				// If the parents are the same and the indices are the same of the
				// dragPane and the replacement, finish the drag because we dropped
				// the dragPane on top of itself
				if (dragPaneListIndex == replacementListIndex && dragPaneIndex == replacementIndex)
				{
					completeDragAccordion(event);
					return;
				}

				// Get the list for the source card and the source card itself
				BList sourceList = board.getLists().getMembers().get(dragPaneListIndex);
				Card sourceCard = sourceList.getCards().getMembers().get(dragPaneIndex);

				// Now, get the list for the target card
				BList targetList = board.getLists().getMembers().get(replacementListIndex);

				// Now, we do the swap
				if (dragPaneListIndex == replacementListIndex)
					System.out.println(sourceList.moveCard(sourceCard, replacementIndex, user));
				else
					System.out.println(sourceCard.switchList(targetList, replacementIndex, user));

				// Now, update the lists
				updateLists();

				// Make sure we save it
				saveTracker.addBoardChange(oldBoard, board, user);

				// Complete the drag
				completeDragAccordion(event);
			} else
			{
				// Complete the drag
				completeDragAccordion(event);
			}
		}
	}

	/**
	 * @param event - DragEvent that is occuring
	 * 
	 *              Still not 100% sure why this is necessary, but again it breaks
	 *              without it. This again works exactly the same as above
	 */
	@FXML
	public void Accordion_OnDragOver(DragEvent event)
	{
		Dragboard db = event.getDragboard();

		if (accFormat != null && db.hasContent(accFormat))
			event.acceptTransferModes(TransferMode.ANY);
	}

}
