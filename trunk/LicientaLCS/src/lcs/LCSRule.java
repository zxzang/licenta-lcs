package lcs;

import java.util.Random;
import java.util.Vector;

public class LCSRule {
	private Position current;
	private Position next;
	private int fitness;
	
	public Position getCurrent(){
		return current; 
	}
	
	public Position getNext(){
		return next;
	}
	
	public int getFitness(){
		return fitness;
	}
	
	public void setFitness(int value){
		fitness = value;
	}
	
	public LCSRule(Position src, Position dst, int fitness){
		this.current = src;
		this.next = dst;
		this.fitness = fitness;
	}
	
	private static Vector<LCSRule> selectActionSet(Vector<LCSRule> ruleSet,
			Position src, Vector<Position> available){
		Vector<LCSRule> res = new Vector<LCSRule>();
		
		for(LCSRule x: ruleSet){
			if (x.getCurrent() == src &&
					available.contains(x.getNext()))
				res.add(x);
		}
		
		return res;
	}
	
	public static LCSRule selectRule(Vector<LCSRule> ruleSet,
			Position src, Vector<Position> available){
		
		LCSRule res = null;
		Vector<LCSRule> validRules = selectActionSet(ruleSet, src, available);
		
		int minFitness = 10000;
		long totalFitness = 0;
		long chosenFitness;
		long currentFitness = 0;
		Random rand = new Random();
		
		//Fitness can be negative; to be able to generate a true random
		//	amongst the rules we will have to make them all positive
		//	and after that randomize depending on fitness value.
		
		for(LCSRule x:validRules){
			if (x.getFitness() < minFitness)
				minFitness = x.getFitness();
		}
		
		if (minFitness < 0){
			for(LCSRule x:validRules)
				x.setFitness(x.getFitness() + minFitness + 1);
		}
		
		for(LCSRule x:validRules)
			totalFitness += x.getFitness();
		
		chosenFitness = Math.abs(rand.nextLong()%totalFitness);
		
		for(LCSRule x:validRules){
			currentFitness += x.getFitness();
			if (currentFitness > totalFitness){
				res = x;
				break;
			}
		}
		
		//	Reset fitness of rules to their original
		if (minFitness < 0){
			for(LCSRule x:validRules)
				x.setFitness(x.getFitness() - minFitness - 1);
		}		
		
		return res;
	}
	
	public static void updateRule(LCSRule rule, int updatedFitness){
		
	}
	
	
}
