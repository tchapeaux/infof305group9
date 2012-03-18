
import java.awt.Checkbox;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Stack;
import javax.swing.JButton;
import javax.swing.JFormattedTextField;
import javax.swing.JPanel;
import javax.swing.JSlider;
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
            {
                drawTasks(father.getTasks(),g);
                drawTimeSeparation(father.getTasks().length,g);
                g.drawString("Start speed:", this.getWidth()-148, 287 + 31*father.getTasks().length);
            }
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
            if (HumanSched.getState())
            {
                Point2DFloatList temp = new Point2DFloatList();
                temp.add(new Point2DFloat(Float.parseFloat(startSpeed.getText())/100,0));

                System.out.println(Float.parseFloat(startSpeed.getText())/100+" 0");
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
    }

    private void drawTasks(Task[] tasks, Graphics g) {
        float maxTime = Main.TIME_INTERVAL;
        this.getWidth();
        int start= Main.TIME_INTERVAL;
        float taskStart [] = new float [tasks.length];
        float taskWCET [] = new float [tasks.length];
        int startingTask=0;
        for (int i=0; i< tasks.length; i++)
        {
            if (Math.min(start, (int)tasks[i].startTime) != start)
            {
                start = Math.min(start, (int)tasks[i].startTime);
                startingTask =i;
            }
            taskStart [i] = tasks[i].startTime;
            taskWCET[i]=tasks[i].wcet;
        }
        
        float newtSeparator=Main.TIME_INTERVAL;
        int numberOfTheTask=99;
        int numberOfTheSlider=99;
        int numberOfBreakingTask=99;
        for (int i=0; i< taskWCET.length; i++)
        {
            if (taskWCET[i]>start)
            {
                newtSeparator = Math.min(newtSeparator, taskWCET[i]);
                numberOfTheTask=i;                                              // we are gonna finish this work
            }
        }
        for (int i=0; i< timeInterval.size(); i++)
        {
            if (timeInterval.get(i).getValue()>start)
            {
                newtSeparator = Math.min(newtSeparator, timeInterval.get(i).getValue());
                numberOfTheTask=99;
                numberOfTheSlider=i;                                            // Slider limit progression
            }
        }
        for (int i=0; i< startingTask; i++)
        {
            if (taskStart[i]>start && taskStart[i] < newtSeparator)
            {
                newtSeparator = taskStart[i];
                numberOfTheTask=99;
                numberOfBreakingTask=1;
                numberOfTheTask=99;                                              // another task begin before end of current
            }
        }
        
        for (int i=0; i<tasks.length; i++)
        {
            g.setColor(Color.blue);
            
            g.fillRect((int)(tasks[i].startTime*this.getWidth()/maxTime),
                       250+21+31*i,
                       (int)((tasks[i].wcet)/maxTime*this.getWidth()),
                       29);
            g.setColor(Color.black);
            g.drawRect((int)(tasks[i].startTime*this.getWidth()/maxTime),
                       250+20+31*i,
                       (int)((tasks[i].endTime-tasks[i].startTime)/maxTime*this.getWidth()),
                       30);
                    /*tasks[i].endTime
                            tasks[i].wcet*instantProcSpeed*/

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
            father.repaint();
        }
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
}