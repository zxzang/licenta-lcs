package lcs;

/**
 * Edge for connecting 2 Positions.
 * @author Iustin Dumitrescu
 *
 */
public class Edge {
	/**
	 * The first position to which the edge is connected.
	 */
	Position first;
	String firstName;

	/**
	 * The second position to which the edge is connected.
	 */
	Position second;
	String secondName;

	/**
	 * Basic constructor.
	 * NOTE: not to be used yet.
	 * @param f - the first Position.
	 * @param s - the second Position.
	 */
	public Edge(final Position f, final Position s) {
		this.first = f;
		this.second = s;
	}

	/**
	 * Basic constructor.
	 * @param f - the first Position's name.
	 * @param s - the second Position's name.
	 */
	public Edge(final String f, final String s) {
		this.firstName = f;
		this.secondName = s;
	}

	/**
	 * Constructor used for HyperEdges.
	 * Do nothing and do it fabulously.
	 */
	public Edge() {
	}

}
