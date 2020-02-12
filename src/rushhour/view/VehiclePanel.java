package rushhour.view;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;

import javax.swing.JPanel;

import rushhour.tools.Tools;

public class VehiclePanel extends JPanel {

	private static final long serialVersionUID = -5354185283853430638L;
	private Point position;
	private Color colour;
	protected Image image;
	protected int gridWidth, gridHeight;
	private Point gridLocation;
	private Point offset;
	public final int id;
	public boolean canSelect;
	private static boolean isMoving;
	private static int animationDelay = 2;
	
	public VehiclePanel (int id, Color colour, int rotation) {
		super();
		this.id = id;
		position = new Point(0,0);
		setGridLocation(new Point(0,0));
		this.colour = colour;
		isMoving = false;
		//setLayout(null);
		//this.setBackground(colour);
		//setOpaque(false);
	}
	
	public void moveTo (Point p) {
		position = new Point(Math.max(0, p.x),Math.max(0, p.y));
		setLocation(position);
//		invalidate();
//		repaint();
//		validate();
	}
	
	public void moveTo (int x, int y) {
		moveTo(new Point(x,y));
	}
	
	@Override
	public void paintComponent(Graphics g) {
		g.setColor(colour);
		g.fillRect(1, 1, getWidth(), getHeight());
		g.drawImage(image, 0, 0, getWidth(), getHeight(), null);
		//super.paint(g);
	}
	
	public void resize(double w, double h) {
		try {
			moveTo((int)(gridLocation.x*w), (int)(gridLocation.y*h));
			setSize((int)(w*gridWidth), (int)(h*gridHeight));
		} catch (Exception e) {
			
		}
	}

	protected void setGridLocation(Point gridLocation) {
		this.gridLocation = gridLocation;
	}
	
	protected void setGridLocation(int x, int y) {
		setGridLocation(new Point(x,y));
	}

	public Point getGridLocation() {
		return gridLocation;
	}

	public void setOffset(Point point) {
		offset = new Point(getLocation().x - point.x,  getLocation().y - point.y);
	}
	
	public void setMousePoint(Point point) {
		int w = getWidth();
		int h = getHeight();
		if (w>h) {
			moveTo(point.x - offset.x, getLocation().y);
		} else {
			moveTo(getLocation().x, point.y - offset.y);
		}
	}
	
	public Point getMousePoint(Point point) {
		int w = getWidth();
		int h = getHeight();
		if (w>h) {
			return new Point(point.x - offset.x, getLocation().y);
		} else {
			return new Point(getLocation().x, point.y - offset.y);
		}
	}

	public boolean containsPoint(Point p) {
		Point l = getLocation();
		return p.x > l.x && p.y > l.y && p.x < (l.x+getWidth()) && p.y <(l.y+getHeight());
	}
	
	public Point getSnapPoint() {
		offset = new Point(0,0);
		double gridx = getLocation().getX()/Tools.WIDTH;
		double gridy = getLocation().getY()/Tools.HEIGHT;
	//	System.out.println(getLocation().x + " : " + getLocation().y);
		int x, y;
		if (gridx % 1 < 0.5) {
			x = (int)(gridx);
		} else {
			x = (int)(gridx+1);
		}
		if (gridy % 1 < 0.5) {
			y = (int)(gridy);
		} else {
			y = (int)(gridy+1);
		}
		canSelect = true;
		return new Point(x,y);
	}
	
	public void glideTo(final Point newPoint) {
		isMoving = true;
		int w = getWidth();
		int h = getHeight();
		int x = (int) (newPoint.x * Tools.WIDTH);
		int y = (int) (newPoint.y * Tools.HEIGHT);

		if (w>h) {
			int pixels = x - getLocation().x;
			if(pixels > 0) {
				for(int i = 0 ; i < pixels ; i++) {
					moveTo(getLocation().x+1, getLocation().y);
					animationDelay = Math.max(4,400/(((pixels-i)+1)*3));
					Tools.wait(animationDelay);
				}
			} else {
				for(int i = 0 ; i > pixels ; i--) {
					moveTo(getLocation().x -1, getLocation().y);
					animationDelay = Math.max(4,400/((((-pixels)+i)+1)*3));
					Tools.wait(animationDelay);
				}
			}
		} else {
			int pixels = y - getLocation().y;
			if(pixels > 0) {
				for(int i = 0 ; i < pixels ; i++) {
					moveTo(getLocation().x, getLocation().y+1);
					animationDelay = Math.max(4,400/(((pixels-i)+1)*3));
					Tools.wait(animationDelay);
				}
			} else {
				for(int i = 0 ; i > pixels ; i--) {
					moveTo(getLocation().x, getLocation().y-1);
					animationDelay = Math.max(4,400/((((-pixels)+i)+1)*3));
					Tools.wait(animationDelay);
				}
			}
		}
		isMoving = false;
	}

	public int getId() {
		return id;
	}
	
	public int getGridWidth() {
		return gridWidth;
	}
	
	public int getGridHeight() {
		return gridHeight;
	}

	public Point getOffset() {
		return offset;
	}
	
	public boolean isMoving() {
		return isMoving;
	}

	public static int getAnimationSpeed() {
		return animationDelay;
	}

	public static void setAnimationSpeed(int animationSpeed) {
		VehiclePanel.animationDelay = animationSpeed;
	}
}
