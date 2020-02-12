package rushhour.model.vehicle;

import java.awt.Point;

public class VehicleData {

	public final int vid;
	public final Point origin, end, min, max;
	public final boolean isVertical;
	
	public VehicleData (int vid, Point origin, Point end,
			Point min, Point max) {
		this.vid = vid;
		this.origin = origin;
		this.end = end;
		this.min = min;
		this.max = max;
		isVertical = origin.y < end.y;
	}
	
	@Override
	public String toString() {
		return vid + ": " + origin + " - " + end + " (" + isVertical + ").";
	}

	public synchronized void translate(int dx, int dy) {
		origin.translate(dx, dy);
		end.translate(dx, dy);
	}
	
	public synchronized void moveTo(Point origin) {
		translate(origin.x-this.origin.x, origin.y-this.origin.y);
	}
	
}
