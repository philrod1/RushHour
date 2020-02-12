package rushhour.solver;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

import rushhour.model.move.Move;
import rushhour.model.state.State;
import rushhour.tools.Tools;

public class RandomSolver2 extends AbstractSolver {

	@Override
	public Stack<Move> solve(State state) {
		
		List<State> visited = new LinkedList<State>();
		
		start = System.currentTimeMillis();
		
		List<State> children = state.getChildren();
		
		State current = Tools.getRandomItem(children);
		nodeCount++;
		
		while(!current.isGoal()) {
			nodeCount++;
			children = current.getChildren();
			State next = Tools.removeRandomItem(children);
			while(visited.contains(next)) {
				next = Tools.removeRandomItem(children);
				if(next == null) {
					visited.clear();
					children = current.getChildren();
					next = Tools.removeRandomItem(children);
				}
			}
			visited.add(next);
			current = next;
		}
		
		System.out.println("GOAL");
		time = System.currentTimeMillis() - start;
		return calculateMoves(state, current);
	}

}
