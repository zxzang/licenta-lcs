package lcs;

import java.util.Vector;

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
	 * Basic constructor.
	 * @param pos - positions of the robots within the graph.
	 */
	public Environment(final Vector<Position> pos) {
		this.robotPos = pos;
		this.agents = new Vector<Robot>();
		this.sortTop();
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
	 * Used to topologically sort the graph. 
	 */
	private final void sortTop() {
		
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
