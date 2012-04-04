
import java.util.*;
/** 
  * This InitialScheduler returns a single speed for the hole time interval
  * and sorts the tasks vector according to EDF priority
  * @see InitialScheduler
  * @author de Kryger Ode
  * @version 2012.04.14
  */
public class SingleFrequencyScheduler implements InitialScheduler {

    /** 
     * computes the speeds according to Single Frequency's algorithm
     * @param batch a set of jobs
     * @param speeds write a single point with the speed and the time interval
     * @param timeInterval time interval
     * @return true if it succeeds to schedule the set of jobs
    */	
    public boolean schedule(Task[] batch, Point2DFloatList speeds, float timeInterval)
    {
        float theSpeed = this.computeSpeed(batch,timeInterval);
    	try
    	{
            if (!this.checkFeasability(theSpeed))
		return false;
            this.EDF(batch);
    	}
    	catch (Exception e)
    	{
            e.printStackTrace();
    	}

        Point2DFloat p = new Point2DFloat(theSpeed, timeInterval);
	speeds.clear();
        speeds.add(p);
        return true;
    }

    /** 
     * computes the single speed
     * @param batch a set of jobs
     * @param timeInterval time interval
     * @return the computed speed
    */	
    protected float computeSpeed(Task[] batch,float timeInterval)
    {
        float theSpeed = 0;
        for (Task task:batch)
            theSpeed += ( task.getWcet() / task.getEndTime() );
        return theSpeed;
    }

    /** 
     * @return true if the utility factor is below or equal to 1
     * @param theSpeed the computed speed is the utility factor
    */	
    protected boolean checkFeasability(float theSpeed)
    {
    	if (theSpeed > 1)
            return false;
	return true;
    }

    protected void EDF(Task[] batch) throws Exception
    {
        Arrays.sort(batch, new EndTimeComparator());
    }

    public String getName()
    {
	return "SingleFrequencyScheduler";
    }
}

