import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.util.Date;
import javax.swing.JFrame;
import javax.swing.JPanel;

public final class Panel extends JFrame
{

    protected JPanel container = new JPanel();
    protected JPanel controlContainer = new JPanel();
    protected static final long serialVersionUID = 1L;

    protected Simulation[] listSimulation;
    protected ControlPannel control = new ControlPannel(this);

    protected float timeFactor = 0.5F;
    public float getTimeFactor() {return timeFactor;}
    public void setTimeFactor(float newValue) {timeFactor = newValue;}

    public float getCurrentTime()
    {
	return listSimulation[0].getCurrentTime();
    }

    public Panel(int width, int height, Simulation[] listSimulations)
    {

        listSimulation=listSimulations;

        this.setTitle("Task Scheduler Simulation");
        this.setSize(width, height);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLocationRelativeTo(null);

        container.setBackground(Color.white);
        controlContainer.setBackground(Color.white);

        container.setLayout(new GridLayout(Main.NUMBER_OF_SIMS,1));

        for (int i=0; i<listSimulations.length; i++)
        {
            SimulationPanel tmp = new SimulationPanel(listSimulations[i]);
            tmp.setVisible(true);
            container.add(tmp);
        }
        container.setPreferredSize(new Dimension(this.getWidth()-20, this.getHeight()-100));
        control.setPreferredSize(new Dimension(this.getWidth()-20, 50));

        controlContainer.add(container, BorderLayout.PAGE_START);
        controlContainer.add(control, BorderLayout.PAGE_END);

        this.setContentPane(controlContainer);
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
				    allSimulationsAreDone = true;
				// !!! this suppose that all simulations are going at the same speed
			}
			container.repaint();
			try {
				Thread.sleep(20);


			} catch (InterruptedException e) {
				e.printStackTrace();
			}
                        control.setProgress((int)listSimulation[0].getCurrentTime());
		}
    }

    void showTime(int i) {
        for (Simulation sim: listSimulation)
            sim.showTime(i);
    }
}
