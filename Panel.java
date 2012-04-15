import java.awt.*;
import java.io.File;
import java.util.Date;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 * Main Panel class during the simulation, which contains the different SimulationPanel and update them synchronously
 * @author Mayeur Bernard
 * @version 2012.04.14
 */

public final class Panel extends JFrame
{

    protected JPanel container;
    protected JPanel controlContainer;
    protected GeneratorPanel genContainer = new GeneratorPanel(this);

    protected static final long serialVersionUID = 1L;
    protected boolean wait = true;

    protected Simulation[] listSimulation;
    protected ControlPanel control = new ControlPanel(this);

    protected Task [] tasks;

    protected float timeFactor = 0.5F;
    public float getTimeFactor() {return timeFactor;}
    public void setTimeFactor(float newValue) {timeFactor = newValue;}

    public float getCurrentTime()
    {
        if (listSimulation != null)
            return listSimulation[0].getCurrentTime();
        else
            return 0;
    }

    public Panel(int width, int height)
    {
        this.setTitle("Task Scheduler Simulation");
        this.setSize(width, height);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLocationRelativeTo(null);
        this.setBackground(Color.white);
        this.setContentPane(genContainer);
        this.setVisible(true);
        this.run();
    }

    public Panel(){

        this(800,600);
    }

    public void run(){
                while (wait)
                {
                    try {
				Thread.sleep(100);


			} catch (InterruptedException e) {
				e.printStackTrace();
			}
                }
                Date t = new Date();
                Main.setStartTime(t.getTime());
                long lastTime = Main.getStartTime();
		long currentTime = lastTime;
		boolean allSimulationsAreDone = false;

		while (!allSimulationsAreDone)
		{
			t = new Date();
			lastTime = currentTime;
			currentTime = t.getTime();
			float interval = (currentTime - lastTime)*timeFactor;

			for (int i=0; i< listSimulation.length; i++)
			{
				listSimulation[i].compute(interval);
				if (listSimulation[i].isDone())
				    allSimulationsAreDone = true;
			}
			container.repaint();
			try {
				Thread.sleep(20);


			} catch (InterruptedException e) {
				e.printStackTrace();
			}
                        control.setProgress((int)listSimulation[0].getCurrentTime());
			control.repaint();
		}
    }

    public void showTime(int i) {
        for (Simulation sim: listSimulation)
            sim.showTime(i);
    }

    /**
     * Switch from GeneratorPanel to the Simulation screen (Simulation¨anems + ControlPanel)
     */
    public void switchView() {
        this.setVisible(false);
        if (this.getContentPane() == controlContainer)
        {
            this.setContentPane(genContainer);
            this.wait=true;
        }
        else if (this.getContentPane() == genContainer)
        {

            controlContainer = new JPanel();
            controlContainer.setBackground(Color.white);

            container = new JPanel();
            container.setBackground(Color.white);
            container.setLayout(new GridLayout(Main.NUMBER_OF_SIMS,1));

            for (int i=0; i<listSimulation.length; i++)
            {
                SimulationPanel tmp = new SimulationPanel(listSimulation[i]);
                tmp.setVisible(true);
                container.add(tmp);
            }
            container.setPreferredSize(new Dimension(this.getWidth()-20, this.getHeight()-100));
            control.setPreferredSize(new Dimension(this.getWidth()-20, 50));

            controlContainer.add(container, BorderLayout.PAGE_START);
            controlContainer.add(control, BorderLayout.PAGE_END);
            this.setContentPane(controlContainer);
            this.wait=false;

        }
        this.setVisible(true);
        this.repaint();

    }

    public String printTaskBatch()
    {
	String s = new String();
	s += Integer.toString(listSimulation[0].getTaskBatch().length) + "\n";
	for (Task t:listSimulation[0].getTaskBatch())
	{
	    s += t.print() + "\n";
	}
	return s;
    }


    public String printHumanScheduling()
    {
	for (Simulation simu:listSimulation)
	    if (simu.hasHumanInitialScheduler())
		return simu.getSpeeds().asString();

	return "No Human Scheduling";
    }
    
    public void generateBatchFromFile() {
        JFileChooser chooseFile = new JFileChooser();
        int returnVal = chooseFile.showOpenDialog(this);

        if (returnVal == JFileChooser.APPROVE_OPTION)
	{
            File file = chooseFile.getSelectedFile();
            tasks=Task.createBatchFromFile(file.getPath());
        }
    }

    void generateRandomBatch() {
            tasks = null;
	    tasks = Task.createRandomBatch(Main.NUMBER_OF_TASK, Main.TIME_INTERVAL);
    }
    
    Task[] getTasks() {
        return tasks;
    }

    void setSimulations(Simulation[] simulations) {
        listSimulation=simulations;
    }
}
