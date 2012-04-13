/**
 * Interface allowing to have a strategy pattern for the inline scheduling algorithm
 *
 * @author thomas
 * @version 2012.04.14
 */

public interface InlineScheduler
{
    /**
     * Write a new set of speeds for the given 'batch' of Tasks, set of speeds and time interval
     * @param batch a system of Tasks being computed in the Simulation
     * @param speeds the current set of speeds
     * @param timeInterval the duration of the time interval to consider
     * @return false if there is a problem with the returned set of speeds
     */
    	public boolean schedule(Task[] batch, Point2DFloatList speeds, float timeInterval);

	/**
	 * Return the name of the algorithm, for displaying purpose
	 * @return the name as a String
	 */
	public String getName();

}
