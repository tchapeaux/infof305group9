import java.io.*;
import java.util.*;
import java.lang.*;
import java.awt.*;
import javax.swing.*;

public class SimulationPanel extends JPanel {

	public static final int pixelsPerSecond = 50;
	public static final int pixelsForText=200;

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
			if (t1+(getWidth()-pixelsForText)/pixelsPerSecond-3 > task.getStartTime() & t1-3 < task.getEndTime())
			{
				int startPos=155+(int)((-t1+task.getStartTime())*pixelsPerSecond);
				int endPos=startPos+(int)(task.getEndTime()-task.getStartTime())*pixelsPerSecond;
				int firstPix=Math.max(6,startPos);
				int lastPix=Math.min(getWidth()-pixelsForText,endPos);
				g.drawLine(firstPix,8+i*high,lastPix, 8+i*high);
				g.drawLine(firstPix,8+(i+1)*high,lastPix, 8+(i+1)*high);
				
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
			
			//draw Work
			
		}
		g.setColor(Color.black);
		g.drawLine(getWidth()-pixelsForText, 6, getWidth()-pixelsForText, getHeight()-6);
		
		Font font = new Font("Arial", Font.BOLD, 20);
		g.setFont(font);          
		g.drawString("Statistiques", getWidth()-pixelsForText/2-55, 30);

		font = new Font("Arial", Font.PLAIN, 15);
		g.setFont(font);          
		g.drawString(sim.getSchedulerType(), getWidth()-pixelsForText+15, 50);
		
		g.setColor(Color.red);
		g.drawLine(155, 6, 155, getHeight()-6);
	}               
}