package models.trelloServer;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import models.trello.Board;
import models.trello.User;

/**
 * TrelloClient class
 * 
 * The purpose of this is simply to be a proxy for the TrelloServer that the
 * user wants to interact with
 *
 * @implements TrelloServerInterface
 */
public class TrelloClient implements TrelloServerInterface
{

	// The server that we are connected to
	private TrelloServerInterface trelloServer;

	/**
	 * Default constructor
	 */
	protected TrelloClient()
	{
		
	}
	
	/**
	 * @param host       - Hostname to connect to
	 * @param serverName - Servername to connect to
	 * @throws RemoteException, NotBoundException
	 */
	public TrelloClient(String host, String serverName) throws RemoteException, NotBoundException
	{
		// Connect to the registry and lookup the server
		Registry registry = LocateRegistry.getRegistry(host);
		trelloServer = (TrelloServerInterface) registry.lookup(serverName);
	}

	/**
	 * @param username - Name of the user to authenticate
	 * @param password - Password of the user to authenticate
	 * @return the authenticated user
	 * @throws RemoteException
	 */
	@Override
	public User authenticateUser(String name, String password) throws RemoteException, UserNotFoundException
	{
		return trelloServer.authenticateUser(name, password);
	}

	/**
	 * @param name - Name of the user to find
	 * @return the User, if found
	 * @throws RemoteException
	 * @throws UserNotFoundException
	 */
	@Override
	public User findUser(String name) throws RemoteException, UserNotFoundException
	{
		return trelloServer.findUser(name);
	}

	/**
	 * @param name  - Of the new board
	 * @param owner - Owner of the new board
	 * @return the new board
	 * @throws RemoteException
	 */
	@Override
	public Board createBoard(String name, User requester) throws RemoteException, UserNotFoundException
	{
		return trelloServer.createBoard(name, requester);
	}

	/**
	 * @param name      - Of the board to retrieve
	 * @param requester - User requesting the board
	 * @return the requested board
	 * @throws RemoteException
	 */
	@Override
	public Board getBoard(String name, User requester)
			throws RemoteException, UserNotFoundException, BoardNotFoundException
	{
		return trelloServer.getBoard(name, requester);
	}

	/**
	 * @param oldBoard - The old copy of the board to replace
	 * @param newBoard - The new copy of the board
	 * @return whether or not the board successfully updated
	 * @throws RemoteException
	 */
	@Override
	public boolean updateBoard(Board oldBoard, Board newBoard, User requester)
			throws RemoteException, BoardNotFoundException, UserNotFoundException
	{
		return trelloServer.updateBoard(oldBoard, newBoard, requester);
	}

	/**
	 * @param board     - The board to be deleted
	 * @param requester - The user requesting to delete the board
	 * @return whether or not the board could be deleted
	 * @throws RemoteException
	 * @throws BoardNotFoundException
	 * @throws UserNotFoundException
	 */
	@Override
	public boolean deleteBoard(Board board, User requester) throws RemoteException, UserNotFoundException
	{
		return trelloServer.deleteBoard(board, requester);
	}
	
	/**
	 * @return the URL of the design
	 * @throws RemoteException
	 */
	public String getDesign() throws RemoteException
	{
		return trelloServer.getDesign();
	}

}
