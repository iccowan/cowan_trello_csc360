package models.trello.structures;

import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;

import javafx.collections.ObservableList;

/**
 * 
 * HasMembers Abstract Class
 *
 * @param <T> - Type for the Collection
 */
public interface HasMembers<T> extends Serializable, Iterable<T>
{

	/**
	 * @param member - User to be added as a member
	 */
	public void addMember(T member);

	/**
	 * @param member - Member to remove from the members set
	 * @return boolean - Returns whether or not the member was added successfully
	 */
	public boolean removeMember(T member);

	/**
	 * @param member - Member to see if it exists here
	 * @return boolean - Returns whether or not it exists here
	 */
	public boolean hasMember(T member);

	/**
	 * @return the members
	 */
	public Collection<T> getMembers();

	/**
	 * @return the observable list
	 */
	public ObservableList<T> getObservableMembers();

	/**
	 * @return Iterator<T> - Returns an iterator for the set
	 */
	public Iterator<T> iterator();

}
