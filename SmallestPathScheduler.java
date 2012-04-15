import java.util.*;

/**
  * This InitialScheduler returns a speeds list for different time intervals
  * and sorts the tasks vector according to EDF priority
  * @see InitialScheduler
  * @author de Kryger Ode
  * @version 2012.04.14
  */
public class SmallestPathScheduler implements InitialScheduler
{
	/**
	  * compute the speeds according to Smallest Path's algorithm
          * @param batch a set of jobs
          * @param speeds write a speeds list for different time intervals
          * @param timeInterval time interval
          * @return true if it succeeds to schedule the set of jobs
          */
	public boolean schedule(Task[] batch, Point2DFloatList speeds, float timeInterval)
	{
		Point2DFloatList L = new Point2DFloatList();
		Point2DFloatList La = new Point2DFloatList();
		Point2DFloatList Ld = new Point2DFloatList();
		Point2DFloatList Lv = new Point2DFloatList();
		Point2DFloatList Lw = new Point2DFloatList();
		Point2DFloatList V = new Point2DFloatList();
		Point2DFloatList theSpeeds = new Point2DFloatList();
		Point2DFloat p = new Point2DFloat(1,timeInterval);
		speeds.add(p);
		try
		{
			if (!this.checkFeasability(batch))
				return false;
			this.makeStartPoint(batch, La, Ld, Lv, Lw, L);
			this.makeUpperPointsList(La, batch);
			this.makeLowerPointsList(Ld, batch);
			this.meltLists(La, Ld, L);
			this.makeSmallestPath(L, La, Ld, Lv, Lw, V);
			this.computeSpeeds(V, theSpeeds);
			if (!this.checkSpeeds(theSpeeds))
				return false;
			this.EDF(batch);
			if (!this.testBatchAndSpeeds(batch, theSpeeds))
				return false;
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		speeds.clear();
		speeds.addAll(theSpeeds);
		return true;
	}

	protected void makeStartPoint(Task[] batch, Point2DFloatList La, Point2DFloatList Ld, Point2DFloatList Lv, Point2DFloatList Lw, Point2DFloatList L)
	{
		Arrays.sort(batch, new StartTimeComparator());
		Point2DFloat startPoint = new Point2DFloat (batch[0].getStartTime(), 0);
		La.add(startPoint);
		Ld.add(startPoint);
		Lv.add(startPoint);
		Lw.add(startPoint);
		L.add(startPoint);
	}

	/**
	* compute La, the "ceiling"
        */
	protected void makeUpperPointsList(Point2DFloatList La, Task[] batch)
	{
		Arrays.sort(batch, new StartTimeComparator());

		for (Task theTask:batch)
		{
			Point2DFloat thePoint = new Point2DFloat(theTask.getStartTime(), computeUpperPoint(batch, theTask));
			if (!La.contains(thePoint))
				La.add(thePoint);
		}
	}

	protected float computeUpperPoint(Task[] inBatchTemp, Task inTask)
	{
		float res = 0;
		for (int i = 0; i < inBatchTemp.length && inBatchTemp[i].getStartTime() < inTask.getStartTime(); i++)
			res += inBatchTemp[i].getWcet();
		return res;
	}

	/**
	* compute Ld, the "floor"
        */
	protected void makeLowerPointsList(Point2DFloatList Ld, Task[] batch)
	{
		Arrays.sort(batch, new EndTimeComparator());

		for (Task theTask:batch)
		{
			Point2DFloat thePoint = new Point2DFloat(theTask.getEndTime(), computeLowerPoint(batch, theTask));
			if (!Ld.contains(thePoint))
				Ld.add(thePoint);
		}
	}

	protected float computeLowerPoint(Task[] inBatchTemp, Task inTask)
	{
		float res = 0;
		for (int i = 0; i < inBatchTemp.length && inBatchTemp[i].getEndTime() <= inTask.getEndTime(); i++)
			res += inBatchTemp[i].getWcet();
		return res;
	}

	/**
	* melts La and Ld into L
        */
	protected void meltLists(Point2DFloatList La, Point2DFloatList Ld, Point2DFloatList L)
	{
		int i = 1, j = 1;
		Point2DFloat theLaPoint = null;
		Point2DFloat theLdPoint = null;

		while (i < La.size() && j < Ld.size())
		{
			theLaPoint = La.get(i);
			theLdPoint = Ld.get(j);
			if (Point2DFloat.compareOnX(theLaPoint, theLdPoint) == -1)
			{
				L.add(theLaPoint);
				i++;
			}
			else if (Point2DFloat.compareOnX(theLaPoint, theLdPoint) == 1)
			{
				L.add(theLdPoint);
				j++;
			}
			else // both points are equal
			{
				L.add(theLaPoint);
				i++;
				j++;
			}
		}

		if (j < Ld.size())
			L.addAll(Ld.subList(j, Ld.size()));
	}

	/**
	* the computed smallest path is set into V
        */
	protected void makeSmallestPath(Point2DFloatList L, Point2DFloatList La, Point2DFloatList Ld, Point2DFloatList Lv, Point2DFloatList Lw, Point2DFloatList V)
	{
		Point2DFloatList startPointsList = new Point2DFloatList();
		Point2DFloatList Temp = new Point2DFloatList();
		Point2DFloat startPoint = L.get(0);
		startPointsList.add(startPoint);
		Point2DFloat newStartPoint;
		int index;

		for (int i = 1; i < L.size(); i++)
		{
			Point2DFloat currentPoint = L.get(i);

			if (La.contains(currentPoint))
			{
				Lv.add(currentPoint);
				this.removeHatAngle(Lv);
				if (Lv.size() == 2 && Lv.contains(startPoint) && Lv.contains(currentPoint))
				{
					newStartPoint = lastUpperPoint(Lw, startPoint, currentPoint);
                                        if (newStartPoint != null)
                                        {
                                            index = Lw.lastIndexOf(newStartPoint);
                                            Temp.addAll(Lw.subList(0, index));
                                            Lw.removeAll(Temp);
                                            Lv.clear();
                                            Lv.add(newStartPoint);
                                            Lv.add(currentPoint);
                                            startPointsList.add(newStartPoint);
                                            startPoint = (Point2DFloat)newStartPoint.clone();
                                            Temp.clear();
                                        }
				}
			}

			if (Ld.contains(currentPoint))
			{
				Lw.add(currentPoint);
				this.removeCupAngle(Lw);
				if (Lw.size() == 2 && Lw.contains(startPoint) && Lw.contains(currentPoint))
				{
					newStartPoint = lastLowerPoint(Lv, startPoint, currentPoint);
                                        if (newStartPoint != null)
                                        {
                                            index = Lv.lastIndexOf(newStartPoint);
                                            Temp.addAll(Lv.subList(0, index));
                                            Lv.removeAll(Temp);
                                            Lw.clear();
                                            Lw.add(newStartPoint);
                                            Lw.add(currentPoint);
                                            startPointsList.add(newStartPoint);
                                            startPoint = (Point2DFloat)newStartPoint.clone();
                                            Temp.clear();
                                        }
				}
			}
		}
		V.addAll(startPointsList);
		Lw.removeFirst();
		V.addAll(Lw);
	}

	/**
	  * search the last Lw's point which is upper than the straight line between origin and currentPoint
          * @return the last upper point or null
          */
	protected Point2DFloat lastUpperPoint(Point2DFloatList Lw, Point2DFloat origin, Point2DFloat currentPoint)
	{
		Point2DFloat res = null;
		Point2DFloat tmp;
		for (int i = Lw.size() - 1; i >= 0; i--)
		{
			tmp = Lw.get(i);
			if ( Point2DFloat.getGradient(origin, tmp) > Point2DFloat.getGradient(origin, currentPoint) )
			{
				res = tmp;
				break;
			}
		}
		return res;
	}

	/**
	  * search the last Lv's point which is lower than the straight line between origin and currentPoint
          * @return the last lower point or null
          */
	protected Point2DFloat lastLowerPoint(Point2DFloatList Lv, Point2DFloat origin, Point2DFloat currentPoint)
	{
		Point2DFloat res = null;
		Point2DFloat tmp;
		for (int i = Lv.size() - 1; i >= 0; i--)
		{
			tmp = Lv.get(i);
			if ( Point2DFloat.getGradient(origin, tmp) < Point2DFloat.getGradient(origin, currentPoint) )
			{
				res = tmp;
				break;
			}
		}
		return res;
	}

	protected void removeHatAngle(Point2DFloatList Lv)
	{
		int i = 0;
		float theFirstGradient;
		float theSecondGradient;

		while (i <= (Lv.size() - 3) && Lv.size() >= 3)
		{
			theFirstGradient = Point2DFloat.getGradient(Lv.get(i), Lv.get(i+1));
			theSecondGradient = Point2DFloat.getGradient(Lv.get(i+1), Lv.get(i+2));
			if (theFirstGradient > theSecondGradient)
                        {
				Lv.remove(i+1);
                                i = 0;
                        }
			else
				i++;
		}
	}

	protected void removeCupAngle(Point2DFloatList Lw)
	{
		int i = 0;
		float theFirstGradient;
		float theSecondGradient;

		while (i <= (Lw.size() - 3) && Lw.size() >= 3)
		{
			theFirstGradient = Point2DFloat.getGradient(Lw.get(i), Lw.get(i+1));
			theSecondGradient = Point2DFloat.getGradient(Lw.get(i+1), Lw.get(i+2));
			if (theFirstGradient < theSecondGradient)
                        {
				Lw.remove(i+1);
                                i = 0;
                        }
			else
				i++;
		}
	}

	/**
	  * computes the speeds from each straight line segment from V
          * the speed is added with his last moment of time
          */
        protected void computeSpeeds(Point2DFloatList V, Point2DFloatList speeds)
        {
            for(int i = 0; i < V.size()-1; i++)
            {
                float theDerivative = Point2DFloat.getGradient(V.get(i), V.get(i+1));
                speeds.add(new Point2DFloat(theDerivative, V.get(i+1).getX()));
            }
        }

	protected void EDF(Task[] batch) throws Exception
	{
		Arrays.sort(batch, new EndTimeComparator());
	}

       /**
        * @param batch the set of jobs received from outside
        * @return true if the utility factor is below or equal to 1
        */
	protected boolean checkFeasability(Task[] batch)
	{
		float sum = 0, limit = 1;
		for (Task task:batch)
			sum += ( task.getWcet() / task.getEndTime() );
		if (sum > limit)
			return false;
		return true;
	}

       /**
        * checks if all the computed speeds are below or equal to 1
        */
	protected boolean checkSpeeds(Point2DFloatList speeds)
	{
            for(int i = 0; i < speeds.size()-1; i++)
            {
                float theSpeed = speeds.get(i).getX();
		if (theSpeed > 1)
			return false;
            }
	    return true;
	}

	/**
        * simules execution to test if there's no error
        */
	protected boolean testBatchAndSpeeds(Task[] batch, Point2DFloatList speeds)
        {
        	float theTime, theDuration, theSpeed, theSpeedTimeLimit, theRealDuration;
		Task theTask;
		int i = 0, j = 0;

		theTask = batch[i];
		theTime = theTask.getStartTime();
		theDuration = theTask.getWcet();
		theSpeed = speeds.get(j).getX();
		theSpeedTimeLimit = speeds.get(j).getY();

		while (i < batch.length)
		{
			if (theTime > Main.TIME_INTERVAL)
				return false;
			if (theSpeed == 0)
			{
				theTime = theSpeedTimeLimit;
				theRealDuration = 0;
			}
			else
				theRealDuration = theDuration / theSpeed;

			if (theTime + theRealDuration <= theSpeedTimeLimit)
			{
				theTime += theRealDuration;
				i++;
				if (i < batch.length)
				{
					theTask = batch[i];
					if (theTask.getStartTime() > theTime)
						theTime = theTask.getStartTime();
					theDuration = theTask.getWcet();
				}
				if (theTime == theSpeedTimeLimit)
				{
					j++;
					if (j < speeds.size())
					{
						theSpeed = speeds.get(j).getX();
						theSpeedTimeLimit = speeds.get(j).getY();
					}
				}
			}
			else
			{
				theTime += (theTask.getEndTime() - theTime) / theSpeed;
				theDuration = theTask.getStartTime() + theTask.getWcet() - theSpeedTimeLimit;
				j++;
				if (j < speeds.size())
				{
					theSpeed = speeds.get(j).getX();
					theSpeedTimeLimit = speeds.get(j).getY();
				}
			}
		}
		return true;
        }

	public static void runScheduleTest()
        {
		float timeInterval = 550;
		Point2DFloatList speeds = new Point2DFloatList();
		Point2DFloatList expectedSpeeds = new Point2DFloatList();
        	Task[] batch = new Task[6];
		SmallestPathScheduler Sp = new SmallestPathScheduler();

		batch[0] = new Task();
		batch[0].setValues(0, 120, 60, 60);
		batch[1] = new Task();
		batch[1].setValues(0, 230, 10, 10);
		batch[2] = new Task();
		batch[2].setValues(0, 250, 20, 20);
		batch[3] = new Task();
		batch[3].setValues(150, 230, 40, 40);
		batch[4] = new Task();
		batch[4].setValues(180, 520, 30, 30);
		batch[5] = new Task();
		batch[5].setValues(500, 550, 10, 10);

		Sp.schedule(batch, speeds, timeInterval);

		expectedSpeeds.add(new Point2DFloat((float)0.52, (float)250));
		expectedSpeeds.add(new Point2DFloat((float)0.12, (float)500));
		expectedSpeeds.add(new Point2DFloat((float)0.2, (float)550));
	}

	public String getName()
	{
		return "SmallestPathScheduler";
	}
}

