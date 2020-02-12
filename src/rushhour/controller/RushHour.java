package rushhour.controller;

import java.awt.Point;
import java.io.File;
import java.util.Stack;

import rushhour.exceptions.FileLoadErrorException;
import rushhour.model.game.GameAnalysis;
import rushhour.model.move.Move;
import rushhour.model.state.State;
import rushhour.model.vehicle.VehicleData;
import rushhour.solver.Solver;
import rushhour.solver.heuristic.Heuristic;
import rushhour.tools.FileTools;
import rushhour.tools.MouseStuff;
import rushhour.tools.Tools;
import rushhour.view.GameChooser;
import rushhour.view.GameFrame;
import rushhour.view.GamePanel;
import rushhour.view.GridPanel;
import rushhour.view.VehiclePanel;

public class RushHour {

	private State state;
	private Solver solver;
	private Heuristic heuristic;
	private final MouseStuff mouseStuff;
	private final GridPanel gridPanel;
	private final GamePanel gamePanel;
	private final Stack<Move> undoStack;
	private final Stack<Move> redoStack;
	private Point from;
	private Stack<Move> solution;
	private File file;
	private GameAnalysis game;
	private GameChooser chooser;
	


	private int count = 0;
	
	
	private int movesToEnd = 0; //TODO TEMP

	public RushHour() {
		undoStack = new Stack<Move>();
		redoStack = new Stack<Move>();
		state = null;
		solver = null;
		mouseStuff = new MouseStuff(this);
		gridPanel = new GridPanel(new int[6][6], mouseStuff);
		gamePanel = new GamePanel(gridPanel, mouseStuff);
		VehiclePanel.setAnimationSpeed(5);
	}

	public void solve() {
		setMessage("Solving ...");
		solution = solver.solve(state);
		setMessage(solver.getMessage());
		movesToEnd = solution.size();
	}

	public void play() {
		if (solution != null) {
			mouseStuff.block();
			for (Move move : solution) {
				movesToEnd--;
				doMove(move);
			}
			mouseStuff.unblock();
		} else {
			solve();
			if (solution == null)
				return;
			play();
		}
	}

	public static void main(String[] args) {
		final RushHour controller = new RushHour();
//		System.out.println(controller == null);
		new GameFrame("Rush Hour", controller);
	}

	public Point calculatePosition(VehiclePanel selected, Point point) {
		Point offset = selected.getOffset();
		point.translate(offset.x, offset.y);
		VehicleData vData = state.getVehicleData(selected.id);
		int maxx = (int) (vData.max.x * Tools.WIDTH);
		int minx = (int) (vData.min.x * Tools.WIDTH);
		int maxy = (int) (vData.max.y * Tools.HEIGHT);
		int miny = (int) (vData.min.y * Tools.HEIGHT);
		int x = Math.min(Math.max(minx, point.x), maxx);
		int y = Math.min(Math.max(miny, point.y), maxy);
		return new Point(x, y);
	}

	public void select(VehiclePanel selected) {
		from = selected.getGridLocation();
	}

	private void doMove(Move move) {
		redoStack.clear();
		solution = null;
		undoStack.push(move);
		makeMove(move);
	}

	public void moveSelectedTo(Point to, VehiclePanel selected) {
		selected.moveTo(calculatePosition(selected, to));
	}

	public void releaseSelected(VehiclePanel selected) {
		if (selected != null) {
			Point p = selected.getSnapPoint();
			doMove(new Move(selected.id, from, p));
			while (selected.isMoving()) {
				Tools.wait(100);
			}
			selected = null;
		}
	}

	private void makeMove(Move move) {
		gamePanel.setCount(++count);
		VehiclePanel sel = gridPanel.getVehicle(move.vid);
		sel.glideTo(move.getTo());
		while (sel.isMoving()) {
			Tools.wait(100);
		}
		state.move(move.vid, move.getTo());
	}

	public void back() {
		if (undoStack.size() > 0) {
			count = count -2;
			Move move = undoStack.pop();
			Move move2 = move.reverse();
			makeMove(move2);
			redoStack.push(move);
		}
	}

	public void next() {
		if (redoStack.size() > 0) {
			Move move = redoStack.pop();
			makeMove(move);
			undoStack.push(move);
		} else if (solution != null && solution.size() > 0) {
			Move move = solution.pop();
			makeMove(move);
			undoStack.push(move);
		}
	}

	public GamePanel getGamePanel() {
		return gamePanel;
	}

	public void setMessage(String message) {
		gamePanel.setMessage(message);
		
	}

	public void load() throws FileLoadErrorException {
		if (game != null) {
			reset();
			state.reset();
			int[][] grid = game.getIntGrid();
			for (int y = 0 ; y < 6 ; y++) {
				for(int x = 0 ; x < 6 ; x++) {
					if(grid[x][y] > 0) {
						int vid = grid[x][y];
						if(x < 4 && grid[x+2][y] == vid) {
							state.addVehicle(vid, new Point (x,y), new Point(x+2,y));
							grid[x+2][y] = 0;
							grid[x+1][y] = 0;
						} else if(x < 5 && grid[x+1][y] == vid) {
							state.addVehicle(vid, new Point (x,y), new Point(x+1,y));
							grid[x+1][y] = 0;
						} else if(y < 4 && grid[x][y+2] == vid) {
							state.addVehicle(vid, new Point (x,y), new Point(x,y+2));
							grid[x][y+2] = 0;
							grid[x][y+1] = 0;
						} else if(y < 5 && grid[x][y+1] == vid) {
							state.addVehicle(vid, new Point (x,y), new Point(x,y+1));
							grid[x][y+1] = 0;
						}
					}
				}
			}
			gridPanel.update(state.getGrid());
			setMessage(game.getName());

		} else {
			setMessage("Select a game first ...");
		}
	}
	
	public void setup () {
		if (game!=null && state!=null && state.hashCode() != 888427202) {
			System.out.println(state.hashCode());
			chooser.setHTML(game.getAnalysis(state));
		} else {
			setMessage("Nothing to analyse!");
		}
	}

	public void reload() throws FileLoadErrorException {
		load();
		return;
//		if (file != null) {
//			reset();
//			FileTools.loadFile(state, file);
//			gridPanel.update(state.getGrid());
//			setMessage("Reset " + file.getName());
//		}
	}

	private void reset() {
		state.reset();
		count = 0;
		gamePanel.setCount(count);
		solution = null;
		undoStack.clear();
		redoStack.clear();
	}

	public void setGameAnalysis (GameAnalysis game) {
		this.game = game;
	}
	
	public void setChooser(GameChooser chooser) {
		this.chooser = chooser;
	}
	
	public void setState (State state) {
		System.out.println("Set state");
	//	if(this.state != null && !this.state.equals(state)) {
			this.state = state;
			//TODO - Reset?
	//	}
	}
	
	public void setSolver (Solver solver) {
	//	if(this.solver != null && !this.solver.equals(solver)) {
			this.solver = solver;
			if(solver.isInformed()){
				solver.setHeuristic(heuristic);
			}
			//TODO - Reset?
	//	}
	}
	
	public void setHeuristic (Heuristic heuristic) {
//		if(this.solver != null && solver.isInformed()) {
			this.heuristic = heuristic;
			if(solver.isInformed()){
				solver.setHeuristic(heuristic);
			}
//		}
	}
}
