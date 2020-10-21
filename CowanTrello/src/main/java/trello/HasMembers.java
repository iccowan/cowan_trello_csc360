package trello;

import java.util.Collection;
import java.util.Iterator;

/**
 * 
 * HasMembers Abstract Class
 *
 * @param <T> - Type for the Collection
 */
public abstract class HasMembers<T> implements Iterable<T>
{
	
	protected Collection<T> members;
	
	/**
	 * @param member - User to be added as a member
	 */
	public abstract void addMember(T member);
	
	/**
	 * @param member   - Member to remove from the members set
	 * @return boolean - If the member is removed successfully, returns true
	 * 					 If the member is not removed successfully, returns false
	 */
	public abstract boolean removeMember(T member);
	
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
	 * @return the members
	 */
	public abstract Collection<T> getMembers();
	
	/**
	 * @return Iterator<T> - Returns an iterator for the set
	 */
	public Iterator<T> iterator()
	{
		return new HasMembersIterator<T>(this.members);
	}

}
