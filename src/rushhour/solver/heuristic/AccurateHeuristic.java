package rushhour.solver.heuristic;

import rushhour.model.state.State;
import rushhour.solver.BreadthFirst;
import rushhour.solver.Solver;

/*
 * This is a very accurate heuristic, but completely pointless
 * as it takes too long.  If you are going to use breath-first
 * search as a heuristic, why not just use it as the solver?
 */
public class AccurateHeuristic implements Heuristic {

	@Override
	public int value(State state) {
		Solver solver = new BreadthFirst();
		return solver.solve(state).size();
	}

}
