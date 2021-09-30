package controllers.save;

/**
 * SaveTrackObserverInterface Interface
 * 
 * This should be implemented by observers of the SaveTrackController
 * so we can notify when there are changes
 *
 */
public interface SaveTrackObserverInterface
{
	
	/**
	 * Method called when there may be changes
	 */
	public void saveTrackHasChangesChanged();
	
}
