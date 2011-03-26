package lcs;

import java.util.*;

/**
 * 
 * @author Dascalu Sorin
 *
 */
public class Robot implements Runnable {

	Position current;
	Position target;
	Environment env;
	LinkedList<Position> lastSteps;
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
			nextMove = null;
			
			while (nextMove == null){
				if (type == 1) // Robot care vrea neaparat sa se miste pe cea mai buna pozitie
					nextMove = getNextMoveAbsolute(adjacent);
				else // Robotul decide sa urmeze cea mai buna cale libera					
					nextMove = getNextMoveAvailable(adjacent);
				}
			env.makeAction(no, nextMove);
			current = nextMove;
		}
		
		System.out.println("Landed on the promised land!");
			
	}
	
	/*
	 * Basic constructor
	 * @param n - robot number
	 * @param t - type of robot
	 */
	public Robot(int n, int t){
		no = n;		
		type = t;
		lastSteps = new LinkedList<Position>();
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
	
	
	
	
	
	
}
