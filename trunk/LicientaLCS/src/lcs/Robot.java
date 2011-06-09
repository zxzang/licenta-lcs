package lcs;

import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Vector;

import org.apache.log4j.*;


/**
 * 
 * @author Dascalu Sorin
 *
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
	int robotId;	
	private static Logger logger = Logger.getLogger("Robot");
	private static Barrier bar = Environment.robotBar;
	
	/**
	 * Number of robots instantiated of this class
	 */
	static int noRobots = 0;
	
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
		robotId = robotNum;
		this.type = type;
		noStepsBack = nSteps;
		lastSteps = new LinkedList<PositionNRoutes>();
		Robot.noRobots++;
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
	
	@Override
	public void run() {
		logger.debug(getName() + " is acting like a robot \n"+
				"\tand started from " + current + "[" + 
				current.getTopologicPostion() + "]");
		Position nextMove = null;
		Vector<Position> adjacent;
		String adjacentStr;
		
		while (current != target) {
			logger.debug("My turn - i am on " + current);			
			adjacent = env.getAdjacent(this);

			adjacentStr = "";
			for(Position x:adjacent)
				adjacentStr += x + " ";
			//logger.debug("All adjacents: "+adjacentStr);

			removeDeadEnds(adjacent);
			
			adjacentStr = "";
			for(Position x:adjacent)
				adjacentStr += x + " ";
			logger.debug("Valid adjacents: "+adjacentStr);

			if (adjacent.size() == 1) { // we're stuck
				logger.debug(getName() + " is on his way backwards");
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
				
				if (nextMove == null){
					logger.debug(this.getName() + " held his ground");
					try{
						bar.enterBarrier();
					} catch (InterruptedException ex) {
						logger.error(this.getName() + "could not enter the barrier");
					}
				} else {
					lastSteps.addLast(new PositionNRoutes(current, adjacent.size()));
					if (lastSteps.size() >= noStepsBack)
						lastSteps.removeFirst();
					env.makeAction(robotId, nextMove);					
					
					current = nextMove;
					
					//	Presupunem ca ar fi o pozitie buna daca tot am ajuns in ea
					//	Cand se va dovedi ca nu e buna o sa ii dam feedback negativ si se va anula efectul benefic
					current.givePositiveFeedback();
					
					try{
						bar.enterBarrier();
					} catch (InterruptedException ex) {
						logger.error(this.getName() + "could not enter the barrier");
					}

				}
				/**
				 * XXX aici ii dai un pos feedback de reward.
				 * Mai incolo ii scazi tot atat. In final un drum prost o sa aiba 0
				 * si nu ceva < 0.
				 * 
				 * --- eu am gandit ca nu e relevanta neaparat o recompensa negativa
				 * Un robot va alege intre doua pozitii x si y pe cea ce recompensa mai mare,
				 * nu pe cea cu recompensa pozitiva. Asa ca pentru el e acceasi chestie
				 * daca vede o recompensa pozitiva mica sau una negativa. Blocarea rutelor 
				 * nu sta in semnul recompensei ci in structura aia de rute blocate.
				 * Ideea de a ii da o recompensa si apoi de a o lua inapoi e sa 
				 * presupun ca robotu merge bun and pat him on the head dar daca se dovedeste
				 * ca e gresit sa ii iau recompensa inapoi pt nu a induce in eroare alti roboti.
				 * Am presupus ca un drum e drum bun until proven otherwise.
				 * Am putea sa nu alocam deloc recompensa si sa dam o recompensa pozitiva
				 * ultimilor 10 pasi atunci cand robotul chiar ajunge in pozitia finala
				 * (un fel de GoBackNMark doar ca varianta pozitiva). Dar nu mi se pare o
				 * varianta buna - ar trebui sa presupunem ca cel mai lung drum are un anumit
				 * numar de pasi - gotta think about it ( o varianta ar fi sa tinem minte tot
				 * drumu parcurs si sa il marcam pozitiv - cam complicat tho).
				 *  Zi-mi daca ti se pare o solutie mai buna.
				 *  XXX un position prost va avea 0 la fel ca unul nedescoperit inca.
				 */				
			}
		}
		
		logger.info("Landed on the promised land!");
		env.removeFromMap(robotId);
		bar.decThreadNum(); //	We must not wait for this robot anymore
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
			logger.error("[Robot.getNextMove] Unknown option " + type);
			return null;
		}
	}
	
	Position getNextMoveAbsolute(Vector<Position> available) {
		//logger.debug("eu " + getName() + " cer mutare absolute best");
		Position bestPos = null;
		// XXX what?! MIN_VALUE e deja negativ, you sure?
		int bestReward = -Integer.MIN_VALUE;
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
	
	Position getNextMoveAvailable(Vector<Position> available) {
		//System.out.println("eu " + getName() + " cer mutare best available");
		int tempReward;
		PositionNReward queueElement;
		Position nextMove;
		PriorityQueue<PositionNReward> posQueue =
			new PriorityQueue<PositionNReward>(11);
		
		/**
		 * I will the try the first best move available.
		 * If all my neighbours are occupied I will hold my ground.
		 */
		
		for (Position i: available) {
			tempReward = i.getFeedback() -
				(i.getTopologicPostion() - target.getTopologicPostion());
			queueElement = new PositionNReward(i, tempReward);
			posQueue.add(queueElement);
		}
			
		while (!posQueue.isEmpty()) {
			nextMove = posQueue.poll().pos;
			logger.debug("permits available on " + nextMove +
					" : " + nextMove.sem.availablePermits());
			if (nextMove.sem.tryAcquire()) {
				logger.debug("acquired "+nextMove);
				return nextMove;
			}
		}
		
		return null;		
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
			logger.debug(getName() + " is moving backward");
			
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
			
			// there is one way .. the way back. Block the position I say
			if (noARoutes <= 1 && toBlock)
				toBlock = true;			
			else // blocked a route but others are available .. position stands
				toBlock = false;
			
			/*
			 * Actual movement backwards
			 */
			while (!nextMove.sem.tryAcquire()){
				try{
					logger.debug(getName() + " held his ground " + current +
							"\n\twaiting for a previous " + nextMove +
							" to be free");
					bar.enterBarrier();
				} catch (InterruptedException ex){
					logger.error(getName() + "could not enter barrier");
				}
			}

			env.makeAction(robotId, nextMove);
			current = nextMove;
			try{
				bar.enterBarrier();
			} catch (InterruptedException ex){
				logger.error(getName() + "could not enter barrier");
			}
		}
	}
	
}
