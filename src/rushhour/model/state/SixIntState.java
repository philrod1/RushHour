package rushhour.model.state;

import java.awt.Point;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import rushhour.model.move.Move;
import rushhour.model.vehicle.VehicleData;

public class SixIntState implements State {
	
	private int r0, r1, r2, r3, r4, r5;
	private final int MASK = 0x1F;
	private State parent;
	
	public SixIntState () {
		this.parent = null;
		r0 = 0;
		r1 = 0;
		r2 = 0;
		r3 = 0;
		r4 = 0;
		r5 = 0;
	}
	
	private SixIntState (int r0, int r1, int r2, int r3, int r4, int r5, State parent) {
		this.parent = parent;
		this.r0 = r0;
		this.r1 = r1;
		this.r2 = r2;
		this.r3 = r3;
		this.r4 = r4;
		this.r5 = r5;
	}

	@Override
	public boolean addVehicle(int vid, Point origin, Point end) {
		if(canAddVehicle(vid, origin, end)) {
			for(int y = origin.y ; y <= end.y ; y++) {
				for(int x = origin.x ; x <= end.x ; x++) {
					switch(y) {
						case 0: r0 |= (vid << (x*5)); break;
						case 1:	r1 |= (vid << (x*5)); break;
						case 2: r2 |= (vid << (x*5)); break;
						case 3: r3 |= (vid << (x*5)); break;
						case 4: r4 |= (vid << (x*5)); break;
						case 5: r5 |= (vid << (x*5)); break;
					}
				}
			}
			return true;
		} else {
			return false;
		}
	}

	@Override
	public boolean canAddVehicle(int vid, Point origin, Point end) {
		int row = 0;
		for(int y = origin.y ; y <= end.y ; y++) {
			if (y>5) return false;
			row = getRow(y);
			for(int x = origin.x ; x <= end.x ; x++) {
				if (x>5) return false;
				if(((row >> (x*5)) & MASK) != 0 
						&& ((row >> (x*5)) & vid) != vid) {
					return false;
				}
			}
		}
		return true;
	}

	@Override
	public int removeVehicle(Point point) {
		return removeVehicle(point.x, point.y);
	}

	@Override
	public int removeVehicle(int x, int y) {
		int vid = getVID(x, y);
		removeVehicle(vid);
		return vid;
	}

	@Override
	public boolean removeVehicle(int vid) {
		for (int i = 0 ; i < 6 ; i++) {
			if (((r0>>(i*5))&MASK)==vid) {
				r0 &= ~(MASK<<(i*5));
			}
			if (((r1>>(i*5))&MASK)==vid) {
				r1 &= ~(MASK<<(i*5));
			}
			if (((r2>>(i*5))&MASK)==vid) {
				r2 &= ~(MASK<<(i*5));
			}
			if (((r3>>(i*5))&MASK)==vid) {
				r3 &= ~(MASK<<(i*5));
			}
			if (((r4>>(i*5))&MASK)==vid) {
				r4 &= ~(MASK<<(i*5));
			}
			if (((r5>>(i*5))&MASK)==vid) {
				r5 &= ~(MASK<<(i*5));
			}
		}
		return true;
	}

	@Override
	public int getVID(Point point) {
		return getVID(point.x, point.y);
	}

	@Override
	public int getVID(int x, int y) {
		int row = getRow(y);
		return (row >> (x*5)) & MASK;
	}

	@Override
	public Point getVehicleOrigin(int vid) {
		for(int x = 0 ; x < 6 ; x++) {
			if (((r0>>(x*5))&MASK)==vid) {
				return new Point (x,0);
			}
			if (((r1>>(x*5))&MASK)==vid) {
				return new Point (x,1);
			}
			if (((r2>>(x*5))&MASK)==vid) {
				return new Point (x,2);
			}
			if (((r3>>(x*5))&MASK)==vid) {
				return new Point (x,3);
			}
			if (((r4>>(x*5))&MASK)==vid) {
				return new Point (x,4);
			}
			if (((r5>>(x*5))&MASK)==vid) {
				return new Point (x,5);
			}
		}
		return null;
	}

	@Override
	public Point getVehicleEnd(int vid) {
		for(int x = 5 ; x >= 0 ; x--) {
			if (((r5>>(x*5))&MASK)==vid) {
				return new Point (x,5);
			}
			if (((r4>>(x*5))&MASK)==vid) {
				return new Point (x,4);
			}
			if (((r3>>(x*5))&MASK)==vid) {
				return new Point (x,3);
			}
			if (((r2>>(x*5))&MASK)==vid) {
				return new Point (x,2);
			}
			if (((r1>>(x*5))&MASK)==vid) {
				return new Point (x,1);
			}
			if (((r0>>(x*5))&MASK)==vid) {
				return new Point (x,0);
			}
		}
		return null;
	}

	@Override
	public VehicleData getVehicleData(int vid) {
		if (vid == 0) return null;
		Point from = getVehicleOrigin(vid);
		if (from == null) return null;
		Point to = getVehicleEnd(vid);
		if (to == null) return null;
		return new VehicleData(vid, from, to, new Point(getMinX(vid),getMinY(vid)), new Point(getMaxX(vid),getMaxY(vid)));
	}

	@Override
	public VehicleData getVehicleData(int x, int y) {
		int row = getRow(y);
		int vid = (row >> (x*5))&MASK;
		return getVehicleData(vid);
	}

	@Override
	public VehicleData getVehicleData(Point point) {
		return getVehicleData(point.x, point.y);
	}

	@Override
	public boolean move(int vid, Point to) {
		Point origin = getVehicleOrigin(vid);
		if (origin == null) return false;
		Point end = getVehicleEnd(vid);
		Point tail = new Point(to.x + (end.x-origin.x), to.y + (end.y-origin.y));
		if(canMoveTo(vid, to, tail, origin, end)) {
			removeVehicle(vid);
			addVehicle(vid, to, tail);
			return true;
		} else {
			return false;
		}
	}

	@Override
	public void print() {
		int[][] state = getGrid();
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

	@Override
	public void reset() {
		r0 = 0;
		r1 = 0;
		r2 = 0;
		r3 = 0;
		r4 = 0;
		r5 = 0;
	}

	@Override
	public List<State> getChildren() {
		List<State> children = new LinkedList<State>();		
		for (int y = 0 ; y < 6 ; y++) {
			int row = getRow(y);
			for (int x = 0 ; x < 6 ; x++) {
				int vid = (row>>(x*5)) &MASK;
				if (vid == 0) {
					// ABOVE ...
					if (y > 1) {
						int above1 = getRow(y-1);
						int vidN1 = (above1>>(x*5)) &MASK;
						if (vidN1 != 0) {
							int above2 = getRow(y-2);
							int vidN2 = (above2>>(x*5)) &MASK;
							if (vidN2 == vidN1) {
								// At least a vertical car, maybe a truck...
								if (y > 2) {
									// It could be a truck
									int above3 = getRow(y-3);
									int vidN3 = (above3>>(x*5)) &MASK;
									if (vidN3 == vidN1) {
										// It IS a truck!
										State clone = clone();
										clone.move(vidN1, new Point (x,y-2));
										((SixIntState) clone).setParent(this);
										children.add(clone);
									} else {
										// It's not a truck
										// It's a vertical car.
										State clone = clone();
										clone.move(vidN1, new Point (x,y-1));
										((SixIntState) clone).setParent(this);
										children.add(clone);
									}
								} else {
									// There isn't room for a vertical truck
									// It's a vertical car.
									State clone = clone();
									clone.move(vidN1, new Point (x,y-1));
									((SixIntState) clone).setParent(this);
									children.add(clone);
								}
							}
						}
					}
					
					// BELOW ...
					if (y < 4) {
						int below1 = getRow(y+1);
						int vidS1 = (below1>>(x*5)) &MASK;
						if (vidS1 != 0) {
							int below2 = getRow(y+2);
							int vidS2 = (below2>>(x*5)) &MASK;
							if (vidS2 == vidS1) {
								// At least a vertical car, maybe a truck...
								State clone = clone();
								((SixIntState) clone).setParent(this);
								clone.move(vidS1, new Point (x,y));
								children.add(clone);
							}
						}
					}
					// LEFT ...
					if (x > 1) {
						int vidW1 = (row>>((x-1)*5)) &MASK;
						if (vidW1 != 0) {
							int vidW2 = (row>>((x-2)*5)) &MASK;
							if (vidW2 == vidW1) {
								// At least a horizontal car, maybe a truck...
								if (x > 2) {
									// It could be a truck
									int vidW3 = (row>>((x-3)*5)) &MASK;
									if (vidW3 == vidW1) {
										// It IS a truck!
										State clone = clone();
										((SixIntState) clone).setParent(this);
										clone.move(vidW1, new Point (x-2,y));
										children.add(clone);
									} else {
										// It's not a truck
										// It's a vertical car.
										State clone = clone();
										((SixIntState) clone).setParent(this);
										clone.move(vidW1, new Point (x-1,y));
										children.add(clone);
									}
								} else {
									// There isn't room for a vertical truck
									// It's a vertical car.
									State clone = clone();
									((SixIntState) clone).setParent(this);
									clone.move(vidW1, new Point (x-1,y));
									children.add(clone);
								}
							}
						}
					}
					// RIGHT ...
					if (x < 4) {
						int vidE1 = (row>>((x+1)*5)) &MASK;
						if (vidE1 != 0) {
							int vidE2 = (row>>((x+2)*5)) &MASK;
							if (vidE2 == vidE1) {
								// At least a vertical car, maybe a truck...
								State clone = clone();
								((SixIntState) clone).setParent(this);
								clone.move(vidE1, new Point (x,y));
								children.add(clone);
							}
						}
					}
				}
			}
		}
		return children;
	}

	@Override
	public boolean isGoal() {
		return (r2 & 0x3FF00000) == 0x2100000;
	}

	@Override
	public Move calculateMove(State next) {
		int[][] state = getGrid();
		int[][] that = next.getGrid();
		int vid = 0;
		Point from = null;
		Point to = null;
		for (int x = 0 ; x < 6 ; x++) {
			for (int y = 0 ; y < 6 ; y++) {
				if (state[x][y] > that[x][y]) {
					vid = state[x][y];
					break;
				}
			}
			if (vid>0) break;
		}
		for (int x = 0 ; x < 6 ; x++) {
			for (int y = 0 ; y < 6 ; y++) {
				if (state[x][y] == vid) {
					from = new Point(x,y);
					break;
				}
			}
			if (from != null) break;
		}
		for (int x = 0 ; x < 6 ; x++) {
			for (int y = 0 ; y < 6 ; y++) {
				if (that[x][y] == vid) {
					to = new Point(x,y);
					return new Move(vid, from, to);
				}
			}
		}
		return null;
	}

	@Override
	public int[][] getGrid() {
		int[][] grid = new int[6][6];
		int row = 0;
		for(int y = 0 ; y < 6 ; y++) {
			row = getRow(y);
			for(int x = 0 ; x < 6 ; x++) {
				grid[x][y] = (row>>(x*5))&MASK;
			}
		}
		return grid;
	}

	public State clone() {
		return new SixIntState(r0, r1, r2, r3, r4, r5, parent);
	}
	
	@Override
	public boolean equals(Object that) {
		if (that == null) return false;
		if (that instanceof SixIntState) {
			SixIntState s = (SixIntState) that;
			return r0==s.r0 && r1==s.r1 && r2==s.r2 && r3==s.r3 && r4==s.r4 && r5==s.r5;
		} else {
			return false;
		}
	}
	
	@Override
	public int hashCode() {
		return Arrays.hashCode(new int[]{r0,1,r2,r3,r4,r5});
	}
	
	
	private int getRow(int y) {
		switch(y) {
			case 0: return r0;
			case 1:	return r1;
			case 2: return r2;
			case 3: return r3;
			case 4: return r4;
			case 5: return r5;
			default: return 0xFFFFFFFF;
		}
	}
	
	private boolean canMoveTo(int vid, Point to, Point tail, Point origin, Point end) {
		boolean vertical = origin.x == end.x;
		if(vertical) {
			if(to.x != origin.x) {
				return false;
			}
		} else {
			if(to.y != origin.y) {
				return false;
			}
		}
		return canAddVehicle(vid, to, tail);
	}
	
	private synchronized int getMaxX (int vid) {
		Point origin = getVehicleOrigin(vid);
		Point end = getVehicleEnd(vid);
		if (origin == null || end == null) return -1;
		if (origin.x == end.x) return origin.x;
		int dx = 0;
		int row = getRow(origin.y);
		for (int x = end.x + 1 ; x < 6 ; x++) {
			if (((row>>(x*5))&MASK) == 0) {
				dx++;
			} else {
				break;
			}
		}
		return origin.x + dx;
	}
	
	private synchronized int getMinX (int vid) {
		Point origin = getVehicleOrigin(vid);
		Point end = getVehicleEnd(vid);
		if (origin == null || end == null) return -1;
		if (origin.x == end.x) return origin.x;
		int row = getRow(origin.y);
		for (int x = origin.x - 1 ; x >= 0 ; x--) {
			if (((row>>(x*5))&MASK) > 0) {
				return x + 1;
			}
		}
		return 0;
	}
	
	private synchronized int getMaxY (int vid) {
		Point origin = getVehicleOrigin(vid);
		Point end = getVehicleEnd(vid);
		if (origin == null || end == null) return -1;
		if (origin.y == end.y) return origin.y;
		int dy = 0;
		for (int y = end.y + 1 ; y < 6 ; y++) {
			int row = getRow(y);
			if (((row>>(origin.x*5))&MASK) == 0) {
				dy++;
			} else {
				break;
			}
		}
		return origin.y + dy;
	}
	
	private synchronized int getMinY (int vid) {
		Point origin = getVehicleOrigin(vid);
		Point end = getVehicleEnd(vid);
		if (origin == null || end == null) return -1;
		if (origin.y == end.y) return origin.y;
		for (int y = origin.y - 1 ; y >= 0 ; y--) {
			int row = getRow(y);
			if (((row>>(origin.x*5)&MASK)) > 0) {
				return y + 1;
			}
		}
		return 0;
	}

	@Override
	public State getParent() {
		return parent;
	}
	
	private void setParent(State parent) {
		this.parent = parent;
	}
}
