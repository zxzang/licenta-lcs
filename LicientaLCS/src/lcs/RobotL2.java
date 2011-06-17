package lcs;

import java.util.Vector;

public class RobotL2 extends Robot {

	private Vector<LCSRule> ruleSet;
	private LCSRule selectedRule;
	
	public RobotL2(int robotNum) {
		super(robotNum, Robot.BESTAVAILABLE);
		ruleSet = new Vector<LCSRule>();
		// TODO Auto-generated constructor stub
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
		
		return res;
	}
	
	/**
	 * Function stub; will only be used on the 2nd level robots
	 */
	public void updateChosenRule(){
		env.giveReward(selectedRule);
	}
}
