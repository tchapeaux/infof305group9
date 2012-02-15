public class TimeDivisionElem
// Used by TimeDivision to store information on one task in the interval
// This is essentially a C++ struct so everything is public
// (Consistency is handled by the TimeDivision class)
{
	public long taskID; // can be equal to 0 (--> no task to compute)
	public float computationStartTime;
	public float computationEndTime;

	public TimeDivisionElem()
	{	}

	public TimeDivisionElem(TimeDivisionElem other)
	{
		this.taskID = other.taskID;
		this.computationStartTime = other.computationStartTime;
		this.computationEndTime = other.computationEndTime;
	}

	public TimeDivisionElem(long id, float st, float et)
	{
		this.taskID = id;
		this.computationStartTime = st;
		this.computationEndTime = et;
	}

	public String toString()
	{
		return "ID: " + this.taskID + " CompStartTime: " + this.computationStartTime + "endTime: " + this.computationEndTime;
	}
}
