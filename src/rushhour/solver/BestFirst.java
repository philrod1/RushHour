package rushhour.solver;

import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Stack;

import rushhour.model.move.Move;
import rushhour.model.state.State;

/*
 * This is A* if the heuristic is admissible, A otherwise.
 */
public class BestFirst extends AbstractSolver {

	private List<HeuristicNode> visited;
	private PriorityQueue<HeuristicNode> agenda;

	@Override
	public Stack<Move> solve(State state) {
		visited = new LinkedList<HeuristicNode>();
		agenda = new PriorityQueue<HeuristicNode>();
		
		start = System.currentTimeMillis();
		
		agenda.add(new HeuristicNode(state, heuristic.value(state)));
		
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
				HeuristicNode node = new HeuristicNode(child, heuristic.value(child));
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

	public class HeuristicNode implements Comparable<HeuristicNode> {
		public final int h;
		public final State state;
		public HeuristicNode (State state, int h) {
			this.state = state;
			this.h = h;
		}
		
		@Override
		public int compareTo(HeuristicNode that) {
			return this.h - that.h;
		}
		
		@Override
		public boolean equals(Object that) {
			if (that == null) return false;
			if (that instanceof HeuristicNode) {
				return ((HeuristicNode)that).state.equals(state);
			} else {
				return false;
			}
		}
		
		@Override
		public int hashCode() {
			return state.hashCode();
		}
	}
}
