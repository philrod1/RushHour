package rushhour.view;

import java.awt.Font;
import java.awt.GridLayout;
import java.awt.LayoutManager;
import java.awt.event.MouseListener;

import javax.swing.JButton;
import javax.swing.JPanel;

public class ControlPanel extends JPanel {

	private static final long serialVersionUID = 3773119900144845162L;
//	private final JButton[] buttons;
	
	public ControlPanel (MouseListener ml) {
		Font font = new Font("Courier",0,10);
		JButton[] buttons = { new JButton("Back"), 
					new JButton("Next"), 
					new JButton("Solve"), 
					new JButton("Play"),
					new JButton("Load"),
					new JButton("Reset"),
					new JButton("Setup")
		};
		setOpaque(false);
		LayoutManager layout = new GridLayout(1,7,5,5);
		setLayout(layout);
		for(JButton button : buttons) {
			add(button);
			button.addMouseListener(ml);
			button.setFont(font);
		}
	}
	
}
