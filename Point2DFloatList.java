
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.*;

/**
  * Encapsulation of LinkedList<Point2DFloat>
  * @author de Kryger Ode
  * @version 2012.04.14
  */
public class Point2DFloatList extends LinkedList<Point2DFloat>{


    private static final long serialVersionUID = -3685724234208861669L;

	public Point2DFloat get(int i)
    {
        return (Point2DFloat)super.get(i);
    }

    public boolean equals(Point2DFloatList l)
    {
	if (this.size() != l.size())
		return false;
	for(int i = 0; i < this.size(); i++)
	{
		if ( !this.get(i).equals(l.get(i)) )
			return false;
	}
	return true;
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
    public String asString()
    {
	String s = new String();
	s = s + "Size:\t" + Integer.toString(this.size()) + "\n";
        for (int j = 0; j < this.size(); j++)
            s = s + this.get(j).asString() + "\n";
	return s;
    }

    public void fillWithFile(String filepath)
    {
	    try{
		FileInputStream fstream = new FileInputStream(filepath);
	        DataInputStream in = new DataInputStream(fstream);
		BufferedReader br = new BufferedReader(new InputStreamReader(in));
	        String strLine = br.readLine();
		strLine = strLine.substring(6);// "Size:\t"
		int size = Integer.parseInt(strLine);
		for (int i = 0; i < size; i++)
		{
		    strLine = br.readLine();
		    String[] values = strLine.split(",");

		    if (values.length != 2)
			System.err.println("Error : Incorrect input file");
		    else
		    {
			Point2DFloat p = new Point2DFloat(Float.parseFloat(values[0]), Float.parseFloat(values[1]));
			this.add(p);
		    }


		}
		in.close();
	    } catch (Exception e) {
		System.err.println("Error: " + e.getMessage());
	    }

    }
}

