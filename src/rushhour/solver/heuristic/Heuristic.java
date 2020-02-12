package rushhour.solver.heuristic;

import rushhour.model.state.State;

public interface Heuristic {
	int value(State state);
}
