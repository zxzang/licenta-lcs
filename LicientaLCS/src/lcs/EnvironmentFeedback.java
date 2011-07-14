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
	
	/**
	 * A class that implements this interface can store the commands from update.
	 * This is done so all the changes can be done at once.
	 * Useful for changes that need to sleep.
	 */
	public void change();
	
	/**
	 * Required due to UBI implementation.
	 * Clears a node.
	 * @param pos
	 */
	public void clear(Position pos);
	
}
