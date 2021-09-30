package models.trello.structures;

/**
 * CloneableInterface Interface
 * 
 * Classes implementing this interface definitely have a clone method
 * 
 * This should be unnecessary, but Java's Cloneable interface is horribly broken
 * because it does not require a clone method, so it is what it is
 *
 */
public interface CloneableInterface
{

	/**
	 * @return the Object that has been cloned
	 */
	public Object clone();

}
