package models.trello;

import java.io.Serializable;
import java.util.ArrayList;

import models.trello.structures.CloneableInterface;
import models.trello.structures.HasMembersList;
import models.trello.structures.HasMembersSet;

/**
 * Board Class
 * 
 */
public class Board implements Serializable, CloneableInterface
{

	private static final long serialVersionUID = 4545177788465451067L;

	private String name;
	private User owner;
	private HasMembersSet<User> members = new HasMembersSet<User>();
	private HasMembersList<BList> lists = new HasMembersList<BList>();

	/**
	 * Default constructor
	 */
	public Board()
	{
	}

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
	 * @param list - BList to add to the lists This is package accessible to be used
	 *             when creating a new list
	 */
	void addList(BList list)
	{
		lists.addMember(list);
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
	 * @return boolean - Returns true or false depending on whether or not the list
	 *         was removed successfully
	 */
	public boolean removeList(BList list, User requester)
	{
		if (hasMember(requester))
			return lists.removeMember(list);
		return false;
	}

	/**
	 * @param list - The list to check and see if it exists
	 * @return boolean - Returns true or false depending on whether or not the list
	 *         exists
	 */
	public boolean hasList(BList list)
	{
		return lists.hasMember(list);
	}

	/**
	 * @param listName - Name of the list to be found
	 * @return the BList, if found
	 */
	public BList findList(String listName)
	{
		// Loop through all of the lists and see if we can find the requested list
		for (BList l : lists)
			if (l.getName().equals(listName))
				return l;

		// Could not be found
		return null;
	}

	/**
	 * @param list  - List to move in the board
	 * @param index - New index in the list list for the list
	 * @return boolean - Returns true or false depending on whether or not the list
	 *         was moved successfully
	 */
	public boolean moveList(BList list, int index, User requester)
	{
		// The requester must be a member
		if (hasMember(requester))
		{
			if (!lists.removeMember(list))
				return false;

			lists.addMember(index, list);

			// Success
			return true;
		}

		// Unsuccessful
		return false;
	}

	/**
	 * @param member    - User to add
	 * @param requester - Requesting user, should be the owner
	 */
	public void addMember(User member, User requester)
	{
		if (requester.equals(owner))
		{
			this.members.addMember(member);
			member.addBoard(this);
		}
	}

	/**
	 * @param member    - User to remove
	 * @param requester - Requesting user, should be the owner
	 * @return boolean - Returns true or false depending on whether or not the
	 *         member was removed successfully
	 */
	public boolean removeMember(User member, User requester)
	{
		// Only let the owner remove members and don't let the
		// owner remove themselves as a member
		if (requester.equals(owner) && !member.equals(owner))
			if (this.members.removeMember(member) && member.removeBoard(this))
				return true;

		return false;
	}

	/**
	 * @param member - User to check if they exist as a member
	 * @return boolean - Returns depending on whether the member exists or not
	 */
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
		if (that == null)
			return false;

		// Make sure the names are the same
		if (!this.name.equals(that.name))
			return false;

		// Make sure the owner is the same
		if (!this.owner.equals(that.owner))
			return false;

		// Make sure all of this lists are contained within that
		for (BList l : this.lists)
			if (!that.hasList(l))
				return false;

		// Make sure all of that lists are contained within this
		for (BList l : that.lists)
			if (!this.hasList(l))
				return false;

		// Everything is the same
		return true;
	}

	/**
	 * @return Board
	 */
	@Override
	public Board clone()
	{
		try
		{
			Board newBoard = new Board(this.name, new User(this.owner.getName(), this.owner.getPassword()));
			HasMembersSet<User> newMembers = new HasMembersSet<User>();
			HasMembersList<BList> newLists = lists.clone();

			for (User mem : members)
				newMembers.addMember(mem);

			// Update the parent board for each list
			for (BList l : newLists)
			{
				l.setBoard(newBoard);
			}

			newBoard.setMembers(newMembers);
			newBoard.setLists(newLists);

			return newBoard;
		} catch (CloneNotSupportedException e)
		{
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * @Overrides the Object toString() method
	 */
	@Override
	public String toString()
	{
		return this.name;
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
