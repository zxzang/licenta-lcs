package graph;


import lcs.Edge;
import lcs.Environment;
import lcs.Position;

import org.apache.commons.collections15.Transformer;

import edu.uci.ics.jung.graph.DirectedSparseGraph;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.UndirectedSparseGraph;
import edu.uci.ics.jung.io.graphml.GraphMetadata;
import edu.uci.ics.jung.io.graphml.GraphMetadata.EdgeDefault;

/**
 * Used to create a graph from a GraphML.
 * @author  Dumitrescu Iustin
 */
public class GraphTransformer implements
		Transformer<GraphMetadata, Graph<Position, Edge>> {

	private static final String StepsBack = "noStepsBack";
	
	/**
	 * @uml.property  name="env"
	 * @uml.associationEnd  
	 */
	Environment env;
	
	@Override
	public final Graph<Position, Edge> transform(
			final GraphMetadata metadata) {
		
		String stepsBack;
		stepsBack = metadata.getProperty(StepsBack);
		
		if (stepsBack != null) {
			int steps;
			try {
				steps = Integer.parseInt(stepsBack);
				env.stepsBack = steps;
			} catch (NumberFormatException e) {}
		}
		
		if (metadata.getEdgeDefault().equals(EdgeDefault.DIRECTED)) {
			return new DirectedSparseGraph<Position, Edge>();
		} else {
			return new UndirectedSparseGraph<Position, Edge>();
		}
	}
	
	public GraphTransformer(Environment env) {
		this.env = env;
	}
}
