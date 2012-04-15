import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * GUI allowing the user to have an input on the simulation.
 * Functionalities :
 * - Speed up/down (max : 2x normal speed, min : stopped)
 * - create dump files : a file describing the system of Tasks, another describing the scheduling of the user.
 * - Display current speed and current time in the simulations
 * - Display the progression of the simulations (progress bar)
 * - Once the simulations are finished, allow the user to move across the progress bar to observe the whole simulation
 * @see JPanel
 * @see ActionListener
 * @see ChangeListener
 * @author Mayeur Bernard
 * @version 2012.04.14
 */
class ControlPanel extends JPanel implements ActionListener, ChangeListener{

    protected static final float maxSpeed = 2;
    protected JButton moreSpeed = new JButton("+");
    protected JButton lessSpeed = new JButton("-");
    protected JButton makeDump = new JButton("Dump");
    protected Panel father;
    JProgressBar progressBar = new JProgressBar(0, Main.TIME_INTERVAL);
    JSlider timeSlide = new JSlider(JSlider.HORIZONTAL,-Main.TIME_INTERVAL/2,0, 0);

    /**
     * Tell the ControlPanel that progress has been made in the simulations.
     * If max time is reached, the time slider is displayed.
     * @param newtime the updated current time in the simulations
     */
    public void setProgress(int newTime)
    {
        progressBar.setValue(newTime);
        if (newTime>=Main.TIME_INTERVAL)
        {
            progressBar.setVisible(false);
            timeSlide.setVisible(true);
            father.repaint();
        }
    }

    public ControlPanel(Panel father)
    {
        this.father=father;

        this.setVisible(true);
        moreSpeed.setLocation(this.getWidth()-150, this.getHeight()/2);
        lessSpeed.setLocation(this.getWidth()-130, this.getHeight()/2);

        moreSpeed.setActionCommand("speed+");
        moreSpeed.setVisible(true);

        lessSpeed.setActionCommand("speed-");
        lessSpeed.setVisible(true);

	makeDump.setActionCommand("dump");
	makeDump.setVisible(true);

        moreSpeed.addActionListener(this);
        lessSpeed.addActionListener(this);
	makeDump.addActionListener(this);

        progressBar.setValue(0);

        timeSlide.setMajorTickSpacing(100);
        timeSlide.setVisible(false);
        timeSlide.addChangeListener(this);

        this.add(timeSlide);
        this.add(progressBar);
        this.add(lessSpeed);
        this.add(moreSpeed);
	this.add(makeDump);
    }

    @Override
	public void paintComponent(Graphics g){
	super.paintComponent(g);
         g.drawString("Speed: "+father.getTimeFactor(),this.getWidth()-100,this.getHeight()/2);
	 g.drawString("CurrentTime: " + father.getCurrentTime(), this.getWidth() - 300, this.getHeight()/2 + 10);

    }

    @Override
    public void actionPerformed(ActionEvent e)
    {
        if ("speed+".equals(e.getActionCommand()))
        {
            if (father.getTimeFactor()>=maxSpeed-0.1)
            {
                father.setTimeFactor(maxSpeed);
                moreSpeed.setEnabled(false);
            }
            else
                father.setTimeFactor(father.getTimeFactor()+(float)0.1);
            lessSpeed.setEnabled(true);
        }
        else if ("speed-".equals(e.getActionCommand()))
        {
            if (father.getTimeFactor()<=0.1)
            {
                father.setTimeFactor(0);
                lessSpeed.setEnabled(false);
            }
            else
                father.setTimeFactor(father.getTimeFactor()-(float)0.1);
            moreSpeed.setEnabled(true);
        }
	else if ("dump".equals(e.getActionCommand()))
	{
	    Date t = new Date();
	    long nowTime = t.getTime();
	    try {
		BufferedWriter out = new BufferedWriter(new FileWriter(Long.toString(nowTime) + "_batch.txt"));
		out.write(father.printTaskBatch());
		out.close();
		out = new BufferedWriter(new FileWriter(Long.toString(nowTime) + "_scheduling.txt"));
		out.write(father.printHumanScheduling());
		out.close();

	    } catch (IOException ex) {
		ex.printStackTrace();
	    }
	}
        father.repaint();
    }

    @Override
    public void stateChanged(ChangeEvent e)
    {
        JSlider source = (JSlider)e.getSource();
        if (!source.getValueIsAdjusting())
        {
            father.showTime((int)source.getValue());
            father.repaint();
        }
    }
}
