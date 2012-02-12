//import java.io.*;
import java.util.*;
import java.util.List;
//import java.lang.*;
import java.awt.*;
import javax.swing.*;

public class SimulationPanel extends JPanel {

	public static final int pixelsPerSecond = 200;
	public static final int pixelsForText=200;
	public static final int presentTimeLine=155;
    private static final long serialVersionUID = 1L;

	protected Simulation sim;
	public SimulationPanel(Simulation simu) {sim=simu;}
	public int i=0;
	public void paintComponent(Graphics g){
		g.drawRoundRect(5, 5, getWidth()-10, getHeight()-10, 20, 20);
		Date t = new Date();
		double t1 = (double)(t.getTime() - Main.getStartTime())/1000;
		int high = Math.min(50,(getHeight())/sim.getNumberOfTasks()-1);
		Task task;
		for (int i=0; (task=sim.getTask(i))!= null;i++)
		{

			//Draw task enter & deadline as rectangle
			{
				int startPos=presentTimeLine+(int)((-t1+task.getStartTime())*pixelsPerSecond);
				int endPos=startPos+(int)(task.getEndTime()-task.getStartTime())*pixelsPerSecond;
				int firstPix=Math.max(6,startPos);
				int lastPix=Math.min(getWidth()-pixelsForText,endPos);
				if (lastPix > 5 & firstPix < getWidth()-pixelsForText)
				{
					g.drawLine(firstPix,8+i*high,lastPix, 8+i*high);
					g.drawLine(firstPix,8+(i+1)*high,lastPix, 8+(i+1)*high);
				}
				if (Math.max(5,startPos) !=5)
				{
					g.drawLine(firstPix,8+i*high,firstPix, 8+(i+1)*high);
				}
				if (lastPix!=getWidth()-pixelsForText)
				{
					g.drawLine(lastPix,8+i*high,lastPix, 8+(i+1)*high);
				}
			}
			
			//draw Completion
			{
				List<float[]> evolution=task.getCompletionEvolution();
				for (int j=0; j<evolution.size(); j++)
				{
					int startx= (int) ((evolution.get(j)[0] - Main.getStartTime())/1000 +task.getStartTime())*pixelsPerSecond;
					int starty= (int) (8+i*high+high*evolution.get(j)[1]);
					g.drawLine(startx, starty, startx, starty);
				}
			}
			
			//draw Work
			{
				int startx=  (int) (Math.max(presentTimeLine,presentTimeLine+(int)((-t1+task.getStartTime())*pixelsPerSecond))); 
				int starty= (int) (8+i*high+high*task.getCompletion());
				g.fillRect(startx, starty+2, (int)task.worstComputationTimeLeft()*pixelsPerSecond, (int)(1.0-task.getCompletion())*high-4);
				g.drawString(String.valueOf(task.getCompletion()*100), startx-30, starty+high/2+5);
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
}