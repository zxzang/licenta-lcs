package lcsgui;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

import org.ubiety.ubigraph.UbigraphClient;

import edu.uci.ics.jung.graph.Graph;

import lcs.Edge;
import lcs.Environment;
import lcs.EnvironmentFeedback;
import lcs.Position;

public class UBIClient implements EnvironmentFeedback {
	private Environment env;
	
	/**
	 * The client that will communicate with the visualization server.
	 */
	private UbigraphClient ubiClient;
	
	/**
	 * Maps positions from the environment to the UBI representation.
	 */
	private HashMap<Position, Integer> positionToGUI;

	/**
	 * Maps edges from the environment to the UBI representation.
	 */
	private HashMap<Edge, Integer> edgeToGUI;

	@Override
	public void update() {
	}

	@Override
	public void update(Position src, Position dst) {
	}
	
	private void initFrame(String address) {
		
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
			
			edgeToGUI.put(edge, edgeIndex);
		}
	}
	
	public UBIClient(Environment env, String ip) {
		this.env = env;
		initFrame(ip);
		env.addToFeedback(this);
	}
}
