package lcsmain;

import lcs.Environment;
import lcsgui.GraphFrame;
import org.apache.log4j.*;

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
	 * Logger used to log debug data and errors.
	 */
	public static Logger logger = Logger.getLogger("LcsMain");
	
	/**
	 * The main for the whole application.
	 * @param args - the input file.
	 */
	public static void main(final String[] args) {
		
		PropertyConfigurator.configure("logger.properties");
		
		if (args.length != 1) {
			System.err.println("Please provide an input xml.");
			return;
		}
		
		env = new Environment(args[0]);
		gFrame = new GraphFrame(env);
		env.addAgents(0.5);
		env.startAgents();
		// TODO robot position
		// TODO poate ar fi bine sa separam robotii de topologie 
		//		xmlul sa contina doar cum arata graful si atat
	}

}
