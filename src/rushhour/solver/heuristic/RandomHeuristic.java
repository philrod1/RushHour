package rushhour.solver.heuristic;

import java.util.Random;

import rushhour.model.state.State;

/*
 * An inadmissible heuristic, just for comparisons
 */
public class RandomHeuristic implements Heuristic {

	private Random rng = new Random();
	
	@Override
	public int value(State state) {
		return rng.nextInt(20) + 1;
	}

}
