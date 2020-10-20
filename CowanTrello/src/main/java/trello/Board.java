package trello;

import java.util.ArrayList;
import java.util.Set;

/**
 * Board Class
 * 
 */
public class Board
{

	private String name;
	private User owner;
	private Set<User> members;
	private ArrayList<BList> lists;

	/**
	 * @param name  - Name of the board to be created
	 * @param owner - Ownning user of the board to be created
	 */
	public Board(String name, User owner)
	{
		this.name = name;
		this.owner = owner;
	}

	/**
	 * @param member - User to add as a member to the board
	 */
	public void addMember(User member)
	{
		members.add(member);
	}

	/**
	 * @param member - User to remove from the members set
	 * @return boolean - If the member is removed successfully, returns true
	 * 					 If the member is not removed successfully, returns false
	 */
	public boolean removeMember(User member)
	{
		// Check and make sure the user is a member of the board
		if (! members.contains(member))
			return false;

		// Now, we know the member exists within the board, so we can remove the member
		members.remove(member);

		// Success
		return true;
	}

	/**
	 * @param list - List to add to the board
	 */
	public void addList(BList list)
	{
		lists.add(list);
	}

	/**
	 * @param list - List to remove from the board
	 * @return boolean - If the list is removed successfully, returns true
	 * 					 If the list is not removed successfully, returns false
	 */
	public boolean removeList(BList list)
	{
		// Check and make sure the list exists in the board
		if (! lists.contains(list))
			return false;

		// Now, we know the list exists within the board, so we can remove the list
		int listIndex = lists.indexOf(list);
		lists.remove(listIndex);

		// Success
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
	 * @return the owner
	 */
	public User getOwner()
	{
		return owner;
	}

	/**
	 * @return the members
	 */
	public Set<User> getMembers()
	{
		return members;
	}

	/**
	 * @return the lists
	 */
	public ArrayList<BList> getLists()
	{
		return lists;
	}

}
