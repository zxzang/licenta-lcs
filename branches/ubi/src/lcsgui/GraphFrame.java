package lcsgui;

import org.ubiety.ubigraph.UbigraphClient;

import lcs.Environment;

/**
 * A gui for the {@link Environment} type.
 */
public class GraphFrame {
	/**
	 * Serial Version UID.
	 * Eclipse wouldn't shut up about it.
	 */
	private static final long serialVersionUID = 4323232456487666646L;
	
	/**
	 * The {@link Environment} to be displayed.
	 */
	private Environment env;
	
	private UbigraphClient graph = new UbigraphClient(url);
	
	/**
	 * Constructor for the class.
	 * @param e - the {@link Environment} to be displayed.
	 * @param address - the ubi server for visualization. 
	 */
	public GraphFrame(final Environment e, final String address) {
		this.env = e;
		
		graph = new UbigraphClient(address);
	}

}
