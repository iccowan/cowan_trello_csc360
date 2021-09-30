package models.trello;

import java.io.Serializable;
import java.util.ArrayList;

import models.trello.structures.CloneableInterface;
import models.trello.structures.HasMembersList;

/**
 * BList Class
 *
 */
public class BList implements Serializable, CloneableInterface
{

	private static final long serialVersionUID = 4884207810307478686L;

	private String name;
	private HasMembersList<Card> cards = new HasMembersList<Card>();
	private Board board;

	/*
	 * Default Constructor
	 */
	public BList()
	{
	}

	/**
	 * @param name
	 * @param board
	 */
	public BList(String name, Board board)
	{
		this.name = name;
		this.board = board;
		board.addList(this);
	}

	/**
	 * @param card - Card to add to the board
	 */
	public void addCard(Card card, User requester)
	{
		if (board.hasMember(requester))
			cards.addMember(card);
	}

	/**
	 * @param card - Card to add to the board If we are accessing from within the
	 *             package, we don't need to check for members because this can be
	 *             used when a card is created
	 */
	void addCard(Card card)
	{
		cards.addMember(card);
	}

	/**
	 * @param card  - Card to add to the board
	 * @param index - Index for the new card to be added
	 */
	public void addCard(Card card, int index, User requester)
	{
		if (board.hasMember(requester))
			cards.addMember(index, card);
	}

	/**
	 * @param card - Card to remove from the list
	 * @return boolean - Returns true or false depending on whether or not the card
	 *         was removed successfully
	 */
	public boolean removeCard(Card card, User requester)
	{
		if (board.hasMember(requester))
			return cards.removeMember(card);
		return false;
	}

	/**
	 * @param name - Name of the card to find
	 * @return the Card, if found
	 */
	public Card findCard(String name)
	{
		// Loop through all of the cards and see if we can find a card
		// by the requested name
		for (Card c : cards)
			if (c.getName().equals(name))
				return c;

		// Could not be found
		return null;
	}

	/**
	 * @param card  - Card to move in the list
	 * @param index - New index in the list for the card
	 * @return boolean - Returns true or false depending on whether or not the card
	 *         was moved successfully
	 */
	public boolean moveCard(Card card, int index, User requester)
	{
		if (board.hasMember(requester))
		{
			if (!cards.removeMember(card))
				return false;

			cards.addMember(index, card);

			// Success
			return true;
		}
		return false;
	}

	/**
	 * @param c - Card that we want to check and see if it exists here
	 * @return boolean - Returns whether or not the card exists here
	 */
	public boolean hasCard(Card c)
	{
		return cards.hasMember(c);
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
	 * @return the cards
	 */
	public HasMembersList<Card> getCards()
	{
		return cards;
	}

	/**
	 * @return the board
	 */
	public Board getBoard()
	{
		return board;
	}

	/**
	 * @param cards the cards to set
	 */
	public void setCards(HasMembersList<Card> cards)
	{
		this.cards = cards;
	}

	/**
	 * @param board the board to set
	 */
	public void setBoard(Board board)
	{
		this.board = board;
	}

	/**
	 * @return BList
	 */
	public BList clone()
	{
		try
		{
			BList newList = new BList(this.name, new Board(this.board.getName(), this.board.getOwner()));
			HasMembersList<Card> newCards = cards.clone();
			newList.setCards(newCards);

			// Set the new list as the parent for each new card
			for (Card c : newCards)
				c.setList(newList);

			return newList;
		} catch (CloneNotSupportedException e)
		{
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * @param that - List to compare
	 * @return boolean
	 */
	@Override
	public boolean equals(Object thatObj)
	{
		BList that = (BList) thatObj;
		if (that == null)
			return false;

		// If the names aren't the same, not equal
		if (!this.name.equals(that.name))
			return false;

		// Make sure all of this cards belong to that board
		for (Card c : this.cards)
			if (!that.hasCard(c))
				return false;

		// Make sure all of that cards belong to that board
		for (Card c : that.cards)
			if (!this.hasCard(c))
				return false;

		// Equal
		return true;
	}

	/**
	 * @param all - Array list of all objects to serialize
	 */
	public static void serializeToXML(ArrayList<BList> all)
	{
		XMLSerializer.<BList>serializeToXML(all, "BList");
	}

	/**
	 * @return ArrayList<BList> - The array list of objects that we want to return
	 */
	public static ArrayList<BList> deserializeFromXML()
	{
		return XMLSerializer.<BList>deserializeFromXML("BList");
	}

}
