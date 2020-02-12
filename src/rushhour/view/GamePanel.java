package rushhour.view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JTextField;

import rushhour.tools.MouseStuff;
import rushhour.tools.Tools;

public class GamePanel extends JPanel {

	private static final long serialVersionUID = 1860979716621182121L;
	private final Image bg;
	private final GridPanel gp;
	private final JTextField text;
	private final JTextField moves;
	private final JTextField count;
	
	public GamePanel (GridPanel gp, MouseStuff ms) {
		super();
		this.gp = gp;
		this.bg = Tools.instance.loadImage("background.png").getImage();
		setPreferredSize(new Dimension(bg.getWidth(null), bg.getHeight(null)));
		setLayout(null);
		
		text = new JTextField();
		text.setEditable(false);
		text.setBorder(BorderFactory.createEmptyBorder());
		Font font = new Font("Courier",1,12);
		text.setFont(font);
		
		moves = new JTextField("Moves:");
		moves.setEditable(false);
		moves.setBorder(BorderFactory.createEmptyBorder());
		Font font2 = new Font("Courier",1,12);
		moves.setFont(font2);
		moves.setBounds(410, 20, 40, 30);
		moves.setBackground(new Color(0xCACACA));
		moves.setHorizontalAlignment(JTextField.LEFT);
		this.add(moves);
		
		count = new JTextField("0");
		count.setEditable(false);
		count.setBorder(BorderFactory.createEmptyBorder());
		count.setFont(font2);
		count.setBounds(460, 20, 50, 30);
		count.setBackground(new Color(0xCACACA));
		count.setHorizontalAlignment(JTextField.LEFT);
		this.add(count);
		
		this.add(gp);
		gp.setBounds(48, 68, 432, 426);
		ControlPanel cp = new ControlPanel(ms);
		this.add(cp);
		cp.setBounds(20, 505, 488, 28);
		this.add(text);
		
		text.setBackground(new Color(0xCACACA));
		text.setBounds(50, 540, 420, 20);
		text.setHorizontalAlignment(JTextField.CENTER);
	}
	
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
//		Graphics2D g2 = (Graphics2D)g;
//		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
//		                        RenderingHints.VALUE_ANTIALIAS_ON);
//		g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
//				RenderingHints.VALUE_INTERPOLATION_BICUBIC);
		
		g.drawImage(bg, 0, 0, getWidth(), getHeight(), null);

	}
	
	public void update(int[][] state) {
		gp.update(state);
	}
	
	public void setMessage(String message) {
		text.setText(message);
	}

	public void setCount(int count) {
		this.count.setText(""+count);
	}
}
