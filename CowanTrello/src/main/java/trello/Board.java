package trello;

import java.util.ArrayList;

/**
 * Board Class
 * 
 */
public class Board
{

	private String name;
	private User owner;
	private HasMembersList<BList> lists;

	/**
	 * @param name  - Name of the board to be created
	 * @param owner - Owning user of the board to be created
	 */
	public Board(String name, User owner)
	{
		this.name = name;
		this.owner = owner;
		this.lists = new HasMembersList<BList>();
	}

	/**
	 * @param list - List to add to the board
	 */
	public void addList(BList list)
	{
		lists.addMember(list);
	}

	/**
	 * @param list - List to remove from the board
	 * @return boolean - If the list is removed successfully, returns true
	 * 					 If the list is not removed successfully, returns false
	 */
	public boolean removeList(BList list)
	{
		return lists.removeMember(list);
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
	 * @return the lists
	 */
	public ArrayList<BList> getLists()
	{
		return lists.getMembers();
	}

}
