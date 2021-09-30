package models.trello.structures;

import java.util.ArrayList;
import java.util.Iterator;

import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * 
 * HasMembersList Class implements HasMembers
 *
 * @param <T> - Type for the ArrayList
 */
public class HasMembersList<T> implements HasMembers<T>
{

	private static final long serialVersionUID = 7867416076725651572L;
	private ArrayList<T> members = new ArrayList<T>();
	private SimpleListProperty<T> observableMembers = new SerSimpleListProperty<T>();

	/**
	 * Default constructor
	 */
	public HasMembersList()
	{
	}

	/**
	 * Updates the SimpleListProperty since adding an element really doesn't work
	 */
	private void updateObservableMembers()
	{
		ArrayList<T> newList = new ArrayList<T>();
		for (T mem : members)
			newList.add(mem);
		
		observableMembers.set(FXCollections.observableArrayList(newList));
	}

	/**
	 * @param member - T to add to all lists
	 */
	private void addToLists(T member)
	{
		members.add(member);

		// Update the observable list
		updateObservableMembers();
	}

	/**
	 * @param index  - Index to add in the list
	 * @param member - T to add to all lists
	 */
	private void addToLists(int index, T member)
	{
		members.add(index, member);

		// Update the observable list
		updateObservableMembers();
	}

	/**
	 * @param index - Index to remove from the lists
	 */
	private boolean removeFromLists(T member)
	{
		boolean success = members.remove(member);

		// Update the observable list
		updateObservableMembers();
		
		return success;
	}

	/**
	 * @param member - Member to be added as a member
	 */
	public void addMember(T member)
	{
		addToLists(member);
	}

	/**
	 * @param member - Member to add
	 * @param index  - Index for the new member to be added
	 */
	public void addMember(int index, T member)
	{
		// If the index is not valid, add to the end of the list
		// If the index is valid, add to that index
		if (index < 0 || index >= members.size())
			addToLists(member);
		else
			addToLists(index, member);
	}

	/**
	 * @param member - Member to remove from the members set
	 * @return boolean - Returns whether or not the member was removed successfully
	 */
	public boolean removeMember(T member)
	{
		// Check and make sure the list exists in the board
		if (! members.contains(member))
			return false;
		
		// Remove from the lists
		return removeFromLists(member);
	}

	/**
	 * @param member - Member to see if it exists here
	 * @return boolean - Returns whether or not it exists here
	 */
	public boolean hasMember(T member)
	{
		// Check every member and see if the member equals it
		for (T m : members)
			if (member.equals(m))
				return true; // Here

		// Not here
		return false;
	}

	/**
	 * @return boolean - Whether or not the collections of T are equal
	 */
	@SuppressWarnings("unchecked")
	@Override
	public boolean equals(Object thatObj)
	{
		HasMembersList<T> that = (HasMembersList<T>) thatObj;

		return this.members.equals(that.members);
	}

	/**
	 * @Overrides the Object clone() method
	 */
	@SuppressWarnings("unchecked")
	@Override
	public HasMembersList<T> clone() throws CloneNotSupportedException
	{
		try
		{
			HasMembersList<T> newList = new HasMembersList<T>();

			for (T mem : members)
			{
				CloneableInterface memCloneable = (CloneableInterface) mem;
				T memClone = (T) memCloneable.clone();
				newList.addMember(memClone);
			}

			return newList;
		} catch (Exception e)
		{
			e.printStackTrace();
			throw new CloneNotSupportedException();
		}
	}

	/**
	 * @return Iterator<T> - Returns an iterator for the set
	 */
	public Iterator<T> iterator()
	{
		return new HasMembersIterator<T>(members);
	}

	/**
	 * @return the members
	 */
	public ArrayList<T> getMembers()
	{
		return members;
	}

	/**
	 * @param members the members to set
	 */
	public void setMembers(ArrayList<T> members)
	{
		this.members = members;
		updateObservableMembers();
	}

	/**
	 * @return the observableMembers
	 */
	public ObservableList<T> getObservableMembers()
	{
		updateObservableMembers();
		return observableMembers;
	}

	/**
	 * @param observableMembers the observableMembers to set
	 */
	public void setObservableMembers(SimpleListProperty<T> observableMembers)
	{
		// Do nothing, but required for serialization
	}

}
