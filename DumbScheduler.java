public class DumbScheduler implements Scheduler
{
	public Point2DFloatList schedule(Task[] batch, float timeInterval)
	{
		Point2DFloat p = new Point2DFloat(1,timeInterval);
		Point2DFloatList l = new Point2DFloatList();
		l.add(p);
		return l;
	}

	public String getName()
	{
		return "DumbScheduler";
	}
}
