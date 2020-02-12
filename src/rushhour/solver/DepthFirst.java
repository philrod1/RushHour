package rushhour.solver;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

import rushhour.model.move.Move;
import rushhour.model.state.State;

public class DepthFirst extends AbstractSolver {

	private List<State> visited;
	private Stack<State> agenda;
	
	@Override
	public Stack<Move> solve(State state) {
		
		visited = new LinkedList<State>();
		agenda = new Stack<State>();
		nodeCount = 0;
		
		start = System.currentTimeMillis();
		
		agenda.add(state);
		
		while(agenda.size() > 0) {
			State current = agenda.pop();
			nodeCount++;
			if (current.isGoal()) {
				return calculateMoves(state, current);
			}
			visited.add(current);
			List<State> children = current.getChildren();
			for(State child : children) {
				if (!agenda.contains(child) && !visited.contains(child)) {
					agenda.push(child);
				}
			}
		}
		time = System.currentTimeMillis() - start;
		message = "No solution!  " + nodeCount + " nodes visited.  Time: " + time;
		return null;
	}
}
