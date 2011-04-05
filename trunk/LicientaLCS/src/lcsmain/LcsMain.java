package lcsmain;

import lcs.Environment;
import lcsgui.GraphFrame;

public class LcsMain {
	
	static Environment env;
	static GraphFrame gFrame;
	
	/**
	 * The main for the whole application.
	 * @param args - the input file.
	 */
	public static void main(String[] args) {
		
		if (args.length != 1) {
			System.err.println("Please provide an input xml.");
			return;
		}
		
		env = new Environment(args[0]);
		gFrame = new GraphFrame(env);
		// TODO robot position
	}

}
