package trello;

import java.util.ArrayList;

/**
 * Card Class
 *
 */
public class Card
{

	private String name;
	private BList list;
	private HasMembersSet<Label> labels = new HasMembersSet<Label>();
	private HasMembersSet<User> members = new HasMembersSet<User>();
	private HasMembersList<Component> components = new HasMembersList<Component>();
	
	/**
	 * Default constructor
	 */
	public Card() {}

	/**
	 * @param name - The name of the card
	 * @param list - The list that the card is a part of
	 */
	public Card(String name, BList list)
	{
		this.name = name;
		this.list = list;
	}

	/**
	 * @param newList - The new list to add the card to
	 * @param newIndex - The new index for the card to be inserted into for the list
	 * @return boolean - If the list is successfully switched, returns true
	 * 					 If the list is unable to be switched, returns false
	 */
	public boolean switchList(BList newList, int newIndex, User requester)
	{
		// If the new list is not a part of the same board, we fail
		if (! list.getBoard().equals(newList.getBoard()))
			return false;
		
		// First, we need to let the current list know that we're leaving
		// This method will return false if it's unable to do what we want
		if(! list.removeCard(this, requester))
			return false;
		
		// Now, we're out of the list so let's continue adding to the new list
		newList.addCard(this, newIndex, requester);
		
		// Success
		return true;
	}
	
	/**
	 * @param label - Label to be added to the card
	 */
	public void addLabel(Label label, User requester)
	{
		if (list.getBoard().hasMember(requester))
			labels.addMember(label);
	}
	
	/**
	 * @param label    - Label to remove from the card
	 * @return boolean - If the label is removed successfully, returns true
	 * 					 If the label is not removed successfully, returns false
	 */
	public boolean removeLabel(Label label, User requester)
	{
		if (list.getBoard().hasMember(requester))
			return labels.removeMember(label);
		return false;
	}
	
	/**
	 * @param label - See if the label is contained here
	 * @return boolean
	 */
	public boolean hasLabel(Label label)
	{
		return labels.hasMember(label);
	}
	
	public void addMember(User member, User requester)
	{
		if (list.getBoard().getOwner().equals(requester))
		members.addMember(member);
	}
	
	public boolean removeMember(User member, User requester)
	{
		if (requester.equals(list.getBoard().getOwner()))
			return this.members.removeMember(member);
		return false;
	}
	
	public boolean hasMember(User member)
	{
		return this.members.hasMember(member);
	}

	/**
	 * @param component - Component to be added to the card
	 */
	public void addComponent(Component component, User requester)
	{
		if (list.getBoard().hasMember(requester))
			components.addMember(component);
	}
	
	/**
	 * @param component - Component to remove from the card
	 * @return boolean  - If the component is removed successfully, returns true
	 * 					  If the component is not removed successfully, returns false
	 */
	public boolean removeComponent(Component component, User requester)
	{
		if (list.getBoard().hasMember(requester))
			return components.removeMember(component);
		return false;
	}
	
	/**
	 * @param component - Component to see if it exists here
	 * @return boolean
	 */
	public boolean hasComponent(Component component)
	{
		return components.hasMember(component);
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
	public HasMembersSet<Label> getLabels()
	{
		return labels;
	}
	
	/*
	 * @return the members
	 */
	public HasMembersSet<User> getMembers()
	{
		return members;
	}

	/**
	 * @return the components
	 */
	public HasMembersList<Component> getComponents()
	{
		return components;
	}

	/**
	 * @param list the list to set
	 */
	public void setList(BList list)
	{
		this.list = list;
	}

	/**
	 * @param labels the labels to set
	 */
	public void setLabels(HasMembersSet<Label> labels)
	{
		this.labels = labels;
	}
	
	/*
	 * @param members the members to set
	 */
	public void setMembers(HasMembersSet<User> members)
	{
		this.members = members;
	}

	/**
	 * @param components the components to set
	 */
	public void setComponents(HasMembersList<Component> components)
	{
		this.components = components;
	}
	
	/**
	 * @param that - The card to check for equality
	 * @return boolean - Whether or not the cards are equal
	 */
	@Override
	public boolean equals(Object thatObj)
	{
		Card that = (Card) thatObj;
		
		// Make sure the names are the same
		if (! this.name.equals(that.name))
			return false;
		
		// Make sure all of this labels belong to that card
		for(Label l : this.labels)
			if (! that.hasLabel(l))
				return false;
		
		// Make sure all of that labels belong to this card
		for(Label l : that.labels)
			if (! this.hasLabel(l))
				return false;
		
		// Make sure all of this components belong to that card
		for(Component c : this.components)
			if (! that.hasComponent(c))
				return false;
		
		// Make sure all of that components belong to this card
		for(Component c : that.components)
			if (! this.hasComponent(c))
				return false;
		
		// If we get here, we're equal
		return true;
	}
	
	/**
	 * @param all - Array list of all objects to serialize
	 */
	public static void serializeToXML(ArrayList<Card> all)
	{
		XMLSerializer.<Card>serializeToXML(all, "Card");
	}
	
	/**
	 * @return ArrayList<Card> - The array list of objects that we want to return
	 */
	public static ArrayList<Card> deserializeFromXML()
	{
		return XMLSerializer.<Card>deserializeFromXML("Card");
	}
	
}
