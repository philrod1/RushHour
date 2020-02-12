package rushhour.solver;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Stack;

import rushhour.model.move.Move;
import rushhour.model.state.State;

public class EnforcedHillClimbing extends AbstractSolver {

	@Override
	public Stack<Move> solve(State state) {
		
		start = System.currentTimeMillis();
		Queue<State> open = new LinkedList<State>();
		List<State> visited = new LinkedList<State>();
		open.add(state);
		int bestHeuristic = heuristic.value(state);
		
		while(!open.isEmpty()) {
			State currentState = open.remove();
			nodeCount++;
			visited.add(currentState);
			List<State> children = currentState.getChildren();
			for(State child : children) {
				if(!visited.contains(child) && !open.contains(child)) {
					if(child.isGoal()) {
						return calculateMoves(state, child);
					}
					open.add(child);
					int heuristicValue = heuristic.value(child);
					if (heuristicValue > bestHeuristic) {
						open.clear();
						open.add(child);
						bestHeuristic = heuristicValue;
						break;
					}
				}
			}
		}
		
		return fail();
	}

	@Override
	public boolean isInformed() {
		return true;
	}

}