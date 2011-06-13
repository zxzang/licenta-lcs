package lcs;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Scanner;
import java.util.Vector;

import javax.swing.JFrame;

import lcsgui.RewardPanel;
import lcsmain.LcsMain;

import org.apache.log4j.Logger;

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
	 * The number of agents active in the environment.
	 */
	int activeAgents;
	
	/**
	 * The network in the Environment.
	 */
	Graph<Position, Edge> network;
	
	/**
	 * The number of steps a robot will backtrack.
	 */
	public int stepsBack;
	
	/**
	 * Logger from mains
	 */
	private static Logger logger = Logger.getLogger("Environment");
	
	/**
	 * Barrier used to sync robot moves
	 */
	static Barrier robotBar;
	
	/* Debug */
    JFrame rewardFrame;
    RewardPanel rewardPan;
    Scanner sc = new Scanner(System.in);
    /* ----- */
    
	/**
	 * Basic constructor.
	 * @param input - the filename from which the graph is to be read.
	 */
	public Environment(final String input) {
		this.agents = new Vector<Robot>();
		Environment.robotBar = new Barrier();
		
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
		
		//this.sortTop();
		//this.bfs();
		this.dfs();
		
		logger.info("Target position is " + targetPosition + 
				" with top position " + targetPosition.getTopologicPostion());
		
		/* debug purpose */
		
		if (LcsMain.DEBUG) {
			ArrayList<Position> c = new ArrayList<Position>(
					network.getVertices());
			for (Iterator<Position> p = c.iterator(); p.hasNext();) {
				Position pos = p.next();
				logger.debug(pos + " with top " + pos.getTopologicPostion());
			}
	
			try{
				Thread.sleep(10000);
			} catch (InterruptedException ex){}
		}
		
		/*rewardPan = new RewardPanel(this);
		rewardFrame = new JFrame();
		rewardFrame.setSize(400, 200);
		rewardFrame.add(rewardPan);
		rewardFrame.setVisible(true);*/
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
		activeAgents = 0;
		Collection<Position> verts = network.getVertices();
		this.agents = new Vector<Robot>();
		
		/* for each position */
		for (Position p : verts) {
			
			if (p.robotNames == null)
				continue;
			
			/* for each stored robot name */
			for (String name : p.robotNames) {
				Robot r;
				/*XXX asta nu cumva va adauga acelasi tip?
                 * p.robotNames.size != tot numarul de roboti (= n final)
                 */
				if (activeAgents >= percentBestPos * p.robotNames.size())
					r = new Robot(activeAgents, Robot.BESTAVAILABLE);
				else 
					r = new Robot(activeAgents, Robot.BESTPOSITION);
				r.setName(name);
				r.setCurrentGoal(this.targetPosition);
				r.setStartPosition(p);
				r.setEnvironment(this);
				this.agents.add(r);				
				activeAgents++;
				logger.info("Added agent " + r.getName());
			}
			p.robotNames = null;
		}
		Environment.robotBar.setNumThreads(activeAgents);
		this.setRobotPositions();

		//this.setPositionReward();
		this.setStepsBack();
	}
	
	/**
	 * Sets the number of steps an agent is to retreat.
	 */
	private void setStepsBack() {
		int steps = network.getVertexCount() / activeAgents;
		for (Robot agent : agents) {
			agent.setNumberStepsBack(steps);
		}
	}
	
	/**
	 * Sets the reward for the Position class.
	 */
	private void setPositionReward() {
		int nVerts = network.getVertexCount();
		int nAgents = agents.size();
		int reward = 10;
		
		if (nAgents == 0)
		        return;
		
		if (nAgents == 1) {
		        reward = nVerts;
		} else {
		        reward = nVerts / (nAgents - 1);
		}
		logger.debug("Position reward set to " + reward);
		
		Position.setMinReward(reward);
		//targetPosition.givePositiveFeedback(reward * Math.pow(2, arg1));
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
	
	private void bfs() {
		LinkedList<Position> toSearch = new LinkedList<Position>();
		
		targetPosition.setTopologicPostion(0);
		targetPosition.userVar = 1;
		toSearch.add(targetPosition);
		
		while (toSearch.isEmpty() == false) {
			Position p = toSearch.pop();
			Collection<Position> neigh = network.getNeighbors(p);
			
			for (Position n : neigh) {
				if (n.userVar == 0) {
					n.userVar = 1;
					n.setTopologicPostion(p.getTopologicPostion()+1);
					toSearch.add(n);
				}
			}
		}
		
	}
	
	private void dfs() {
		LinkedList<Position> order = new LinkedList<Position>();
		explore(targetPosition, order);
		int i = 0;
		for (Position p : order) {
			//System.out.println(p + " " + i);
			p.setTopologicPostion(i);
			i++;
		}
		
	}
	
	private void explore(Position vert, LinkedList<Position> queue) {
		Collection<Position> neigh = network.getNeighbors(vert);
		vert.userVar = 1;
		for (Position n : neigh) { 
			if (n.userVar == 0) {
				explore(n, queue);
			}
		}
		queue.addFirst(vert);
	}
	
	/**
	 * Used to topologically sort the graph.
	 */
	private void sortTop() {
		
		// --- Replaced this BFS - might suit our needs better
		
		// TODO remove stupid comments after testing code
		// L - Empty list that will contain the sorted nodes
		LinkedList<Position> l = new LinkedList<Position>();
		
		// S - Set of all nodes with no incoming edges
		ArrayList<Position> c = new ArrayList<Position>(
				network.getVertices());
		
		Position pos = null;
		for (Iterator<Position> p = c.iterator(); p.hasNext();) {
			pos = p.next();			
			p.remove();
		}
		c.add(pos);
		
		for (Iterator<Position> p = c.iterator(); p.hasNext();) {
			pos = p.next();
			this.visit(pos, l);
		}
		
		int i = 0;
		for (Iterator<Position> iter = l.iterator(); iter.hasNext(); i++) {
			Position p = iter.next();
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
	 * Starts all agents.
	 */
	public final void startAgents() {
		for (Robot r : agents) {
			logger.debug("Starting " + r.getName());
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
	public final synchronized int makeAction(final int robotId, final Position dst) {
		//logger.debug("MakeAction");
		
		Robot r = agents.get(robotId);		
		Position intialPos = (Position) robotPos.get(robotId);
		
		//	Safeguard - let's not give the semaphore more permits than 1
		if (intialPos.sem.availablePermits() == 0)
			intialPos.sem.release();
		robotPos.set(robotId, dst);
		
		logger.debug(r.getName() + " got to " + dst + 
				"[" + dst.getTopologicPostion() + "]");
		
		//rewardPan.update();
		
		//while(!sc.nextLine().equals(""));
		
		return 0;
	}
	
	public final void removeFromMap(int robotId) {
		targetPosition.sem.release();
		Robot r = agents.get(robotId);
		logger.debug(r.getName() + " ejected from map");
		//robotPos.set(robotId, -1);
		activeAgents--;
		if (activeAgents == 0) {
			logger.info("All agents ejected from map.");
		}
	}

}
