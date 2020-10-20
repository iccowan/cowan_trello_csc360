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

}
