package lcs;

import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Vector;

import org.apache.log4j.Logger;

/**
 * An abstraction for an agent traversing {@link Environment}.
 */
public class Robot extends Thread {
	
	/**
	 * Class that associates a certain position with the number
	 * of available routes that go out of it.
	 */
	static class PositionNRoutes {
		Position pos;
		int nR;
		public PositionNRoutes(Position x, int y) {
			pos = x;
			nR = y;
		}
	}
	
	/**
	 * Class that associates a certain position with the its reward.
	 */
	static class PositionNReward implements Comparable<PositionNReward> {
		Position pos;
		int reward;
		public PositionNReward(Position x, int y) {
			pos = x;
			reward = y;
		}
		
		@Override
		public int compareTo(PositionNReward o) {
			if (this.reward < o.reward)
				return 1;
			if (this.reward == o.reward)
				return 0;
			return -1;
		}
	}
	
	/**
	 * Current position of the robot.
	 */
	Position current;
	
	/**
	 * Target position of robot.
	 */
	Position target;
	
	/**
	 * Environment where the robot moves.
	 */
	Environment env;
	
	/**
	 * Last steps taken by the robot.
	 */
	LinkedList<PositionNRoutes> lastSteps;
	
	/**
	 * Number of steps the robot remembers.
	 */
	int noStepsBack;
	
	/**
	 * Robot Identification number.
	 */
	int robotId;	
	
	/**
	 * Class logger.
	 */
	private static Logger logger = Logger.getLogger("Robot");
	
	/**
	 * Common barrier used by all agents.
	 */
	private static Barrier bar = Environment.robotBar;
	
	/**
	 * Flag that specifies if this robot has found a path to the goal.
	 */
	private boolean foundPath;
	
	/**
	 * Number of robots instantiated of this class.
	 */
	static int noRobots = 0;
	
	/**
	 * Only wants to move on the best position.
	 */
	public static final int BESTPOSITION = 1;
	
	/**
	 * Will move on the best position available.
	 */
	public static final int BESTAVAILABLE = 2;
	
	/**
	 * Robot that will take into account 2 position further.
	 */
	public static final int FORESEE = 3;
	
	/**
	 * The type of the robot.
	 */
	int type;
	
	/**
	 * Basic constructor
	 * @param robotNum - robot number
	 * @param type - type of robot
	 */
	public Robot(int robotNum, int type) {
		robotId = robotNum;
		this.type = type;
		lastSteps = new LinkedList<PositionNRoutes>();
		Robot.noRobots++;
		this.foundPath = false;
	}
	
	/**
	 * Retrieves the position on which the robot is on.
	 * @return the current position.
	 */
	public Position getCurrentPosition() {
		return this.current;
	}

	/**
	 * Sets the working environment.
	 * @param x - The environment to be set.
	 */
	public void setEnvironment(Environment x) {
		env = x;
	}
	
	/**
	 * Sets the destination of the agent
	 */
	public void setCurrentGoal( Position stop) {
		target = stop;
	}
	
	/**
	 * Sets the start position of the agent
	 */
	public void setStartPosition(Position start) {
		current = start;
	}
	
	/**
	 * Sets the number steps to retreat.
	 * @param nSteps - number of steps to backtrack.
	 */
	protected void setNumberStepsBack(int steps) {
		this.noStepsBack = steps;
	}
	
	@Override
	public void run() {
		logger.debug(getName() + " is acting like a robot \n"+
				"\tand started from " + current + "[" + 
				current.getTopologicPostion() + "]");
		Position nextMove = null;
		Vector<Position> adjacent;
		
		while (current != target) {
			logger.debug("My turn - i am on " + current + "" +
					"\nheading for " + target );
			
			adjacent = env.getAdjacent(current);
			removeDeadEnds(adjacent);
			
			nextMove = null;
			
			if (current.isDeadEnd()){
				nextMove = current.getWayBack();
				logger.debug("I'm on a dead end, going " + nextMove);
			}

			if (adjacent.size() == 1 && nextMove == null) { // we're stuck
				logger.debug(getName() + " is on his way backwards");
				goBackNMark();
			} else {
							
				//	I'll wait to move till I get a free position in which I can move
				if (nextMove == null)
					nextMove = getNextMove(adjacent);

				
				if (nextMove == null) {
					logger.debug(this.getName() + " held his ground");
					
					try {
						bar.enterBarrier();
					} catch (InterruptedException ex) {
						logger.error(this.getName() + "could not enter the barrier");
					}
				} else {
					/**
					 * If this has proven to be a good position reward the 
					 * 	lastSteps with half of my reward.
					 */
					int reward = env.getReducedReward(nextMove);
					
					logger.debug("chose "+nextMove);
					lastSteps.addLast(new PositionNRoutes(current, adjacent.size()));
					if (lastSteps.size() >= noStepsBack)
						lastSteps.removeFirst();
					
					if (reward > 0 && !foundPath) {
						for(PositionNRoutes x : lastSteps)
							//	Will update pheromone only if I am giving a bigger
							//		reward than the one it had before.
							//	Implemented a safeguard procedure in the givePositionFeedback
							//		function so as not not to be greater the reward though.
							if (x.pos.pheromone < reward)
								x.pos.givePositiveFeedback(reward);
						foundPath = true;
					}
					
					env.makeAction(robotId, nextMove);
					updateChosenRule();
					
					current = nextMove;
					
					try {
						bar.enterBarrier();
					} catch (InterruptedException ex) {
						logger.error(this.getName() + "could not enter the barrier");
					}
				}
			}
		}
		
		logger.info("Landed on the promised land!");
		env.removeFromMap(robotId);
		bar.decThreadNum(); //	We must not wait for this robot anymore
	}
	
	/**
	 * Function stub; will only be used on the 2nd level robots
	 */
	public void updateChosenRule(){
		
	}
	
	/**
	 * Returns the best Position to take out of the given neighbors.
	 * @param available - a vector of potential successors.
	 * @return The best position to go to.
	 */
	protected Position getNextMove(Vector<Position> available) {
		switch (this.type) {
		case BESTPOSITION:
			// Robot care vrea neaparat sa se miste pe cea mai buna pozitie
			return getNextMoveAbsolute(available);
		case BESTAVAILABLE:
			// Robotul decide sa urmeze cea mai buna cale libera
			return getNextMoveAvailable(available);
		default:
			logger.error("[Robot.getNextMove] Unknown option " + type);
			return null;
		}
	}
	
	/** 
	 * If possible it reserves the best Position to move into
	 * 	and returns the position.
	 * If not it returns null.
	 * @param available - available Positions to move into
	 * @return null or Position in which to move
	 */
	private Position getNextMoveAbsolute(Vector<Position> available) {
		Position bestPos = null;
		int bestReward = Integer.MIN_VALUE;
		int tempReward;
		
		/**
		 * I will try to get the absolute move.
		 * If I fail I will hold my ground
		 */
		for (Position i: available) {
			tempReward = i.getFeedback() -
				(i.getTopologicPostion() - target.getTopologicPostion());
			if (tempReward > bestReward) {
				bestReward = tempReward;
				bestPos = i;
			}
		}
		
		if (bestPos != null && bestPos.sem.tryAcquire())
			return bestPos;
		else
			return null;
	}
	
	/** 
	 * If possible it reserves the next best Position to move into
	 * 	and returns the position.
	 * If all Positions are taken it returns null.
	 * @param available - available Positions to move into
	 * @return null or Position in which to move
	 */
	private Position getNextMoveAvailable(Vector<Position> available) {
		int tempReward;
		PositionNReward queueElement;
		Position nextMove;
		PriorityQueue<PositionNReward> posQueue =
			new PriorityQueue<PositionNReward>(11);
		
		/**
		 * I will the try the first best move available.
		 * If all my neighbors are occupied I will hold my ground.
		 */
		
		for (Position i: available) {
			tempReward = i.getFeedback() -
				(i.getTopologicPostion() - target.getTopologicPostion());
			queueElement = new PositionNReward(i, tempReward);
			posQueue.add(queueElement);
		}
			
		while (!posQueue.isEmpty()) {
			nextMove = posQueue.poll().pos;
			if (nextMove.sem.tryAcquire()) {
				return nextMove;
			}
		}
		
		return null;
	}
	
	/**
	 * Removes Positions that have proven to be dead ends or part of dead ends
	 * 	from a vector of Positions.
	 * @param adjacent - vector of adjacent Positions of a certain Position
	 */
	void removeDeadEnds(Vector<Position> adjacent) {
		Vector<Position> toRemove = new Vector<Position>();
		for(Position x:adjacent)
			if (x.isDeadEnd())
				toRemove.add(x);
		for(Position x: toRemove)
			adjacent.remove(x);
	}
	
	/**
	 * Goes back a number of memorized steps and rewards negatively
	 * 	all the the Positions on that path.
	 */
	void goBackNMark() {
		// should I block the current node ?
		boolean toBlock = true;
		// used to stock next position on the road back and the number of routes for that position
		PositionNRoutes aux; 
		// the next position in my road back
		Position nextMove;
		// the no. of available routes at me current position
		int noARoutes;
		
		while (!lastSteps.isEmpty()) {
			logger.debug(getName() + " is moving backward");
			
			aux = lastSteps.removeLast();
			nextMove = aux.pos;

			/* Part where I check out how to block certain routes */
			if (toBlock) {
				nextMove.blockRoute(current);
				current.setDeadEnd();// its fully blocked  - aka a deadEnd 
				current.blockRoute(nextMove);
				current.setWayBack(env.getAdjacent(current));
				aux.nR--;
			}
			
			current.giveNegativeFeedback();
			
			noARoutes = aux.nR;
			
			// there is one way .. the way back. Block the position I say
			if (noARoutes <= 1 && toBlock) {
				toBlock = true;			
			} else {// blocked a route but others are available .. position stands
				toBlock = false;
			}
			
			
			/* Actual movement backwards */
			while (!nextMove.sem.tryAcquire()) {
				try {
					logger.debug(getName() + " held his ground " + current +
							"\n\twaiting for a previous " + nextMove +
							" to be free");
					bar.enterBarrier();
				} catch (InterruptedException ex) {
					logger.error(getName() + "could not enter barrier");
				}
				if (nextMove.isDeadEnd()) {
					nextMove = nextMove.getWayBack();
				}
			}
			
			env.makeAction(robotId, nextMove);
			current = nextMove;
			try {
				bar.enterBarrier();
			} catch (InterruptedException ex) {
				logger.error(getName() + "could not enter barrier");
			}
		}
	}
	
	@Override
	public String toString() {
		return this.getName();
	}
	
}
