package lcs;

import java.util.Vector;

public class RobotL2 extends Robot {

	private Vector<LCSRule> ruleSet;
	private LCSRule selectedRule;
	
	public RobotL2(int robotNum) {
		super(robotNum, Robot.FORESEE);
		ruleSet = new Vector<LCSRule>();
	}

	@Override
	protected Position getNextMove(Vector<Position> available) {
		Position res = null;
		
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
	}
	
	/**
	 * Requests the {@link Environment} to reward the rule.
	 */
	public void updateChosenRule() {
		env.giveReward(selectedRule);
	}
}
