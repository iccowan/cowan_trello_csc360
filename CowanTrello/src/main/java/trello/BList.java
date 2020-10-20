package trello;

import java.util.ArrayList;

/**
 * BList Class
 *
 */
public class BList
{

	private String name;
	private ArrayList<Card> cards;
	private Board board;

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
	public void addCard(Card card)
	{
		cards.add(card);
	}

	/**
	 * @param card - Card to remove from the list
	 * @return boolean - If the card is removed successfully, returns true
	 * 					 If the card is not removed successfully, returns false
	 */
	public boolean removeCard(Card card)
	{
		// Check and make sure the card exists
		if (! cards.contains(card))
			return false;

		// Now, we know the card exists within the list, so we can remove the card
		int cardIndex = cards.indexOf(card);
		cards.remove(cardIndex);

		// Success
		return true;
	}

	/**
	 * @param card     - Card to move in the list
	 * @param newIndex - New index in the list for the card
	 * @return boolean - If the card is moved successfully, returns true
	 * 					 If the card is not moved successfully, returns false
	 */
	public boolean moveCard(Card card, int newIndex)
	{
		// Check and make sure the card exists
		if (! cards.contains(card))
			return false;

		// Make sure the new index is valid
		if (newIndex < 0 || newIndex >= cards.size())
			return false;

		// Now, we know the card exists and the index is valid,
		// so let's move the card to the new index
		int oldIndex = cards.indexOf(card);
		cards.remove(oldIndex);
		cards.add(newIndex, card);

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
		return cards;
	}

	/**
	 * @return the board
	 */
	public Board getBoard()
	{
		return board;
	}

}
