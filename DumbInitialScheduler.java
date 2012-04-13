/**
  * This InitialScheduler returns a single maximal speed for the whole time interval
  * and sorts the tasks vector according to EDF priority
  * @see InitialScheduler
  * @author Chapeaux Thomas
  * @version 2012.04.14
  */

public class DumbInitialScheduler implements InitialScheduler
{
    @Override
    public boolean schedule(Task[] batch, Point2DFloatList speeds, float timeInterval)
    {
	speeds.clear();
	Point2DFloat p = new Point2DFloat(1,timeInterval);
	speeds.add(p);
	return true;
    }
    @Override
    public String getName()
    {
	return "DumbScheduler";
    }
}
