package rushhour.model.state;

import java.awt.Point;
import java.awt.print.Printable;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import rushhour.model.move.Move;
import rushhour.model.vehicle.VehicleData;

public class SixLongs implements State {
	
	private final long[] state;
	private static final int IDMASK = 0x1F;
	private static final int TMASK = 0x20;
	private static final int SMASK = 0xC0;
	private static final int VMASK = 0x100;
	private static final int MASK = 0x1FF;
	private static final int INFO = 0x120;
	private static final long GOAL = 0x201000000000L;
	private static final long GOALMASK = 0xFFFF000000000L;
	private static final int BITS = 9;
	private State parent;
	
	public SixLongs () {
		this.parent = null;
		state = new long[6];
	}
	
	private SixLongs (long[] state, State parent) {
		this.parent = parent;
		this.state = state;
	}

	@Override
	public boolean addVehicle(int vid, Point origin, Point end) {
		if(canAddVehicle(vid, origin, end)) {
			long value = vid;
			boolean vert = origin.x == end.x;
			int length;
			if (vert) {
				value |= VMASK;
				length = end.y - origin.y;
				if(length == 2) { //Truck
					value |= TMASK;
				}
				for (int i = 0 ; i <= length ; i++) {
					value &= ~SMASK; // Clear the old segment information
					value |= i << 6;
//					printData(value);
					state[origin.y+i] |= (value << (origin.x * BITS));
				}
			} else {
				length = end.x - origin.x;
				if(length == 2) { //Truck
					value |= TMASK;
				}
				for (int i = 0 ; i <= length ; i++) {
					value &= ~SMASK; // Clear the old segment information
					value |= i << 6;
					state[origin.y] |= (value << ((origin.x + i) * BITS));
				}
			}
		} else {
			return false;
		}
		return true;
	}

	@Override
	public boolean canAddVehicle(int vid, Point origin, Point end) {
		if(isValidVehicle(origin,end) && !vehicleExists(vid)) {
			for(int y = origin.y ; y <= end.y ; y++) {
				if (y>5) return false;
				for(int x = origin.x ; x <= end.x ; x++) {
					if (x>5) return false;
					int value =  (int) valueAt(x, y);
					if(value != 0 && (value&vid) != vid) {
						return false;
					}
				}
			}
		} else {
			return false;
		}
		return true;
	}

	private boolean vehicleExists(int vid) {
		for(int y = 0 ; y < 6 ; y++) {
			for(int x = 0 ; x < 6 ; x++) {
				if((int) ((state[y] >> (x * BITS)) & IDMASK) == vid) {
					return true;
				}
			}
		}
		return false;
	}

	private boolean isValidVehicle(Point origin, Point end) {
		int width = end.x - origin.x;
		int height = end.y - origin.y;
		if (width == 0 && (height == 1 || height == 2)) {
			return true;
		}
		if (height == 0 && (width == 1 || width == 2)) {
			return true;
		}
		return false;
	}

	@Override
	public int removeVehicle(Point point) {
		return removeVehicle(point.x, point.y);
	}

	@Override
	public int removeVehicle(int x, int y) {
		int vid = (int) ((state[y] >> (x * BITS)) & IDMASK);
		removeVehicle(vid);
		return vid;
	}

	@Override
	public boolean removeVehicle(int vid) {
		for(int y = 0 ; y < 6 ; y++) {
			for(int x = 0 ; x < 6 ; x++) {
				int value = (int) getValue(getVID(x, y));
				if((value & IDMASK) == vid) {
					state[y]  &= ~(((long)MASK)<<(x*BITS));
				}
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
		return (int) ((state[y] >> (x*BITS)) & IDMASK); 
	}

	@Override
	public Point getVehicleOrigin(int vid) {
//		System.out.println("Looking for " + vid);
//		print();
		for(int y = 0 ; y < 6 ; y++) {
			long row = state[y];
			for(int x = 0 ; x < 6 ; x++) {
				int id = (int) ((row >> (x*BITS)) & IDMASK);
				if (id == vid) {
					return new Point(x,y);
				}
			}	
		}
		return null;
	}

	@Override
	public Point getVehicleEnd(int vid) {
		for(int y = 5 ; y >= 0 ; y--) {
			long row = state[y];
			for (int x = 5 ; x >= 0 ; x--) {
				int id = (int) ((row >> (x*BITS)) & IDMASK);
				if (id == vid) {
					return new Point(x,y);
				}
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
		return new VehicleData(vid, from, to, 
				new Point(getMinX(vid),getMinY(vid)), 
				new Point(getMaxX(vid),getMaxY(vid)));
	}

	@Override
	public VehicleData getVehicleData(int x, int y) {
		return getVehicleData(vid(x,y));
	}

	@Override
	public VehicleData getVehicleData(Point point) {
		return getVehicleData(vid(point));
	}

	@Override
	public boolean move(int vid, Point to) {
		long value = getValue(vid);
		if(canMoveTo(vid,to)) {
//			System.out.println("Can move " + vid);
			removeVehicle(vid);
			addVehicle(value, to.x, to.y);
			return true;
		}
		return false;
	}

	@Override
	public void print() {
//		for(int i = 0 ; i < 6 ; i++) {
//			System.out.println(state[i]);
//		}
//		System.out.println("Is Goal: " + isGoal());

//		
//		printData(valueAt(3, 1));
//		System.out.println();
		int[][] grid = getGrid();
		for (int y = 0; y < 6; y++) {
			for (int x = 0; x < 6; x++) {
				String c;
				if(grid[x][y] > 0) {
					if(grid[x][y] < 10) {
						c = "" + grid[x][y];
					} else {
						c = "" + (char)(grid[x][y]+55);
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
		for(int y = 0 ; y < 6 ; y++) {
			state[y] = 0L;
		}
	}

	@Override
	public List<State> getChildren() {
//		int up = 0;
//		int down = 0;
//		int left = 0;
//		int right = 0;
		List<State> children = new LinkedList<State>();		
		for (int y = 0 ; y < 6 ; y++) {
			Long row = state[y];
			for (int x = 0 ; x < 6 ; x++) {
				int value = (int) (row >> (BITS*x)) & MASK;
//				System.out.println(x + " : " + y + "  " + value);
				if(value == 0) {
					//up
					int y2 = y;
					while(y2 > 0) {
						long v2 = (state[y2-1] >> (BITS*x)) & MASK;
						if(v2 > 0) {
							if(isVert(v2)) {
								SixLongs clone = (SixLongs) this.clone();
								((SixLongs) clone).setParent(this);
								int vid = (int)v2 & IDMASK;
								clone.removeVehicle(vid);
								clone.addVehicle(v2&(~SMASK),x,y-(lengthOf(v2)-1));
								children.add(clone);
//								up++;
								break;
							} else {
								break;
							}
						}
						y2--;
					}
					//down
					y2 = y;
					while(y2 < 5) {
						long v2 = (state[y2+1] >> (BITS*x)) & MASK;
						if(v2 > 0) {
							if(isVert(v2)) {
								SixLongs clone = (SixLongs) this.clone();
								((SixLongs) clone).setParent(this);
								int vid = (int)v2 & IDMASK;
								clone.removeVehicle(vid);
								clone.addVehicle(v2,x,y);
								children.add(clone);
//								down++;
								break;
							} else {
								break;
							}
						}
						y2++;
					}
					//left
					int x2 = x;
					while(x2 > 0) {
						long v2 = (state[y] >> (BITS*(x2-1))) & MASK;
						if(v2 > 0) {
							if(isVert(v2)) {
								break;
							} else {
								SixLongs clone = (SixLongs) this.clone();
								((SixLongs) clone).setParent(this);
								int vid = (int)v2 & IDMASK;
								clone.removeVehicle(vid);
								clone.addVehicle(v2&(~SMASK),x-(lengthOf(v2)-1),y);
								children.add(clone);
//								left++;
								break;
							}
						}
						x2--;
					}
					//right
					x2 = x;
					while(x2 < 5) {
						long v2 = (state[y] >> (BITS*(x2+1))) & MASK;
						if(v2 > 0) {
							if(isVert(v2)) {
								break;
							} else {
								SixLongs clone = (SixLongs) this.clone();
								((SixLongs) clone).setParent(this);
								int vid = (int)v2 & IDMASK;
								clone.removeVehicle(vid);
								clone.addVehicle(v2,x,y);
								children.add(clone);
//								right++;
								break;
							}
						}
						x2++;
					}
				} // Occupied
			}
		}
//		for (State s : children) {
//			System.out.println("============================");
//			s.print();
//		}
//		System.out.println(up + " " + down + " " + left + " " + right);
//		System.out.println(children.size());
		return children;
	}

	@Override
	public boolean isGoal() {
		return (state[2] & GOALMASK) == GOAL;
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
		for(int y = 0 ; y < 6 ; y++) {
			for(int x = 0 ; x < 6 ; x++) {
				grid[x][y] = (int) ((state[y]>>(x*BITS))&IDMASK);
			}
		}
		return grid;
	}

	private void printData(long value) {
		System.out.println("VID:   " + ( value & IDMASK));
		System.out.println("TRUCK: " + ((value & TMASK)==TMASK));
		System.out.println("VERT:  " + ((value & VMASK)==VMASK));
		System.out.println("SEG:   " + ((value & SMASK)>>6));
	}
	
	private long valueAt(int x, int y) {
//		System.out.println((state[y]>>(BITS*x)) & MASK);
		return (state[y]>>(BITS*x)) & MASK;
	}
	
	private long valueAt(Point point) {
		return valueAt(point.x, point.y);
	}
	
	private int lengthOf(int vid) {
		if(isTruck(vid)) {
			return 3;
		}
		return 2;
	}
	
	private int lengthOf(long value) {
		if(isTruck(value & TMASK)) {
			return 3;
		}
		return 2;
	}
	
	private boolean isVert (long value) {
		return (value & VMASK)==VMASK;
	}
	
	private boolean isVert (int vid) {
//		System.out.println(Integer.toHexString(vid));
//		System.out.println(Long.toHexString(getValue(vid)));
//		System.out.println(Long.toHexString(getValue(vid) & IDMASK));
		return (getValue(vid) & VMASK)==VMASK;
	}
	
	private boolean isTruck (long value) {
		return (value & TMASK)==TMASK;
	}
	
	private boolean isTruck (int vid) {
		return (getValue(vid) & TMASK)==TMASK;
	}
	
	private int segment (long value) {
		return (int) ((value & SMASK)>>6);
	}
	
	private int vid (long value) {
		return (int) (value & IDMASK);
	}
	
	private int vid (Point point) {
		return (int) (valueAt(point) & IDMASK);
	}
	
	private int vid (int x, int y) {
		return (int) (valueAt(x,y) & IDMASK);
	}
	
	private long getValue(int vid) {
		Point origin = getVehicleOrigin(vid);
		return valueAt(origin);
	}
	
	private int getMaxY(int vid) {
		Point end = getVehicleEnd(vid);
		if(isVert(vid)) {
			for (int y = end.y + 1 ; y < 6 ; y++) {
				if(valueAt(end.x, y) > 0) {
					return y - lengthOf(vid);
				}
			}
		} else {
			return end.x;
		}
		return 6 - lengthOf(vid);
	}

	private int getMaxX(int vid) {
		Point end = getVehicleEnd(vid);
		if(isVert(vid)) {
			return end.x;
		} else {
			for (int x = end.x + 1 ; x < 6 ; x++) {
				if(valueAt(x, end.y) > 0) {
					return x - lengthOf(vid);
				}
			}
		}
		return 6 - lengthOf(vid);
	}

	private int getMinY(int vid) {
		Point origin = getVehicleOrigin(vid);
		if(isVert(vid)) {
			for (int y = origin.y - 1 ; y >= 0 ; y--) {
				if(valueAt(origin.x, y) > 0) {
					return y+1;
				}
			}
		} else {
			return origin.y;
		}
		return 0;
	}

	private int getMinX(int vid) {
		Point origin = getVehicleOrigin(vid);
		if(isVert(vid)) {
			return origin.x;
		} else {
			for (int x = origin.x - 1 ; x >= 0 ; x--) {
//				System.out.println(Long.toHexString(valueAt(x, origin.y)));
				if(valueAt(x, origin.y) > 0) {
					return x+1;
				}
			}
		}
		return 0;
	}

	private boolean canMoveTo(int vid, Point to) {
		Point from = getVehicleOrigin(vid);
		if(isVert(vid)) {
			if(to.y > from.y){ 
				return to.y <= getMaxY(vid);
			} else {
				return to.y >= getMinY(vid);
			}
		} else {
			if(to.x > from.x){ 
				return to.x <= getMaxX(vid);
			} else {
				return to.x >= getMinX(vid);
			}
		}
	}
	
	@Override
	public State clone() {
		long[] newState = new long[6];
		for (int y = 0 ; y < 6 ; y++) {
			newState[y] = state[y];
		}
		return new SixLongs(newState, parent);
	}
	
	private void addVehicle(long value, int x, int y) {
		switch(((int)value) & INFO) {
			case 0x120: state[y] |= value<<(x*BITS);
						state[y+1] |= value<<(x*BITS);
						state[y+2] |= value<<(x*BITS);
						break; //vertical truck;
			case 0x100: state[y] |= value<<(x*BITS);
						state[y+1] |= value<<(x*BITS);
						break; //vertical car;
			case 0x020: state[y] |= value<<(x*BITS);
						state[y] |= value<<((x+1)*BITS);
						state[y] |= value<<((x+2)*BITS);
						break; //horizontal truck;
			default   : state[y] |= value<<(x*BITS);
						state[y] |= value<<((x+1)*BITS); 
						//horizontal car;
		}
	}
	
	@Override
	public boolean equals(Object that) {
		if (that == null) return false;
		if (that instanceof SixLongs) {
			SixLongs s = (SixLongs) that;
			for(int y = 0 ; y < 6 ; y++) {
				if (state[y] != s.state[y]) return false;
			}
		} else {
			return false;
		}
		return true;
	}
	
	@Override
	public int hashCode() {
		return Arrays.hashCode(state);
	}

	@Override
	public State getParent() {
		return parent;
	}
	
	private void setParent(State parent) {
		this.parent = parent;
	}
}
