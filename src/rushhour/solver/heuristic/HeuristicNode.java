package rushhour.solver.heuristic;

import rushhour.model.state.State;

/*
 * This is not a heuristic at all, it is used by the A algorithm
 * I could have made this a private class to A_Star_, but it may
 * be useful elsewhere.
 */
public class HeuristicNode implements Comparable<HeuristicNode> {
	public final int g, h, f;
	public final State state;
	public HeuristicNode (State state, int g, int h) {
		this.state = state;
		this.g = g;
		this.h = h;
		f = g + h;
	}
	
	@Override
	public int compareTo(HeuristicNode that) {
		return this.f - that.f;
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
