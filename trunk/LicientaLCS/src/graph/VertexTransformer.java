package graph;

import lcs.Position;

import org.apache.commons.collections15.Transformer;

import edu.uci.ics.jung.io.graphml.NodeMetadata;

/**
 * @author Dumitrescu Iustin
 *
 */
public class VertexTransformer implements Transformer<NodeMetadata, Position> {

	private static final String nodeObstacle = "nodeObstacle";

	@Override
	public final Position transform(final NodeMetadata metadata) {
		String property = metadata.getProperty(nodeObstacle);
		Position ret;
		if (property.equals("true")) {
			ret = new Position(true);
		} else {
			ret = new Position(false);
		}
		return ret;
	}

}
