package models.trelloServer;

/**
 * BoardNotFoundException
 * @extends Exception
 * 
 */
public class BoardNotFoundException extends Exception
{

	private static final long serialVersionUID = 5316991156275077337L;

	public BoardNotFoundException()
	{
		super();
	}

	public BoardNotFoundException(String message)
	{
		super(message);
	}

}
