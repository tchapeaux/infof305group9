import java.util.*;

public class Task
{

	protected static long nextID = 0;
		public static long getNextID()
		{
			return nextID++;
		}

	protected long id;
		public long getId() {return this.id;}

	protected float startTime;
		public float getStartTime() {return this.startTime;}
	protected float endTime;
		public float getEndTime() {return this.endTime;}
		public float timeLeftToComplete(float currentTime)
		{
			return (this.getEndTime() - currentTime);
		}

	// actual_et -> time needed to execute the task at CPU speed = 1.
	// wcet -> "advertised" execution time (worst scenario)
	// typically: actual_et is used by the simulation, wcet is used by the scheduler.
	protected float wcet;
		public float getWcet() {return this.wcet;}
	protected float actual_et;
		public float getActualEt() {return this.actual_et;}

	protected List<float[]> completion = new ArrayList<float[]>(); // float [0] = time; float [1] = completion (0->1)
	// TODO: get rid of the magic numbers
	// --> add a "CompletionPoint" class (struct)?
		public float getCompletion() {return (this.completion.get(this.completion.size()-1))[1];}

		public List<float[]> getCompletionEvolution() {return completion;}

		public void updateCompletion(float interv)
		{
			float newCompletionPoint[]=new float[2];
			Date t = new Date();
			newCompletionPoint[0]=t.getTime()-Main.getStartTime();
			newCompletionPoint[1]=this.getCompletion() + interv;
			this.completion.add(newCompletionPoint);
		}

	public void giveCPU(float duration, float speed)
	{
		this.updateCompletion(speed*(duration/this.getActualEt()));
	}

	public float worstComputationTimeLeft()
	{
		return (wcet - this.getCompletion()*wcet);
	}

	public Task()
	{
		float firstCompletionPoint[]=new float[2];
		firstCompletionPoint[0]=Main.getStartTime();
		firstCompletionPoint[1]=0;
		this.completion.add(firstCompletionPoint);
		this.id = Task.getNextID();
	}

	public void setValues(float st, float et, float wcet, float actual_et)
	{
		this.startTime = st;
		this.endTime = et;
		this.wcet = wcet;
		this.actual_et = actual_et;
	}

	public static float generateInRange(float min, float max)
	{
		Random r = new Random();

		int temp = r.nextInt(	(int)	((max-min)*1000)	);
		return (float)(temp/1000.0) + min;
	}

	public void generateValuesFor(float timeInterval)
	// set random values for the task, with start and ending time in timeInterval
	{
		float duration = Task.generateInRange(timeInterval/4, 3*timeInterval/4);
		float newSt = Task.generateInRange((float)0.0, timeInterval - duration);
		float newEt = newSt + duration;
		float newWcet = Task.generateInRange(duration/2, duration);
		float newActualEt = Task.generateInRange(newWcet/2, newWcet);

		this.setValues(newSt, newEt, newWcet, newActualEt);
	}

	public static Task[] createRandomBatch(int numberOfTasks, float timeInterval)
	{
		// TODO : better generation (considering number of tasks, etc)

		Task[] batch;
		batch = new Task[numberOfTasks];
		for (int i = 0; i < numberOfTasks; ++i)
		{
			batch[i] = new Task();
			batch[i].generateValuesFor(timeInterval);
		}

		return batch;
	}

	// for Schedulers test
    public static Task[] createTestBatch()
    {
		Task[] batch;
		batch = new Task[6];

		batch[0] = new Task();
		batch[0].setValues(1000, 2000, 500, 500);
		batch[1] = new Task();
		batch[1].setValues(4000, 5000, 500, 500);
		batch[2] = new Task();
		batch[2].setValues(6000, 7000, 500, 500);
		batch[3] = new Task();
		batch[3].setValues(9000, 10000, 500, 500);
		batch[4] = new Task();
		batch[4].setValues(11000, 12000, 500, 500);
		batch[5] = new Task();
		batch[5].setValues(13000, 14000, 500, 500);

		return batch;
    }
}
