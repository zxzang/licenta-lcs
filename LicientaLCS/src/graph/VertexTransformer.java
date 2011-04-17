package graph;

import lcs.Position;

import org.apache.commons.collections15.Transformer;

import edu.uci.ics.jung.io.graphml.NodeMetadata;

/**
 * @author Dumitrescu Iustin
 *
 */
public class VertexTransformer implements Transformer<NodeMetadata, Position> {

	/**
	 * The string with which a vertex has its type defined.
	 */
	private static final String NODETYPE = "nodeType";

	@Override
	public final Position transform(final NodeMetadata metadata) {
		String property = metadata.getProperty(NODETYPE);
		
		Position ret;
		
		if (property.equals("obstacle")) {
			ret = new Position(Position.TYPEOBSTACLE);
		} else if (property.equals("final")) {
			ret = new Position(Position.TYPEFINAL);
		} else {
			ret = new Position(Position.TYPENORMAL);
		}
		
		ret.setName(metadata.getId());
		return ret;
	}

}
