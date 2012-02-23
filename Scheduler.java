interface Scheduler
{
	// Update the "speed" attribute of each Task in the batch
	// Reorder the Array with the most prioritary task first
	public Point2DFloatList schedule(Task[] batch, float timeInterval);
	public String getName();
}
