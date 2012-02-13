import java.util.*;

public class EndTimeComparator implements Comparator<Task>
{
	public int compare(Task task1, Task task2)
	{
        	float endTime1 = task1.getEndTime();
        	float endTime2 = task2.getEndTime();

        	if(endTime1 > endTime2)
            		return 1;
        	else if(endTime1 < endTime2)
            		return -1;
        	else
            		return 0;
    }
}
