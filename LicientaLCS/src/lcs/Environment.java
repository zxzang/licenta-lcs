package lcs;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Vector;

import org.apache.log4j.Logger;

import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.io.GraphIOException;
import edu.uci.ics.jung.io.graphml.GraphMLReader2;
import graph.EdgeTransformer;
import graph.HyperEdgeTransformer;

/**
 * Used to synchronize the agents and give them the input they need.
 * @author Iustin Dumitrescu
 *
 */
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
	 * The number of steps a robot will backtrack.
	 */
	protected int stepsBack = 10;
	
	/**
	 * Logger from mains
	 */
	private static Logger logger = Logger.getLogger("Environment");
	
	// TODO consider adding a barrier for sync + a sync function

	/**
	 * Basic constructor.
	 * @param input - the filename from which the graph is to be read.
	 */
	public Environment(final String input) {
		this.agents = new Vector<Robot>();
		
		try {
			this.getGraph(input);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			logger.error("The file " + input + " was not found.");
			System.exit(-1);
		} catch (GraphIOException e) {
			e.printStackTrace();
			logger.error("There was a problem reading"
					+ " the graph from " + input + ".");
			System.exit(-1);
		}
		
		this.getTarget();
		
		this.sortTop(); // XXX happy debuging
		
		// XXX teai gandit mult
		logger.info("Target position is " + targetPosition + 
				" with  top position " + targetPosition.getTopologicPostion());
	}
	
	/**
	 * Searches through the vertices for the one marked as target.
	 */
	private void getTarget() {
		for (Position p : network.getVertices()) {
			if (p.type == Position.TYPEFINAL) {
				this.targetPosition = p;
				break;
			}
		}
	}
	
	/**
	 * Adds agents on the map
	 * @param percentBestPos - the percentage of robots that will act
	 * 		on the bestPosition behavior
	 */
	public void addAgents(double percentBestPos) {
		int n = 0;
		Collection<Position> verts = network.getVertices();
		this.agents = new Vector<Robot>();
		
		/* for each position */
		for (Position p : verts) {
			
			if (p.robotNames == null)
				continue;
			
			/* for each stored robot name */
			for (String name : p.robotNames) {
				Robot r;
				if (n >= percentBestPos * p.robotNames.size())
					r = new Robot(n, Robot.BESTAVAILABLE, stepsBack);
				else 
					r = new Robot(n, Robot.BESTPOSITION, stepsBack);
				r.setName(name);
				r.setCurrentGoal(this.targetPosition);
				r.setStartPosition(p);
				r.setEnvironment(this);
				this.agents.add(r);				
				n++;
				logger.info("Added agent " + r.getName());
			}
			p.robotNames = null;
		}
		
		this.setRobotPositions();
		
	}
	
	/**
	 * Initiates the robotPos vector.
	 */
	private void setRobotPositions() {
		this.robotPos = new Vector<Position>(this.agents.size());
		for (Robot r : agents) {
			robotPos.add(r.getCurrentPosition());
		}
	}

	/**
	 * Set the vector that contains positions for the agents.
	 * @param pos - the position vector.
	 */
	// --- ne mai trebuie asta ?
	// was on the may-delete list
	public final void setRobotPositions(final Vector<Position> pos) {
		this.robotPos = pos;
	}
	
	/**
	 * Reads and constructs the graph from a GraphML.
	 * @param filename - the filename which will be read.
	 * @throws FileNotFoundException the file was not found.
	 * @throws GraphIOException there was a problem reading the graph.
	 */
	private void getGraph(final String filename) throws
			FileNotFoundException, GraphIOException {
		BufferedReader fileR = new BufferedReader(new
			FileReader(filename));
		
		GraphTransformer graphTransformer = new GraphTransformer(this);
		
		VertexTransformer vertexTransformer = new VertexTransformer();

		EdgeTransformer edgeTransformer = new EdgeTransformer();
		
		HyperEdgeTransformer hyperEdgeTransformer = new
			HyperEdgeTransformer();
		
		GraphMLReader2<Graph<Position, Edge>, Position, Edge>
			graphReader = new
			GraphMLReader2<Graph<Position, Edge>, Position, Edge>(
					fileR, graphTransformer,
					vertexTransformer, edgeTransformer,
					hyperEdgeTransformer);
		
		this.network = graphReader.readGraph();
	}

	/**
	 * Used to topologically sort the graph.
	 */
	private void sortTop() {
		// TODO remove stupid comments after testing code
		// L - Empty list that will contain the sorted nodes
		LinkedList<Position> l = new LinkedList<Position>();
		
		// S - Set of all nodes with no incoming edges
		ArrayList<Position> c = new ArrayList<Position>(
				network.getVertices());
		
		for (Iterator<Position> p = c.iterator(); p.hasNext();) {
			Position pos = p.next();
			if (network.inDegree(pos) > 0) {
				p.remove();
			}
		}
		
		for (Iterator<Position> p = c.iterator(); p.hasNext();) {
			Position pos = p.next();
			this.visit(pos, l);
		}
		
		// TODO daca e bine: foreach node setTop
		int i = 0;
		for (Iterator<Position> iter = l.iterator(); iter.hasNext(); i++) {
			Position p = iter.next();
			//System.out.println(p + " " + i);
			p.setTopologicPostion(i);
		}
	}
	
	/**
	 * Part of the topological sort algorithm.
	 * Recursive function that marks nodes.
	 * @param n - the visited node.
	 * @param l - the list of visited nodes.
	 */
	private void visit(final Position n, final LinkedList<Position> l) {
		// if n has not been visited yet then
		if (n.userVar == 0) {
			// mark n as visited
			n.userVar = 1;
			
			Collection<Position> succ = network.getSuccessors(n);
			//for each node m with an edge from n to m do
			for (Iterator<Position> itSucc = succ.iterator();
					itSucc.hasNext();) {
				Position m = itSucc.next();
				// visit(m)
				visit(m, l);
			}
			//add n to L
			l.add(n);
		}
	}

	/**
	 * Adds an agent to the Environment.
	 * @param robot - agent to be added.
	 */
	public final void addAgent(final Robot robot) {
		robot.setCurrentGoal(this.targetPosition);
		robot.setEnvironment(this);
		this.agents.add(robot); // TODO consider removing
	}
	
	/**
	 * Starts all agents.
	 */
	public final void startAgents() {
		for (Robot r : agents) {
			logger.debug("Starting " + r.getName());
			if (r.getName().compareTo("bender") != 0)
				r.start();
		}
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
		Position robPos = robot.getCurrentPosition();
		LinkedList<Edge> outEdges = new LinkedList<Edge>(network.getOutEdges(robPos));
		/*Vector<Position> ret =
			new Vector<Position>(network.getNeighbors(robPos));*/
		Vector<Position> ret = new Vector<Position>(outEdges.size());
		
		for (Edge e : outEdges) {
			ret.add(network.getOpposite(robPos, e));
		}
		
		return ret;
	}
	
	/**
	 * Gets the graph stored in the environment.
	 * @return the internal graph.
	 */
	public final Graph<Position, Edge> getGraph() {
		return this.network;
	}

	/**
	 * Used by agents to move through the environment.
	 * @param robot - the agent that wants to make an action.
	 * @param dst - the destination desired by the agent.
	 * @return 0 / 1
	 */
	public final int makeAction(final int robotId, final Position dst) {
		logger.debug("[Enviroment] MakeAction");
		
		Robot r = agents.get(robotId);		
		robotPos.set(robotId, dst);
		
		logger.debug(r.getName() + " got to "+dst.getTopologicPostion());
		return 0;
	}

}
