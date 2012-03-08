public class HumanScheduler implements Scheduler
{
    protected Point2DFloatList mySpeeds;

    public HumanScheduler initialize(Point2DFloatList theSpeeds)
    {
	mySpeeds = theSpeeds;
        return this;
    }

    public void update(float deltaSpeed, float time)
    {
	for (Point2DFloat speed:mySpeeds)
	{
	    if (time < speed.y)
	    {
		speed.x += deltaSpeed;
		return;
	    }
	}
    }

    public boolean schedule(Task[] batch, Point2DFloatList speeds, float timeInterval)
    {
	speeds = mySpeeds;
	return true;
    }

    public String getName()
    {
	return "You";
    }

}
