import java.util.*;
//import java.lang.*;

public class Simulation
{

	protected Task[] taskBatch;
	protected Scheduler scheduler;
	protected float currentTime; // elapsed time
		public float getCurrentTime() {return this.currentTime;}
	protected float energyUsed;
		public float getEnergyUsed() {return this.energyUsed;}
	protected float timeInterval;
		
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

	public Simulation(Task[] taskBatch, Scheduler scheduler, float timeInterval)
	{
		this.currentTime = 0;
		this.energyUsed = 0;
		this.timeInterval = timeInterval;

		this.taskBatch = new Task[taskBatch.length];
		for (int i=0; i<taskBatch.length; i++)
		{
			this.taskBatch[i] = taskBatch[i].clone();
		}
		
		this.scheduler = scheduler;
		speeds = scheduler.schedule(this.taskBatch, timeInterval);
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

		if (!tasksInInterval.empty())
		{
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
				if (elem.taskID == 0)
					continue;
				Task t = this.getTask(elem.taskID);
				float compTime = elem.computationEndTime - elem.computationStartTime;
				t.giveCPU(compTime, this.getCurrentSpeed());

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

}
