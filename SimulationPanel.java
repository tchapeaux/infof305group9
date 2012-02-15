//import java.io.*;
import java.util.*;
import java.util.List;
//import java.lang.*;
import java.awt.*;
import javax.swing.*;

public class SimulationPanel extends JPanel {

	public static final int pixelsPerSecond = 200;
	public static final int pixelsForText=200;
	public static final int presentTimeLine=255;
    private static final long serialVersionUID = 1L;

	protected Simulation sim;
	public SimulationPanel(Simulation simu) {sim=simu;}
	public int i=0;
	public void paintComponent(Graphics g){
		g.drawRoundRect(5, 5, getWidth()-10, getHeight()-10, 20, 20);
		Date t = new Date();
		double t1 = sim.getCurrentTime();
		int height = Math.min(50,(getHeight())/sim.getNumberOfTasks()-1);
		Task task;
		for (int i=0; (task=sim.getTask(i))!= null;i++)
		{
			//Draw task enter & deadline as rectangle
			{
				int startPos=presentTimeLine+(int)((-t1+task.getStartTime())/1000*pixelsPerSecond);
				int endPos=startPos+(int)((task.getEndTime()-task.getStartTime())/1000*pixelsPerSecond);

				int firstPix=Math.max(6,startPos);
				int lastPix=Math.min(getWidth()-pixelsForText,endPos);
				if (lastPix > 5 & firstPix < getWidth()-pixelsForText)
				{
					g.drawLine(firstPix,8+i*height,lastPix, 8+i*height);
					g.drawLine(firstPix,8+(i+1)*height,lastPix, 8+(i+1)*height);
				}
				if (Math.max(5,startPos) !=5)
				{
					g.drawLine(firstPix,8+i*height,firstPix, 8+(i+1)*height);
				}
				if (lastPix!=getWidth()-pixelsForText)
				{
					g.drawLine(lastPix,8+i*height,lastPix, 8+(i+1)*height);
				}
			}

			//draw Completion
			{
				List<float[]> evolution=task.getCompletionEvolution();
				for (int j=0; j<evolution.size(); j++)
				{
					int startx= (int) ((evolution.get(j)[0] - Main.getStartTime())/1000 +task.getStartTime()/1000)*pixelsPerSecond;
					int starty= (int) (8+i*height+height*evolution.get(j)[1]);
					g.drawLine(startx, starty, startx, starty); // TODO:????
				}
			}

			//draw Work
			{
				int startx=  (int) (Math.max(presentTimeLine,presentTimeLine+(int)((-t1+task.getStartTime())/1000*pixelsPerSecond)));
				int starty= (int) (8+i*height+height*task.getCompletion());

				System.out.println("Startx: "+startx);
				System.out.println("Starty: "+starty);
				System.out.println("worst: "+task.worstComputationTimeLeft());
				System.out.println("completion: "+task.getCompletion());
				
				g.fillRect(startx, starty+2, Math.min (getWidth()-startx-pixelsForText,(int)(task.worstComputationTimeLeft()/1000*pixelsPerSecond)), (int)((1.0-task.getCompletion())*height)-4);
				g.drawString(Float.toString((int)Math.min(task.getCompletion()*100,100)), startx-30, starty+height/2+5);
			}
		}
		g.setColor(Color.black);
		g.drawLine(getWidth()-pixelsForText, 6, getWidth()-pixelsForText, getHeight()-6);

		Font font = new Font("Arial", Font.BOLD, 20);
		g.setFont(font);
		g.drawString("Statistiques", getWidth()-pixelsForText/2-55, 30);

		font = new Font("Arial", Font.PLAIN, 15);
		g.setFont(font);
		g.drawString(sim.getSchedulerType(), getWidth()-pixelsForText+15, 50);

		font = new Font("Arial", Font.PLAIN, 15);
		g.setFont(font);
		g.drawString("Energy used:", getWidth()-pixelsForText+15, 70);

		font = new Font("Arial", Font.PLAIN, 35);
		g.setFont(font);
		g.drawString(String.valueOf(sim.getEnergyUsed()), getWidth()-pixelsForText+60, 130);

		g.setColor(Color.red);
		g.drawLine(presentTimeLine, 6, presentTimeLine, getHeight()-6);
	}

	public Simulation getSimulation()
	{
		return sim;
	}
}
