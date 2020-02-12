package rushhour.view;

import java.awt.Color;
import java.awt.Point;

import rushhour.tools.Tools;

public class TruckPanel extends VehiclePanel {

	private static final long serialVersionUID = 7234977938288155476L;

	public TruckPanel(int id, Color colour, Point origin, int rotation) {
		super(id, colour, rotation);
		image = Tools.getRotatedImage(Tools.instance.loadImage("truck.png").getImage(), rotation);
		setSize(image.getWidth(null),image.getHeight(null));
		if (getWidth() > getHeight()) {
			gridWidth = 3;
			gridHeight = 1;
		} else {
			gridWidth = 1;
			gridHeight = 3;
		}

		setGridLocation(origin);
	}

}
