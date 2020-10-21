package trello;

import java.util.HashSet;
import java.util.Set;

/**
 * 
 * HasMembersSet Class Implements HasMembers
 *
 * @param <T> - Type for the Set
 */
public class HasMembersSet<T> extends HasMembers<T>
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
