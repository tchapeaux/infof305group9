import java.util.*;

public class CriticalIntervalScheduler implements Scheduler
{
	public void schedule(Task[] batch)
	{
		try 
		{
			this.checkFeasability(batch);
			this.EDF(batch);
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		} 
		finally 
		{
			for (Task task:batch)
			{
				task.setSpeed(1);
			}
		}
	}

	protected void EDF(Task[] batch) throws Exception
	{
		Arrays.sort(batch, new EndTimeComparator());
	}

	protected void checkFeasability(Task[] batch) throws Exception
	{
		float sum = 0, limit = 1;
		for (Task task:batch)
		{
			sum += ( task.getWcet() / task.getEndTime() );
		}
		if (sum > limit)
		{
			throw new Exception("Unfeasable System!");
		}
	}

	public String getName()
	{
		return "CriticalIntervalScheduler";
	}
}
