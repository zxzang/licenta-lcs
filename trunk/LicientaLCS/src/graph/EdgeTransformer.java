package graph;

import lcs.Edge;

import org.apache.commons.collections15.Transformer;

import edu.uci.ics.jung.io.graphml.EdgeMetadata;

/**
 * Used to read the input XML.
 */
public class EdgeTransformer implements Transformer<EdgeMetadata, Edge> {

	@Override
	public final Edge transform(final EdgeMetadata metadata) {
		Edge ret;
		String source = metadata.getSource();
		String target = metadata.getTarget();
		ret = new Edge(source, target);
		ret.setName(metadata.getId());
		return ret;
	}

}
