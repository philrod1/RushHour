package rushhour.model.game;

import rushhour.model.state.State;
import rushhour.solver.Solver;
import rushhour.solver.heuristic.Heuristic;

public class GameConfiguration {

	private State state;
	private Solver solver;
	private Heuristic heuristic;
	
	public GameConfiguration () {
		state = null;
		solver = null;
	}
	
	public State getState() {
		return state;
	}
	public void setState(State state) {
		this.state = state;
	}
	public Solver getSolver() {
		return solver;
	}
	public void setSolver(Solver solver) {
		this.solver = solver;
	}
	
	public Heuristic getHeuristic() {
		return heuristic;
	}

	public void setHeuristic(Heuristic heuristic) {
		this.heuristic = heuristic;
	}

	public boolean isValid() {
		return state != null && solver != null && (solver.isInformed()) ? heuristic != null : true;
	}
	
}
