
import java.util.*;

public class SingleFrequencyScheduler implements Scheduler {
    
    public Point2DFloatList schedule(Task[] batch, float timeInterval)
    {
        float theSpeed = this.computeSpeed(batch);
    	try
    	{	
            this.checkFeasability(theSpeed);
            this.EDF(batch);
    	}
    	catch (Exception e)
    	{
            e.printStackTrace();
    	}
        
        Point2DFloat p = new Point2DFloat(theSpeed, timeInterval);
        Point2DFloatList l = new Point2DFloatList();
        l.add(p);
        return l;
    }
    
    protected float computeSpeed(Task[] batch)
    {
        float theSpeed = 0;
        for (Task task:batch)
            theSpeed += ( task.getWcet() / task.getEndTime() );
        return theSpeed;
    }
    
    protected void checkFeasability(float theSpeed) throws Exception
    {
    	if (theSpeed > 1)
            throw new Exception("Unfeasable System!");
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

