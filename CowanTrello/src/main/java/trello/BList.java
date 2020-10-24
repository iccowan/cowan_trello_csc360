package trello;

import java.util.ArrayList;

/**
 * BList Class
 *
 */
public class BList
{

	private String name;
	private HasMembersList<Card> cards = new HasMembersList<Card>();
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
		this.board = board;
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
	 * @return boolean - If the card is removed successfully, returns true
	 * 					 If the card is not removed successfully, returns false
	 */
	public boolean removeCard(Card card, User requester)
	{
		if (board.hasMember(requester))
			return cards.removeMember(card);
		return false;
	}

	/**
	 * @param card     - Card to move in the list
	 * @param index    - New index in the list for the card
	 * @return boolean - If the card is moved successfully, returns true
	 * 					 If the card is not moved successfully, returns false
	 */
	public boolean moveCard(Card card, int index, User requester)
	{
		if (board.hasMember(requester))
		{
			if(! cards.removeMember(card))
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
	 * @param that - List to compare
	 * @return boolean
	 */
	@Override
	public boolean equals(Object thatObj)
	{
		BList that = (BList) thatObj;
		
		// If the names aren't the same, not equal
		if (! this.name.equals(that.name))
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
