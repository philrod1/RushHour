package rushhour.view;

import java.awt.Color;
import java.awt.Point;

import rushhour.tools.Tools;

public class RedCarPanel extends CarPanel {

	private static final long serialVersionUID = 1958955296820424744L;

	public RedCarPanel(Color colour, Point origin) {
		super(1, colour, origin, 0);
		image = Tools.instance.loadImage("red.png").getImage();
		setBounds(0,0,image.getWidth(null),image.getHeight(null));
		gridWidth = 2;
		gridHeight = 1;
	}

}
