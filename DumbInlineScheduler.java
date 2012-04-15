/**
 * Example of inline scheduler, which doesn't do any real scheduling
 * @see InlineScheduler
 * @author Chapeaux Thomas
 */
public class DumbInlineScheduler implements InlineScheduler
{
    @Override
	public boolean schedule(Task[] batch, Point2DFloatList speeds, float timeInterval)
	{
		return true;
	}
    @Override
	public String getName()
	{
		return "DumbScheduler";
	}
}
