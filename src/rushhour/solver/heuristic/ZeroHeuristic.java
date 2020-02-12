package rushhour.solver.heuristic;

import rushhour.model.state.State;

/*
 * An admissible, but not very informative heuristic
 */
public class ZeroHeuristic implements Heuristic {

	@Override
	public int value(State state) {
		return 0;
	}

}
