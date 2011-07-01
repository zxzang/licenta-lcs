package lcs;

import java.util.Vector;

public class RobotL2 extends Robot {

	private Vector<LCSRule> ruleSet;
	private LCSRule selectedRule;
	
	public RobotL2(int robotNum) {
		super(robotNum, Robot.FORESEE);
		ruleSet = new Vector<LCSRule>();
	}
	
	protected void removePastSeenNodes(Vector<Position> available){
		for(Position x:available)
			if (lastSteps.contains(x))
				available.remove(x);
	}

	@Override
	protected Position getNextMove(Vector<Position> available) {
		Position res = null;
		//Position source = null;
		//removePastSeenNodes(available);
		
		/*
		if (lastSteps.size() >= 2)
			source = lastSteps.get(1).pos;
		if (source != null && available.contains(source) 
				&& available.size() > 1)
			available.remove(source);
			*/
		if (available.size() > 0){
			selectedRule = LCSRule.selectRule(ruleSet, current, available);
			
			if (selectedRule == null) {
				for(Position x : available) {
					int fitness = - Math.abs(x.getTopologicPostion() -
							current.getTopologicPostion());
					LCSRule newRule = new LCSRule(current, x, fitness);
					ruleSet.add(newRule);
				}
				selectedRule = LCSRule.selectRule(ruleSet, current, available);
			}
			
			if (selectedRule != null)
				res = selectedRule.getNext();
			
			if (res.sem.tryAcquire())
				return res;
			
			return null;
		} else
			return null;
		
	}
	
	/**
	 * Requests the {@link Environment} to reward the rule.
	 */
	public void updateChosenRule() {
		env.giveReward(selectedRule);
	}
}
