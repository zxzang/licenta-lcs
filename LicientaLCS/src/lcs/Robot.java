package lcs;

import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Vector;

/**
 * 
 * @author Dascalu Sorin
 *
 */
public class Robot implements Runnable {

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
			//return o.reward - this.reward;
			if (this.reward < o.reward)
				return 1;
			if (this.reward == o.reward)
				return 0;
			return -1;
		}
	}
	
	Position current;
	Position target;
	Environment env;
	LinkedList<PositionNRoutes> lastSteps;
	int noStepsBack;
	int no;
	private String name;
	
	/**
	 * 	Robot type
	 * 		1  -  Only wants to move on the best position
	 * 		2  -  Will move on the best position available
	 */
	public static final int BESTPOSITION = 1;
	public static final int BESTAVAILABLE = 2;
	int type;
	
	/**
	 * Basic constructor
	 * @param robotNum - robot number
	 * @param type - type of robot
	 * @param nSteps - number of steps to backtrack.
	 */
	public Robot(int robotNum, int type, int nSteps) {
		no = robotNum;
		this.type = type;
		noStepsBack = nSteps;
		lastSteps = new LinkedList<PositionNRoutes>();
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
	
	/*
	 * Sets the destination of the agent
	 */
	public void setCurrentGoal( Position stop) {
		target = stop;
	}
	
	/*
	 * Sets the start position of the agent
	 */
	public void setStartPosition(Position start) {
		current = start;
	}
	
	/**
	 * Gives the robot a name.
	 * @param name - the string to be associated.
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	/**
	 * Returns the name of the robot.
	 * If the name isn't set it will return the currentThread.
	 * @return a string representation of the robot.
	 */
	public String getName() {
		if (this.name == null)
			return Thread.currentThread().toString();
		return this.name;
	}
	
	@Override
	public void run() {
		System.out.println(getName() + " is acting like a robot");
		Position nextMove = null;
		Vector<Position> adjacent;
		
		while (current != target) {
			
			adjacent = env.getAdjacent(this);
			removeDeadEnds(adjacent);
			if (adjacent.size() == 1) { // we're stuck
				goBackNMark();
			} else {
				nextMove = null;
				
				//	I'll wait to move till I get a free position in which I can move
				nextMove = getNextMove(adjacent);
				
				/**
				 * XXX This is error prone.
				 * Situatie: [A][B][0] un rand numerotat 1-3.
				 * A care se afla pe pozitia 1 ar vrea sa treaca pe poz 2.
				 * B vrea pe pozitia 3 care e libera.
				 * initial A o sa tryAquire pe 2, o sa fail + o sa piarda o tura.
				 * B o sa treaca pe 3, astfel 2 va fi liber.
				 * Tot consider ca ar trebui sa se faca o buna parte in env
				 * astfel robotii sa mearga la aceasi viteza.
				 * Daca nu vrei sa mearga la aceasi viteza si sa lasam la 
				 * mana schedelurului thats dandy.
				 * give opinion.
				 * As vrea chiar sa fac un package pt robot + rules. This isnt mandatory.
				 */
				
				lastSteps.addLast(new PositionNRoutes(current, adjacent.size()));
				if (lastSteps.size() >= noStepsBack)
					lastSteps.removeFirst();
				env.makeAction(no, nextMove);
				
				current.sem.release();
				current = nextMove;
				
				//	Presupunem ca ar fi o pozitie buna daca tot am ajuns in ea
				//	Cand se va dovedi ca nu e buna o sa ii dam feedback negativ si se va anula efectul benefic
				current.givePositiveFeedback();
				/**
				 * XXX aici ii dai un pos feedback de reward.
				 * Mai incolo ii scazi tot atat. In final un drum prost o sa aiba 0
				 * si nu ceva < 0.
				 */
			}
		}
		
		System.out.println("Landed on the promised land!");
	}
	
	/**
	 * Returns the best Position to take out of the given neighbors.
	 * @param available - a vector of potential successors.
	 * @return The best position to go to.
	 */
	Position getNextMove(Vector<Position> available) {
		switch (this.type) {
		case BESTPOSITION:
			// Robot care vrea neaparat sa se miste pe cea mai buna pozitie
			return getNextMoveAbsolute(available);
		case BESTAVAILABLE:
			// Robotul decide sa urmeze cea mai buna cale libera
			return getNextMoveAvailable(available);
		default:
			System.err.println("[Robot.getNextMove] Unknown option " + type);
			return null;
		}
	}
	
	Position getNextMoveAbsolute(Vector<Position> available) {
		System.out.println("eu " + getName() + " cer mutare absolute best");
		Position bestPos = null;
		// FIXME what?! MIN_VALUE e deja negativ, you sure?
		int bestReward = -Integer.MIN_VALUE;
		int tempReward;
		
		// If I fail to acquire a position I will recompute
		// the bestPosition until it is available to me
		// logic: the bestPosition might dynamically change - what is good now might
		//	      not be good 2 steps from now when it will be available
		while (true) {
			for (Position i: available) {
				tempReward = i.getFeedback() -
					(i.getTopologicPostion() - target.getTopologicPostion());
				if (tempReward > bestReward) {
					bestReward = tempReward;
					bestPos = i;
				}
			}
			
			/* not sure I'm licking this
			 * poate ar trebui sa intoarca o pozitie si sa faca acquire
			 * in functia principala?
			 * toti roboti o sa comunice cu env. asa vad eu
			 * nu o sa fie unul mai rapid ca altul
			 * o sa fie sincronizati printr-o bariera.
			 * discuss.
			 */
			if (bestPos.sem.tryAcquire())
				return bestPos;
		}
	}
	
	Position getNextMoveAvailable(Vector<Position> available) {
		System.out.println("eu " + getName() + " cer mutare best available");
		int tempReward;
		PositionNReward queueElement;
		Position nextMove;
		PriorityQueue<PositionNReward> posQueue =
			new PriorityQueue<PositionNReward>(11);
		
		//	Maybe all of my available positions are taken
		//	If that happens, priorities might change until my next iteration through 
		//		the positions, so I might as well compute them again
		while (true) {
			// Priority queue used to store the order of positions
			// The order is from greatest to lowest, so the comparator must be implemented like below			 
			
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
		}
		
	}
	
	void removeDeadEnds(Vector<Position> adjacent) {
		Vector<Position> toRemove = new Vector<Position>();
		for(Position x:adjacent)
			if (x.isDeadEnd())
				toRemove.add(x);
		for(Position x: toRemove)
			adjacent.remove(x);
	}
	
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
			
			aux = lastSteps.removeLast();
			nextMove = aux.pos;

			/*
			 *  Part where I check out how to block certain routes
			 */
			if (toBlock) {
				nextMove.blockRoute(current);
				current.setDeadEnd();// its fully blocked  - aka a deadEnd 
				current.blockRoute(nextMove);
				current.giveNegativeFeedback();
				aux.nR--;
			}
			
			noARoutes = aux.nR;
			
			if (noARoutes <= 1 && toBlock) // there is one way .. the way back. Block the position I say
				toBlock = true;			
			else // blocked a route but others are available .. position stands
				toBlock = false;
			
			/*
			 * Actual movement backwards
			 */
			
			/**
			 * XXX just realised this ... all of a sudden noi nu tinem cont ca
			 * graful e orientat. Daca vrei sa te intorci si nu poti?
			 */
			env.makeAction(no, nextMove);
			current = nextMove;
		}
	}
	
}
