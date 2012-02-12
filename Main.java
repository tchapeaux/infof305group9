//import java.io.*;
import java.util.*;
//import java.sql.Timestamp;

public class Main {

		protected static final int TIME_INTERVAL = 6; // s
		protected static final int NUMBER_OF_SIMS = 3;

		protected static Pannel pannel;
		protected static long startTime;

		public static long getStartTime()
		{
			return startTime;
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
			long lastTime = startTime;
			long currentTime = startTime;


			pannel = new Pannel(1200,800,simulations);
			pannel.run();

			do
			{
				t = new Date();
				lastTime = currentTime;
				currentTime = t.getTime();
				long interval = (currentTime - lastTime);

				for (Simulation sim:simulations)
				{
					sim.compute(interval);
				}
				try {
					Thread.sleep(100);
				} catch (Exception e) {/*TODO*/};

			} while ((currentTime - startTime)/1000 < Main.TIME_INTERVAL);
        }
}
