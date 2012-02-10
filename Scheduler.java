interface Scheduler
// Used for, like, a Stratgy Pattern or something
{
	// Update the "speed" attribute of each Task in the batch
	// Reorder the Array with the most prioritary task first
	public void schedule(Task[] batch);
	public String getName();
}
