package lcs;

import java.util.*;

/**
 * 
 * @author Dascalu Sorin
 *
 */
public class Robot implements Runnable {

	class PositionNRoutes{
		Position pos;
		int nR;
		public PositionNRoutes(Position x, int y){
			pos = x;
			nR = y;
		}
	}
	
	Position current;
	Position target;
	Environment env;
	LinkedList<PositionNRoutes> lastSteps;
	int noStepsBack;
	int no;
	
	/**
	 * 	Robot type
	 * 		1  -  Only wants to move on the best position
	 * 		2  -  Will move on the best position available
	 */
	public static final int bestPosition = 1;
	public static final int bestAvailable = 2;
	int type;
	
	
	@Override
	public void run() {
		System.out.println(Thread.currentThread()+"is acting like a robot");
		Position nextMove = null;
		Vector<Position> adjacent;
		
		while (current != target){
			
			adjacent = env.getAdjacent(this);
			removeDeadEnds(adjacent);
			if (adjacent.size() == 1){ // we're stuck
				goBackNMark();
			}
			else {
				nextMove = null;
				
				//	I'll wait to move till I get a free position in which I can move
				while (nextMove == null){
					nextMove = getNextMove(adjacent);
				}
				
				lastSteps.addLast(new PositionNRoutes(current, adjacent.size()));
				if (lastSteps.size() >= noStepsBack)
					lastSteps.removeFirst();
				env.makeAction(no, nextMove);
				current = nextMove;
			}
		}
		
		System.out.println("Landed on the promised land!");			
	}
	
	/**
	 * Basic constructor
	 * @param n - robot number
	 * @param t - type of robot
	 */
	public Robot(int n, int t, int nSteps){
		no = n;		
		type = t;
		noStepsBack = nSteps;
		lastSteps = new LinkedList<PositionNRoutes>();
	}

	/**
	 * Sets the working environment.
	 * @param x - The environment to be set.
	 */
	public void setEnvironment(Environment x){
		env = x;
	}
	
	/*
	 * Sets the destination of the agent
	 */
	public void setCurrentGoal( Position stop){
		target = stop;		
	}
	
	/*
	 * Sets the start position of the agent
	 */
	public void setStartPosition(Position start){
		current = start;		
	}
	
	/*
	 * 
	 */
	Position getNextMove(Vector<Position> available){
		switch (this.type) {
		case bestPosition:
			// Robot care vrea neaparat sa se miste pe cea mai buna pozitie
			return getNextMoveAbsolute(available);
		case bestAvailable:
			// Robotul decide sa urmeze cea mai buna cale libera
			return getNextMoveAvailable(available);
		default:
			System.err.println("[Robot.getNextMove] Unknown option " + type);
			return null;
		}
	}
	Position getNextMoveAbsolute(Vector<Position> available){
		System.out.println("eu " + Thread.currentThread() + "cer mutare absolute best ");
		return null;
	}
	
	Position getNextMoveAvailable(Vector<Position> available){
		System.out.println("eu " + Thread.currentThread() + "cer mutare best available");
		return null;
	}
	
	/**
	 * Retrieves the position on which the robot is on.
	 * @return the current position.
	 */
	public Position getCurrentPosition() {
		return this.current;
	}
	
	void removeDeadEnds(Vector<Position> adjacent){
		Vector<Position> toRemove = new Vector<Position>();
		// why? daca ai scoate direct ar da exceptie?
		// pica la runtime .. am incercat intr-un lab si nu merge sa scoti un element din un vector cat timp
		//	iterezi pe el - java thing
		for(Position x:adjacent)
			if (x.isDeadEnd())
				toRemove.add(x);
		for(Position x: toRemove)
			adjacent.remove(x);
	}
	
	void goBackNMark(){
		// should I block the current node ?
		boolean toBlock = true;
		// used to stock next position on the road back and the number of routes for that position
		PositionNRoutes aux; 
		// the next position in my road back
		Position nextMove;
		// the no. of available routes of me current position
		int noARoutes;		
		
		while (!lastSteps.isEmpty()){						
			
			aux = lastSteps.removeLast();
			nextMove = aux.pos;

			/*
			 *  Part where I check out how to block certain routes
			 */
			if (toBlock){				
				nextMove.blockRoute(current);
				aux.nR--;
			}
			
			noARoutes = aux.nR;
			
			if (noARoutes <= 1 && toBlock) // there is one way .. the way back... block the position I say
				toBlock = true;
			else // blocked a route but others are available .. position stands
				toBlock = false;
			
			/*
			 * Actual movement backwards
			 */
			
			env.makeAction(no, nextMove);
			current = nextMove;
		}
		
		
	}
	
	
	
	
	
	
}
