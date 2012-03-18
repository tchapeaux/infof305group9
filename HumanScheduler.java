public class HumanScheduler implements InitialScheduler, InlineScheduler
{
    protected Point2DFloatList mySpeeds;

    public void initialize(Point2DFloatList theSpeeds)
    {
	this.mySpeeds = theSpeeds;
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
	speeds.clear();
	for(Point2DFloat speed:mySpeeds)
	{
	    speeds.add(speed);
	}
	return true;
    }

    public String getName()
    {
	return "You";
    }

}
