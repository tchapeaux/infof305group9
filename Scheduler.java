interface Scheduler
{
	public Point2DFloatList schedule(Task[] batch, float timeInterval);
	// Return the list of speeds in the time interval
	// Reorder the Array with the most prioritary task first
	public String getName();
}
