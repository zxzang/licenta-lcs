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
	
	/*
	 * 	Robot type
	 * 		1  -  Only wants to move on the best position
	 * 		2  -  Will move on the best position available
	 */	
	int type;
	
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
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
					if (type == 1) // Robot care vrea neaparat sa se miste pe cea mai buna pozitie
						nextMove = getNextMoveAbsolute(adjacent);
					else // Robotul decide sa urmeze cea mai buna cale libera					
						nextMove = getNextMoveAvailable(adjacent);
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
	
	/*
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

	/*
	 * Sets the working environment
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
	Position getNextMoveAbsolute(Vector<Position> available){
		System.out.println("eu " + Thread.currentThread() + "cer mutare absolute best ");
		return null;
	}
	
	Position getNextMoveAvailable(Vector<Position> available){
		System.out.println("eu " + Thread.currentThread() + "cer mutare best available");
		return null;
	}
	
	void removeDeadEnds(Vector<Position> adjacent){
		Vector<Position> toRemove = new Vector<Position>();
		
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
		int noOCurrentRoutes = 1;
		
		while (!lastSteps.isEmpty()){
			aux = lastSteps.removeLast();
			nextMove = aux.pos;
			noARoutes = aux.nR;			
			
			if (toBlock){
				current.blockRoute(nextMove);
				nextMove.blockRoute(current);
				noOCurrentRoutes --;
			}
			
		}
		
		
	}
	
	
	
	
	
	
}
