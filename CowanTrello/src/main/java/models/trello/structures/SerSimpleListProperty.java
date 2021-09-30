package models.trello.structures;

import java.io.Serializable;

import javafx.beans.property.SimpleListProperty;

/**
 * SerSimpleListProperty<E>
 * 
 * This is simply used so that we have a serializable SimpleListProperty
 *
 * @param <E> - Object in the list
 */
public class SerSimpleListProperty<E> extends SimpleListProperty<E> implements Serializable
{

	private static final long serialVersionUID = 764558044684347037L;

}
