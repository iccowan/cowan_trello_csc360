package trello;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * 
 * HasMembersSet Class Implements HasMembers
 *
 * @param <T> - Type for the Set
 */
public class HasMembersSet<T> implements HasMembers<T>
{

	private Set<T> members = new HashSet<T>();
	
	/**
	 * Default constructor
	 */
	public HasMembersSet() {}

	/**
	 * @param member - User to be added as a member
	 */
	public void addMember(T member)
	{
		members.add(member);
	}

	/**
	 * @param member   - Member to remove from the members set
	 * @return boolean - If the member is removed successfully, returns true
	 * 					 If the member is not removed successfully, returns false
	 */
	public boolean removeMember(T member)
	{
		// Check and make sure the user is a member
		if (! members.contains(member))
			return false;

		// Now, we know the member exists, so we can remove the member
		members.remove(member);

		// Success
		return true;
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
	}
	
}
