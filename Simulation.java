import java.util.*;
//import java.lang.*;

public class Simulation
{

	protected Task[] taskBatch;
	protected Scheduler scheduler;
	protected float currentTime; // elapsed time
		public float getCurrentTime() {return this.currentTime;}
	protected int energyUsed;
		public int getEnergyUsed() {return this.energyUsed;}

	public Simulation(Task[] taskBatch, Scheduler scheduler)
	{
		this.currentTime = 0;
		this.energyUsed = 0;

		this.taskBatch = Arrays.copyOf(taskBatch, taskBatch.length); // each Simulation needs its own batch
		this.scheduler = scheduler;
		scheduler.schedule(this.taskBatch);
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

		// TODO : if in-line algorithm : reschedule the tasks
		// Find the most prioritary task to be computed
		for(Task task:taskBatch) // ??-> does this go through the array in the logical order??
		{
			/*
			System.out.println("::::::::");
			System.out.println(Float.toString(task.getCompletion()));
			System.out.println(Float.toString(task.getStartTime()) + " <= " + Float.toString(startPoint));
			System.out.println(Float.toString(task.getEndTime()) + " > " + Float.toString(startPoint));
			*/
			if (task.getCompletion() < 1.0
			 && task.getStartTime() <= startPoint
			 && startPoint < task.getEndTime())
			// --> the task is chosen to be computed
			{
				float timeLeftToCompute = (((float)1.0 - task.getCompletion()))*task.getActualEt();
				// The actual time spent on that task is either
				//		- elapsedMs if the task is not completed during the rest of the interval
				//		- timeLeftToCompute if the task is completed before the end of the interval
				float taskComputationTime = Math.min(elapsedMs, timeLeftToCompute);
				task.updateCompletion((task.getSpeed()*(taskComputationTime/task.getActualEt())));


				this.currentTime = this.currentTime + taskComputationTime;
				//TODO : energy used


				if (taskComputationTime < elapsedMs)
				// --> Another task will be computed during the remaining of the interval.
				{
					this.compute(elapsedMs - taskComputationTime);
				}


				System.out.println("new completion : " + Float.toString(task.getCompletion()));
				break;
			}
		}

		if (startPoint == this.currentTime)
		// no computable task were found
		// --> time passes anyway
		{
				this.currentTime = this.currentTime + elapsedMs;
		}
	}

	public Task getTask(int i)
	{
		if (i<taskBatch.length)
			return taskBatch[i];
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
