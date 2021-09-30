package controllers.save;

import java.util.ArrayList;
import java.util.Iterator;

import models.trello.Board;
import models.trello.User;
import models.trelloServer.TrelloServerInterface;

/**
 * SaveTrackController Class This is used to handle saving for the Trello
 *
 */
public class SaveTrackController
{
	// We create only ONE instance - Singleton
	// This isn't memory intensive and we will definitely use it at some point,
	// so we can just go ahead and create the instance
	private static SaveTrackController instance = new SaveTrackController();

	private final int OLD_BOARD = 0;
	private final int NEW_BOARD = 1;
	private final int USE_INDEX = 2;

	private boolean hasChanges = false;
	private ArrayList<Object[]> changedBoards = new ArrayList<Object[]>();

	// We use the observer pattern so the save button will be enabled/disabled
	// when there are changes that have been made
	private ArrayList<SaveTrackObserverInterface> observers = new ArrayList<SaveTrackObserverInterface>();

	/**
	 * Private constructor
	 */
	private SaveTrackController()
	{
	}

	/**
	 * @return the Singleton instance
	 */
	public static SaveTrackController getInstance()
	{
		return instance;
	}
	
	/**
	 * This resets the save tracker completely.
	 * This should really only be used for testing or if something goes wrong.
	 * This can BREAK things immensely if used improperly.
	 */
	public static SaveTrackController reset()
	{
		instance = new SaveTrackController();
		return instance;
	}

	/**
	 * @param obs - SaveTrackObserverInterface to add as an observer
	 */
	public static void subscribeToChanges(SaveTrackObserverInterface obs)
	{
		instance.observers.add(obs);
	}

	/**
	 * @param obs - SaveTrackObserverInterface to remove as an observer
	 * @return whether or not the observer was removed
	 */
	public static boolean unsubscribeFromChanges(SaveTrackObserverInterface obs)
	{
		return instance.observers.remove(obs);
	}

	/**
	 * Notify the observers that there has been a change made
	 */
	private void notifyObservers()
	{
		for (SaveTrackObserverInterface obs : observers)
			obs.saveTrackHasChangesChanged();
	}

	/**
	 * @param value - Whether or not there are changes
	 */
	private void toggleHasChanges(boolean value)
	{
		hasChanges = value;

		// Notify that there may have been a change
		notifyObservers();
	}

	/**
	 * @param oldBoard - Old board from the change
	 * @param newBoard - New board that has been changed
	 * @param user     - User that made the changes
	 */
	public void addBoardChange(Board oldBoard, Board newBoard, User user)
	{
		// Create a triplet with the oldBoard, newBoard, and user
		Object[] boardPair = new Object[3];

		// We need to make sure that the board doesn't already exit
		// If it does already exist and we save it, the user will have
		// a lot of duplicates when the log back in
		for (Object[] boards : changedBoards)
		{
			// In this case, we actually want to check memory references
			// Because of mutability, if the newBoard is already here,
			// then the changes are already in the newBoard
			if (((Board) boards[NEW_BOARD]) == newBoard)
			{
				// Already going to save it, just return
				return;
			}
		}

		// If we get here, we need to add the changes to the list
		boardPair[OLD_BOARD] = oldBoard;
		boardPair[NEW_BOARD] = newBoard;
		boardPair[USE_INDEX] = user;
		changedBoards.add(boardPair);

		// We now have changes
		toggleHasChanges(true);
	}

	/**
	 * @param newBoard - Board changes to remove from the changes
	 */
	public void removeBoardChange(Board newBoard)
	{
		// We loop through all of the changes
		for (Object[] boards : changedBoards)
		{
			// In this case, we actually want to check memory references
			if (((Board) boards[NEW_BOARD]) == newBoard)
			{
				// If we are at the point where we have the new board,
				// we just remove it from the changedBoards
				changedBoards.remove(boards);

				// If the changedBoards is now empty, we no longer have
				// changes that need to be made
				if (changedBoards.isEmpty())
					toggleHasChanges(false);

				// Stop here because there's no need to keep
				// looping through since each newBoard only exists once
				// in the changes list
				return;
			}
		}
	}

	/**
	 * @param trelloClient - Client to push the changes through
	 * @return whether or not the changes were able to be saved
	 */
	public boolean saveAllChanges(TrelloServerInterface trelloClient)
	{
		// Assume it's going to work
		boolean success = true;

		// We need to use the iterator so we can remove from the list
		// as we iterate through it
		Iterator<Object[]> changedBoardsIterator = changedBoards.iterator();

		// Iterate through the changes
		while (changedBoardsIterator.hasNext())
		{
			// Get the board triplet
			Object[] boards = changedBoardsIterator.next();
			try
			{
				// Attempt updating the board
				boolean didSave = trelloClient.updateBoard((Board) boards[OLD_BOARD], (Board) boards[NEW_BOARD],
						(User) boards[USE_INDEX]);

				// If we didn't save, then we were not successful
				if (!didSave)
					// Not successful
					success = false;
				else
					// We were successful, so remove the change
					changedBoardsIterator.remove();
			} catch (Exception e)
			{
				// Not successful and there was an exception that we can show debugging
				// information about
				success = false;
				e.printStackTrace();
			}
		}

		// We have changes depending on whether or not there are still changes
		// remaining in the list
		toggleHasChanges(!changedBoards.isEmpty());

		// Return whether or not we were successful
		return success;
	}

	/**
	 * @return whether or not changes exist - used by observers
	 */
	public boolean hasChanges()
	{
		return this.hasChanges;
	}
}
