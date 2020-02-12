package rushhour.solver;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import rushhour.model.move.Move;
import rushhour.model.state.State;
import rushhour.solver.heuristic.Heuristic;


public abstract class AbstractSolver implements Solver {

	protected String message;
	protected int searchSize = -1;
	protected int nSolutions = -1;
	protected Stack<Move> solution;
	protected Heuristic heuristic;
	protected int nodeCount;
	protected long start, time;
	
	@Override
	public String getMessage() {
		return message;
	}
	
	/**
	 * Defaults to false (uninformed search)
	 */
	@Override
	public boolean isInformed() {
		return false;
	}
	
	@Override
	public Heuristic getHeuristic() {
		return heuristic;
	}
	
	@Override
	public void setHeuristic(Heuristic heuristic) {
		this.heuristic = heuristic;
	}
	
	protected  Stack<Move> calculateMoves(State from, State to) {
		int depth = 0;
		List<Move> moves = new LinkedList<Move>();
		while (!from.equals(to)) {
			depth++;
			State next = to.getParent();
			moves.add(next.calculateMove(to));
			to = next;
		}
		long millis = System.currentTimeMillis() - start;
		String time = ""+millis ; //Tools.formatTime(millis);
		message = "Solution at depth " + depth + ", " + nodeCount + " nodes visited.  Time: " + time;
		Collections.reverse(moves);
		Stack<Move> stack = new Stack<Move>();
		stack.addAll(moves);
		return stack;
	}
	
	protected Stack<Move> fail() {
		time = System.currentTimeMillis() - start;
		message = "No solution!  " + nodeCount + " nodes visited.  Time: " + time;
		return null;
	}
}
