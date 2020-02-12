package rushhour.view;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;

import rushhour.controller.RushHour;

public class GameFrame extends JFrame {

	private static final long serialVersionUID = 4208649157275353992L;
	
	public GameFrame (String title, RushHour controller) {
		super(title);
		setPreferredSize(new Dimension(800,600));
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		GridBagLayout layout = null;
//		GridBagConstraints c = new GridBagConstraints();
		JPanel main = new JPanel(layout);
		controller.getGamePanel().setBounds(0,0,525, 570);
		GameChooser chooser = new GameChooser(controller);
		chooser.setBounds(531,0,260,600);
		controller.setChooser(chooser);
//		chooser.setPreferredSize(new Dimension(100, 600));
//		c.fill = GridBagConstraints.BOTH;
//		c.weightx = 0.86;
//		c.weighty = 1.0;
//		c.gridx = 0;
//		c.gridy = 0;
//		main.add(controller.getGamePanel(), c);
//		c.fill = GridBagConstraints.BOTH;
//		c.weightx = 0.14;
//		c.weighty = 1.0;
//		c.gridx = 1;
//		c.gridy = 0;
//		main.add(chooser,c);
		main.add(controller.getGamePanel());
		main.add(chooser);
		getContentPane().add(main);
		pack();
		setVisible(true);
	}
}
