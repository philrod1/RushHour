package rushhour.solver;

import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Stack;

import rushhour.model.move.Move;
import rushhour.model.state.State;
import rushhour.solver.heuristic.HeuristicNode;

/*
 * This is A* if the heuristic is admissible, A otherwise.
 */
public class A_Star_ extends AbstractSolver {

	private List<HeuristicNode> visited;
	private PriorityQueue<HeuristicNode> agenda;

	@Override
	public Stack<Move> solve(State state) {
		visited = new LinkedList<HeuristicNode>();
		agenda = new PriorityQueue<HeuristicNode>();
		
		start = System.currentTimeMillis();
		
		agenda.add(new HeuristicNode(state, 0, heuristic.value(state)));
		
		while(agenda.size() > 0) {
			HeuristicNode currNode = agenda.poll();
			State current = currNode.state;
			nodeCount++;
			if (current.isGoal()) {
				return calculateMoves(state, current);
			}
			visited.add(currNode);
			List<State> children = current.getChildren();
			for(State child : children) {
				HeuristicNode node = new HeuristicNode(child, currNode.g+1, heuristic.value(child));
				if (!agenda.contains(node) && !visited.contains(node)) {
					agenda.offer(node);
				}
			}
		}
		time = System.currentTimeMillis() - start;
		message = "No solution!  " + nodeCount + " nodes visited.  Time: " + time;
		return null;
	}
	
	@Override
	public boolean isInformed() {
		return true;
	}

}
