package lcsgui;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;

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
	 * Used to remember the direction of a edge.
	 * Required for proper arrow orientation.
	 */
	class EdgeDirection {
		public int edgeIndex;
		public Position src;
		public Position dst;
		public EdgeDirection(int index, Position src, Position dst) {
			edgeIndex = index;
			this.src = src;
			this.dst = dst;
		}
	}

	/**
	 * Maps edges from the environment to the UBI representation.
	 */
	private HashMap<Edge, EdgeDirection> edgeToGUI;
	
	/**
	 * Retains what is needed to change.
	 */
	class PositionPair {
		public Position src;
		public Position dst;
		public EdgeDirection edgeDir;
		public PositionPair(Position src, Position dst) {
			this.src = src;
			this.dst = dst;
			edgeDir = findEdge(src, dst);
		}
		public boolean isSameDir() {
			return src == edgeDir.src && dst == edgeDir.dst;
		}
	}
	
	/**
	 * The edges to animate.
	 * This is used so we won't have to do them all sequential.
	 */
	private LinkedList<PositionPair> toChange;
	
	/**
	 * Holds the edge styles for the arrow.
	 */
	private int styles[];
	
	/**
	 * Reverse arrowhead.
	 */
	private int reverseStyles[];
	
	/**
	 * Styles used for vertices.
	 */
	private int ocupiedPosStyle;
	
	/**
	 * Finds the edge that connects 2 positions.
	 * @param src - First {@link Position}.
	 * @param dst - Second {@link Position}.
	 * @return - the edge direction for the UBI client.
	 */
	public EdgeDirection findEdge(Position src, Position dst) {
		Edge e = env.getGraph().findEdge(src, dst);
		return edgeToGUI.get(e);
	}
	
	@Override
	public void update() {
	}
	
	@Override
	public void update(Position src, Position dst) {
		toChange.add(new PositionPair(src, dst));
	}
	
	@Override
	public void change() {
		/* How much one change function should take. */
		int timeMs = MainGui.timeMs;
		int steps = styles.length;
		final int sleep = timeMs / steps;
		
		for (int i = 0; i < steps; i++) {
			for (PositionPair pair : toChange) {
				int edgeStyle;
				if (pair.isSameDir())
					edgeStyle = styles[i];
				else
					edgeStyle = reverseStyles[i];
					
				ubiClient.changeEdgeStyle(pair.edgeDir.edgeIndex, edgeStyle);
				ubiClient.changeVertexStyle(positionToGUI.get(pair.src), 0);
			}
			
			try {
				Thread.sleep(sleep);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		/* Set them all back to the default style. */
		for (PositionPair pair : toChange) {
			ubiClient.changeEdgeStyle(pair.edgeDir.edgeIndex, 0);
			ubiClient.changeVertexStyle(positionToGUI.get(pair.dst), ocupiedPosStyle);
		}
		
		toChange.clear();
	}
	
	@Override
	public void clear(Position pos) {
		System.err.println("sadas");
		ubiClient.changeVertexStyle(positionToGUI.get(pos), ocupiedPosStyle);
	}
	
	/**
	 * Initializes the connection to the UBI server.
	 * @param address - address of the server.
	 */
	private void initFrame(String address) {
		
		ubiClient = new UbigraphClient("http://" + address + ":20738/RPC2");
		
		ubiClient.clear();
		
		Graph<Position, Edge> graph = this.env.getGraph();
		Collection<Position> positions = graph.getVertices();
		Collection<Edge> edges = graph.getEdges();
		positionToGUI = new HashMap<Position, Integer>(graph.getVertexCount());
		edgeToGUI = new HashMap<Edge, EdgeDirection>(graph.getEdgeCount());
		
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
			
			EdgeDirection eDir = new EdgeDirection(edgeIndex, vert1, vert2);
			
			edgeToGUI.put(edge, eDir);
		}
	}
	
	/**
	 * Creates styles for the arrow on the edges.
	 */
	private void createStyles() {
		/* Number of steps the arrow will make. */
		final int steps = 20;
		styles = new int[steps];
		reverseStyles = new int[steps];
		
		for (int i = 0; i < steps; i++) {
			styles[i] = ubiClient.newEdgeStyle(0);
			ubiClient.setEdgeStyleAttribute(styles[i], "arrow", "true");
			double x = i;
			ubiClient.setEdgeStyleAttribute(styles[i], "arrow_position", "" + (x/steps));
			/* Set the color to red. Change to other if you want. */
			ubiClient.setEdgeStyleAttribute(styles[i], "color", "#ff0000");
			
			/* Create a new style with the arrow head in the other way. */
			reverseStyles[i] = ubiClient.newEdgeStyle(styles[i]);
			ubiClient.setEdgeStyleAttribute(reverseStyles[i], "arrow_reverse", "true");
			ubiClient.setEdgeStyleAttribute(reverseStyles[i], "arrow_position", "" + (1 - x/steps));
		}
		
		ocupiedPosStyle = ubiClient.newVertexStyle(0);
		ubiClient.setVertexStyleAttribute(ocupiedPosStyle, "color", "#ff0000");
	}
	
	public UBIClient(Environment env, String ip) {
		this.env = env;
		initFrame(ip);
		createStyles();
		toChange = new LinkedList<UBIClient.PositionPair>();
		env.addToFeedback(this);
	}
}
