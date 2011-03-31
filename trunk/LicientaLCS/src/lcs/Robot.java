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
		for(Position x:adjacent)
			if (x.isDeadEnd())
				toRemove.add(x);
		for(Position x: toRemove)
			adjacent.remove(x);
	}
	
	void goBackNMark(){
		boolean toBlock = true;
		PositionNRoutes aux;
		Position nextMove;
		int noARoutes;		
		
		aux = lastSteps.removeLast();
		nextMove = aux.pos;
		noARoutes = aux.nR;			
		
		while (!lastSteps.isEmpty()){						
			
			if (toBlock){				
				nextMove.blockRoute(current);
				noARoutes --;
			}
			
			aux = lastSteps.removeLast();
			nextMove = aux.pos;
			
			if (noARoutes == 1);
			
		}
		
		
	}
	
	
	
	
	
	
}
