import java.util.*;

/* This Scheduler returns a speeds vector for different time intervals
	   and rearrange the tasks vector according to EDF priority */
public class SmallestPathScheduler implements Scheduler
{
	public void schedule(Task[] batch)
	{
		try
		{
			LinkedList<Point2DFloat> L = new LinkedList<Point2DFloat>();
			LinkedList<Point2DFloat> La = new LinkedList<Point2DFloat>();
			LinkedList<Point2DFloat> Ld = new LinkedList<Point2DFloat>();

			this.makePointsLists(L, La, Ld, batch);

			//this.EDF(batch);
			//this.checkFeasability(batch);
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

	protected void makePointsLists(LinkedList<Point2DFloat> L, LinkedList<Point2DFloat> La, LinkedList<Point2DFloat> Ld, Task[] batch)
	{
		Task[] theBatchTemp = batch.clone();
		float theDurationsSum = 0;
		int i = 0;

		Arrays.sort(theBatchTemp, new StartTimeComparator());

		while(theBatchTemp[i].getStartTime() == 0)
		{
			theDurationsSum += theBatchTemp[i].getWcet();
			i++;
		}

		while(i < theBatchTemp.length)
		{
			La.addLast(new Point2DFloat(theBatchTemp[i].getStartTime(), theDurationsSum));		
			i++;
			if(i < theBatchTemp.length)
				theDurationsSum += theBatchTemp[i-1].getWcet();
		}

		/*for (int j = 0; j < La.size(); j++)
		{
			System.out.println(La.get(j).getX());
			System.out.println(La.get(j).getY());
		}*/
	}

	protected void EDF(Task[] batch) throws Exception
	{
		Arrays.sort(batch, new EndTimeComparator());
	}

	protected void checkFeasability(Task[] batch) throws Exception
	{
		
	}

	public String getName()
	{
		return "SmallestPathScheduler";
	}
}
