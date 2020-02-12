package rushhour.model.vehicle;

import java.awt.Point;

public class Vehicle {

	public final int vid;
	public final Point origin, end;
	public final boolean isVertical;
	private final int[][] grid;
	public final int length;

	public Vehicle(int vid, Point origin, Point end, int[][] grid) {
		for (int y = 0; y < 6; y++) {
			for (int x = 0; x < 6; x++) {
				if (grid[x][y] == vid) {
					grid[x][y] = 0;
				}
			}
		}
		this.vid = vid;
		this.origin = origin;
		this.end = end;
		this.grid = grid;
		isVertical = origin.y < end.y;
		if (!isVertical) {
			length = end.x - origin.x + 1;
			for (int x = origin.x; x < origin.x + length; x++) {
				grid[x][origin.y] = vid;
			}
		} else {
			length = end.y - origin.y + 1;
			for (int y = origin.y; y < origin.y + length; y++) {
				grid[origin.x][y] = vid;
			}
		}
	}

	@Override
	public String toString() {
		return vid + ": " + origin + " - " + end;
	}

	private void translate(int dx, int dy) {
		origin.translate(dx, dy);
		end.translate(dx, dy);
	}

	public synchronized void moveTo(Point origin) {
		if (!isVertical) {
			for (int x = this.origin.x; x <= this.end.x; x++) {
				grid[x][origin.y] = 0;
			}
			translate(origin.x - this.origin.x, 0);
			for (int x = this.origin.x; x <= this.end.x; x++) {
				grid[x][origin.y] = vid;
			}
		} else {
			for (int y = this.origin.y; y <= this.end.y; y++) {
				grid[origin.x][y] = 0;
			}
			translate(0, origin.y - this.origin.y);
			for (int y = this.origin.y; y <= this.end.y; y++) {
				grid[origin.x][y] = vid;
			}
		}
		
	}

	public synchronized int getMinX() {
		if (!isVertical) {
			for (int x = origin.x - 1; x >= 0; x--) {
				if (grid[x][origin.y] > 0) {
					return x + 1;
				}
			}
			return 0;
		}
		return origin.x;
	}

	public synchronized int getMaxX() {
		if (!isVertical) {
			for (int x = end.x + 1; x < 6; x++) {
				if (grid[x][end.y] > 0) {
					return x - length;
				}
			}
			return 6 - length;
		}
		return end.x;
	}

	public synchronized int getMinY() {
		if (isVertical) {
			for (int y = origin.y - 1; y >= 0; y--) {
				if (grid[origin.x][y] > 0) {
					return y + 1;
				}
			}
			return 0;
		}
		return origin.y;
	}

	public synchronized int getMaxY() {
		if (isVertical) {
			for (int y = end.y + 1; y < 6; y++) {
				if (grid[end.x][y] > 0) {
					return y - length;
				}
			}
			return 6 - length;
		}
		return end.y;
	}

	public synchronized boolean canMoveTo(Point point) {
		if (!isVertical) {
			if (point.y != origin.y || point.x < 0 || (point.x + length) > 6) {
				return false;
			} else {
				if (point.x < origin.x) {
					for (int x = point.x; x < origin.x; x++) {
						if (grid[x][point.y] != 0) {
							return false;
						}
					}
					return true;
				} else {
					for (int x = origin.x; x < point.x; x++) {
						if (grid[x][point.y] != 0 && grid[x][point.y] != vid) {
							return false;
						}
					}
					return true;
				}
			}
		} else {
			if (point.x != origin.x || point.y < 0 || (point.y + length) > 6) {
				return false;
			} else {
				if (point.y < origin.y) {
					for (int y = point.y; y < origin.y; y++) {
						if (grid[point.x][y] != 0) {
							return false;
						}
					}
					return true;
				} else {
					for (int y = origin.y; y < point.y; y++) {
						if (grid[point.x][y] != 0 && grid[point.x][y] != vid) {
							return false;
						}
					}
					return true;
				}
			}
		}
	}

	public Vehicle clone(int[][] grid) {
		return new Vehicle(vid, origin, end, grid, isVertical, length);
	}
	
	private Vehicle (int vid, Point origin, Point end, int[][] grid, boolean isVertical, int length) {
		this.vid = vid;
		this.origin = (Point) origin.clone();
		this.end = (Point) end.clone();
		this.grid = grid;
		this.isVertical = isVertical;
		this.length = length;
	}
	
	private int[][] cloneGrid (int[][] original) {
		int[][] clone = new int[6][6];
		for(int y = 0 ; y < 6 ; y++) {
			for(int x = 0 ; x < 6 ; x++) {
				clone[x][y] = original[x][y];
			}
		}
		return clone;
	}
}
