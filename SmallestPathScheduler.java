import java.util.*;

/* This Scheduler returns a speeds list for different time intervals
	and rearrange the tasks vector according to EDF priority */
public class SmallestPathScheduler implements Scheduler
{
	public boolean schedule(Task[] batch, Point2DFloatList speeds, float timeInterval)
	{
		Point2DFloatList L = new Point2DFloatList();
		Point2DFloatList La = new Point2DFloatList();
		Point2DFloatList Ld = new Point2DFloatList();
		Point2DFloatList Lv = new Point2DFloatList();
		Point2DFloatList Lw = new Point2DFloatList();
		Point2DFloatList V = new Point2DFloatList();
		Point2DFloatList theSpeeds = new Point2DFloatList();
		try
		{
			if (!this.checkFeasability(batch))
				return false;
			this.makeStartPoint(batch, La, Ld, Lv, Lw, L);
			this.makeUpperPointsList(La, batch);
			//System.out.println("------------------------La");
			//La.print();
			this.makeLowerPointsList(Ld, batch);
			//System.out.println("------------------------Ld");
			//Ld.print();
			// check A(t) >= D(t)
			this.meltLists(La, Ld, L);
			//System.out.println("------------------------L");
			//L.print();
			this.makeSmallestPath(L, La, Ld, Lv, Lw, V);
			//System.out.println("------------------------V");
			//V.print();
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
			/*System.out.println("-------------------------");
			System.out.println("Lv");
			Lv.print();
			System.out.println("Lw");
			Lw.print();*/
		}
		V.addAll(startPointsList);
		Lw.removeFirst();
		V.addAll(Lw);
	}

	protected Point2DFloat lastUpperPoint(Point2DFloatList Lw, Point2DFloat origin, Point2DFloat currentPoint)
	{
		Point2DFloat res = null;
		Point2DFloat tmp;
		for (int i = Lw.size() - 1; i >= 0; i--)
		{
			tmp = Lw.get(i);
			if ( Point2DFloat.getGradient(origin, tmp) > Point2DFloat.getGradient(origin, currentPoint) ) // > ou >= ?
			{
				res = tmp;
				break;
			}	
		}	
		return res;
	}

	protected Point2DFloat lastLowerPoint(Point2DFloatList Lv, Point2DFloat origin, Point2DFloat currentPoint)
	{
		Point2DFloat res = null;
		Point2DFloat tmp;
		for (int i = Lv.size() - 1; i >= 0; i--)
		{
			tmp = Lv.get(i);
			if ( Point2DFloat.getGradient(origin, tmp) < Point2DFloat.getGradient(origin, currentPoint) ) // > ou >= ?
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

	protected boolean checkFeasability(Task[] batch)
	{
		float sum = 0, limit = 1;
		for (Task task:batch)
			sum += ( task.getWcet() / task.getEndTime() );     
		if (sum > limit)
			return false;
		return true;
	}
        
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

	// according to EDF
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

			/*System.out.println("\ni");
			System.out.println(i);
			System.out.println("j");
			System.out.println(j);
			System.out.println("theTime");
			System.out.println(theTime);
			System.out.println("theDuration");
			System.out.println(theDuration);
			System.out.println("theRealDuration");
			System.out.println(theRealDuration);	
			System.out.println("theSpeed");
			System.out.println(theSpeed);
			System.out.println("theSpeedTimeLimit");
			System.out.println(theSpeedTimeLimit);*/			

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

	public static void testSmallestPathSchedulersArticleExample()
        {
		float timeInterval = 550;
		Point2DFloatList speeds = new Point2DFloatList(); 
        	Task[] batch = new Task[6];
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

		SmallestPathScheduler Sp = new SmallestPathScheduler();
		Sp.schedule(batch, speeds, timeInterval);

		System.out.println("\nExample Test:");
		System.out.println("speeds from SmallestPathScheduler: "); 
		speeds.print();
        }

	public static void testSmallestPathSchedulersLimitCases()
        {
		float timeInterval = 4;
		Point2DFloatList speeds = new Point2DFloatList();
        	Task[] batch = new Task[2];
		batch[0] = new Task();
		batch[0].setValues(1, 2, 1, 1);
		batch[1] = new Task();
		batch[1].setValues(3, 4, 1, 1);

		SmallestPathScheduler Sp = new SmallestPathScheduler();
		Sp.schedule(batch, speeds, timeInterval);

		System.out.println("\nLimit Cases Test:");
		System.out.println("speeds from SmallestPathScheduler: "); 
		speeds.print();
        }

	public static void testSmallestPathSchedulerWithRandomBatch()
        {
        	Task[] batch = Task.createRandomBatch(Main.NUMBER_OF_TASK, Main.TIME_INTERVAL);
	
		SmallestPathScheduler Sp = new SmallestPathScheduler();
		Point2DFloatList speeds = new Point2DFloatList();
		Sp.schedule(batch, speeds, Main.TIME_INTERVAL);

		System.out.println("\nTest 3:");
		System.out.println("speeds from SmallestPathScheduler"); 
		speeds.print();

		if (!Sp.testBatchAndSpeeds(batch, speeds))
			System.out.println("RandomTest failed");
		else
			System.out.println("RandomTest succeed");
        }

	public String getName()
	{
		return "SmallestPathScheduler";
	}
}

