package lcsmain;

import lcs.Environment;
import lcsgui.MainGui;

import org.apache.log4j.*;

public class LcsMain {
	
	/**
	 * The {@link Environment} used by the application.
	 */
	static Environment env;
	
	public final static boolean DEBUG = false;
	
	/**
	 * The {@link MainGui} used for visualizing the {@link Environment}.
	 */
	static MainGui gui;
	
	/**
	 * Logger used to log debug data and errors.
	 */
	public static Logger logger;
	
	private static void setLoggers(){
		logger = Logger.getLogger("LcsMain");
		PropertyConfigurator.configure("logger.properties");
		
	}
	
	/**
	 * The main for the whole application.
	 * @param args - the input file.
	 */
	public static void main(final String[] args) {
		
		setLoggers();		
		
		if (args.length != 1 && args.length != 2) {
			System.err.println("Input format: xml [ip].");
			return;
		}
		
		env = new Environment(args[0]);
		env.addAgents();
		if (args.length == 2) {
			gui = new MainGui(env, args[1]);
		} else {
			gui = new MainGui(env);
		}
		env.startAgents();
		/* TODO poate ar fi bine sa separam robotii de topologie 
				xmlul sa contina doar cum arata graful si atat
			XXX yeah ... asta a fost o chestie de dragul de a avea ceva ce merge
			nu vreau sa fie asta final
			poate adaugam prin gui si acolo tot dam start.
			ne mai gandim dupa facem ceva cu graful.
		*/
	}

}
