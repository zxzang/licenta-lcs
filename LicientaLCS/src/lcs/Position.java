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
	boolean obstacle;

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
	 * Basic constructor.
	 * @param isObstacle - specifies if this position is a obstacle.
	 */
	public Position(final boolean isObstacle) {
		this.obstacle = isObstacle;
		if (!this.obstacle) {
			this.pheromone = 0;
			this.sem = new Semaphore(1, true);
			this.robot = -1;
			this.topPos = -1;
			this.blockedRoutes = new LinkedList<Position>();
			this.deadEnd = false;
		}
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
		this.pheromone++;
	}

	/**
	 * An agent can inform others that this position was harmful.
	 */
	protected final void giveNegativeFeedback() {
		this.pheromone--;
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
	
	public final void blockRoute(Position blocked){
		blockedRoutes.add(blocked);
	}
	
	public final LinkedList<Position> getBlockedRoutes(){
		return blockedRoutes;
	}
	
	public final boolean isDeadEnd(){
		return deadEnd;
	}
	
	public final void setDeadEnd(){
		deadEnd = true;
	}

}
