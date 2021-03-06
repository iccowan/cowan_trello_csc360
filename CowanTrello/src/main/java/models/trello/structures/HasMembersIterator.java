package models.trello.structures;

import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;

/**
 * HasMembersIterator Class
 * 
 * @param <T> - Type for the Set
 * 
 *            This will allow for iteration over the HasMembersList/Set
 *
 */
public class HasMembersIterator<T> implements Iterator<T>, Serializable
{

	private static final long serialVersionUID = -3734941650203164252L;
	private Iterator<T> listIt;

	/**
	 * @param col - The collection to iterate over
	 */
	HasMembersIterator(Collection<T> col)
	{
		this.listIt = col.iterator();
	}

	/**
	 * @return boolean - Returns whether or not there is a next value in the
	 *         collection
	 */
	@Override
	public boolean hasNext()
	{
		return listIt.hasNext();
	}

	/**
	 * @return T - Returns the next value in the collection
	 */
	@Override
	public T next()
	{
		return listIt.next();
	}

}
