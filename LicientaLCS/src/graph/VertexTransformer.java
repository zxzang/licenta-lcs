package graph;

import lcs.Position;

import org.apache.commons.collections15.Transformer;

import edu.uci.ics.jung.io.graphml.NodeMetadata;

/**
 * @author Dumitrescu Iustin
 *
 */
public class VertexTransformer implements Transformer<NodeMetadata, Position> {

	private static final String nodeType = "nodeType";

	@Override
	public final Position transform(final NodeMetadata metadata) {
		String property = metadata.getProperty(nodeType);
		Position ret;
		if (property.equals("obstacle")) {
			ret = new Position(Position.typeObstacle);
		} else if (property.equals("final")) {
			ret = new Position(Position.typeFinal);
		} else {
			ret = new Position(Position.typeNormal);
		}
		return ret;
	}

}
