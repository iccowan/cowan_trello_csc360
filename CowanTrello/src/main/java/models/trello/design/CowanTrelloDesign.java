package models.trello.design;

/**
 * CowanTrelloDesign class
 *
 * @extends Design
 *
 */
class CowanTrelloDesign extends Design
{
	/**
	 * Constructor
	 */
	CowanTrelloDesign()
	{
		super(Design.class.getResource("css/CowanTrello.css").toExternalForm());
	}
}
