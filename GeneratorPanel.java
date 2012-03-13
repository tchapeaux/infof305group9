
import java.awt.Checkbox;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;
import java.util.Stack;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;


public class GeneratorPanel extends JPanel implements ActionListener,ChangeListener {

    protected JButton randomGen = new JButton("Generate Random batch");
    protected JButton fileGen = new JButton("Generate batch from file");
    protected JButton confirm = new JButton("Confirm");
    protected Panel father;
    protected Checkbox SmallestPath = new Checkbox("Smallest Path Scheduler", null, true);
    protected Checkbox SingleFreq = new Checkbox("Single Frequency Scheduler", null, true);
    protected Checkbox DumbSched = new Checkbox("Dumb Scheduler", null, true);
    protected Checkbox HumanSched = new Checkbox("Human Scheduler", null, false);

    protected LinkedList<JFormattedTextField> CPUSpeed;
    protected LinkedList<JSlider> timeInterval;

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
        this.add(confirm);

        confirm.setBounds(1200 - 350 + insets.left, 800 - 75 + insets.top,
             300, 25);

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
                drawTasks(father.getTasks(),g);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if ("random".equals(e.getActionCommand()))
        {
	    father.generateRandomBatch();
	    //father.generateRandomFifoBatch();
            createProgressBar();
            this.repaint();
        }
        else if ("fromFile".equals(e.getActionCommand()))
	{
	    father.generateBatchFromFile();
            createProgressBar();
            this.repaint();
	}
        else if ("confirm".equals(e.getActionCommand()))
        {
            Stack pushMe = new Stack();
            if (SmallestPath.getState()) pushMe.push(new SmallestPathScheduler());
            if (SingleFreq.getState()) pushMe.push(new SingleFrequencyScheduler());
            if (DumbSched.getState()) pushMe.push(new DumbInitialScheduler());
            if (HumanSched.getState()) pushMe.push(new HumanScheduler().initialize(new Point2DFloatList()));

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
    }

    private void drawTasks(Task[] tasks, Graphics g) {
        float maxTime = Main.TIME_INTERVAL;
        this.getWidth();
        for (int i=0; i<tasks.length; i++)
        {
            g.drawRect((int)(tasks[i].startTime/maxTime*this.getWidth()),
                       250+20+31*i,
                       (int)((tasks[i].endTime-tasks[i].startTime)/maxTime*this.getWidth()),
                       30);
                    /*tasks[i].endTime
                            tasks[i].wcet*instantProcSpeed*/

        }
    }

    private void createProgressBar() {
        //CPUSpeed = new LinkedList<JSpinner>();

        CPUSpeed = new LinkedList<JFormattedTextField>();

        timeInterval = new LinkedList<JSlider>();
        CPUSpeed.add(new JFormattedTextField(1.0));
        timeInterval.add(new JSlider());

        CPUSpeed.getLast().setValue(1.0);
        timeInterval.getLast().setMajorTickSpacing(5);

        CPUSpeed.getLast().setColumns(10);
        //CPUSpeed.getLast().addChangeListener(this);
        timeInterval.getLast().addChangeListener(this);

        Insets insets = this.getInsets();

        this.add(CPUSpeed.getLast());
        this.add(timeInterval.getLast());

        CPUSpeed.getLast().setBounds(this.getWidth()-200 + insets.left,
                    250+20+31*father.getTasks().length + 30* CPUSpeed.size() + insets.top,
                    100,
                    25);
        timeInterval.getLast().setBounds(100 + insets.left,
                    250+20+31*father.getTasks().length + 30* CPUSpeed.size() + insets.top,
                    this.getWidth()-300,
                    25);

    }


    @Override
    public void stateChanged(ChangeEvent e)
    {
        JSlider source = (JSlider)e.getSource();
        if (!source.getValueIsAdjusting())
        {
            addSlider();
            father.repaint();
        }
    }

    private void addSlider() {
        throw new UnsupportedOperationException("Not yet implemented");
    }

}
