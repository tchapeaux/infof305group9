public class DumbScheduler implements Scheduler
{
	public void schedule(Task[] batch)
	{
		for (Task task:batch)
		{
			task.setSpeed(1);
		}
	}

	public String getName()
	{
		return "DumbScheduler";
	}
}
