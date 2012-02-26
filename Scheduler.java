interface Scheduler
{
	public boolean schedule(Task[] batch, Point2DFloatList speeds, float timeInterval);
	// Return the list of speeds in the time interval
	// Reorder the Array with the most prioritary task first
	public String getName();
}
