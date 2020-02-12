package rushhour.tools;

import java.awt.Point;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.LinkedList;
import java.util.List;

import rushhour.exceptions.FileLoadErrorException;
import rushhour.model.state.State;

public class FileTools {
	
	public static String slurp (InputStream in) {
		try {
		    StringBuffer out = new StringBuffer();
		    byte[] b = new byte[4096];
		    for (int n; (n = in.read(b)) != -1;) {
		        out.append(new String(b, 0, n));
		    }
		    return out.toString();
		} catch (IOException e) {}
		return null;
	}
	
	public static String readFileContents(File file) {
		BufferedReader br = null;
		try {
			br = new BufferedReader(new  FileReader(file));
		} catch (FileNotFoundException e1) {return null;}
		String contents = "";
		String line = null;
		do {
			try {
				line = br.readLine();
			} catch (IOException e) {
				line = null;
			}
			if(line != null) {
				contents += line + "\n";
			}
		} while (line != null);
		return contents;
	}
	
	public static void loadFile (State state, String file) throws FileLoadErrorException {
		state.reset();
		try {
			simulateLoad(state, file);
		} catch (FileLoadErrorException e) {
			throw e;
		}
			String[] br = file.split("\\r?\\n");
			int vid = 2;
			for(String line : br){
				if(line != null) {
					if (!line.startsWith("#")) {
						String[] tokens = line.split("\\s+");
						Point origin = new Point(Integer.parseInt(tokens[1]),Integer.parseInt(tokens[2]));
						int length = (tokens[0].equalsIgnoreCase("truck")) ? 2 : 1;
						Point end;
						if (tokens.length == 4) {
							end = new Point (origin.x,origin.y+length);
						} else {
							end = new Point (origin.x+length,origin.y);
						}
						int id = (tokens[0].equalsIgnoreCase("redcar")) ? 1 : vid++;
						state.addVehicle(id, origin, end);
					}
				}
			}
	
	}
	
	private static boolean invalidLine(String line) {
		return !line.matches("(redcar|car|truck)\\s+[0-9]\\s+[0-9](\\s+rotate|)");
	}

	private static void simulateLoad (State state, String file) throws FileLoadErrorException {
		List<Integer> lineNums = new LinkedList<Integer>();
		boolean success = true;
		int lineNumber = 0;

			String[] br = file.split("\\r?\\n");
			int vid = 2;
			for(String line : br){
				
				if(line != null) {
					if (!line.startsWith("#")) {
						lineNumber++;
						if (invalidLine(line)) throw new FileLoadErrorException("Malformed line in file " + file + " (line " + lineNumber + ")");
						String[] tokens = line.split("\\s+");
						Point origin = new Point(Integer.parseInt(tokens[1]),Integer.parseInt(tokens[2]));
						int length = (tokens[0].equalsIgnoreCase("truck")) ? 2 : 1;
						Point end;
						if (tokens.length == 4) {
							end = new Point (origin.x,origin.y+length);
						} else {
							end = new Point (origin.x+length,origin.y);
						}
						int id = (tokens[0].equalsIgnoreCase("redcar")) ? 1 : vid++;
						boolean result = state.canAddVehicle(id, origin, end);
						success &= result;
						if (!result) lineNums.add(lineNumber);
					}
				}
			}
		if (!success) {
			if (lineNums.size() > 1) {
				throw new FileLoadErrorException("Could not process lines" + getLineNums(lineNums) + ".");
			} else {
				throw new FileLoadErrorException("Could not process line " + lineNums.get(0) + ".");
			}
		}
	}
	
	private static String getLineNums(List<Integer> lineNums) {
		String result = "";
		for (Integer n : lineNums) {
			result += " " + n + ",";
		}
		result = result.substring(0, result.length()-1);
		return result;
	}
//
//	private static boolean addVehicle (int vid, Point origin, Point end) {
//		if (vid < 1) return false;
//		if (origin.equals(end)) return false;
//		if (vehicleExists(vid)) return false;
//		if (!clearToAdd(origin, end)) return false;
//		if (origin.x != end.x && origin.y != end.y) return false;
//		if (end.x - origin.x > 2) return false;
//		if (end.y - origin.y > 2) return false;
//		addVehicleToState(vid, origin, end);
//		return true;
//	}
//	
//	private static boolean vehicleExists(int vid) {
//		for(int y = 0 ; y < 6 ; y++) {
//			for(int x = 0 ; x < 6 ; x++) {
//				if (grid[x][y] == vid) return true;
//			}
//		}
//		return false;
//	}
//	
//	private static boolean clearToAdd(Point origin, Point end) {
//		for(int y = origin.y ; y <= end.y ; y++) {
//			for(int x = origin.x ; x <= end.x ; x++) {
//				if (grid[x][y] > 0) return false;
//			}
//		}
//		return true;
//	}
//	
//	private static void addVehicleToState (int vid, Point origin, Point end) {
//		for(int y = origin.y ; y <= end.y ; y++) {
//			for(int x = origin.x ; x <= end.x ; x++) {
//				grid[x][y] = vid;
//			}
//		}
//	}
	public static void saveFile(int[][] grid, String filename) throws FileNotFoundException {
		String content = "";
		for (int y = 0 ; y < 6 ; y++) {
			for(int x = 0 ; x < 6 ; x++) {
				if(grid[x][y] > 0) {
					int vid = grid[x][y];
					if(x < 4 && grid[x+2][y] == vid) {
						content += "truck\t" + x + " " + y + "\n";
						grid[x+2][y] = 0;
						grid[x+1][y] = 0;
					} else if(x < 5 && grid[x+1][y] == vid) {
						content += ((vid==1)?"red":"") + "car\t" + x + " " + y + "\n";
						grid[x+1][y] = 0;
					} else if(y < 4 && grid[x][y+2] == vid) {
						content += "truck\t" + x + " " + y + " rotate\n";
						grid[x][y+2] = 0;
						grid[x][y+1] = 0;
					} else if(y < 5 && grid[x][y+1] == vid) {
						content += "car\t" + x + " " + y + " rotate\n";
						grid[x][y+1] = 0;
					}
				}
			}
		}
		PrintWriter out = new PrintWriter(filename);
		out.print(content);
		out.close();
	}
}
