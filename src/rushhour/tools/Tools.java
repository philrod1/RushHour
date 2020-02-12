package rushhour.tools;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.List;
import java.util.Random;

import javax.swing.ImageIcon;

import res.Res;

public class Tools {
	
	private static Random rng = new Random();
	public final static double WIDTH = 71.38461538461539;
	public final static double HEIGHT = 71.38461538461539;
	public static final Tools instance = new Tools();
	
	public Tools() {}

	public ImageIcon loadImage(String filename) {
		Res res = new Res();
		URL url = res.getClass().getResource(filename);
		if(url == null) { System.out.println(filename);}
			return new ImageIcon(url);
	}
	
	public static String fileSeperator() {
		return System.getProperty("file.separator");
	}
	
	public static Image getRotatedImage (Image image, int degrees) {
		BufferedImage rotatedImage = null;
		
		if(degrees == 0) {
			return image;
		} else if (degrees == 180) {
			rotatedImage = new BufferedImage(image.getWidth(null), image.getHeight(null), BufferedImage.TYPE_INT_ARGB);
			Graphics2D g2d = (Graphics2D) rotatedImage.getGraphics();
			g2d.drawImage(image, 0, 0, null);
			AffineTransform tx = AffineTransform.getScaleInstance(-1, -1);
			tx.translate(-image.getWidth(null), -image.getHeight(null));
			AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
			rotatedImage = op.filter(rotatedImage, null);
			
		} else if (degrees == 90 || degrees == 270)  {
			
			rotatedImage = new BufferedImage(image.getHeight(null), image.getWidth(null), BufferedImage.TYPE_INT_ARGB);
			Graphics2D g2d = (Graphics2D) rotatedImage.getGraphics();
			AffineTransform tx = AffineTransform.getRotateInstance(Math.toRadians(degrees));
			g2d.setTransform(tx);
			
			if (degrees == 270) g2d.drawImage(image, -100, 0, null);
			else g2d.drawImage(image, 0, -image.getHeight(null), null);
		} else {
			return null;
		}
	    
	    return rotatedImage;
	}

	public static void wait(int ms) {
		try {
			Thread.sleep(ms);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static Color colour(int id) {
		int hex = 0;
		switch(id) {
			case 2: hex = 0xFFA500; break;
			case 3: hex = 0x0091F0; break;
			case 4: hex = 0x228B22; break;
			case 5: hex = 0xFF69B4; break;
			case 6: hex = 0xFFD700; break;
			case 7: hex = 0xA0522D; break;
			case 8: hex = 0x8FBC8F; break;
			case 9: hex = 0x9932CC; break;
			case 10: hex = 0xA9A9A9; break;
			case 11: hex = 0xFF8C00; break;
			case 12: hex = 0x87CEFA; break;
			case 13: hex = 0x9ACD32; break;
			case 14: hex = 0x6A5ACD; break;
			case 15: hex = 0xBDB76B; break;
			case 16: hex = 0xDDA0DD; break;
			case 17: hex = 0xFFA07A; break;
			default: hex = 0x4169E1;
		}
		return getColour(hex);
	}
	
	public static Color getColour(int hex) {
		return new Color(hex);
	}
	
	public static int getRandomInt(int max) {
		return rng.nextInt(max);
	}
	
	public static String formatTime(long milliseconds) {
		long time = milliseconds / 1000;
		int seconds = (int) (time % 60);
		int minutes = (int) ((time % 3600) / 60);
//		int hours   = (int) (time / 3600);
		return ((minutes<10)?"0":"") + minutes + ":" 
				+ ((seconds<10)?"0":"") + seconds;
	}
	
	public static int[][] cloneGrid (int[][] original) {
		int[][] clone = new int[6][6];
		for(int y = 0 ; y < 6 ; y++) {
			for(int x = 0 ; x < 6 ; x++) {
				clone[x][y] = original[x][y];
			}
		}
		return clone;
	}
	
	public static <T> T getRandomItem(List<T> list) {
		if (list.size() < 1) return null;
		return list.get(getRandomInt(list.size()));
	}
	
	public static <T> T removeRandomItem(List<T> list) {
		if (list.size() < 1) return null;
		return list.remove(getRandomInt(list.size()));
	}
}
