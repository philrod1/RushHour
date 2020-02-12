package rushhour.solver;

import java.util.*;

import rushhour.model.move.Move;
import rushhour.model.state.State;
import rushhour.tools.Tools;

public class BreadthFirst extends AbstractSolver {

	private Set<State> visited;
	private LinkedHashSet<State> agenda;
	
	@Override
	public Stack<Move> solve(State state) {
		
		visited = new HashSet<>();
		agenda = new LinkedHashSet<>();
		nodeCount = 0;
		
		start = System.currentTimeMillis();
		
		agenda.add(state);
		
		while(agenda.size() > 0) {
			State current = agenda.iterator().next();
			agenda.remove(current);
			nodeCount++;
			if (current.isGoal()) {
				return calculateMoves(state, current);
			}
			visited.add(current);
			List<State> children = current.getChildren();
			for(State child : children) {
				if (child.isGoal()) {
					return calculateMoves(state, child);
				} else if (!agenda.contains(child) && !visited.contains(child)) {
					agenda.add(child);
				}
			}
		}
		return fail();
	}
	
}
