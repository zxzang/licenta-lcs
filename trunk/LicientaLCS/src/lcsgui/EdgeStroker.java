package lcsgui;

import java.awt.BasicStroke;
import java.awt.Stroke;

import lcs.Edge;

import org.apache.commons.collections15.Transformer;

public class EdgeStroker implements Transformer<Edge, Stroke> {
	
	final static Stroke straightStroke = new BasicStroke();
	final static float dash[] = {10.0f};
	final static Stroke interuptedStroke = new BasicStroke(1.0f, BasicStroke.CAP_ROUND,
			BasicStroke.JOIN_MITER, 10.0f, dash, 0.0f);

	@Override
	public Stroke transform(Edge e) {
		if (e.userVar != null)
			return interuptedStroke;
		return straightStroke;
	}

}
