package rushhour.model.state;

import java.awt.Point;
import java.util.List;

import rushhour.model.move.Move;
import rushhour.model.vehicle.VehicleData;


public interface State {
	
	boolean addVehicle (int vid, Point origin, Point end);
	
	boolean canAddVehicle (int vid, Point origin, Point end);

	int removeVehicle (Point point);
	
	int removeVehicle (int x, int y);
	
	boolean removeVehicle(int vid);
	
	int getVID (Point point);
	
	int getVID (int x, int y);
	
	Point getVehicleOrigin (int vid);
	
	Point getVehicleEnd (int vid);
	
	VehicleData getVehicleData(int vid);
	
	VehicleData getVehicleData(int x, int y);
	
	VehicleData getVehicleData(Point point);
	
	boolean move (int vid, Point to);
	
	void print();
	
	void reset();
	
	List<State> getChildren();
	
	boolean isGoal();
	
	Move calculateMove(State next);
	
	int[][] getGrid();
	
	State getParent();
}
