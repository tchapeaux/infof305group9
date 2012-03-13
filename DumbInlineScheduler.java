public class DumbInlineScheduler implements InlineScheduler
{
	public boolean schedule(Task[] batch, Point2DFloatList speeds, float timeInterval)
	{
		return true;
	}

	public String getName()
	{
		return "DumbScheduler";
	}
}
