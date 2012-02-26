public class DumbScheduler implements Scheduler
{
	public boolean schedule(Task[] batch, Point2DFloatList speeds, float timeInterval)
	{
		speeds.clear();
		Point2DFloat p = new Point2DFloat(1,timeInterval);
		speeds.add(p);
		return true;
	}

	public String getName()
	{
		return "DumbScheduler";
	}
}
