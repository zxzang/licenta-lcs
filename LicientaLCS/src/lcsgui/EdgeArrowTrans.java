package lcsgui;

import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.Path2D;

import lcs.Edge;
import lcs.Position;

import org.apache.commons.collections15.Transformer;

import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.util.Context;

public class EdgeArrowTrans implements
		Transformer<Context<Graph<Position, Edge>, Edge>, Shape> {

	Path2D.Double p;
	
	public EdgeArrowTrans() {
		System.err.println("sefdsa");
	}
	
	@Override
	public Shape transform(Context<Graph<Position, Edge>, Edge> c) {
		//c.graph
		if (p == null) {
			p = new Path2D.Double();
			p.lineTo(1, 1);
			p.lineTo(1, 0);
		}
		System.err.println("sfasdfsa");
		return new Rectangle(0, 0, 10, 20);
		//TODO TO THINK
		//return null;
	}

}
