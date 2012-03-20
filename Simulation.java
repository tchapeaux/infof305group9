import java.util.*;
//import java.lang.*;

public class Simulation
{
	protected Task[] taskBatch;
	protected InitialScheduler myInitialScheduler;
	protected InlineScheduler myInlineScheduler;
	public boolean hasHumanInitialScheduler() {return (myInitialScheduler.getName() == "You");}
	protected float currentTime=-2000; // elapsed time
        protected float showedTime=0;
		public float getCurrentTime() {return this.currentTime+this.showedTime;}
		public float getRelativeShowedTime() {return this.showedTime;}
	protected float energyUsed=0;
		public float getEnergyUsed() {return this.energyUsed;}
	protected float timeInterval; // duration of the time interval considered by the simulation
	public boolean isDone() { return (currentTime >= timeInterval); }

	protected Point2DFloatList speeds;
	protected Point2DFloatList getSpeeds() {return speeds;}
	// each speeds[i] means that the CPU has to go at speed speeds[i].x until currentTime = speeds[i].y
	public float getCurrentSpeed()
	{
		for (int i = 0; i < getSpeeds().size(); i++)
		{
		    Point2DFloat p = getSpeeds().get(i);
			if (p.getY() > getCurrentTime())
			{
			    return p.getX();
			}
		}
		// No correct value was found => return last value or 1
		if (!getSpeeds().isEmpty())
		    return getSpeeds().get(getSpeeds().size()-1).getX();
		else
		    return 1;
	}

	protected boolean isComputing;
	    public boolean isComputing() {return isComputing;}
	    protected void setIsComputing(boolean newValue) {isComputing=newValue;}

	public Simulation(InitialScheduler initialScheduler, InlineScheduler inlineScheduler, Task[] taskBatch)
	{
		this.timeInterval = Main.TIME_INTERVAL;
		this.taskBatch = new Task[Main.NUMBER_OF_TASK];

		Point2DFloatList theSpeeds = new Point2DFloatList();

		for (int i=0; i<taskBatch.length; i++)
			this.taskBatch[i] = taskBatch[i].clone();

		this.myInitialScheduler = initialScheduler;
                myInitialScheduler.schedule(this.taskBatch, theSpeeds, timeInterval);

		this.myInlineScheduler = inlineScheduler;

		this.speeds = theSpeeds;
		isComputing = false;
		isComputing = false;
		//System.out.println("I'm " + myInitialScheduler.getName() + " and I got this :");
		//this.speeds.print();
	}

	public float upperAcceptedSpeed(float speed)
	// return the closest speed achievable by the CPU which is superior or equal to speed
	{
		// TODO
		return speed;
	}

	public void compute(float elapsedMs)
	// Compute the evolution of the system in the interval between currentTime and currentTime + elapsedMs
	{
		float startPoint = this.currentTime;
		float endPoint = startPoint + elapsedMs;
		Stack<Task> tasksInInterval = new Stack<Task>();

		myInlineScheduler.schedule(this.taskBatch, this.getSpeeds(), this.timeInterval);

		for(int i = 0; i < taskBatch.length; i++) // ??-> does this go through the array in the logical order??
		{
		    Task eachTask = taskBatch[i];
			if (eachTask.getCompletion() < 1.0
			 && !(eachTask.getStartTime() > endPoint) // the task is not AFTER the interval
			 && !(eachTask.getEndTime() < startPoint)) // the task is not BEFORE the interval
			{
				tasksInInterval.push(eachTask);
			}
		}
		setIsComputing(false);
		if (!tasksInInterval.empty())
		{
		    setIsComputing(true);
			// We must divide the time interval between all the computable tasks.
			TimeDivision td = new TimeDivision(startPoint, endPoint);
			Task i = null;
			do
			{
				i = tasksInInterval.pop();
				td.addTask(i);
			} while (!tasksInInterval.empty());

			// Now we give CPU time to each time according to our TimeDivision
			for (TimeDivisionElem elem:td.getTDElemList())
			{
				if (elem.taskID == -1)
					continue;
				Task t = this.getTask(elem.taskID);
				float compTime = elem.computationEndTime - elem.computationStartTime;
				t.giveCPU(compTime, this.getCurrentSpeed(), this.currentTime);

				// energy
				this.energyUsed = this.energyUsed + this.getCurrentSpeed()*this.getCurrentSpeed()*compTime;//quadratic evolution of energy use
			}
		}

		this.currentTime = this.currentTime + elapsedMs;
	}

	public Task getTask(long i)
	{
		for (Task t:taskBatch)
			if (t.getId() == i)
				return t;
		return null;
	}

	public int getNumberOfTasks()
	{
		return taskBatch.length;
	}

	public String getInitialSchedulerType()
	{
		return myInitialScheduler.getName();
	}

    void showTime(int i) {
        this.showedTime=i;
    }

    Task[] getTaskBatch() {
        return taskBatch;
    }

}
