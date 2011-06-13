package lcs;

public class PositionPair {
	
	/**
	 * The first position of the pair.
	 */
	Position first;
	
	/**
	 * The second position of the pair.
	 */
	Position second;
	
	/**
	 * The combined reward of the pair.
	 */
	int reward;
	
	/**
	 * Basic constructor for the class.
	 */
	public PositionPair(Position first, Position second) {
		this.first = first;
		this.second = second;
		calculateReward();
	}
	
	/**
	 * Calculates the combined reward of this position.
	 * @return - the reward.
	 */
	public int calculateReward() {
		int maximum = first.getFeedback();
		if (second.getFeedback() > maximum) {
			maximum = second.getFeedback();
		}
		
		return reward;
	}
	
	/**
	 * Retrieves the combined reward of this pair.
	 * @return - the reward.
	 */
	public int getReward() {
		return reward;
	}
	
}
