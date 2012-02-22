import java.util.*;

/* This Scheduler returns a speeds list for different time intervals
	and rearrange the tasks vector according to EDF priority */
public class SmallestPathScheduler implements Scheduler
{
	public void schedule(Task[] batch)
	{
		try
		{	
			Point2DFloatList L = new Point2DFloatList();
			Point2DFloatList La = new Point2DFloatList();
			Point2DFloatList Ld = new Point2DFloatList();
			Point2DFloatList V = new Point2DFloatList();
			Point2DFloatList speeds = new Point2DFloatList();

			this.checkFeasability(batch);
			this.makeUpperPointsList(La, batch);
			this.makeLowerPointsList(Ld, batch);
			// check A(t) >= D(t)
			this.meltLists(La, Ld, L);
			this.makeSmallestPath(L, La, Ld, V);
                        this.computeSpeeds(V, speeds);
                        this.checkSpeeds(speeds);
			this.EDF(batch);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	protected void makeUpperPointsList(Point2DFloatList La, Task[] batch)
	{
		Task[] theBatchTemp = batch.clone();
		float theDurationsSum = 0;
		int i = 0;
		La.add(new Point2DFloat(0, 0));

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
	}

	protected void makeLowerPointsList(Point2DFloatList Ld, Task[] batch)
	{
		Task[] theBatchTemp = batch.clone();
		float theDurationsSum = 0;
		int i = 0;
		float theEndTime;
		Ld.addLast(new Point2DFloat(0, 0));
	
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
	}

	protected void meltLists(Point2DFloatList La, Point2DFloatList Ld, Point2DFloatList L)
	{
		int i = 0;
		Point2DFloat theLaPoint;
		Point2DFloat theLdPoint;
		for (; i < La.size() && i < Ld.size(); i++)
		{
			theLaPoint = La.get(i);
			theLdPoint = Ld.get(i);
			if (Point2DFloat.compareOnX(theLaPoint, theLdPoint) == -1)
			{
				L.add(theLaPoint);
				L.add(theLdPoint);
			}
			else if (Point2DFloat.compareOnX(theLaPoint, theLdPoint) == 1)
			{
				L.add(theLdPoint);
				L.add(theLaPoint);
			}
			else
				L.add(theLaPoint);
		}

		if (i < La.size() - 1)
			L.addAll(La.subList(i, La.size()));	
		else if (i < Ld.size() - 1)
			L.addAll(Ld.subList(i, Ld.size()));
	}

	protected void makeSmallestPath(Point2DFloatList L, Point2DFloatList La, Point2DFloatList Ld, Point2DFloatList V)
	{
		Point2DFloatList Lv = new Point2DFloatList();
		Point2DFloatList Lw = new Point2DFloatList();
		Point2DFloatList Temp = new Point2DFloatList();
		Point2DFloat startPoint = L.get(0);
		Point2DFloat newStartPoint;
		int index;

		for (int i = 0; i < L.size(); i++)
		{
			Point2DFloat currentPoint = L.get(i);
                        
			if (La.contains(currentPoint))
			{
				Lv.add(currentPoint);
				if (this.removeHatAngle(Lv) && Lv.size() == 2 && Lv.contains(startPoint) && Lv.contains(currentPoint))
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
                                            V.addAll(Temp);
                                            startPoint = (Point2DFloat)newStartPoint.clone();
                                            Temp.clear();
                                        }
				}
			}

			if (Ld.contains(currentPoint))
			{
				Lw.add(currentPoint);
				if (this.removeCupAngle(Lw) && Lw.size() == 2 && Lw.contains(startPoint) && Lw.contains(currentPoint))
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
                                            V.addAll(Temp);
                                            startPoint = (Point2DFloat)newStartPoint.clone();
                                            Temp.clear();
                                        }
				}
			}
		}
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

	protected boolean removeHatAngle(Point2DFloatList Lv)
	{
		int i = 0;
		float theFirstGradient;
		float theSecondGradient;
                boolean removed = false;

		while (i <= (Lv.size() - 3) && Lv.size() >= 3)
		{
			theFirstGradient = Point2DFloat.getGradient(Lv.get(i), Lv.get(i+1));
			theSecondGradient = Point2DFloat.getGradient(Lv.get(i+1), Lv.get(i+2));
			if (theFirstGradient > theSecondGradient)
                        {
				Lv.remove(i+1);
                                removed = true;
                                i = 0;
                        }
			else
				i++;
		}
                return removed;
	}

	protected boolean removeCupAngle(Point2DFloatList Lw)
	{
		int i = 0;
		float theFirstGradient;
		float theSecondGradient;
                boolean removed = false;

		while (i <= (Lw.size() - 3) && Lw.size() >= 3)
		{
			theFirstGradient = Point2DFloat.getGradient(Lw.get(i), Lw.get(i+1));
			theSecondGradient = Point2DFloat.getGradient(Lw.get(i+1), Lw.get(i+2));
			if (theFirstGradient < theSecondGradient)
                        {
				Lw.remove(i+1);
                                removed = true;
                                i = 0;
                        }
			else
				i++;
		}	
                return removed;
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

	protected void checkFeasability(Task[] batch) throws Exception
	{
		float sum = 0, limit = 1;
		for (Task task:batch)
			sum += ( task.getWcet() / task.getEndTime() );       
		if (sum > limit)
			throw new Exception("Unfeasable System!");
	}
        
	protected void checkSpeeds(Point2DFloatList speeds) throws Exception
	{
            for(int i = 0; i < speeds.size()-1; i++)
            {
                float theSpeed = speeds.get(i).getX();
		if (theSpeed > 1)
			throw new Exception("Unfeasable System!");
            }		
	}

	public String getName()
	{
		return "SmallestPathScheduler";
	}
}

