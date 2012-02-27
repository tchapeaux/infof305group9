import java.util.*;
//import java.lang.*;

public class Simulation
{
	protected Task[] taskBatch;
	protected Scheduler scheduler;
	protected float currentTime=0; // elapsed time
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
		float t = 0;
		for (Point2DFloat p:getSpeeds())
		{
			if (t + p.getY() > getCurrentTime())
				return p.getX();
			t += p.getY();
		}
		return 1;
	}

	protected boolean isComputing;
	    public boolean isComputing() {return isComputing;}
	    protected void setIsComputing(boolean newValue) {isComputing=newValue;}

	public Simulation(Scheduler scheduler)
	{
		this.timeInterval = Main.TIME_INTERVAL;
		Task[] taskBatch = new Task[Main.NUMBER_OF_TASK];
		this.taskBatch = new Task[Main.NUMBER_OF_TASK];

		Point2DFloatList theSpeeds = new Point2DFloatList();
		taskBatch = Task.createRandomBatch(Main.NUMBER_OF_TASK, Main.TIME_INTERVAL);
		while (!scheduler.schedule(taskBatch, theSpeeds, timeInterval))
			taskBatch = Task.createRandomBatch(Main.NUMBER_OF_TASK, Main.TIME_INTERVAL);

		for (int i=0; i<taskBatch.length; i++)
			this.taskBatch[i] = taskBatch[i].clone();
		this.scheduler = scheduler;
		this.speeds = theSpeeds;
		isComputing = false;
		isComputing = false;
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

		// TODO : if in-line algorithm : reschedule the tasks
		for(Task eachTask:taskBatch) // ??-> does this go through the array in the logical order??
		{
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

	public String getSchedulerType()
	{
		return scheduler.getName();
	}

    void showTime(int i) {
        this.showedTime=i;
    }

}
