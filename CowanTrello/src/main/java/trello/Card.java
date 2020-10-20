package trello;

import java.util.ArrayList;
import java.util.Set;

/**
 * Card Class
 *
 */
public class Card
{

	private String name;
	private BList list;
	private HasMembersSet<Label> labels;
	private HasMembersList<Component> components;

	/**
	 * @param name - The name of the card
	 * @param list - The list that the card is a part of
	 */
	public Card(String name, BList list)
	{
		this.name = name;
		this.list = list;
		this.labels = new HasMembersSet<Label>();
		this.components = new HasMembersList<Component>();
	}

	/**
	 * @param newList - The new list to add the card to
	 * @param newIndex - The new index for the card to be inserted into for the list
	 * @return boolean - If the list is successfully switched, returns true
	 * 					 If the list is unable to be switched, returns false
	 */
	public boolean switchList(BList newList, int newIndex)
	{
		// First, we need to let the current list know that we're leaving
		// This method will return false if it's unable to do what we want
		if(! list.removeCard(this))
			return false;
		
		// Now, we're out of the list so let's continue adding to the new list
		newList.addCard(newIndex, this);
		
		// Success
		return true;
	}
	
	/**
	 * @param label - Label to be added to the card
	 */
	public void addLabel(Label label)
	{
		labels.addMember(label);
	}
	
	/**
	 * @param label    - Label to remove from the card
	 * @return boolean - If the label is removed successfully, returns true
	 * 					 If the label is not removed successfully, returns false
	 */
	public boolean removeLabel(Label label)
	{
		return labels.removeMember(label);
	}

	/**
	 * @param component - Component to be added to the card
	 */
	public void addComponent(Component component)
	{
		components.addMember(component);
	}
	
	/**
	 * @param component - Component to remove from the card
	 * @return boolean  - If the component is removed successfully, returns true
	 * 					  If the component is not removed successfully, returns false
	 */
	public boolean removeComponent(Component component)
	{
		return components.removeMember(component);
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
	 * @return the list
	 */
	public BList getList()
	{
		return list;
	}
	
	/**
	 * @return the labels
	 */
	public Set<Label> getLabels()
	{
		return labels.getMembers();
	}

	/**
	 * @return the components
	 */
	public ArrayList<Component> getComponents()
	{
		return components.getMembers();
	}

}
