import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.*;

public class Task
{

	protected static int nextID = 0;
		public static int getNextID()
		{
			return nextID++;
		}

    private static boolean isFeasable(Task[] batch, float timeInterval) {
        Point2DFloatList theSpeeds = new Point2DFloatList();
            SmallestPathScheduler test = new SmallestPathScheduler();
            return test.schedule(batch, theSpeeds, timeInterval);
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
		return (1-this.getCompletion())*wcet;
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
	    if (min == max)
		return max;
	    // else
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
            Arrays.sort(batch, new EndTimeComparator());
            return batch;
	}

	public static Task[] createRandomFifoBatch(int numberOfTasks, float timeInterval)
	{
            Task[] batch;
            batch = new Task[numberOfTasks];
	    float eachInterv = timeInterval / (float)numberOfTasks;
            do
            {
		float lastStart = 0;
		float lastEnd = 0;
                for (int i = 0; i < numberOfTasks; ++i)
                {
		    float startInterv = i*eachInterv;
		    System.out.println("startInterv = " + Float.toString(startInterv));
		    float endInterv = (i+1)*eachInterv;
		    float st = Task.generateInRange(lastStart, lastStart + eachInterv);
		    System.out.println("st = " + Float.toString(st));
		    System.out.println("max("+ Float.toString(lastEnd) + "," + Float.toString(lastStart + eachInterv) + ")");
		    float et = Task.generateInRange(Math.max(lastEnd, lastStart + eachInterv), endInterv);
		    System.out.println("et = " + Float.toString(et));
		    float wcet = Task.generateInRange((et-st)/2, et-st);
		    float aet = Task.generateInRange(wcet/2, wcet);
		    lastStart = st; // for next loop
		    lastEnd = et;
                    batch[i] = new Task();
                    batch[i].setValues(st,et,wcet,aet);
                }
            }
            while (!isFeasable(batch, timeInterval));
            return batch;
	}

	public static Task[] createBatchFromFile(String filepath)
	{
	    Task[] batch = null;
	    try{
		FileInputStream fstream = new FileInputStream(filepath);
	        DataInputStream in = new DataInputStream(fstream);
		BufferedReader br = new BufferedReader(new InputStreamReader(in));
	        String strLine = br.readLine();
		int batchSize = Integer.parseInt(strLine);
		batch = new Task[batchSize];
		for (int i = 0; i < batchSize; i++)
		{
		    strLine = br.readLine();
		    String[] values = strLine.split(",");
		    if (values.length != 4)
			System.err.println("Error : Incorrect input file");
		    else
		    {
			batch[i] = new Task();
			batch[i].setValues(
				Float.parseFloat(values[0]),
				Float.parseFloat(values[1]),
				Float.parseFloat(values[2]),
				Float.parseFloat(values[3])
				);
		    }


		}
		in.close();
	    } catch (Exception e) {
		System.err.println("Error: " + e.getMessage());
	    }
            Arrays.sort(batch, new EndTimeComparator());
	    return batch;
	}

	public static Task[] createTestBatch()
	{
        	Task[] batch = new Task[6];
		batch[0] = new Task();
		batch[0].setValues(0, 1200, 600, 600);
		batch[1] = new Task();
		batch[1].setValues(0, 2300, 100, 100);
		batch[2] = new Task();
		batch[2].setValues(0, 2500, 200, 200);
		batch[3] = new Task();
		batch[3].setValues(1500, 2300, 400, 400);
		batch[4] = new Task();
		batch[4].setValues(1800, 5200, 300, 300);
		batch[5] = new Task();
		batch[5].setValues(5000, 5500, 100, 100);
		return batch;
	}

    public Task clone()
    {
    	Task task = new Task();
    	task.setValues(this.startTime, this.endTime, this.wcet, this.actual_et, this.id);
		return task;
    }

    public String print()
    {
	return Float.toString(this.getStartTime())
		+ " , "
		+ Float.toString(this.getEndTime())
		+ " , "
		+ Float.toString(this.getWcet())
		+ " , "
		+ Float.toString(this.getActualEt())
		;
    }
}
