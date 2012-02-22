//import java.io.*;
import java.util.*;
//import java.sql.Timestamp;

public class Main {

		protected static final int TIME_INTERVAL = 15000; // ms
		protected static final int NUMBER_OF_SIMS = 3;

		protected static Pannel pannel;
		protected static long startTime;

		public static long getStartTime()
		{
			return startTime;
		}

		public static long getTIME_INTERVAL()
		{
			return TIME_INTERVAL;
		}


        public static void main(String[] args){

		Task[] taskBatch = Task.createRandomBatch(10, Main.TIME_INTERVAL);
		Simulation[] simulations = new Simulation[Main.NUMBER_OF_SIMS];
		for(int i = 0; i < Main.NUMBER_OF_SIMS; i++)
		{
			simulations[i] = new Simulation(taskBatch, new DumbScheduler());
		}

		Date t = new Date();
		startTime = t.getTime(); // number of ms since epoch

		pannel = new Pannel(1200,800,simulations);
		pannel.run();

		// test Schedulers
        	/*Task[] testBatch = Task.createTestBatch();
		SmallestPathScheduler Sp = new SmallestPathScheduler();
		SingleFrequencyScheduler Sf = new SingleFrequencyScheduler();
		Sp.schedule(testBatch);
		Sf.schedule(testBatch);*/
        }
}
