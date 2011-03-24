package lcs;

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
		}
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

}
