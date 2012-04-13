/**
 * Interface allowing to have a strategy pattern for the initial scheduling algorithm.
 *
 * @author thomas
 * @version 2012.04.14
 */

interface InitialScheduler
{
    /**
     * Write in speeds the scheduling computed for the system of tasks described by batch.
     * Also reorder 'batch' by order of priority (EDF)
     * @param batch a system of Tasks
     * @param speeds the list in which to write the resulting speeds
     * @param timeInterval the duration of the time interval to consider
     * @return true if the algorithm managed to find a valid scheduling
     */
	public boolean schedule(Task[] batch, Point2DFloatList speeds, float timeInterval);

	/**
	 *
	 * @return
	 */

	public String getName();
}
