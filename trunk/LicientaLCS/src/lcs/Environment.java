package lcs;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Vector;

import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.io.GraphIOException;
import edu.uci.ics.jung.io.graphml.GraphMLReader2;
import graph.EdgeTransformer;
import graph.GraphTransformer;
import graph.HyperEdgeTransformer;
import graph.VertexTransformer;

/**
 * Used to synchronize the agents and give them the input they need.
 * @author Iustin Dumitrescu
 *
 */
//asdasd
public class Environment {

	/**
	 * The position for each robot within the environment.
	 */
	Vector<Position> robotPos;

	/**
	 * The target that each robot is going for.
	 */
	Position targetPosition;

	/**
	 * The agents that are currently in the environment.
	 */
	Vector<Robot> agents;
	
	/**
	 * The network in the Environment.
	 */
	Graph<Position, Edge> network;

	/**
	 * Basic constructor.
	 * @param pos - positions of the robots within the graph.
	 * @param input - the filename from which the graph is to be read.
	 */
	public Environment(final Vector<Position> pos, final String input) {
		this.robotPos = pos;
		this.agents = new Vector<Robot>();
		this.sortTop();
		
		try {
			this.getGraph(input);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			System.err.println("The file " + input + " was not found.");
			System.exit(-1);
		} catch (GraphIOException e) {
			e.printStackTrace();
			System.err.println("There was a problem reading"
					+ " the graph from " + input + ".");
			System.exit(-1);
		}
		
	}
	
	/**
	 * Reads and constructs the graph from a GraphML.
	 * @param filename - the filename which will be read.
	 * @throws FileNotFoundException the file was not found.
	 * @throws GraphIOException there was a problem reading the graph.
	 */
	private void getGraph(String filename) throws FileNotFoundException,
			GraphIOException {
		BufferedReader fileR = new BufferedReader(new FileReader(filename));
		
		GraphTransformer graphTransformer = new GraphTransformer();
		
		VertexTransformer vertexTransformer = new VertexTransformer();

		EdgeTransformer edgeTransformer = new EdgeTransformer();
		
		HyperEdgeTransformer hyperEdgeTransformer = new HyperEdgeTransformer();
		
		GraphMLReader2<Graph<Position, Edge>, Position, Edge>
			graphReader = new
			GraphMLReader2<Graph<Position, Edge>, Position, Edge> (
					fileR, graphTransformer, vertexTransformer,
			       edgeTransformer, hyperEdgeTransformer);
		
		this.network = graphReader.readGraph();
	}

	/**
	 * Used to topologically sort the graph.
	 */
	private void sortTop() {
		// TODO
	}

	/**
	 * Adds an agent to the Environment.
	 * @param robot - agent to be added.
	 */
	public final void addAgent(final Robot robot) {
		robot.setCurrentGoal(this.targetPosition);
		robot.setEnvironment(this);
		this.agents.add(robot); // TODO add current position
	}

	/**
	 * Gets all adjacent positions for the specified agent.
	 * This includes anything but obstacles.
	 * @param robot - the index of the agent.
	 * @return A vector of all available positions.
	 */
	public final Vector<Position> getAdjacent(final int robot) {
		return this.getAdjacent(agents.get(robot));
	}

	/**
	 * Gets all adjacent positions for the specified agent.
	 * This includes anything but obstacles.
	 * @param robot - the agent to be used.
	 * @return A vector of all available positions.
	 */
	public final Vector<Position> getAdjacent(final Robot robot) {
		Vector<Position> ret = new Vector<Position>();
		// TODO
		return ret;
	}

	/**
	 * Used by agents to move through the environment.
	 * @param robot - the agent that wants to make an action.
	 * @param dst - the destination desired by the agent.
	 * @return 0 / 1
	 */
	public final int makeAction(final int robot, final Position dst) {
		// TODO
		return 0;
	}

}
