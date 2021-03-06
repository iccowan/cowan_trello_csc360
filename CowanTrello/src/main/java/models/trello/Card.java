package models.trello;

import java.io.Serializable;
import java.util.ArrayList;

import models.trello.structures.CloneableInterface;
import models.trello.structures.HasMembersList;
import models.trello.structures.HasMembersSet;

/**
 * Card Class
 *
 */
public class Card implements Serializable, CloneableInterface
{

	private static final long serialVersionUID = 7760358257875432091L;

	private String name;
	private BList list;
	private HasMembersSet<Label> labels = new HasMembersSet<Label>();
	private HasMembersSet<User> members = new HasMembersSet<User>();
	private HasMembersList<Component> components = new HasMembersList<Component>();

	/**
	 * Default constructor
	 */
	public Card()
	{
	}

	/**
	 * @param name - The name of the card
	 * @param list - The list that the card is a part of
	 */
	public Card(String name, BList list)
	{
		this.name = name;
		this.list = list;
		list.addCard(this);
	}

	/**
	 * @param newList   - The new list to add the card to
	 * @param newIndex  - The new index for the card to be inserted into for the
	 *                  list
	 * @param requester - The requesting user, should be a member of the board
	 * @return boolean - Returns depending on whether or not the list was switched
	 *         successfully
	 */
	public boolean switchList(BList newList, int newIndex, User requester)
	{
		// If the new list is not a part of the same board, we fail
		if (! list.getBoard().equals(newList.getBoard()))
			return false;

		// First, we need to let the current list know that we're leaving
		// This method will return false if it's unable to do what we want
		if (! list.removeCard(this, requester))
			return false;

		// Now, we're out of the list so let's continue adding to the new list
		newList.addCard(this, newIndex, requester);

		// Update the list that the card is a part of
		this.setList(newList);

		// Success
		return true;
	}

	/**
	 * @param label     - Label to be added to the card
	 * @param requester - The requesting user, should be a member of the board
	 */
	public void addLabel(Label label, User requester)
	{
		if (list.getBoard().hasMember(requester))
			labels.addMember(label);
	}

	/**
	 * @param label     - Label to remove from the card
	 * @param requester - The requesting user, should be a member of the board
	 * @return boolean - Returns depending on whether or not the label was removed
	 *         successfully
	 */
	public boolean removeLabel(Label label, User requester)
	{
		if (list.getBoard().hasMember(requester))
			return labels.removeMember(label);
		return false;
	}

	/**
	 * @param label - See if the label is contained here
	 * @return boolean - Returns whether or not the label exists
	 */
	public boolean hasLabel(Label label)
	{
		return labels.hasMember(label);
	}

	/**
	 * @param member    - User to add as a member
	 * @param requester - The requesting user, should be the owner of the board
	 */
	public void addMember(User member, User requester)
	{
		if (list.getBoard().getOwner().equals(requester))
			members.addMember(member);
	}

	/**
	 * @param member    - User to remove as a member
	 * @param requester - The requesting user, should be the owner of the board
	 * @return boolean - Whether or not the member was removed successfully
	 */
	public boolean removeMember(User member, User requester)
	{
		if (requester.equals(list.getBoard().getOwner()))
			return this.members.removeMember(member);
		return false;
	}

	/**
	 * @param member - User to check if they exist
	 * @return boolean - Whether or not the user exists as a member
	 */
	public boolean hasMember(User member)
	{
		return this.members.hasMember(member);
	}

	/**
	 * @param component - Component to be added to the card
	 * @param requester - The requesting user, should be a member of the board
	 */
	public void addComponent(Component component, User requester)
	{
		if (list.getBoard().hasMember(requester))
			components.addMember(component);
	}

	/**
	 * @param component - Component to remove from the card
	 * @param requester - The requesting user, should be a member of the board
	 * @return boolean - If the component is removed successfully, returns true If
	 *         the component is not removed successfully, returns false
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
	 * @return Card
	 */
	public Card clone()
	{
		try
		{
			Card newCard = new Card(this.name, new BList(this.list.getName(),
					new Board(this.list.getBoard().getName(), this.list.getBoard().getOwner())));

			HasMembersSet<Label> newLabels = labels.clone();
			HasMembersSet<User> newMembers = new HasMembersSet<User>();
			HasMembersList<Component> newComponents = components.clone();

			for (User u : this.members)
				newMembers.addMember(u);

			newCard.setLabels(newLabels);
			newCard.setMembers(newMembers);
			newCard.setComponents(newComponents);

			return newCard;
		} catch (CloneNotSupportedException e)
		{
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * @param thatObj - The card to check for equality
	 * @return boolean - Whether or not the cards are equal
	 */
	@Override
	public boolean equals(Object thatObj)
	{
		Card that = (Card) thatObj;
		if (that == null)
			return false;

		// Make sure the names are the same
		if (!this.name.equals(that.name))
			return false;

		// Make sure all of this labels belong to that card
		for (Label l : this.labels)
			if (!that.hasLabel(l))
				return false;

		// Make sure all of that labels belong to this card
		for (Label l : that.labels)
			if (!this.hasLabel(l))
				return false;

		// Make sure all of this components belong to that card
		for (Component c : this.components)
			if (!that.hasComponent(c))
				return false;

		// Make sure all of that components belong to this card
		for (Component c : that.components)
			if (!this.hasComponent(c))
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
