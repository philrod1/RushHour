package rushhour.model.move;

import java.awt.Point;

public class Move {
	
	public final int vid;
	private final Point from, to;
	
	public Move (int vid, Point from, Point to) {
		this.vid = vid;
		this.from = from;
		this.to = to;
	}

	public Point getFrom() {
		return new Point(from.x, from.y);
	}

	public Point getTo() {
		return new Point(to.x, to.y);
	}
	
	public String toString() {
		return (from.x==to.x)?"Vertical" 
				+ ((from.y == to.y-1)?" car ":" truck "):"Horizontal" 
				+ ((from.x == to.x-1)?" car ":" truck ") + "at (" + from.x +","+from.y+")";
	}
	
	public Move reverse() {
		return new Move (vid,to,from);
	}
}
