package rushhour.view;

import java.awt.Color;
import java.awt.Point;

import rushhour.tools.Tools;

public class CarPanel extends VehiclePanel {

	private static final long serialVersionUID = -2664099520972369290L;

	public CarPanel(int id, Color colour, Point origin, int rotation) {
		super(id, colour, rotation);
		image = Tools.getRotatedImage(Tools.instance.loadImage("car.png").getImage(), rotation);
		setBounds(0,0,image.getWidth(null),image.getHeight(null));
		if (getWidth() > getHeight()) {
			gridWidth = 2;
			gridHeight = 1;
		} else {
			gridWidth = 1;
			gridHeight = 2;
		}

		setGridLocation(origin);
	}

}
