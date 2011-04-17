package lcsmain;

import lcs.Environment;
import lcsgui.GraphFrame;

public class LcsMain {
	
	/**
	 * The {@link Environment} used by the application.
	 */
	static Environment env;
	
	/**
	 * The {@link GraphFrame} used for visualizing the {@link Environment}.
	 */
	static GraphFrame gFrame;
	
	/**
	 * The main for the whole application.
	 * @param args - the input file.
	 */
	public static void main(final String[] args) {
		
		if (args.length != 1) {
			System.err.println("Please provide an input xml.");
			return;
		}
		
		env = new Environment(args[0]);
		gFrame = new GraphFrame(env);
		// TODO robot position
	}

}
