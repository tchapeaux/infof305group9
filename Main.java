import java.awt.Color;
import java.util.*;

public class Main {

		protected static final int TIME_INTERVAL = 15000; // ms
		protected static final int NUMBER_OF_SIMS = 3;
		protected static final int NUMBER_OF_TASK = 6;

		protected static Panel pannel;
		protected static long startTime;
		protected static Color[] colorList;

		public static long getStartTime()
		{
			return startTime;
		}

		public static long getTIME_INTERVAL()
		{
			return TIME_INTERVAL;
		}


        public static void main(String[] args){

			createRandomBatchColor(NUMBER_OF_TASK);

			Task[] tasks = Task.createRandomBatch(NUMBER_OF_TASK, TIME_INTERVAL);
			// Task[] tasks= Task.createTestBatch();

                        Simulation[] simulations = new Simulation[Main.NUMBER_OF_SIMS];

			simulations[0] = new Simulation(new SmallestPathScheduler(), tasks);
			simulations[1] = new Simulation(new SingleFrequencyScheduler(), tasks);
			for(int i = 2; i < Main.NUMBER_OF_SIMS; i++)
                            simulations[i] = new Simulation(new DumbScheduler(), tasks);

			Date t = new Date();
			startTime = t.getTime(); // number of ms since epoch

			pannel = new Panel(1200,800,simulations);

        		/*SmallestPathScheduler.testSmallestPathSchedulersArticleExample();
			SmallestPathScheduler.testSmallestPathSchedulersLimitCases();
			SmallestPathScheduler.testSmallestPathSchedulerWithRandomBatch();*/
        }

        public static Color[] getColor()
        {
        	return colorList;
        }

        protected static void createRandomBatchColor(int numberTask)
        {
        	colorList = new Color[numberTask];
        	Random rand = new Random();
        	for (int i = 0; i<numberTask; i++)
        		colorList[i] = new Color(rand.nextFloat(), rand.nextFloat(), rand.nextFloat());
        }
}
