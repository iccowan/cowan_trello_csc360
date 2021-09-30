package models.trelloServer;

import java.rmi.*;
import models.trello.*;

/**
 * TrelloServerInterface Interface
 * 
 * @extends Remote
 *
 */
public interface TrelloServerInterface extends Remote
{

	/**
	 * @param username - Name of the user to authenticate
	 * @param password - Password of the user to authenticate
	 * @return the authenticated user
	 * @throws RemoteException
	 */
	public User authenticateUser(String name, String password) throws RemoteException, UserNotFoundException;

	/**
	 * @param name - Name of the user to find
	 * @return the User, if found
	 * @throws RemoteException
	 * @throws UserNotFoundException
	 */
	public User findUser(String name) throws RemoteException, UserNotFoundException;

	/**
	 * @param name  - Of the new board
	 * @param owner - Owner of the new board
	 * @return the new board
	 * @throws RemoteException
	 */
	public Board createBoard(String name, User requester) throws RemoteException, UserNotFoundException;

	/**
	 * @param name      - Of the board to retrieve
	 * @param requester - User requesting the board
	 * @return the requested board
	 * @throws RemoteException
	 */
	public Board getBoard(String name, User requester)
			throws RemoteException, UserNotFoundException, BoardNotFoundException;

	/**
	 * @param oldBoard - The old copy of the board to replace
	 * @param newBoard - The new copy of the board
	 * @return whether or not the board successfully updated
	 * @throws RemoteException
	 */
	public boolean updateBoard(Board oldBoard, Board newBoard, User requester)
			throws RemoteException, BoardNotFoundException, UserNotFoundException;

	/**
	 * @param board     - The board to be deleted
	 * @param requester - The user requesting to delete the board
	 * @return whether or not the board could be deleted
	 * @throws RemoteException
	 * @throws BoardNotFoundException
	 * @throws UserNotFoundException
	 */
	public boolean deleteBoard(Board board, User requester) throws RemoteException, UserNotFoundException;
	
	/**
	 * @return the URL of the design
	 * @throws RemoteException
	 */
	public String getDesign() throws RemoteException;

}
