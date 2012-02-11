import java.util.*;
import java.lang.*;
import java.awt.*;
import javax.swing.*;

public class Pannel extends JFrame implements Runnable{
 
    protected JPanel container = new JPanel();

    public Pannel(int width, int height, Simulation[] listSimulations){
        
        this.setTitle("Task Scheduler Simulation");
        this.setSize(width, height);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLocationRelativeTo(null);
           
        container.setBackground(Color.white);
        container.setLayout(new GridLayout(3,1));

		for (int i=0; i<listSimulations.length; i++)
		{
			SimulationPanel tmp = new SimulationPanel(listSimulations[i]);
			tmp.setVisible(true);
			container.add(tmp);
		}
        
        
        this.setContentPane(container);
        this.setVisible(true);
    }

    public Pannel(Simulation[] listSimulations){
           
        this(800,600, listSimulations);
    }

    public void run(){
		while (true)
		{
			container.repaint();
			try {
				Thread.sleep(20);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
    }
}