package models.trello.structures;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleSetProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * 
 * HasMembersSet Class Implements HasMembers
 *
 * @param <T> - Type for the Set
 */
public class HasMembersSet<T> implements HasMembers<T>
{

	private static final long serialVersionUID = -1788415743956684809L;
	private Set<T> members = new HashSet<T>();
	private SimpleListProperty<T> observableList = new SerSimpleListProperty<T>();

	/**
	 * Default constructor
	 */
	public HasMembersSet()
	{
	}
	
	/**
	 * Updates the SimpleListProperty since we cannot simply add/remove
	 * items and we also want it to act like a set, not a list
	 */
	private void updateObservableMembers()
	{
		// Create a new list since lists are more commonly needed to link
		// than sets
		ArrayList<T> membersList = new ArrayList<T>();
		
		// For each member, add them to the list
		for (T mem : members)
			membersList.add(mem);
		
		// Set the observable list
		observableList.set(FXCollections.observableList(membersList));
	}
	
	/**
	 * @param member - T to add to all lists
	 */
	private void addToLists(T member)
	{
		members.add(member);
		
		// Update observable list
		updateObservableMembers();
	}
	
	/**
	 * @param member - T to remove from the lists
	 * @return whether or not the member was removed from the lists
	 */
	private boolean removeFromLists(T member)
	{
		boolean success = members.remove(member);
		
		// Update the observable list
		updateObservableMembers();
		
		return success;
	}

	/**
	 * @param member - User to be added as a member
	 */
	public void addMember(T member)
	{
		addToLists(member);
	}

	/**
	 * @param member - Member to remove from the members set
	 * @return boolean - Returns whether or not the member was removed successfully
	 */
	public boolean removeMember(T member)
	{
		// Check and make sure the user is a member
		if (! members.contains(member))
			return false;

		// Now, we know the member exists, so we can remove the member
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
	public boolean equals(Object thatObject)
	{
		HasMembersSet<T> that = (HasMembersSet<T>) thatObject;

		return this.members.equals(that.members);
	}
	
	/**
	 * @Overrides the Object clone() method
	 */
	@SuppressWarnings("unchecked")
	@Override
	public HasMembersSet<T> clone() throws CloneNotSupportedException
	{
		try
		{
			HasMembersSet<T> newSet = new HasMembersSet<T>();
			Iterator<T> it = iterator();
			
			while(it.hasNext())
			{
				CloneableInterface next = (CloneableInterface) it.next();
				T nextClone = (T) next.clone();
				newSet.addMember(nextClone);
			}
			
			return newSet;
		}
		catch (Exception e)
		{
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
	public Set<T> getMembers()
	{
		return members;
	}

	/**
	 * @param members the members to set
	 */
	public void setMembers(Set<T> members)
	{
		this.members = members;
		updateObservableMembers();
	}

	/**
	 * @return the observableList
	 */
	public ObservableList<T> getObservableMembers()
	{
		updateObservableMembers();
		return observableList;
	}

	/**
	 * @param observableSet the observableSet to set
	 */
	public void setObservableSet(SimpleSetProperty<T> observableSet)
	{
		// Do nothing, required for serialization
	}

}
