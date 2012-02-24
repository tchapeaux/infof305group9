//import java.util.*;
//import java.lang.*;
import java.awt.*;
import javax.swing.*;
import java.util.*;

public class Panel extends JFrame {

    protected JPanel container = new JPanel();
    protected static final long serialVersionUID = 1L;
	protected Simulation[] listSimulation;
	protected float timeFactor = 0.5F;
	public float getTimeFactor() {return timeFactor;}
	public void setTimeFactor(float newValue) {timeFactor = newValue;}

    public Panel(int width, int height, Simulation[] listSimulations){

		listSimulation=listSimulations;
        this.setTitle("Task Scheduler Simulation");
        this.setSize(width, height);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLocationRelativeTo(null);

        container.setBackground(Color.white);
        container.setLayout(new GridLayout(Main.NUMBER_OF_SIMS,1));

		for (int i=0; i<listSimulations.length; i++)
		{
			SimulationPanel tmp = new SimulationPanel(listSimulations[i]);
			tmp.setVisible(true);
			container.add(tmp);
		}


        this.setContentPane(container);
        this.setVisible(true);
        this.run();
    }

    public Panel(Simulation[] listSimulations){

        this(800,600, listSimulations);
    }

    public void run(){

		long lastTime = Main.getStartTime();
		long currentTime = lastTime;
		boolean allSimulationsAreDone = false;

		while (!allSimulationsAreDone)
		{
			Date t = new Date();
			lastTime = currentTime;
			currentTime = t.getTime();
			float interval = (currentTime - lastTime)*timeFactor;

			for (int i=0; i< listSimulation.length; i++)
			{
				listSimulation[i].compute(interval);
				if (listSimulation[i].isDone())
				    allSimulationsAreDone = true; // !!! this suppose that all simulations are going at the same speed
			}
			container.repaint();
			try {
				Thread.sleep(20);


			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
    }
}
