import java.util.*;

public class EndTimeComparator implements Comparator
{
	public int compare(Object task1, Object task2)
	{     
        	float endTime1 = ((Task)task1).getEndTime();        
        	float endTime2 = ((Task)task2).getEndTime();
       
        	if(endTime1 > endTime2)
            		return 1;
        	else if(endTime1 < endTime2)
            		return -1;
        	else
            		return 0;    
    }
}
