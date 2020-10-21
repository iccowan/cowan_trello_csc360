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
	 * Default constructor
	 */
	
	public Board() {}

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
	
	public boolean hasList(BList list)
	{
		return lists.hasMember(list);
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

	/**
	 * @param owner the owner to set
	 */
	public void setOwner(User owner)
	{
		this.owner = owner;
	}

	/**
	 * @param lists the lists to set
	 */
	public void setLists(HasMembersList<BList> lists)
	{
		this.lists = lists;
	}
	
	/**
	 * @param that - Board to compare
	 * @return boolean
	 */
	public boolean equals(Board that)
	{
		// Make sure the names are the same
		if (this.name != that.name)
			return false;
		
		// Make sure the owner is the same
		if (! this.owner.equals(that.owner))
			return false;
		
		// Make sure all of this lists are contained within that
		for (BList l : this.lists)
			if (! that.hasList(l))
				return false;
		
		// Make sure all of that lists are contained within this
		for (BList l : that.lists)
			if(! this.hasList(l))
				return false;
		
		// Everything is the same
		return true;
	}
	
	/**
	 * @return String - The string of the file where the serialized object lives
	 */
	public String serializeToXML()
	{
		return XMLSerializer.<Board>serializeToXML(this);
	}
	
	/**
	 * @param objectFileName - File name where the object lives that we're going to deserialize
	 * @return Board - The list object that we want to return
	 */
	public static Board deserializeFromXML(String objectFileName)
	{
		return XMLSerializer.<Board>deserializeFromXML(objectFileName);
	}

}
