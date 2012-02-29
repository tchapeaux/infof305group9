
import java.awt.Color;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JPanel;


public class GeneratorPanel extends JPanel implements ActionListener {

    protected JButton randomGen = new JButton("Generate Random batch");
    protected JButton fileGen = new JButton("Generate batch from file");
    protected Panel father;

    GeneratorPanel(Panel father)
    {
        this.setBackground(Color.white);
        this.father=father;
        this.setLayout(new GridLayout(10,1));

        randomGen.setActionCommand("random");
        randomGen.setVisible(true);
        randomGen.addActionListener(this);
        this.add(randomGen);

        fileGen.setActionCommand("fromFile");
        fileGen.setVisible(true);
        fileGen.addActionListener(this);
        this.add(fileGen);
    }

    @Override
	public void paintComponent(Graphics g){
        g.drawString("Generation des taches à ordonnancer", this.getWidth()/2-25, 10);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if ("random".equals(e.getActionCommand()))
            father.switchView();
        if ("fromFile".equals(e.getActionCommand()))
	{
	    father.generateBatchFromFile();
	    father.switchView();
	}
    }
}
