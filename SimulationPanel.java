import java.util.List;
import java.awt.*;
import javax.swing.*;

public class SimulationPanel extends JPanel {

	public static final int pixelsPerSecond = 100;
	public static final int pixelsForText=200;
	public static final int presentTimeLine=255;
        protected int getPresentTimeLine() {return (int)(presentTimeLine - sim.getRelativeShowedTime()/1000*pixelsPerSecond);}
    private static final long serialVersionUID = 1L;

	protected Simulation sim;
	public SimulationPanel(Simulation simu) {sim=simu;}
	public int i=0;
    @Override
	public void paintComponent(Graphics g){

		double t1 = sim.getCurrentTime();
		int height = Math.min(50,(getHeight())/sim.getNumberOfTasks()-6);
		Task task;
		for (int i=0; (task=sim.getTask(i))!= null;i++)
		{
			g.setColor(Color.black);
			//Draw task enter & deadline as rectangle
			{
				int startPos=getPresentTimeLine()+(int)((-t1+task.getStartTime())*pixelsPerSecond/1000);
				int endPos=startPos+(int)((task.getEndTime()-task.getStartTime())*pixelsPerSecond/1000);

				int firstPix=Math.max(6,startPos);
				int lastPix=Math.min(getWidth()-pixelsForText,endPos);
				if (lastPix > 5 & firstPix < getWidth()-pixelsForText)
				{
					g.drawLine(firstPix,8+i*height,lastPix, 8+i*height);
					g.drawLine(firstPix,8+(i+1)*height,lastPix, 8+(i+1)*height);
				}
				if (Math.max(5,startPos) !=5 & firstPix<lastPix)
				{
					g.drawLine(firstPix,8+i*height,firstPix, 8+(i+1)*height);
				}
				if (lastPix!=getWidth()-pixelsForText)
				{
					g.drawLine(lastPix,8+i*height,lastPix, 8+(i+1)*height);
				}
			}
			g.setColor(Main.getColor()[task.getId()]);

			//draw Completion
			{
				List<float[]> evolution=task.getCompletionEvolution();
				for (int j=1; j<evolution.size(); j++)
				{
					int startx= Math.max(5, (int) ((evolution.get(j-1)[0]-sim.getCurrentTime())/1000*pixelsPerSecond)+getPresentTimeLine());
					int endx= Math.max(5, (int) ((evolution.get(j)[0]-sim.getCurrentTime())/1000*pixelsPerSecond)+getPresentTimeLine());
					int starty= (int) (8+i*height+height*evolution.get(j-1)[1]);
					int endy= (int) (8+i*height+height*evolution.get(j)[1]);
					if (j==1)
						g.drawLine(endx, starty, endx, endy);
					else
					{
						g.drawLine(startx, starty, endx, endy);
						if (j==evolution.size()-1)
							g.drawLine(Math.max(5, endx), endy, (int)Math.max(5, Math.min((task.getEndTime()-t1)/1000*pixelsPerSecond+getPresentTimeLine(), getPresentTimeLine())), endy);
					}
				}
			}

			//draw Work
			{
				int startx= (int) (Math.max(getPresentTimeLine(),getPresentTimeLine()+(int)((-t1+task.getStartTime())/1000*pixelsPerSecond)));
				int starty= (int) (8+i*height+height*task.getCompletion());

				g.fillRect(startx, starty+2, Math.max(0,Math.min (getWidth()-startx-pixelsForText,(int)(task.worstComputationTimeLeft()/1000*pixelsPerSecond))), (int)((1.0-task.getCompletion())*(height-4)));
				
                                if (getWidth()-pixelsForText-5 > startx-35)
                                    g.drawString(Float.toString((int)Math.min(task.getCompletion()*100,100)), startx-35, 8+i*height+height/2+5);
			}
		}
		drawPannelText(g);
	}

	public Simulation getSimulation()
	{
		return sim;
	}

	public void drawPannelText(Graphics g){

		// SimulationPanel outline
		g.setColor(Color.black);
		g.drawRoundRect(5, 5, getWidth()-10, getHeight()-10, 20, 20);

		// Separator
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
		g.drawString(String.valueOf((int)sim.getEnergyUsed()), getWidth()-pixelsForText+60, 130);

		font = new Font("Arial", Font.PLAIN, 15);
		g.setFont(font);
		float CPUSpeed = 0;
		if(sim.isComputing())
		    CPUSpeed = sim.getCurrentSpeed();
		g.drawString("CPU Speed : " + Float.toString(CPUSpeed), getWidth()-pixelsForText+15, 180);
                if (getPresentTimeLine() <= this.getWidth()-pixelsForText | getPresentTimeLine() > 5)
                {
                    g.setColor(Color.red);
                    g.drawLine(getPresentTimeLine(), 6, getPresentTimeLine() , getHeight()-6);
                    System.out.println("CurrentTime:"+getPresentTimeLine());
                }
	}
}
