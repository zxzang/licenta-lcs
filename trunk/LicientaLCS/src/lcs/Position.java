package lcs;

import java.util.LinkedList;
import java.util.concurrent.Semaphore;

/**
 * Marks a node within the graph.
 * @author Iustin Dumitrescu
 *
 */
public class Position {
	/**
	 * To protect from concurrent changes.
	 */
	Semaphore sem;

	/**
	 * The value left by other agents.
	 */
	int pheromone;
	
	/**
	 * The value of the reward left by one agent.
	 * Ideally: (number of agents - 1) * reward > number of nodes 
	 * 		logic : the robot will have to choose the position with the highest
	 * 				(pheromone - distance between nodes) value
	 * 				if we don't have the condition, the above difference between
	 * 				the first and last node topologically sorted will always be
	 * 				negative 
	 */
	int reward;
	
	/**
	 * The blocked routes from this position.
	 * A type of marking left by robots on the graph edges
	 */
	LinkedList<Position> blockedRoutes;
	
	/**
	 * Is this node a dead-end or not ?
	 * Also, this will be established by one or more agents.
	 */
	boolean deadEnd;

	/**
	 * Indicates if there is a physical, unmovable obstacle within
	 * this place.
	 */
	public static final int typeNormal = 0;
	public static final int typeObstacle = 1;
	public static final int typeFinal = 2;
	int type;

	/**
	 * Indicates which robot is currently occupying this position.
	 * -1 for no agent.
	 */
	int robot;

	/**
	 * The place this position holds within the topological sorting of the
	 * graph.
	 */
	private int topPos;
	
	/**
	 * The name of the node.
	 * Used for GraphML.
	 */
	private String namePos;
	
	/**
	 * A user operated integer.
	 * Can be used for topological sort.
	 */
	public int userVar;

	/**
	 * Basic constructor.
	 * @param isObstacle - specifies if this position is a obstacle.
	 */
	public Position(final int type) {
		this.type = type;
		switch (this.type) {
		case typeFinal:
			this.sem = new Semaphore(1, true);
			this.blockedRoutes = new LinkedList<Position>();
			this.deadEnd = false;
			break;
		case typeNormal:
			this.sem = new Semaphore(1, true);
			this.blockedRoutes = new LinkedList<Position>();
			this.deadEnd = false;
			break;
		case typeObstacle:
			break;
		default:
			System.err.println("[Position.Position]Unknown option " + this.type);
			System.exit(-1);
			break;
		}
		this.robot = -1;
		this.topPos = -1;
		this.pheromone = 0;
		this.userVar = 0;
	}

	/**
	 * Set the name of this position.
	 * @param name - name to be set.
	 */
	public final void setName(final String name) {
		this.namePos = name;
	}

	/**
	 * Get the name of this position.
	 * @return The name of the position.
	 */
	public final String getName() {
		return this.namePos;
	}

	/**
	 * An agent can inform others that this position was beneficial.
	 */
	protected final void givePositiveFeedback() {
		this.pheromone += reward;
	}

	/**
	 * An agent can inform others that this position was harmful.
	 */
	protected final void giveNegativeFeedback() {
		this.pheromone -= reward;
	}

	/**
	 * Gives the current feedback of this position.
	 * @return feedback left by previous agents.
	 */
	public final int getFeedback() {
		return this.pheromone;
	}


	/**
	 * Used by the Environment to specify a position within the
	 * topological sort.
	 * @param position - the position from the sorting.
	 */
	protected final void setTopologicPostion(final int position) {
		this.topPos = position;
	}

	/**
	 * Used by the Environment to retrieve the topological postion.
	 * @return the sort position.
	 */
	protected final int getTopologicPostion() {
		return this.topPos;
	}
	
	/**
	 * Adds a Position to the blocked vector.
	 * @param blocked - the position to be blocked.
	 */
	public final void blockRoute(Position blocked){
		if (!blockedRoutes.contains(blocked))
			blockedRoutes.add(blocked);
	}
	
	/**
	 * Gets the blocked routes from this position.
	 * @return a LinkedList of the blocked routes.
	 */
	public final LinkedList<Position> getBlockedRoutes(){
		return blockedRoutes;
	}
	
	public final boolean isDeadEnd(){
		return deadEnd;
	}
	
	public final void setDeadEnd(){
		deadEnd = true;
	}
	
	/**
	 * Gives a string representation of the node.
	 */
	public String toString() {
		return this.namePos;
	}

}
