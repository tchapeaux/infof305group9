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

			this.makeLAList(La, batch);
			this.makeLDList(Ld, batch);
			this.makeLList(La, Ld, L);

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

	protected void makeLAList(LinkedList<Point2DFloat> La, Task[] batch)
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
			theDurationsSum += theBatchTemp[i-1].getWcet();
		}

		/*for (int j = 0; j < La.size(); j++)
		{
			System.out.println(La.get(j).getX());
			System.out.println(La.get(j).getY());
		}*/
	}

	protected void makeLDList(LinkedList<Point2DFloat> Ld, Task[] batch)
	{
		Task[] theBatchTemp = batch.clone();
		float theDurationsSum = 0;
		int i = 0;
		float theEndTime;
	
		// like EDF so we loose efficience but the two functions remain independant
		Arrays.sort(theBatchTemp, new EndTimeComparator());

		while(i < theBatchTemp.length)
		{
			theEndTime = theBatchTemp[i].getEndTime();
			theDurationsSum += theBatchTemp[i].getWcet();
			i++;
			while (i < theBatchTemp.length && theEndTime == theBatchTemp[i].getEndTime())
			{
				theEndTime = theBatchTemp[i].getEndTime();
				theDurationsSum += theBatchTemp[i].getWcet();
				i++;					
			}

			Ld.addLast(new Point2DFloat(theEndTime, theDurationsSum));
		}

		/*for (int j = 0; j < Ld.size(); j++)
		{
			System.out.println(Ld.get(j).getX());
			System.out.println(Ld.get(j).getY());
		}*/
	}

	protected void makeLList(LinkedList<Point2DFloat> La, LinkedList<Point2DFloat> Ld, LinkedList<Point2DFloat> L)
	{
		/*for (int j = 0; j < Ld.size(); j++)
		{
			System.out.println(Ld.get(j).getX());
			System.out.println(Ld.get(j).getY());
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
