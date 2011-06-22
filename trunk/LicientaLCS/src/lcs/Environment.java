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
	public Vector<Robot> agents;
	
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
	
	int robotType = Robot.BESTAVAILABLE;
	
	Vector<EnvironmentFeedback> feedback;
	
	/* Debug */
	Scanner sc = new Scanner(System.in);
	/* ----- */
	
	/**
	 * Basic constructor.
	 * @param input - the filename from which the graph is to be read.
	 */
	public Environment(final String input) {
		this.agents = new Vector<Robot>();
		this.feedback = new Vector<EnvironmentFeedback>(1);
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
	public void addAgents() {
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
				if (robotType == Robot.FORESEE) {
					r = new RobotL2(activeAgents);
				} else {
					r = new Robot(activeAgents, robotType);
				}
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

		this.setPositionReward();
		this.setStepsBack();
	}
	
	/**
	 * Sets the number of steps an agent is to retreat.
	 */
	private void setStepsBack() {
		int steps = network.getVertexCount() / activeAgents;
		logger.info("Number of steps back " + steps);
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
		
		/* Hard to believe we will have so many vertices so as
		 * maxReward to go over	maxInt.
		 */
		reward = nVerts;
		logger.debug("Position reward set to " + reward);
		
		Position.setMinReward(reward);
		targetPosition.givePositiveFeedback(reward + reward * 2 * nVerts);
		giveFeedback();
		
		logger.debug("Target Position reward set to " + (reward + reward * 2 * nVerts));
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
	
	/**
	 * Traverses the graph with a DFS algorithm and assigns a
	 * sorting order. Starts from the targetPosition.
	 */
	private void dfs() {
		LinkedList<Position> order = new LinkedList<Position>();
		explore(targetPosition, order);
		int i = 0;
		for (Position p : order) {
			p.setTopologicPostion(i);
			i++;
		}
		
	}
	
	/**
	 * Used by the DFS algorithm to explore a given node.
	 * @param vert - the node to be explored.
	 * @param queue - the queue containing the explored nodes.
	 */
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
		return this.getAdjacent(agents.get(robot).getCurrentPosition());
	}

	/**
	 * Gets all adjacent positions for the specified position.
	 * This includes anything but obstacles.
	 * @param position
	 * @return A vector of all available positions.
	 */
	public final Vector<Position> getAdjacent(final Position robPos) {		
		Vector<Position> ret =
			new Vector<Position>(network.getNeighbors(robPos));
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
		
		Robot r = agents.get(robotId);		
		Position intialPos = (Position) robotPos.get(robotId);
		
		//	Safeguard - let's not give the semaphore more permits than 1
		if (intialPos.sem.availablePermits() == 0)
			intialPos.sem.release();
		robotPos.set(robotId, dst);
		
		giveFeedback(intialPos, dst);
		
		logger.debug(r.getName() + " got to " + dst + 
				"[" + dst.getTopologicPostion() + "]");
		
		while(!sc.nextLine().equals(""));
		
		return 0;
	}
	
	/**
	 * Informs the environment that the robot has reached the final position
	 * and therefore should be considered removed from the graph.
	 * @param robotId - the robot to be removed.
	 */
	public final void removeFromMap(int robotId) {
		targetPosition.sem.release();
		Robot r = agents.get(robotId);
		logger.debug(r.getName() + " ejected from map");
		
		activeAgents--;
		
		if (activeAgents == 0) {
			logger.info("All agents ejected from map.");
		}
	}
	
	public int getReducedReward(Position target) {
		int res = target.pheromone;
		int nVerts = network.getVertexCount();
		
		res -= 2 * nVerts;
		
		return res;
	}
	
	/**
	 * Sets the robots of the environment to BESTPOSITION.
	 */
	public void setRobotTypePosition() {
		robotType = Robot.BESTPOSITION;
	}

	/**
	 * Sets the robots of the environment to BESTAVAILABLE.
	 */
	public void setRobotTypeAvailable() {
		robotType = Robot.BESTAVAILABLE;
	}
	
	/**
	 * Sets the robots of the environment to FORESEE.
	 */
	public void setRobotTypeForesee() {
		robotType = Robot.FORESEE;
	}
	
	/**
	 * Updates the chosen rule's fitness
	 * @param choseRule - the rule after which the agent will move
	 */
	public void giveReward(LCSRule chosenRule) {
		int maxReward = Integer.MIN_VALUE;
		Vector<Position> ruleAdjancies = getAdjacent(chosenRule.getNext());
		Position auxiliary;
		
		for(Position x : ruleAdjancies) {
			int distanceFromTarget = Math.abs(x.getTopologicPostion() - 
					targetPosition.getTopologicPostion());
			if (x.getFeedback() - distanceFromTarget > maxReward)
				maxReward = x.getFeedback() - distanceFromTarget;
		}
		
		auxiliary = chosenRule.getCurrent();
		int distanceFromTarget = 
			Math.abs(auxiliary.getTopologicPostion() - 
				targetPosition.getTopologicPostion());
		if (auxiliary.getFeedback() - distanceFromTarget > maxReward)
			maxReward = auxiliary.getFeedback() - distanceFromTarget;
		
		if (maxReward > chosenRule.getFitness())
			chosenRule.setFitness(maxReward);
	}
	
	public void addToFeedback(EnvironmentFeedback fb) {
		this.feedback.add(fb);
	}

	private void giveFeedback() {
		for (EnvironmentFeedback fb : this.feedback) {
			fb.update();
		}
	}
	
	private void giveFeedback(Position src, Position dst) {
		for (EnvironmentFeedback fb : this.feedback) {
			fb.update(src, dst);
		}
	}
	
}
