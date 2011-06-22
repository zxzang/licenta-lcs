package graph;

import lcs.Edge;

import org.apache.commons.collections15.Transformer;

import edu.uci.ics.jung.io.graphml.HyperEdgeMetadata;

/**
 * Used to read the input XML.
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
