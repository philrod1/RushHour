package rushhour.image;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

import res.Res;
import rushhour.tools.FileTools;

public class ImageReader {

	private static int[] points = {16,49,83,116,149,182};
	private static int red = 0xFFEAA6B0;
	private final static int fudge = 10;
	private Point redStart;
	
	public void saveImageAsRH(File image) {
		int[][] grid = new int[6][6];
		int[][] data = new int[6][6];
		int datay = 0, datax = 0;
		BufferedImage img = null;
		redStart = null;
		try {
			img = ImageIO.read(image);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		for(int y : points) {
			for (int x : points) {
				if(isSame(img.getRGB(x, y), red)) {
					System.out.println("RED");
					if(redStart == null) {
						redStart = new Point (datax, datay);
					}
				}
				System.out.println("(" + x + "," + y + ")");
				if(isGrey(img.getRGB(x-9, y-9))){
					img.setRGB(x-9, y-9, 0x00FF0000);
					data[datax][datay] |= 8;
				} else if(isGrey(img.getRGB(x-8, y-8))){
					img.setRGB(x-8, y-8, 0x00FF0000);
					data[datax][datay] |= 8;
				}
				printRGB(img.getRGB(x-9, y-9));
				if(isGrey(img.getRGB(x+10, y-9))){
					img.setRGB(x+10, y-9, 0x00FF0000);
					data[datax][datay] |= 4;
				} else if(isGrey(img.getRGB(x+9, y-8))){
					img.setRGB(x+9, y-8, 0x00FF0000);
					data[datax][datay] |= 4;
				}
				printRGB(img.getRGB(x+9, y-9));
				if(isGrey(img.getRGB(x-9, y+10))){
					img.setRGB(x-9, y+10, 0x00FF0000);
					data[datax][datay] |= 2;
				} else if(isGrey(img.getRGB(x-8, y+9))){
					img.setRGB(x-8, y+9, 0x00FF0000);
					data[datax][datay] |= 2;
				}
				printRGB(img.getRGB(x-9, y+9));
				if(isGrey(img.getRGB(x+10, y+10))){
					img.setRGB(x+10, y+10, 0x00FF0000);
					data[datax][datay] |= 1;
				} else if(isGrey(img.getRGB(x+9, y+9))){
					img.setRGB(x+9, y+9, 0x00FF0000);
					data[datax][datay] |= 1;
				}
				printRGB(img.getRGB(x+10, y+10));
				System.out.println();
				datax++;
			}
			datax = 0;
			datay++;
		}
		
		print(data);
		
		int vid = 1;
		for(int y = 0 ; y < 6 ; y++) {
			for(int x = 0 ; x < 6 ; x++) {
				
				if(data[x][y] == 0xA) {	//Horizontal start
					vid++;
					if(data[x+1][y] == 0x5) { // Horizontal car
						if(redStart.x == x && redStart.y == y) { // red car
							grid[x][y] = 1;
							grid[x+1][y] = 1;
						} else {
							grid[x][y] = vid;
							grid[x+1][y] = vid;
						}
						data[x][y] = 0;
						data[x+1][y] = 0;
					} else if(data[x+2][y] == 0x5) { //Horizontal truck
						grid[x][y] = vid;
						grid[x+1][y] = vid;
						grid[x+2][y] = vid;
						data[x][y] = 0;
						data[x+1][y] = 0;
						data[x+2][y] = 0;
					} else {
						System.out.println("Error at (" + x + ", " + y + ")");
					}
				}
				
				if(data[x][y] == 0xC) {	//Vertical start
					vid++;
					if(data[x][y+1] == 0x3) { // Vertical car
						grid[x][y] = vid;
						grid[x][y+1] = vid;
						data[x][y] = 0;
						data[x][y+1] = 0;
					} else if(data[x][y+2] == 0x3) { //Vertical truck
						grid[x][y] = vid;
						grid[x][y+1] = vid;
						grid[x][y+2] = vid;
						data[x][y] = 0;
						data[x][y+1] = 0;
						data[x][y+2] = 0;
					} else {
						System.out.println("Error at (" + x + ", " + y + ")");
					}
				}
			}
		}
//		print(grid);
//		GridPanel gp = new GridPanel(grid, null);
//		gp.setSize(420,420);
//		JFrame frame = new JFrame(image.getName());
//		frame.setPreferredSize(new Dimension(870,465));
////		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//		frame.getContentPane().setLayout(new GridLayout(1, 2));
//		frame.getContentPane().add(new ImagePanel(img),0);
//		frame.getContentPane().add(gp,1);
//		frame.pack();
//		frame.setVisible(true);
		try {
			FileTools.saveFile(grid, image.getName().split("\\.")[0] + ".rh");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private int average(int p2, int p3, int p4, int p5) {
		int r = (p2 >>> 16) & 255;
		r += (p3 >>> 16) & 255;
		r += (p4 >>> 16) & 255;
		r += (p5 >>> 16) & 255;
		r /= 4;
		r = r << 16;
		
		int g = (p2 >>>8) & 255;
		g += (p3 >>> 8) & 255;
		g += (p4 >>> 8) & 255;
		g += (p5 >>> 8) & 255;
		g /= 4;
		g = g << 8;
		
		int b = p2 & 255;
		b += p3 & 255;
		b += p4 & 255;
		b += p5 & 255;
		b /= 4;
		
		int average = 0xFF000000;
		average |= r;
		average |= g;
		average |= b;
		
		return average;
	}
	
	private void printRGB(int p1) {
		int r1 = (p1 >>> 16) & 255;
		int g1 = (p1 >>> 8) & 255;
		int b1 = p1 & 255;
		System.out.println("  " + r1 + " " + b1 + " " + g1);
	}
	
	private boolean isGrey(int p1) {
		int r1 = (p1 >>> 16) & 255;
		int g1 = (p1 >>> 8) & 255;
		int b1 = p1 & 255;
		return r1 == b1 && r1 == g1 && r1 < 250;
	}
	
	private boolean isSame(int p1, int p2) {
		int r1 = (p1 >>> 16) & 255;
		int g1 = (p1 >>> 8) & 255;
		int b1 = p1 & 255;
		int r2 = (p2 >>> 16) & 255;
		int g2 = (p2 >>> 8) & 255;
		int b2 = p2 & 255;
		return r2 < r1+fudge && r2 > r1-fudge && g2 < g1+fudge && g2 > g1-fudge && b2 < b1+fudge && b2 > b1-fudge;
	}
	
	private boolean isRed(int p2) {
		int r1 = (red >>> 16) & 255;
		int g1 = (red >>> 8) & 255;
		int b1 = red & 255;
		int r2 = (p2 >>> 16) & 255;
		int g2 = (p2 >>> 8) & 255;
		int b2 = p2 & 255;
		return r2 < r1+fudge && r2 > r1-fudge && g2 < g1+fudge && g2 > g1-fudge && b2 < b1+fudge && b2 > b1-fudge;
	}

	public static void main(String[] args) {
		ImageReader ir = new ImageReader();
		Res res = new Res();
		URL url = res.getClass().getResource("extras");
		System.out.println(url.toExternalForm());
		File file = null;
		try {
			file = new File(url.toURI());
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		File[] files = file.listFiles();
		for (File imageFile : files) {
			if (imageFile.getName().endsWith(".png")) {
				System.out.println(imageFile.toString());
				ir.saveImageAsRH(imageFile);
			}
		}
	}

	private static void print(int[][] grid) {
		for (int y = 0; y < 6; y++) {
			for (int x = 0; x < 6; x++) {
				String c;
				if(grid[x][y] > 0) {
					if(grid[x][y] < 10) {
						c = "" + grid[x][y];
					} else {
						c = "" + (char)(grid[x][y]+55);
					}
				} else {
					c = ""+(char)183;
				}
				System.out.print(c + " ");
			}
			System.out.println();
		}
		System.out.println();
	}
	
	private class ImagePanel extends JPanel {
		private static final long serialVersionUID = -6235181740329588207L;
		private BufferedImage bimg;
		public ImagePanel (BufferedImage bimg) {
			this.bimg = bimg;
			setPreferredSize(new Dimension(420, 420));
		}
		public void paint(Graphics g) {
			g.drawImage(bimg,0,0,420,420,null);
		}
	}
}
