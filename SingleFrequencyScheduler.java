
import java.util.*;

public class SingleFrequencyScheduler implements Scheduler {
    
    public boolean schedule(Task[] batch, Point2DFloatList speeds, float timeInterval)
    {
        float theSpeed = this.computeSpeed(batch,timeInterval);
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
	speeds.clear();
        speeds.add(p);
        return true;
    }
    
    protected float computeSpeed(Task[] batch,float timeInterval)
    {
        float theSpeed = 0;
        for (Task task:batch)
            theSpeed += ( task.getWcet() / timeInterval );
        return theSpeed;
    }
    
    protected void checkFeasability(float theSpeed) throws Exception
    {	
    	if (theSpeed > 1)
            throw new Exception("Unfeasable System: speed exceeds unit");
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

