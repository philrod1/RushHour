package rushhour.view;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JPanel;

import rushhour.tools.MouseStuff;
import rushhour.tools.Tools;


public class GridPanel extends JPanel {

	private static final long serialVersionUID = 408883625275477255L;
	private List<VehiclePanel> vehicles;
	private int[][] state;
	private Image bg;
//	private MouseStuff ms;
	
	public GridPanel (int[][] state, MouseStuff ms) {
		super();
//		this.ms = ms;
		this.addMouseListener(ms);
		this.addMouseMotionListener(ms);
		update(state);

		this.bg = Tools.instance.loadImage("grid.png").getImage();
		
//		setOpaque(false);
		setLayout(null);
	}
	
	public void addMouseStuff (MouseStuff ms) {
		addMouseListener(ms);
		addMouseMotionListener(ms);
	}

	@Override
	public void paint(Graphics g) {
		super.paint(g);
	}
	
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.drawImage(bg, 0, 0, getWidth(), getHeight(), null);

	}
	
	public VehiclePanel getVehicleAt(Point p) {
		for(VehiclePanel vp : vehicles) {
			
			if(vp.containsPoint(p)) {
				return vp;
			}
		}
		return null;
	}
	
	public BufferedImage getBufferedImage() {
	    int width = 430;
	    int height = 430;
	    // Create a buffered image in which to draw
	    BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
	    Graphics2D g = bufferedImage.createGraphics();
	    paintComponent(g);
	    for(VehiclePanel v : vehicles) {
	    	BufferedImage bi = new BufferedImage(v.getWidth(), v.getHeight(), BufferedImage.TYPE_INT_RGB);
		    Graphics2D g2 = bi.createGraphics();
	    	v.paintComponent(g2);
	    	g.drawImage(bi, null, v.getBounds().x, v.getBounds().y);
	    }
	    g.dispose();
	    return bufferedImage;
	}
	
	public VehiclePanel getVehicle(int vid) {
		for(VehiclePanel vp : vehicles) {
			if (vp.id == vid) return vp;
		}
		return null;
	}
	
	public void update(int[][] state) {
		this.state = state;
		print();
		vehicles = new LinkedList<VehiclePanel>();
		for(int i = 1 ; i < 20 ; i++) {
			
			Color colour = Tools.colour(i);
			Point origin = null, end = null;
			
			boolean found = false;
			for (int y = 0 ; y < 6 ; y++) {
				for (int x = 0 ; x < 6 ; x++) {
					if (state[x][y] == i) {
						origin = new Point(x,y);
						found = true;
						break;
					}
				}
				if (found) break;
			}
			
			found = false;
			for (int y = 5 ; y >= 0 ; y--) {
				for (int x = 5 ; x >= 0 ; x--) {
					if (state[x][y] == i) {
						end = new Point(x,y);
						found = true;
						break;
					}
				}
				if (found) break;
			}
			if (origin != null && end != null) {
				if (i==1) {
					colour = Color.red;
					vehicles.add(new RedCarPanel(colour, origin));
				} else {
					int length = Math.max(end.x-origin.x, end.y-origin.y);
					int rotate = (origin.x < end.x) ? 90 : 0;
					if (length == 1) vehicles.add(new CarPanel(i, colour, origin, rotate));
					else vehicles.add(new TruckPanel(i, colour, origin, rotate));
				}
			}
		}
		removeAll();
		for(VehiclePanel vp : vehicles) {
			vp.resize(Tools.WIDTH, Tools.HEIGHT);
			add(vp);
		}
		invalidate();
		repaint();
		validate();
	}
	
	public void print() {
		for (int y = 0; y < 6; y++) {
			for (int x = 0; x < 6; x++) {
				String c;
				if(state[x][y] > 0) {
					if(state[x][y] < 10) {
						c = "" + state[x][y];
					} else {
						c = "" + (char)(state[x][y]+55);
					}
				} else {
					c = ""+(char)183;
				}
				System.out.print(c + " ");
			}
			System.out.println();
		}
		System.out.println();
	}
	
	public void reset() {
		removeAll();
		vehicles = new LinkedList<VehiclePanel>();
//		this.vehicles = vehicles;
//		for(VehiclePanel vp : vehicles) {
//			add(vp);
//		}
	}
	
}
