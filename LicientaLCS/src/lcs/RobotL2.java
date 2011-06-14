package lcs;

import java.util.Vector;

public class RobotL2 extends Robot {

	private Vector<LCSRule> ruleSet;
	public RobotL2(int robotNum) {
		super(robotNum, Robot.BESTAVAILABLE);
		ruleSet = new Vector<LCSRule>();
		// TODO Auto-generated constructor stub
	}

	@Override
	Position getNextMove(Vector<Position> available) {
		Position res = null;
		LCSRule selected = LCSRule.selectRule(ruleSet, current, available);
		
		if (selected == null){
			for(Position x: available){
				int fitness = - Math.abs(x.getTopologicPostion() -
						current.getTopologicPostion());
				LCSRule newRule = new LCSRule(current, x, fitness);
				ruleSet.add(newRule);
			}
			selected = LCSRule.selectRule(ruleSet, current, available);
		}
		
		return res;
	}
}
