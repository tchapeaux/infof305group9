import java.util.*;

public class StartTimeComparator implements Comparator<Task>
{
	public int compare(Task task1, Task task2)
	{
        	float startTime1 = task1.getStartTime();
        	float startTime2 = task2.getStartTime();

        	if(startTime1 > startTime2)
            		return 1;
        	else if(startTime1 < startTime2)
            		return -1;
        	else
            		return 0;
    }
}
