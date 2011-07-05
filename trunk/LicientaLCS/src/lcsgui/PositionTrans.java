package lcsgui;

import java.awt.Color;
import java.awt.Paint;

import lcs.Position;

import org.apache.commons.collections15.Transformer;

public class PositionTrans implements Transformer<Position, Paint> {

	@Override
	public Paint transform(Position pos) {
		if (pos.getRobot() == -1)
			return Color.RED;
		return Color.GREEN;
	}


}
