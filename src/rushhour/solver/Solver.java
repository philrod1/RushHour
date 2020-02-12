package rushhour.solver;

import java.util.Stack;

import rushhour.model.move.Move;
import rushhour.model.state.State;
import rushhour.solver.heuristic.Heuristic;

public interface Solver {
	Stack<Move> solve(State state);
	String getMessage();
	
	/**
	 * Is this search method informed or uninformed?
	 * If it is an informed search algorithm, a
	 * heuristic must be provided. 
	 * @return
	 */
	boolean isInformed();
	void setHeuristic(Heuristic heuristic);
	Heuristic getHeuristic();
}
