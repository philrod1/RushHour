package rushhour.model.game;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

import javax.imageio.ImageIO;

import rushhour.exceptions.FileLoadErrorException;
import rushhour.model.state.SixIntState;
import rushhour.model.state.State;
import rushhour.solver.BreadthFirst;
import rushhour.solver.Solver;
import rushhour.tools.FileTools;
import rushhour.tools.Tools;
import rushhour.view.GridPanel;
import temp.Temp;

public class GameAnalysis {
	private final String file;
	private int minSteps;
	private int size;
//	private int nSolutions;
	private final String name;
	private final GridPanel grid;
	private String imgPath;
	private final int[][] intGrid;
	private Solver solver;
	
	public GameAnalysis(String file, String name) {
		this.file = file;
		this.name = name;
		solver = new BreadthFirst();
		State state = new SixIntState();
		grid = new GridPanel(state.getGrid(), null);
		grid.setSize(430,430);
		try {
			FileTools.loadFile(state, file);
		} catch (FileLoadErrorException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		grid.update(state.getGrid());
		intGrid = state.getGrid();
		try {
		    BufferedImage bi = getSnapshot(); // retrieve image
		    Temp temp = new Temp();
		    URL url = temp.getClass().getResource(".");
		    File dir = null;
		    dir = new File(".");
		    imgPath = dir.getPath() +"/../../" + "preview.png";
		    File outputfile = new File(imgPath);
		    imgPath = outputfile.toURI().toString();
		    ImageIO.write(bi, "png", outputfile);
		} catch (IOException e) {
		   
		}
//		Stack<Move> solution = solver.solve(state);
//		minSteps = solution.size();
//		size = solver.calculateSearchSize(state);
//		nSolutions = solver.nSolutions(state);
	}
	
	private BufferedImage getSnapshot() {
	    return grid.getBufferedImage();
	}

	public String getFile() {
		return file;
	}

	public String getAnalysis(State state) {
		return "";
//		return solver.analyse(state);
	}

	public int getMinSteps() {
		return minSteps;
	}

	public int getSize() {
		return size;
	}

	public String getName() {
		return name;
	}
	
	@Override
	public String toString() {
		return name + " " + minSteps + " " + size;
	}
	
	public String toHTML() {
		String html = "<html>\n";
		html += "\t<Body>\n";
//		html += "\t\tName: " + name + "<br/>\n";
//		html += "\t\tMinimal solution: " + minSteps + " steps<br/>\n";
//		html += "\t\tSearch space size: " + size + " nodes<br/>\n";
//		html += "\t\tSolutions: " + nSolutions + "<br/>\n";
		html += "\t\t<img width=\"200\" height=\"200\" src=\"" + imgPath + "\">";
		html += "\t</body>\n";
		html += "</html>\n";
		return html;
	}
	
	public GridPanel getGridPanel() {
		return grid;
	}

	public int[][] getIntGrid() {
		return Tools.cloneGrid(intGrid);
	}
}
