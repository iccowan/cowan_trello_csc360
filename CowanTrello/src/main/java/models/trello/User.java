package models.trello;

import java.io.Serializable;
import java.util.ArrayList;

import models.trello.structures.HasMembersList;

/**
 * User Class
 * 
 */
public class User implements Serializable
{

	private static final long serialVersionUID = -271428118164221489L;

	private String name;
	private String password;
	private HasMembersList<Board> boards = new HasMembersList<Board>();

	/**
	 * Default constructor
	 */
	public User()
	{
	}

	/**
	 * @param name     - The name of the user that we are creating
	 * @param password - The password for the user that we are creating
	 */
	public User(String name, String password)
	{
		this.name = name;
		this.password = password;
	}

	/**
	 * @param name     - The name of the user to login
	 * @param password - The password of the user to login
	 * @return boolean - Returns whether or not the login was successful
	 */
	public boolean login(String name, String password)
	{
		if (this.name.equals(name) && this.password.equals(password))
			return true;
		else
			return false;
	}

	/**
	 * @param board - The board to add to the user
	 */
	public void addBoard(Board board)
	{
		// Add the board to the user's list of boards
		if (!boards.hasMember(board))
			boards.addMember(board);
	}

	/**
	 * @param board - The board to remove from the user's boards
	 * @return boolean - If the board is removed successfully, returns true If the
	 *         board is not removed successfully, returns false
	 */
	public boolean removeBoard(Board board)
	{
		return boards.removeMember(board);
	}

	/**
	 * @param thatObj - The user to check for equality
	 * @return boolean - Whether or not the users are equal
	 */
	@Override
	public boolean equals(Object thatObj)
	{
		User that = (User) thatObj;
		if (that == null)
			return false;

		// Make sure the names and passwords are the same
		// Ideally, the names should be unique for authentication purposes,
		// so this should be all that we need to check
		if (!this.name.equals(that.name))
			return false;

		if (!this.password.equals(that.password))
			return false;

		// If we get here, they're the same user
		return true;
	}

	/**
	 * @return the name
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name)
	{
		this.name = name;
	}

	/**
	 * @param password the password to set
	 */
	public void setPassword(String password)
	{
		this.password = password;
	}

	/**
	 * @return the boards
	 */
	public HasMembersList<Board> getBoards()
	{
		return boards;
	}

	/**
	 * @return the password
	 */
	public String getPassword()
	{
		return password;
	}

	/**
	 * @param boards the boards to set
	 */
	public void setBoards(HasMembersList<Board> boards)
	{
		this.boards = boards;
	}

	public String toString()
	{
		return this.name;
	}

	/**
	 * @param all - Array list of all objects to serialize
	 */
	public static void serializeToXML(ArrayList<User> all)
	{
		XMLSerializer.<User>serializeToXML(all, "User");
	}

	/**
	 * @return ArrayList<User> - The array list of objects that we want to return
	 */
	public static ArrayList<User> deserializeFromXML()
	{
		return XMLSerializer.<User>deserializeFromXML("User");
	}

}
