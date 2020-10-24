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
	private HasMembersSet<User> members = new HasMembersSet<User>();
	private HasMembersList<BList> lists = new HasMembersList<BList>();
	
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
		this.members.addMember(owner);
		owner.addBoard(this);
	}

	/**
	 * @param list - List to add to the board
	 */
	public void addList(BList list, User requester)
	{
		if (hasMember(requester))
			lists.addMember(list);
	}

	/**
	 * @param list - List to remove from the board
	 * @return boolean - If the list is removed successfully, returns true
	 * 					 If the list is not removed successfully, returns false
	 */
	public boolean removeList(BList list, User requester)
	{
		if (hasMember(requester))
			return lists.removeMember(list);
		return false;
	}
	
	public boolean hasList(BList list)
	{
		return lists.hasMember(list);
	}
	
	public void addMember(User member, User requester)
	{
		if (requester.equals(owner))
			this.members.addMember(member);
	}
	
	public boolean removeMember(User member, User requester)
	{
		// Only let the owner remove members and don't let the
		// owner remove themselves as a member
		if (requester.equals(owner) && ! member.equals(owner))
			return this.members.removeMember(member);
		return false;
	}
	
	public boolean hasMember(User member)
	{
		return members.hasMember(member);
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
	public HasMembersSet<User> getMembers()
	{
		return members;
	}

	/**
	 * @return the lists
	 */
	public HasMembersList<BList> getLists()
	{
		return lists;
	}

	/**
	 * @param owner the owner to set
	 */
	public void setOwner(User owner)
	{
		this.owner = owner;
	}
	
	/**
	 * @param members the members to set
	 */
	public void setMembers(HasMembersSet<User> members)
	{
		this.members = members;
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
	@Override
	public boolean equals(Object thatObj)
	{
		Board that = (Board) thatObj;
		
		// Make sure the names are the same
		if (! this.name.equals(that.name))
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
	 * @param all - Array list of all objects to serialize
	 */
	public static void serializeToXML(ArrayList<Board> all)
	{
		XMLSerializer.<Board>serializeToXML(all, "Board");
	}
	
	/**
	 * @return ArrayList<Board> - The array list of objects that we want to return
	 */
	public static ArrayList<Board> deserializeFromXML()
	{
		return XMLSerializer.<Board>deserializeFromXML("Board");
	}

}
