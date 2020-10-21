package trello;

import java.util.ArrayList;

/**
 * BList Class
 *
 */
public class BList
{

	private String name;
	private HasMembersList<Card> cards;
	private Board board;
	
	/*
	 * Default Constructor
	 */
	public BList() {}
	
	/**
	 * @param name
	 * @param board
	 */
	public BList(String name, Board board)
	{
		this.name = name;
		this.cards = new HasMembersList<Card>();
		this.board = board;
	}

	/**
	 * @param card - Card to add to the board
	 */
	public void addCard(Card card)
	{
		cards.addMember(card);
	}
	
	/**
	 * @param card  - Card to add to the board
	 * @param index - Index for the new card to be added
	 */
	public void addCard(int index, Card card)
	{
		cards.addMember(index, card);
	}

	/**
	 * @param card - Card to remove from the list
	 * @return boolean - If the card is removed successfully, returns true
	 * 					 If the card is not removed successfully, returns false
	 */
	public boolean removeCard(Card card)
	{
		return cards.removeMember(card);
	}

	/**
	 * @param card     - Card to move in the list
	 * @param index    - New index in the list for the card
	 * @return boolean - If the card is moved successfully, returns true
	 * 					 If the card is not moved successfully, returns false
	 */
	public boolean moveCard(Card card, int index)
	{
		if(! cards.removeMember(card))
			return false;
		
		cards.addMember(index, card);
		
		// Success
		return true;
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
	public ArrayList<Card> getCards()
	{
		return cards.getMembers();
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
	 * @param that - List to compare
	 * @return boolean
	 */
	public boolean equals(BList that)
	{
		// If the names aren't the same, not equal
		if (this.name != that.name)
			return false;
		
		// Make sure all of this cards belong to that board
		for(Card c : this.cards)
			if (! that.hasCard(c))
				return false;
		
		// Make sure all of that cards belong to that board
		for(Card c : that.cards)
			if(! this.hasCard(c))
				return false;
			
		// Equal
		return true;
	}
	
	/**
	 * @return String - The string of the file where the serialized object lives
	 */
	public String serializeToXML()
	{
		return XMLSerializer.<BList>serializeToXML(this);
	}
	
	/**
	 * @param objectFileName - File name where the object lives that we're going to deserialize
	 * @return BList - The list object that we want to return
	 */
	public static BList deserializeFromXML(String objectFileName)
	{
		return XMLSerializer.<BList>deserializeFromXML(objectFileName);
	}

}
