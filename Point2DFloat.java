/** 
  * @author de Kryger Ode
  * @version 2012.04.14
  */
public class Point2DFloat implements Cloneable{

    float x;
	float y;

	public float getX() {return this.x;}
	public float getY() {return this.y;}

	public Point2DFloat()
	{
		this.x = 0;
		this.y = 0;
	}

	public Point2DFloat(float x, float y)
	{
		this.x = x;
		this.y = y;
	}

        public boolean equals(Point2DFloat pt)
        {
            if(this.getX() == pt.getX() && this.getY() == pt.getY())
                return true;
            else
                return false;
        }

	public static int compareOnX(Point2DFloat p1, Point2DFloat p2)
	{
		if (p1.getX() < p2.getX())
			return -1;
		else if (p1.getX() > p2.getX())
			return 1;
		else
			return 0;
	}

	public static float getGradient(Point2DFloat p1, Point2DFloat p2)
	{
		return ( (p2.getY() - p1.getY()) / (p2.getX() - p1.getX()) );
	}

        public Object clone()
        {
            Object o = null;
            try
            {
                o = super.clone();
            }
            catch(CloneNotSupportedException e)
            {
                e.printStackTrace();
            }
            return o;
        }

        public String asString()
        {
	    return  Float.toString(this.getX()) + " , "+ Float.toString(this.getY());
        }
}

