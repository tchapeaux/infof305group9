public interface InlineScheduler
{
    	public boolean schedule(Task[] batch, Point2DFloatList speeds, float timeInterval);
	// Update the list of speeds for the time interval
	// Reorder the Array with the most prioritary task first
	// return false in case of trouble
	public String getName();

}
