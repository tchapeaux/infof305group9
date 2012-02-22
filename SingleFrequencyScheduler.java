
import java.util.*;

public class SingleFrequencyScheduler implements Scheduler {
    
    public void schedule(Task[] batch)
    {
	try
	{	
            float theSpeed = this.computeSpeed(batch);
            this.checkFeasability(theSpeed);
            this.EDF(batch);
	}
	catch (Exception e)
	{
            e.printStackTrace();
	}
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

