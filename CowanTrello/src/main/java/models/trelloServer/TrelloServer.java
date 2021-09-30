package models.trelloServer;

import java.rmi.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.*;
import java.util.ArrayList;

import models.trello.*;
import models.trello.design.*;

/**
 * TrelloServer
 * 
 * @implements TrelloServerInterface
 *
 */
public class TrelloServer extends UnicastRemoteObject implements TrelloServerInterface
{

	private static final long serialVersionUID = -7496452202861199863L;

	private ArrayList<User> users;
	private Design design;

	/**
	 * @param designName - Name of the requested server design
	 * @throws RemoteException
	 */
	public TrelloServer(String designName) throws RemoteException
	{
		// Get the requested design
		design = DesignFactory.newDesign(designName);

		// Load the users and the boards from the XML files on the disk
		users = User.deserializeFromXML();
	}

	/**
	 * @param name     - Name of the user to authenticate
	 * @param password - Password of the user to authenticate
	 * @return the authenticated user
	 * @throws RemoteException, UserNotFoundException
	 */
	@Override
	public User authenticateUser(String name, String password) throws RemoteException, UserNotFoundException
	{
		// Loop through all of our users and find the user
		for (User u : users)
			if (u.getName().equals(name))
				if (u.login(name, password))
					return u;
				else
					return null; // Unable to authenticate the user, return nothing

		// If we get here, there's no user that exists with the credentials
		throw new UserNotFoundException("User " + name + " not found");
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
		// Loop through all of the users and find the user
		for (User u : users)
			if (u.getName().equals(name))
				return u;

		// If we get here, there's no user that exists by that name
		throw new UserNotFoundException("User " + name + " not found");
	}

	/**
	 * @param u - User to check if they exist on the server
	 * @return the user if found on the server
	 * @throws UserNotFoundException
	 */
	private User getServerUser(User u) throws UserNotFoundException
	{
		// Loop through all of our users and see if the requested user exists
		// If they exist, return the user object from the server
		for (User user : users)
			if (user.equals(u))
				return user;

		// If we get here, the user does not exist
		throw new UserNotFoundException("The user, " + u.getName() + " could not be found.");
	}

	/**
	 * @param name      - Name of the board to retrieve
	 * @param requester - User requesting the board
	 * @throws RemoteException
	 * @throws BoardNotFoundException
	 */
	@Override
	public Board getBoard(String name, User requester)
			throws RemoteException, UserNotFoundException, BoardNotFoundException
	{
		// Let's look at all of the boards in memory, and find the one with the
		// name that is requested

		// Let's get the user from the server that is the requester
		User serverRequester = getServerUser(requester);

		for (Board b : serverRequester.getBoards())
			if (b.getName().equals(name))
				return b;

		// If no board was found, let's throw an exception
		throw new BoardNotFoundException("Board, " + name + " not found");
	}

	/**
	 * @param name      - Name of the board to create
	 * @param requester - User requesting the new board
	 * @return the newly created board
	 * @throws RemoteException
	 */
	@Override
	public Board createBoard(String name, User requester) throws RemoteException, UserNotFoundException
	{
		User serverRequester = getServerUser(requester);

		// Create a new board and return it
		// The requester will be the owner of the board
		Board newBoard = new Board(name, serverRequester);

		// Save the users to XML
		User.serializeToXML(users);

		return newBoard;
	}

	/**
	 * @param oldBoard  - The old board object
	 * @param newBoard  - The new board object to replace the old board
	 * @param requester - The person requesting that the board be updated
	 * @return whether or not the update was successful
	 * @throws RemoteException
	 * @throws BoardNotFoundException
	 */
	@Override
	public boolean updateBoard(Board oldBoard, Board newBoard, User requester)
			throws RemoteException, BoardNotFoundException, UserNotFoundException
	{
		// First, make sure the requester is a member of the board
		if (!oldBoard.hasMember(requester))
			return false;

		User serverRequester = getServerUser(requester);

		// Let's make sure the old board actually exists, and replace it and return true
		if (oldBoard.hasMember(serverRequester))
		{
			for (Board b : serverRequester.getBoards())
			{
				if (b.equals(oldBoard))
				{
					oldBoard = b;
					break;
				}
			}

			// We need to update all of the information on the old board because
			// if not, it'll only be updated for this one user
			// No need to update the members since this cannot be updated
			oldBoard.setName(newBoard.getName());
			oldBoard.setOwner(getServerUser(newBoard.getOwner()));
			oldBoard.setLists(newBoard.getLists());

			// Now, we need to set all of the members as appropriate, but only if the
			// user is the owner of the board
			if (oldBoard.getOwner().equals(serverRequester))
			{
				for (User mem : newBoard.getMembers())
				{
					User serverMem = getServerUser(mem);
					if (!oldBoard.hasMember(serverMem))
						oldBoard.addMember(serverMem, serverRequester);

					serverMem.addBoard(oldBoard);
				}
			}

			// Save all the changes to XML
			User.serializeToXML(users);

			return true;
		}

		// If we get here, the board doesn't exist so throw an exception
		throw new BoardNotFoundException("Board, " + oldBoard.getName() + " not found for user " + requester.getName());
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
		// Make sure that the requester is the owner of the board
		if (!board.getOwner().equals(requester))
			return false;

		// Get the board from the server, not a copy
		User serverRequester = getServerUser(requester);
		int oldBoardIndex = serverRequester.getBoards().getMembers().indexOf(board);
		board = serverRequester.getBoards().getMembers().get(oldBoardIndex);

		// Now, loop through all of the members of the board and make sure they know
		// that the board no longer exists
		for (User mem : board.getMembers())
			mem.removeBoard(board);

		// Save all the changes to XML
		User.serializeToXML(users);

		return true;
	}

	/**
	 * @return the URL of the design
	 * @throws RemoteException
	 */
	public String getDesign() throws RemoteException
	{
		return design.getDesign();
	}

	/**
	 * @return the users
	 */
	public ArrayList<User> getUsers()
	{
		return this.users;
	}

	/**
	 * @param usersList the users to set
	 */
	public void setUsers(ArrayList<User> usersList)
	{
		this.users = usersList;
	}
	
	/**
	 * @param serverName - Name of the server
	 * @param portNumber - Port of the registry
	 */
	public static void startServer(String serverName, int portNumber)
	{
		startServer(serverName, portNumber, "");
	}

	/**
	 * @param serverName - Name of the server
	 * @param portNumber - Port of the registry
	 * @param design - The design to use
	 * @param cssFile - The custom CSS file, if applicable
	 */
	public static void startServer(String serverName, int portNumber, String design)
	{
		try
		{
			Registry rmiregistry = LocateRegistry.getRegistry(portNumber);
			try
			{
				rmiregistry.lookup("SERVER");
			} catch (ConnectException e)
			{
				rmiregistry = LocateRegistry.createRegistry(portNumber);
			} catch (NotBoundException e)
			{
				// Just continue to finally, no need to handle anything here
			} finally
			{
				TrelloServer server = new TrelloServer(design);
				rmiregistry.rebind(serverName, server);
			}

			// Let them know the server is running
			//System.out.println(serverName + " is running on RMI Registry port " + String.valueOf(portNumber) + "...\n");
		} catch (RemoteException e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * @param serverName - Name of the server
	 * @param portNumber - Port of the registry
	 */
	public static void closeServer(String serverName, int portNumber)
	{
		try
		{
			// The registry should be running when this is called
			Registry rmiregistry = LocateRegistry.getRegistry(portNumber);

			rmiregistry.unbind(serverName);

			//System.out.println(serverName + " Closed\n");
		} catch (RemoteException | NotBoundException e)
		{
			e.printStackTrace();
		}
	}

	/***
	 * We now have an interface for this, so we're going to disable the main static function
	/**
	 * @param args
	 * 
	 *             Run with args[0] => Server name, args[1] => Server port number
	 * 
	 *             Ex: java TrelloServer [SERVER_NAME] [PORT_NUMBER (Default 1099)]
	 **
	public static void main(String[] args)
	{
		// Get the requested server name and port number
		String serverName = args[0];
		int portNumber = Integer.valueOf(args[1]);

		// Start the server
		startServer(serverName, portNumber);

		// Now, the server is started and we wait for the EOF key
		// CTRL + D
		Scanner inScanner = new Scanner(System.in);
		while (inScanner.hasNext())
			inScanner.nextLine();

		// Now, the user has ended the program
		// Close the scanner
		inScanner.close();

		// Close the server
		closeServer(serverName, portNumber);
	}
	***/

}
