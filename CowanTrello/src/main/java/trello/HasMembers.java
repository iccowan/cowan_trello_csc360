package trello;

import java.util.Collection;

/**
 * 
 * HasMembers Interface
 *
 * @param <T> - Type for the Collection
 */
public interface HasMembers<T>
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
	 * @return the members
	 */
	public Collection<T> getMembers();

}
