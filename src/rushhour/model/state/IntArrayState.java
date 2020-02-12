package rushhour.model.state;

import java.awt.Point;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import rushhour.model.move.Move;
import rushhour.model.vehicle.VehicleData;


public class IntArrayState implements State {
	
	private final int[][] state;
	private State parent;
	
	public IntArrayState () {
		this.parent = null;
		state = new int[6][6];
	}
	
	private IntArrayState (int[][] state, State parent) {
		this.parent = parent;
		this.state = state;
	}

	@Override
	public boolean addVehicle(int vid, Point origin, Point end) {
		if (vid < 1) return false;
		if (origin.equals(end)) return false;
		if (vehicleExists(vid)) return false;
		if (!clearToAdd(origin, end)) return false;
		if (origin.x != end.x && origin.y != end.y) return false;
		if (end.x - origin.x > 2) return false;
		if (end.y - origin.y > 2) return false;
		addVehicleToState(vid, origin, end);
		return true;
	}
	
	@Override
	public boolean canAddVehicle(int vid, Point origin, Point end) {
		if (vid < 1) return false;
		if (origin.equals(end)) return false;
		if (vehicleExists(vid)) return false;
		if (!clearToAdd(origin, end)) return false;
		if (origin.x != end.x && origin.y != end.y) return false;
		if (end.x - origin.x > 2) return false;
		if (end.y - origin.y > 2) return false;
		return true;
	}

	@Override
	public int removeVehicle(Point point) {
		return removeVehicle(point.x, point.y);
	}

	@Override
	public int removeVehicle(int x, int y) {
		int vid = getVID(x, y);
		if (vid == 0) return 0;
		if ( removeVehicle(vid) ) {
			return vid;
		} else {
			return 0;
		}
	}

	@Override
	public boolean removeVehicle(int vid) {
		boolean hit = false;
		for(int y = 0 ; y < 6 ; y++) {
			for(int x = 0 ; x < 6 ; x++) {
				if (state[x][y] == vid) state[x][y] = 0;
				hit = true;
			}
		}
		return hit;
	}

	@Override
	public int getVID(Point point) {
		return getVID(point.x, point.y);
	}

	@Override
	public int getVID(int x, int y) {
		return state[x][y];
	}

	@Override
	public Point getVehicleOrigin(int vid) {
		for(int y = 0 ; y < 6 ; y++) {
			for(int x = 0 ; x < 6 ; x++) {
				if (state[x][y] == vid) return new Point(x,y);
			}
		}
//		System.out.println("Origin " + vid);
		return null;
	}

	@Override
	public Point getVehicleEnd(int vid) {
		for(int y = 5 ; y >= 0 ; y--) {
			for(int x = 5 ; x >= 0 ; x--) {
				if (state[x][y] == vid) return new Point(x,y);
			}
		}
		System.out.println("End " + vid);
		return null;
	}

	@Override
	public VehicleData getVehicleData(int vid) {
		//TODO - Fix return
		if (vid == 0) return null;
		Point from = getVehicleOrigin(vid);
		if (from == null) return null;
		Point to = getVehicleEnd(vid);
		if (to == null) return null;
		return new VehicleData(vid, from, to, new Point(getMinX(vid),getMinY(vid)), new Point(getMaxX(vid),getMaxY(vid)));
	}

	@Override
	public VehicleData getVehicleData(int x, int y) {
		return getVehicleData(new Point(x,y));
	}

	@Override
	public VehicleData getVehicleData(Point point) {
		int vid = state[point.x][point.y];
		return getVehicleData(vid);
	}

	@Override
	public boolean move(int vid, Point to) {
		if (!canMoveTo(vid, to)) return false;
		VehicleData vData = getVehicleData(vid);
		removeVehicle(vid);
		if (vData != null) {
			int dx = to.x - vData.origin.x;
			int dy = to.y - vData.origin.y;
			vData.translate(dx, dy);
			return addVehicle(vData);
		} else {
			return false;
		}
	}

	@Override
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

	@Override
	public void reset() {
		for(int y = 0 ; y < 6 ; y++) {
			for(int x = 0 ; x < 6 ; x++) {
				state[x][y] = 0;
			}
		}
	}

	@Override
	public List<State> getChildren() {
		
		List<State> children = new LinkedList<State>();
		int[][] tempGrid;
		boolean found;
		
		for (int v = 1 ; v < 20 ; v++) {
			found = false;
			// search for vehicle v
			for (int i = 0 ; i < 6 ; i++) {
				for (int j = 0 ; j < 6 ; j++) {
					if (state[i][j] == v) {
						// find the next segment
						if(j < 5 && state[i][j+1] == v) {
							// vertical something
							if(j < 4 && state[i][j+2] == v) {
								// vertical truck
//								System.out.println("Vehicle " + v + " is a vertical truck.");
								// add any UP moves
								for (int y = 1 ; j - y >= 0 ; y++) {
									if (state[i][j-y] == 0) {
										tempGrid = cloneGrid(state);
										for (int c = 0 ; c < y ; c++) {
											tempGrid[i][j-(c+1)] = v;
											tempGrid[i][(j+2)-c] = 0;
										}
										children.add(new IntArrayState(cloneGrid(tempGrid),this));
									} else {
										break;
									}
								}
								// add any DOWN moves
								for (int y = 1 ; j + y < 4 ; y++) {
									if (state[i][(j+2)+y] == 0) {
										tempGrid = cloneGrid(state);
										for (int c = 0 ; c < y ; c++) {
											tempGrid[i][(j+3)+c] = v;
											tempGrid[i][j+c] = 0;
										}
										children.add(new IntArrayState(cloneGrid(tempGrid),this));
									} else {
										break;
									}
								}
								found = true;
								break;
							} else {
								// vertical car
//								System.out.println("Vehicle " + v + " is a vertical car.");
								// add any UP moves
								for (int y = 1 ; j - y >= 0 ; y++) {
									if (state[i][j-y] == 0) {
										tempGrid = cloneGrid(state);
										for (int c = 0 ; c < y ; c++) {
											tempGrid[i][j-(c+1)] = v;
											tempGrid[i][(j+1)-c] = 0;
										}
										children.add(new IntArrayState(cloneGrid(tempGrid),this));
									} else {
										break;
									}
								}
								// add any DOWN moves
								for (int y = 1 ; j + y < 5 ; y++) {
									if (state[i][(j+1)+y] == 0) {
										tempGrid = cloneGrid(state);
										for (int c = 0 ; c < y ; c++) {
											tempGrid[i][(j+2)+c] = v;
											tempGrid[i][j+c] = 0;
										}
										children.add(new IntArrayState(cloneGrid(tempGrid),this));
									} else {
										break;
									}
								}
								found = true;
								break;
							}
						} else {
							// horizontal something
							if(i < 4 && state[i+2][j] == v) {
								// horizontal truck
//								System.out.println("Vehicle " + v + " is a horizontal truck.");
								
								// add any LEFT moves
								for (int x = 1 ; i - x >= 0 ; x++) {
									if (state[i-x][j] == 0) {
										tempGrid = cloneGrid(state);
										for (int c = 0 ; c < x ; c++) {
											tempGrid[i-(c+1)][j] = v;
											tempGrid[(i+2)-c][j] = 0;
										}
										children.add(new IntArrayState(cloneGrid(tempGrid),this));
									} else {
										break;
									}
								}
								// add any RIGHT moves
								for (int x = 1 ; i + x < 4 ; x++) {
									if (state[(i+2)+x][j] == 0) {
										tempGrid = cloneGrid(state);
										for (int c = 0 ; c < x ; c++) {
											tempGrid[(i+3)+c][j] = v;
											tempGrid[i+c][j] = 0;
										}
										children.add(new IntArrayState(cloneGrid(tempGrid),this));
									} else {
										break;
									}
								}
								found = true;
								break;
							} else {
								// horizontal car
//								System.out.println("Vehicle " + v + " is a horizontal car.");
								// add any LEFT moves
								for (int x = 1 ; i - x >= 0 ; x++) {
									if (state[i-x][j] == 0) {
										tempGrid = cloneGrid(state);
										for (int c = 0 ; c < x ; c++) {
											tempGrid[i-(c+1)][j] = v;
											tempGrid[(i+1)-c][j] = 0;
										}
										children.add(new IntArrayState(cloneGrid(tempGrid),this));
									} else {
										break;
									}
								}
								// add any RIGHT moves
								for (int x = 1 ; i + x < 5 ; x++) {
									if (state[(i+1)+x][j] == 0) {
										tempGrid = cloneGrid(state);
										for (int c = 0 ; c < x ; c++) {
											tempGrid[(i+2)+c][j] = v;
											tempGrid[i+c][j] = 0;
										}
										children.add(new IntArrayState(cloneGrid(tempGrid),this));
									} else {
										break;
									}
								}
								found = true;
								break;
							}
						}
					}
				}
				if (found) break;
			}
		}
		return children;
	}
	
	@Override
	public boolean isGoal() {
		return (state[4][2] == 1) && (state[5][2] == 1);
	}
	
	private synchronized boolean addVehicle (VehicleData vData) {
		return addVehicle(vData.vid, vData.origin, vData.end);
	}
	
	@Override
	public Move calculateMove(State next) {
		if(!(next instanceof IntArrayState)) {
			//TODO - Throw exception.
			return null;
		}
		int[][] that = ((IntArrayState)next).getGrid();
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
	
//	private void printGrid(int[][] grid) {
//		for (int y = 0; y < 6; y++) {
//			for (int x = 0; x < 6; x++) {
//				String c;
//				if(grid[x][y] > 0) {
//					if(grid[x][y] < 10) {
//						c = "" + grid[x][y];
//					} else {
//						c = "" + (char)(grid[x][y]+55);
//					}
//				} else {
//					c = ""+(char)183;
//				}
//				System.out.print(c + " ");
//			}
//			System.out.println();
//		}
//		System.out.println();
//	}
	
	public int[][] getGrid() {
		return cloneGrid(state);
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

	private void addVehicleToState (int vid, Point origin, Point end) {
		for(int y = origin.y ; y <= end.y ; y++) {
			for(int x = origin.x ; x <= end.x ; x++) {
				state[x][y] = vid;
			}
		}
	}
	
	private boolean clearToAdd(Point origin, Point end) {
		for(int y = origin.y ; y <= end.y ; y++) {
			for(int x = origin.x ; x <= end.x ; x++) {
				if (state[x][y] > 0) return false;
			}
		}
		return true;
	}
	
	private synchronized boolean canMoveTo (int vid, Point to) {
		int minx = getMinX(vid);
		int maxx = getMaxX(vid);
		int miny = getMinY(vid);
		int maxy = getMaxY(vid);
		return minx <= to.x 
			&& maxx >= to.x 
			&& miny <= to.y 
			&& maxy >= to.y;
	}
	
	private synchronized int getMaxX (int vid) {
		Point origin = getVehicleOrigin(vid);
		Point end = getVehicleEnd(vid);
		if (origin == null || end == null) return -1;
		if (origin.x == end.x) return origin.x;
		int dx = 0;
		for (int x = end.x + 1 ; x < 6 ; x++) {
			if (state[x][origin.y] == 0) {
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
		for (int x = origin.x - 1 ; x >= 0 ; x--) {
			if (state[x][origin.y] > 0) {
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
			if (state[origin.x][y] == 0) {
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
			if (state[origin.x][y] > 0) {
				return y + 1;
			}
		}
		return 0;
	}
	
	private boolean vehicleExists(int vid) {
		for(int y = 0 ; y < 6 ; y++) {
			for(int x = 0 ; x < 6 ; x++) {
				if (state[x][y] == vid) return true;
			}
		}
		return false;
	}

	@Override
	public boolean equals(Object that) {
		if (that == null) return false;
		if (that instanceof IntArrayState) {
			int[][] thatState = ((IntArrayState) that).getGrid();
			for(int y = 0 ; y < 6 ; y++) {
				for(int x = 0 ; x < 6 ; x++) {
					if (state[x][y] != thatState[x][y]) return false;
				}
			}
			return true;
		} else {
			return false;
		}
	}
	
	@Override
	public int hashCode() {
		return Arrays.deepHashCode(state);
	}

	@Override
	public State getParent() {
		return parent;
	}
}
