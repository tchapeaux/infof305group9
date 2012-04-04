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

                public static void setStartTime(long i)
		{
			startTime= i;
		}

		public static long getTIME_INTERVAL()
		{
			return TIME_INTERVAL;
		}


        public static void main(String[] args){
	
	    SmallestPathScheduler.runScheduleTest();

	    createRandomBatchColor(NUMBER_OF_TASK);

	    pannel = new Panel(1200,800);

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
