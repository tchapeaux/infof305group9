
import java.awt.Checkbox;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * Panel used for configuration of the simulations.
 * Functionnalities :
 * - Allow the user to generate Systems of Tasks randomly or from a custom file.
 * - Allow the user to choose which algorithms will be given to each simulation
 * - Allow the user to try and schedule the system of tasks him/herself, or with a custom file.
 * @see JPanel
 * @see ActionListener
 * @see ChangeListener
 * @author bernard
 */
public class GeneratorPanel extends JPanel implements ActionListener,ChangeListener {

    protected JButton randomGen = new JButton("Generate Random batch");
    protected JButton fileGen = new JButton("Generate batch from file");
    protected JButton confirm = new JButton("Confirm");
    protected JButton repaint = new JButton("Repaint");
    protected JButton fileSche = new JButton("Generate scheduling from file");
    protected Panel father;
    protected Checkbox SmallestPath = new Checkbox("Smallest Path Scheduler", null, true);
    protected Checkbox SingleFreq = new Checkbox("Single Frequency Scheduler", null, true);
    protected Checkbox DumbSched = new Checkbox("Dumb Scheduler", null, true);
    protected Checkbox HumanSched = new Checkbox("Human Scheduler", null, false);

    protected LinkedList<JFormattedTextField> CPUSpeed;
    protected LinkedList<JSlider> timeInterval;
    protected JFormattedTextField startSpeed;

    GeneratorPanel(Panel father)
    {
        this.father=father;
        this.setBackground(Color.white);
        this.setLayout(null);
        Insets insets = this.getInsets();

        randomGen.setActionCommand("random");
        randomGen.addActionListener(this);
        this.add(randomGen);

        randomGen.setBounds(1200/2 -325 + insets.left, 10 + insets.top,
             300, 25);


        fileGen.setActionCommand("fromFile");
        fileGen.addActionListener(this);
        this.add(fileGen);

        fileGen.setBounds(1200/2 + 25 + insets.left, 10 + insets.top,
             300, 25);

        confirm.setActionCommand("confirm");
        confirm.addActionListener(this);

        repaint.setActionCommand("repaint");
        repaint.addActionListener(this);

	fileSche.setActionCommand("scheFromFile");
	fileSche.addActionListener(this);

        SmallestPath.setBounds(250 + insets.left, 120 + insets.top,
             300, 25);
        SingleFreq.setBounds(250 + insets.left, 150 + insets.top,
             300, 25);
        HumanSched.setBounds(250 + insets.left, 180 + insets.top,
             300, 25);
        DumbSched.setBounds(550 + insets.left, 120 + insets.top,
             300, 25);
        this.add(SmallestPath);
        this.add(SingleFreq);
        this.add(HumanSched);
        this.add(DumbSched);

        randomGen.setVisible(true);
        fileGen.setVisible(true);
        SmallestPath.setVisible(true);
        confirm.setVisible(true);

    }

    @Override
	public void paintComponent(Graphics g){
            g.setColor(Color.white);
            g.fillRect(0, 0, getWidth(), getHeight());
            g.setColor(Color.black);
            g.drawString("Choose your parameters:", this.getWidth()/2-80, 50);
            g.drawString("Choose "+Main.NUMBER_OF_SIMS+" algorithms:", 200, 100);
            g.drawString("If you choose Human Scheduler, schedule the tasks:", 200, 250);
            if (father.getTasks() != null)
            {
                drawTasks(father.getTasks(),g);
                drawTimeSeparation(father.getTasks().length,g);
                //g.drawString("Start speed:", this.getWidth()-148, 287 + 31*father.getTasks().length);
            }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if ("random".equals(e.getActionCommand()))
        {
	    father.generateRandomBatch();
	    //father.generateRandomFifoBatch();
            createProgressBar();
            this.add(confirm);
            this.add(repaint);
	    this.add(fileSche);
            Insets insets = this.getInsets();
            confirm.setBounds(this.getWidth() - 350 + insets.left, this.getHeight() - 30 + insets.top, 300, 25);
            repaint.setBounds(this.getWidth() - 700 + insets.left, this.getHeight() - 30 + insets.top, 300, 25);
	    fileSche.setBounds(this.getWidth() - 1050 + insets.left, this.getHeight() - 30 + insets.top, 300,25);
            this.repaint();

        }
        else if ("fromFile".equals(e.getActionCommand()))
	{
	    father.generateBatchFromFile();
            createProgressBar();
            this.add(confirm);
            this.add(repaint);
            this.add(fileSche);
            Insets insets = this.getInsets();
            confirm.setBounds(this.getWidth() - 350 + insets.left, this.getHeight() - 30 + insets.top, 300, 25);
            repaint.setBounds(this.getWidth() - 700 + insets.left, this.getHeight() - 30 + insets.top, 300, 25);
            fileSche.setBounds(this.getWidth() - 1050 + insets.left, this.getHeight() - 30 + insets.top, 300,25);
            this.repaint();
	}
        else if ("confirm".equals(e.getActionCommand()))
        {
            Stack pushMe = new Stack();
            if (SmallestPath.getState()) pushMe.push(new SmallestPathScheduler());
            if (SingleFreq.getState()) pushMe.push(new SingleFrequencyScheduler());
            if (DumbSched.getState()) pushMe.push(new DumbInitialScheduler());
            if (HumanSched.getState())
            {
                Point2DFloatList temp = new Point2DFloatList();
                temp.add(new Point2DFloat(Float.parseFloat(startSpeed.getText())/100,0));

                for (int i= 0 ; i< CPUSpeed.size(); i++)
                {
                    float temp_v = Float.parseFloat(CPUSpeed.get(i).getText())/100;
                    float temp_t = timeInterval.get(i).getValue();
                    temp.add(new Point2DFloat(temp_v,temp_t));

                }
                HumanScheduler tempSched= new HumanScheduler();
                tempSched.initialize(temp);
		pushMe.push(tempSched);

            }

            if (pushMe.size() == Main.NUMBER_OF_SIMS)
            {
                Simulation[] simulations = new Simulation[Main.NUMBER_OF_SIMS];

                simulations[2] = new Simulation((InitialScheduler)pushMe.pop(), new DumbInlineScheduler(),  father.getTasks());
                simulations[1] = new Simulation((InitialScheduler)pushMe.pop(), new DumbInlineScheduler(),  father.getTasks());
                simulations[0] = new Simulation((InitialScheduler)pushMe.pop(), new DumbInlineScheduler(),  father.getTasks());
                father.setSimulations(simulations);
                father.switchView();
            }
        }
	else if ("repaint".equals(e.getActionCommand()))
        {
            father.repaint();
        }
	else if ("scheFromFile".equals(e.getActionCommand()))
	{
        JFileChooser chooseFile = new JFileChooser();
        int returnVal = chooseFile.showOpenDialog(this);

        if (returnVal == JFileChooser.APPROVE_OPTION)
	{
            File file = chooseFile.getSelectedFile();
	    Point2DFloatList pl = new Point2DFloatList();
	    pl.fillWithFile(file.getPath());
            if (pl.size()-1> CPUSpeed.size())
                ; // Error handling
            else
            {
                startSpeed.setText(String.valueOf(pl.get(0).getX()*100));
                for (int i=1; i<pl.size(); i++)
                {
                    CPUSpeed.get(i-1).setText(String.valueOf(pl.get(i).getX()*100));
                    timeInterval.get(i-1).setValue((int)(pl.get(i).getY()));
                }
            }
        }




	}
    }

    private void drawTasks(Task[] tasks, Graphics g) {
        float maxTime = Main.TIME_INTERVAL;
        {
            Point2DFloatList tempSpeeds = new Point2DFloatList();
            tempSpeeds.add(new Point2DFloat(Float.parseFloat(startSpeed.getText())/100,0));

            for (int i= 0 ; i< CPUSpeed.size(); i++)
            {
                float temp_v = Float.parseFloat(CPUSpeed.get(i).getText())/100;
                float temp_t = timeInterval.get(i).getValue();
                tempSpeeds.add(new Point2DFloat(temp_v,temp_t));
            }
            HumanScheduler tempSched= new HumanScheduler();
            tempSched.initialize(tempSpeeds);
	    Simulation tempSim = new Simulation (tempSched, new DumbInlineScheduler(), tasks);
            while (!tempSim.isDone())
                tempSim.compute(10);
            for (int j=0; j<tempSim.getTaskBatch().length; j++) //Task task: tempSim.getTaskBatch())
            {
                g.setColor(Main.colorList[j]);
                Task task = tempSim.getTaskBatch()[j];
                List<float[]> evolution=task.getCompletionEvolution();
                for (int i=1; i<evolution.size(); i++)
                    g.drawLine((int) (evolution.get(i-1)[0]*this.getWidth()/Main.TIME_INTERVAL),
                            (int) (250+20+31*j+evolution.get(i-1)[1]*29),
                            (int) (evolution.get(i)[0]*this.getWidth()/Main.TIME_INTERVAL),
                            (int) (250+20+31*j+evolution.get(i)[1]*29));

            }
        }
        g.setColor(Color.black);
        for (int i=0; i<tasks.length; i++)
        {
            g.drawRect((int)(tasks[i].startTime*this.getWidth()/maxTime),
                       250+20+31*i,
                       (int)((tasks[i].endTime-tasks[i].startTime)/maxTime*this.getWidth()),
                       30);
        }
    }

    private void createProgressBar() {

        CPUSpeed = new LinkedList<JFormattedTextField>();

        timeInterval = new LinkedList<JSlider>();
        startSpeed = new JFormattedTextField ();
        startSpeed.setBounds(getWidth()-75, 250+20+31*father.getTasks().length, 50, 25);
        startSpeed.setValue(100.0);
        this.add(startSpeed);
        addSlider();
        while (timeInterval.getLast().getBounds().getY() < this.getHeight() - 80)
                addSlider();
    }


    @Override
    public void stateChanged(ChangeEvent e)
    {
        JSlider source = (JSlider)e.getSource();
        if (!source.getValueIsAdjusting())
        {
            boolean found=false;
            for (int i = 0 ; i< timeInterval.size(); i++)
            {
                JSlider aSlider=timeInterval.get(i);
                if (aSlider == source)
                    found = true;
                else if (! found)
                    aSlider.setValue(Math.min(aSlider.getValue(), source.getValue()));
                else
                    aSlider.setValue(Math.max(aSlider.getValue(), source.getValue()));

            }
        }
        father.repaint();
    }

    private void addSlider() {
        int lastVal=0;
        if (timeInterval.size() > 0)
            lastVal=timeInterval.getLast().getValue();
        CPUSpeed.add(new JFormattedTextField(1.0));
        timeInterval.add(new JSlider());

        timeInterval.getLast().setBackground(Color.WHITE);

        CPUSpeed.getLast().setValue(100.0);
        timeInterval.getLast().setMajorTickSpacing(5);
        timeInterval.getLast().setMinimum(0);
        timeInterval.getLast().setMaximum(Main.TIME_INTERVAL);
        timeInterval.getLast().setValue(lastVal);
        timeInterval.getLast().addChangeListener(this);

        Insets insets = this.getInsets();

        this.add(CPUSpeed.getLast());
        this.add(timeInterval.getLast());

        CPUSpeed.getLast().setBounds(this.getWidth()-75 + insets.left,
                    250+20+31*father.getTasks().length + 30* CPUSpeed.size() + insets.top,
                    50,
                    25);
        timeInterval.getLast().setBounds(insets.left,
                    250+20+31*father.getTasks().length + 30* CPUSpeed.size() + insets.top,
                    this.getWidth()-75,
                    25);
    }

    private void drawTimeSeparation(int numberOfTask, Graphics g) {
        float maxTime = Main.TIME_INTERVAL;
        g.setColor(Color.red);
        for (Iterator<JSlider> it = timeInterval.iterator(); it.hasNext();) {
            JSlider separat = it.next();
            g.drawLine((int)(separat.getValue()*this.getWidth()/maxTime), 270, (int)(separat.getValue()*this.getWidth()/maxTime), 270+ 31*numberOfTask);
        }
        g.setColor(Color.black);
    }

    private boolean done(float[] taskWCET, float[] taskStart) {
        boolean test = true;
        for (int i=0; test & i<taskWCET.length; i++)
            if (taskStart[i] <= Main.TIME_INTERVAL)
                test = false;
        return test;

    }
}




