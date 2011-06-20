package lcs;

/**
 * An interface to be used by the {@link Environment} to force
 * any listeners to check for modifications.
 */
public interface EnvironmentFeedback {
	
	/**
	 * Basic update function.
	 * The implementor will have to check everything.
	 */
	public void update();
	
	/**
	 * Used to signal just one specific change.
	 * @param src - the node from which the {@link Robot} moved from.
	 * @param dst - the where he moved to.
	 */
	public void update(Position src, Position dst);
	
}
