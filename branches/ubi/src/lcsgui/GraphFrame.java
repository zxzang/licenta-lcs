package lcsgui;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import org.ubiety.ubigraph.UbigraphClient;

import edu.uci.ics.jung.graph.Graph;

import lcs.Edge;
import lcs.Environment;
import lcs.Position;

/**
 * A gui for the {@link Environment} type.
 */
public class GraphFrame {
	/**
	 * Serial Version UID.
	 * Eclipse wouldn't shut up about it.
	 */
	private static final long serialVersionUID = 4323232456487666646L;
	
	/**
	 * The {@link Environment} to be displayed.
	 */
	private Environment env;
	
	/**
	 * The client that will communicate with the visualization server.
	 */
	private UbigraphClient ubiClient;
	
	/**
	 * Maps positions from the environment to the ubi representation.
	 */
	private HashMap<Position, Integer> positionToGUI;

	/**
	 * Maps edges from the environment to the ubi representation.
	 */
	private HashMap<Edge, Integer> edgeToGUI;
	
	/**
	 * Constructor for the class.
	 * @param env - the {@link Environment} to be displayed.
	 * @param address - the ubi server for visualization. 
	 */
	public GraphFrame(final Environment env, final String address) {
		this.env = env;
		
		ubiClient = new UbigraphClient("http://" + address + ":20738/RPC2");
		
		ubiClient.clear();
		
		Graph<Position, Edge> graph = this.env.getGraph();
		Collection<Position> positions = graph.getVertices();
		Collection<Edge> edges = graph.getEdges();
		positionToGUI = new HashMap<Position, Integer>(graph.getVertexCount());
		edgeToGUI = new HashMap<Edge, Integer>(graph.getEdgeCount());
		
		for (Position pos : positions) {
			int vert = ubiClient.newVertex();
			positionToGUI.put(pos, vert);
			
			ubiClient.setVertexAttribute(vert, "label", pos.getName());
		}
		
		for (Edge edge : edges) {
			Collection<Position> verts = graph.getIncidentVertices(edge);
			if (verts.size() != 2) {
				System.err.println("This is weird");
			}
			Iterator<Position> posIt = verts.iterator();
			Position vert1 = posIt.next();
			Position vert2 = posIt.next();
			
			int edgeIndex = ubiClient.newEdge(positionToGUI.get(vert1),
					positionToGUI.get(vert2));
			
			// XXX for some reason this doesn't work
			ubiClient.setEdgeStyleAttribute(edgeIndex, "label", edge.toString());
			
			edgeToGUI.put(edge, edgeIndex);
		}
		
		
	}

}
