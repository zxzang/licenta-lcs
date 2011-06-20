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
 * @author Dumitrescu Iustin
 *
 */
public class GraphTransformer implements
		Transformer<GraphMetadata, Graph<Position, Edge>> {
	
	private static final String robotType = "robotType";
	
	Environment env;
	
	@Override
	public final Graph<Position, Edge> transform(
			final GraphMetadata metadata) {
		String type;
		type = metadata.getProperty(robotType);
		
		if (type != null) {
			if (type.equals("Available")){
				env.setRobotTypeAvailable();
			} else if (type.equals("Position")) {
				env.setRobotTypePosition();
			} else if (type.equals("Foresee")) {
				env.setRobotTypeForesee();
			}
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
