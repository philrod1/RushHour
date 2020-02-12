package rushhour.solver;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

import rushhour.model.move.Move;
import rushhour.model.state.State;
import rushhour.tools.Tools;

public class RandomSolver1 extends AbstractSolver {

	@Override
	public Stack<Move> solve(State state) {
		
		start = System.currentTimeMillis();
		
		List<State> children = state.getChildren();
		
		State current = Tools.getRandomItem(children);
		nodeCount++;
		
		while(!current.isGoal()) {
			nodeCount++;
			children = current.getChildren();
			State next = Tools.getRandomItem(children);
			current = next;
		}
		
		System.out.println("GOAL");
		time = System.currentTimeMillis() - start;
		return calculateMoves(state, current);
	}

}
