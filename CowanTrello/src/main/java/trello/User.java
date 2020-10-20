package trello;

import java.util.ArrayList;

/**
 * User Class
 * 
 */
public class User
{

	private String name;
	private String password;
	private HasMembersList<Board> boards;

	/**
	 * @param name     - The name of the user that we are creating
	 * @param password - The password for the user that we are creating
	 */
	public User(String name, String password)
	{
		this.name = name;
		this.password = password;
		
		this.boards = new HasMembersList<Board>();
	}

	/**
	 * @param name     - The name of the user to login
	 * @param password - The password of the user to login
	 * @return User - The User object of the user that has been logged in
	 */
	public static User login(String name, String password)
	{
		//
		return new User("", "");
	}

	public void addBoard(Board board)
	{
		// Add the board to the user's list of boards
		boards.addMember(board);
	}

	/**
	 * @param board - The board to remove from the user's boards
	 * @return boolean - If the board is removed successfully, returns true
	 * 					 If the board is not removed successfully, returns false
	 */
	public boolean removeBoard(Board board)
	{
		return boards.removeMember(board);
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
	public ArrayList<Board> getBoards()
	{
		return boards.getMembers();
	}

}
