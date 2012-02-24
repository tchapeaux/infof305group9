import java.util.*;

public class Task
{

	protected static int nextID = 0;
		public static int getNextID()
		{
			return nextID++;
		}

    private static boolean isFeasable(Task[] batch, float timeInterval) {
        try
        {
            SingleFrequencyScheduler test = new SingleFrequencyScheduler();
            test.schedule(batch, timeInterval);
        }
        catch (Exception e)
        {
            return false;
        }
        return true;
    }

	protected int id;
		public int getId() {return this.id;}

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

	public void updateCompletion(float duration, float interv, float actualTime)
	{
		float newCompletionPoint[]=new float[2];
		newCompletionPoint[0]=actualTime;
		newCompletionPoint[1]=this.getCompletion() + interv;
		this.completion.add(newCompletionPoint);
	}

	public void giveCPU(float duration, float speed, float actualTime)
	{
		this.updateCompletion(duration, speed*(duration/this.getActualEt()), actualTime);
	}

	public float worstComputationTimeLeft()
	{
		return (wcet - this.getCompletion()*wcet);
	}

	public Task()
	{
		float firstCompletionPoint[]=new float[2];
		firstCompletionPoint[0]=0;
		firstCompletionPoint[1]=0;
		this.completion.add(firstCompletionPoint);
		this.id = Task.getNextID();
	}

	public void setValues(float st, float et, float wcet, float actual_et)
	{
		this.startTime = st;
		this.completion.get(0)[0]=st;
		this.endTime = et;
		this.wcet = wcet;
		this.actual_et = actual_et;
	}
	
	public void setValues(float st, float et, float wcet, float actual_et, int id)
	{
		this.setValues(st, et, wcet, actual_et);
		this.id=id;
	}

	public static float generateInRange(float min, float max)
	{
		Random r = new Random();

		int temp = r.nextInt(	(int)	((max-min)*1000)	);
		return (float)(temp/1000.0) + min;
	}

	public void generateValuesFor(float timeInterval, int id, int numberOfTasks)
	// set random values for the task, with start and ending time in timeInterval
	{
		float duration = Task.generateInRange(timeInterval/10, timeInterval/4);
		float newSt = Task.generateInRange((float)0.0, timeInterval - duration); 
		float newEt = newSt + duration;
                float tempMax = Math.min(duration/2,timeInterval/numberOfTasks);
		float newWcet = Task.generateInRange(tempMax/2, tempMax);
		float newActualEt = Task.generateInRange(newWcet/2, newWcet);

		this.setValues(newSt, newEt, newWcet, newActualEt, id);
	}

	public static Task[] createRandomBatch(int numberOfTasks, float timeInterval)
	{
            // TODO : better generation (considering number of tasks, etc)

            Task[] batch;
            batch = new Task[numberOfTasks];
            do
            {
                for (int i = 0; i < numberOfTasks; ++i)
                {
                    batch[i] = new Task();
                    batch[i].generateValuesFor(timeInterval,i,numberOfTasks);
                }
            }
            while (!isFeasable(batch, timeInterval));
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
    
    public Task clone()
    {
    	Task task = new Task();
    	task.setValues(this.startTime, this.endTime, this.wcet, this.actual_et, this.id);
		return task;
    }
}
