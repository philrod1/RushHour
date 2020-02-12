package rushhour.solver.heuristic;

import java.util.LinkedList;
import java.util.List;

import rushhour.model.state.State;

/*
 * A poor but admissible heuristic.
 * Tested against ZeroHeuristic it halved the search space
 * and took two thirds of the time.
 */
public class BulldozerHeuristic implements Heuristic {

	@Override
	public int value(State state) {
		int redEnd = state.getVehicleEnd(1).x;
		List<Integer> hit = new LinkedList<Integer>();
		int count = 0;
		for(int x = redEnd+1 ; x <= 5 ; x++) {
			int vid = state.getVID(x, 2);
			if(vid > 0 && !hit.contains(vid)) {
				count ++;
			}
		}
		return count;
	}

}
