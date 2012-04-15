/**
 * This Initial and Inline scheduler allows a human (the user) to define and modify dynamically his/her scheduling.
 * @author Chapeaux Thomas
 * @version 2012.04.14
 */

public class HumanScheduler implements InitialScheduler, InlineScheduler
{
    /**
     * The list of speeds to be returned by the scheduler
     */
    protected Point2DFloatList mySpeeds;

    /**
     * Initialize the scheduler with the list of speeds initally chosen by the user
     * @param theSpeeds list of speeds chosen by the user
     */
    public void initialize(Point2DFloatList theSpeeds)
    {
	this.mySpeeds = theSpeeds;
    }

    /**
     * Allow the user to modify  his/her scheduling during the simulation.
     * @param deltaSpeed how much to modify the current speed
     * @param time current time in the simulation
     */
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

    @Override
    public boolean schedule(Task[] batch, Point2DFloatList speeds, float timeInterval)
    {
	speeds.clear();
	for(Point2DFloat speed:mySpeeds)
	{
	    speeds.add(speed);
	}
	return true;
    }

    @Override
    public String getName()
    {
	return "You";
    }

}
