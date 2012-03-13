interface InitialScheduler
{
	public boolean schedule(Task[] batch, Point2DFloatList speeds, float timeInterval);
	// Put the list of speeds for the time interval in 'speeds'
	// Reorder the Array with the most prioritary task first
	// return false in case of trouble
	public String getName();
}
