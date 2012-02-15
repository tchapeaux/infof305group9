import java.util.*;

public class TimeDivision
// Used by Simulation to divide a time interval between several tasks to be computed
{

	protected final float startTime;
	protected final float endTime;
	protected LinkedList<TimeDivisionElem> computationTimes;
	public LinkedList<TimeDivisionElem> getTDElemList() {return this.computationTimes;}

	public TimeDivision(float startTime, float endTime)
	{
		this.startTime = startTime;
		this.endTime = endTime;
		this.computationTimes = new LinkedList<TimeDivisionElem>();
		computationTimes.add(new TimeDivisionElem(0,startTime, endTime));
	}

	public void addTask(Task t)
	// "pre-condition" : the tasks must be provided in the least-to-most prioritary order
	{
		//TODO: check the pre-condition

		ListIterator<TimeDivisionElem> li = computationTimes.listIterator();
		TimeDivisionElem tmpTDE = null;

		// computationTimes is never empty -> no need to check here
		
		TimeDivisionElem elem;
		do
		// Note: chronological order.
		{
			elem = li.next();
			if (elem.computationEndTime < t.getStartTime())
				continue;

			// elem is not completely before task -> we will put the current task here then break

			if (elem.computationEndTime > t.getEndTime())
			// ---------[   elem   ]----------------
			// -----------[Task]--------------------
				tmpTDE = new TimeDivisionElem(elem);

			// cut overlapping time of elem
			elem.computationEndTime = Math.max(this.startTime, t.getStartTime());
			if(elem.computationStartTime == elem.computationEndTime)
			{
				li.remove();
			}

			// add new TimeDivisionElem representing the current task
			{
				TimeDivisionElem taskTDE = new TimeDivisionElem();
				taskTDE.taskID = t.getId();
				taskTDE.computationStartTime = Math.max(this.startTime, t.getStartTime());
				taskTDE.computationEndTime = Math.min(this.endTime, t.getEndTime());
				li.add(taskTDE);
			}

			if (tmpTDE != null)
			{
				TimeDivisionElem oldElemTDE = new TimeDivisionElem();
				oldElemTDE.taskID = tmpTDE.taskID;
				oldElemTDE.computationStartTime = t.getEndTime();
				oldElemTDE.computationEndTime = tmpTDE.computationEndTime;
				li.add(oldElemTDE);
				tmpTDE = null;
			}
			break;
		}
		while (li.hasNext());
	}
}
