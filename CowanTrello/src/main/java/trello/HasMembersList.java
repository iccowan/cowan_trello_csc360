package trello;

import java.util.ArrayList;

/**
 * 
 * HasMembersList Class implements HasMembers
 *
 * @param <T> - Type for the ArrayList
 */
public class HasMembersList<T> extends HasMembers<T>
{
	
	private ArrayList<T> members = new ArrayList<T>();
	
	/**
	 * Default constructor
	 */
	public HasMembersList() {}

	/**
	 * @param member - Member to be added as a member
	 */
	public void addMember(T member)
	{
		members.add(member);
	}
	
	/**
	 * @param member - Member to add
	 * @param index  - Index for the new member to be added
	 */
	public void addMember(int index, T member)
	{
		// If the index is not valid, add to the end of the list
		// If the index is valid, add to that index
		if(index < 0 || index >= members.size())
			members.add(member);
		else
			members.add(index, member);
	}
	
	/**
	 * @param member   - Member to remove from the members set
	 * @return boolean - If the member is removed successfully, returns true
	 * 					 If the member is not removed successfully, returns false
	 */
	public boolean removeMember(T member)
	{
		// Check and make sure the list exists in the board
		if (! members.contains(member))
			return false;

		// Now, we know the member exists, so remove the member
		int memIndex = members.indexOf(member);
		members.remove(memIndex);

		// Success
		return true;
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
	}
	
}
