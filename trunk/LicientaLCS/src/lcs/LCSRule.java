package lcs;

import java.util.Random;
import java.util.Vector;

public class LCSRule {
	
	/**
	 * The first position on which the rule starts.
	 */
	private Position current;
	
	/**
	 * The position to which the rule indicates to move to. 
	 */
	private Position next;
	
	/**
	 * The fitness of the rule.
	 */
	private int fitness;
	
	/**
	 * A random number generator collectively used by the class.
	 */
	static Random rand = new Random();
	
	/**
	 * Gets the first position.
	 * @return - the current position of the class.
	 */
	public Position getCurrent() {
		return current;
	}
	
	/**
	 * The next position of the rule.
	 * @return - the position to which the rule indicates.
	 */
	public Position getNext() {
		return next;
	}
	
	/**
	 * Calculates and returns the fitness of the rule.
	 * @return - the fitness.
	 */
	public int getFitness() {
		if (next.getFeedback() - next.getTopologicPostion() > fitness)
			fitness = next.getFeedback() - next.getTopologicPostion();
		return fitness;
	}
	
	/**
	 * Sets the fitness of the rule.
	 * @param value - the value to which the fitness is to be set.
	 */
	public void setFitness(int value) {
		fitness = value;
	}
	
	/**
	 * Basic constructor.
	 * @param src - the source of the rule.
	 * @param dst - the next position.
	 * @param fitness - a fitness value for the rule.
	 */
	public LCSRule(Position src, Position dst, int fitness) {
		this.current = src;
		this.next = dst;
		this.fitness = fitness;
	}
	
	/**
	 * Selects only the valid rules from a rule set.
	 * @param ruleSet - a vector of all possible rules.
	 * @param src - the source from where to start.
	 * @param available - a vector of all possible adjacent neighbors. 
	 * @return - a vector containing the action set.
	 */
	private static Vector<LCSRule> selectActionSet(Vector<LCSRule> ruleSet,
			Position src, Vector<Position> available) {
		Vector<LCSRule> res = new Vector<LCSRule>();
		
		for(LCSRule x : ruleSet) {
			if (x.getCurrent() == src &&
					available.contains(x.getNext()))
				res.add(x);
		}
		
		return res;
	}
	
	/**
	 * Selects a rule to be executed based on the possibilities.
	 * @param ruleSet - a vector of all possible rules.
	 * @param src - the source from where to start.
	 * @param available - a vector of all possible adjacent neighbors. 
	 * @return - the best rule to be executed.
	 */
	public static LCSRule selectRule(Vector<LCSRule> ruleSet,
			Position src, Vector<Position> available) {
		
		LCSRule res = null;
		Vector<LCSRule> validRules = selectActionSet(ruleSet, src, available);
		
		int minFitness = Integer.MAX_VALUE;
		long totalFitness = 0;
		long chosenFitness;
		long currentFitness = 0;
		
		/* Fitness can be negative; to be able to generate a true random
		 * amongst the rules we will have to make them all positive
		 * and after that randomize depending on fitness value.
		 */
		
		if (validRules.size() == 0)
			return null;
		
		for(LCSRule x : validRules) {
			if (x.getFitness() < minFitness)
				minFitness = x.getFitness();
		}
		
		if (minFitness < 0) {
			for (LCSRule x : validRules)
				x.setFitness(x.getFitness() + Math.abs(minFitness)+ 1);
		}
		
		for (LCSRule x : validRules) {
			totalFitness += x.getFitness();
		}
		
		chosenFitness = Math.abs(rand.nextLong() % totalFitness);
		
		for(LCSRule x : validRules) {
			currentFitness += x.getFitness();
			if (currentFitness > chosenFitness) {
				res = x;
				break;
			}
		}
		
		/*	Reset fitness of rules to their original */
		if (minFitness < 0) {
			for(LCSRule x : validRules)
				x.setFitness(x.getFitness() - Math.abs(minFitness) - 1);
		}
		
		return res;
	}
	
}
