package rushhour.solver.heuristic;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import rushhour.model.state.State;

public class DisturbedVehicles2 implements Heuristic {

	@Override
	public int value(State state) {
		// TODO Auto-generated method stub
		return 0;
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
	
	private class TreeNode {
		int cost;
//		boolean[] disturbed;
		List<Vehicle> inHand;
		int[][] grid;
		
		private TreeNode (int[][] grid, List<Vehicle> inHand) {
			this.grid = grid;
			this.inHand = inHand;
		}
		
		List<TreeNode> expand() {
			List<TreeNode> children = new LinkedList<TreeNode>();
			List<List<Point>> map = new LinkedList<List<Point>>();
			for(Vehicle v : inHand) {
				map.add(getPositions(v));
			}
			List<List<Point>> combinations = combinate(map);
			for(List<Point> lp : combinations) {
				int[][] gc = cloneGrid(grid);
				List<Vehicle> cInHand = new LinkedList<Vehicle>();
				for (int i = 0 ; i < inHand.size() ; i++) {
					 cInHand.addAll(place(inHand.get(i), gc, lp.get(i)));
				}
				children.add(new TreeNode(gc, cInHand));
			}
			return children;
		}
		
		private List<Vehicle> place(Vehicle vehicle, int[][] gc, Point p) {
			// TODO Auto-generated method stub
			return null;
		}

		List<Point> getPositions (Vehicle v) {
			List<Point> points = new LinkedList<Point>();
			int length = (v.isTruck) ? 2 : 1;
			if(v.isVertical) {
				int x = v.start.x;
				for (int y = 0 ; y < 6 - length ; y++) {
//					if
				}
			} else {
				
			}
			return points;
		}
		
		private void print() {
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
			System.out.print("In hand: ");
			for(Vehicle v : inHand) {
				System.out.print(v.vid + " ");
			}
			System.out.println();
		}
	}
	
	private class Vehicle {
		boolean isTruck, isVertical;
		Point start, hotSpot;
		int vid;
		private Vehicle(int vid, boolean isTruck, boolean isVertical, Point start, Point hotSpot) {
			this.vid = vid;
			this.isTruck = isTruck;
			this.isVertical = isVertical;
			this.start = start;
			this.hotSpot = hotSpot;
		}
		
		public int hashCode() {
			return vid;
		}
	}
	private <T> int combinations(List<List<T>> list) {
		int count = 1;
		for (List<T> current : list) {
			count = count * current.size();
		}
		System.out.println(count);
		return count;
	}

	private <T> List<List<T>> combinate(List<List<T>> uncombinedList) {
		System.out.println(uncombinedList.size());
		List<List<T>> list = new ArrayList<List<T>>();
		int index[] = new int[uncombinedList.size()];
		int combinations = combinations(uncombinedList) - 1;
		// Initialize index
		for (int i = 0; i < index.length; i++) {
			index[i] = 0;
		}
		// First combination is always valid
		List<T> combination = new ArrayList<T>();
		for (int m = 0; m < index.length; m++) {
			combination.add(uncombinedList.get(m).get(index[m]));
		}
		list.add(combination);
		for (int k = 0; k < combinations; k++) {
			combination = new ArrayList<T>();
			boolean found = false;
			// We Use reverse order
			for (int l = index.length - 1; l >= 0 && found == false; l--) {
				int currentListSize = uncombinedList.get(l).size();
				if (index[l] < currentListSize - 1) {
					index[l] = index[l] + 1;
					found = true;
				} else {
					// Overflow
					index[l] = 0;
				}
			}
			for (int m = 0; m < index.length; m++) {
				combination.add(uncombinedList.get(m).get(index[m]));
			}
			list.add(combination);
		}
		return list;
	}
}
