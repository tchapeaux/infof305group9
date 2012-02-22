
import java.util.*;

public class Point2DFloatList extends LinkedList{
   
    public Point2DFloat get(int i)
    {
        return (Point2DFloat)super.get(i);
    }
    
    public boolean contains(Point2DFloat inPoint)
    {
        Point2DFloat thePoint;
        for (int i = 0; i < this.size(); i++)
        {
            thePoint = this.get(i);
            if(thePoint.equals(inPoint))
                return true;
        }
        return false;
    }
    
    // pour debug
    public void print()
    {
        for (int j = 0; j < this.size(); j++)
            this.get(j).print();
    }
}

