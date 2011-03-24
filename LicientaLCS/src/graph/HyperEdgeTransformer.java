package graph;

import lcs.Edge;

import org.apache.commons.collections15.Transformer;

import edu.uci.ics.jung.io.graphml.HyperEdgeMetadata;

/**
 * @author Dumitrescu Iustin
 *
 */
public class HyperEdgeTransformer implements 
		Transformer<HyperEdgeMetadata, Edge> {

	@Override
	public final Edge transform(final HyperEdgeMetadata metadata) {
		Edge ret;
		ret = new Edge();
		return ret;
	}

}
