package trello;

import java.util.Collection;
import java.util.Iterator;

/**
 * 
 * HasMembers Abstract Class
 *
 * @param <T> - Type for the Collection
 */
public interface HasMembers<T> extends Iterable<T>
{
	
	/**
	 * @param member - User to be added as a member
	 */
	public void addMember(T member);
	
	/**
	 * @param member   - Member to remove from the members set
	 * @return boolean - If the member is removed successfully, returns true
	 * 					 If the member is not removed successfully, returns false
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
	public abstract Collection<T> getMembers();
	
	/**
	 * @return Iterator<T> - Returns an iterator for the set
	 */
	public Iterator<T> iterator();

}
