package models.trelloServer;

/**
 * UserNotFoundException
 * @extends Exception
 * 
 */
public class UserNotFoundException extends Exception
{

	private static final long serialVersionUID = 5316991156275077337L;

	public UserNotFoundException()
	{
		super();
	}

	public UserNotFoundException(String message)
	{
		super(message);
	}

}
